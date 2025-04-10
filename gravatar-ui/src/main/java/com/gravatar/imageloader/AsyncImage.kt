package com.gravatar.imageloader

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.caverock.androidsvg.SVG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max

// Cache singleton
private object ImageCache {
    // In ImageCache object
    private val MAX_MEMORY = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val CACHE_SIZE = MAX_MEMORY / 8 // Use 1/8th of available memory
    private val memoryCache = object : LruCache<String, Bitmap>(CACHE_SIZE) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            // Estimate bitmap memory usage in kilobytes
            return value.byteCount / 1024
        }
    }

    fun get(key: String): Bitmap? = memoryCache.get(key)
    fun put(key: String, bitmap: Bitmap): Bitmap? = memoryCache.put(key, bitmap)
}

@Composable
public fun AsyncImage(
    imageSource: Any, // Can be a URL (String) or @DrawableRes Int
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: Painter? = null,
    errorPainter: Painter? = null,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    onError: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onLoading: () -> Unit = {}
) {
    var imageState by remember { mutableStateOf<ImageState>(ImageState.Loading) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(imageSource) {
        onLoading()
        coroutineScope.launch(Dispatchers.IO) {
            imageState = try {
                when (imageSource) {
                    is String -> {
                        // Check cache first on background thread
                        ImageCache.get(imageSource)?.let { cached ->
                            withContext(Dispatchers.Main) { onSuccess() }
                            ImageState.SuccessBitmap(cached)
                        } ?: loadNetworkImage(imageSource, context.resources)
                    }

                    is Int -> {
                        // Load drawable on main thread (UI resource access)
                        withContext(Dispatchers.Main) {
                            onSuccess()
                            ImageState.SuccessDrawable(imageSource)
                        }
                    }

                    else -> throw IllegalArgumentException("Unsupported image source type")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AsyncImage", "Error loading image", e)
                    onError()
                    ImageState.Error
                }
            }
        }
    }

    Box(modifier = modifier) {
        when (imageState) {
            is ImageState.Loading -> {
                placeholder?.let {
                    Image(
                        painter = it,
                        contentDescription = contentDescription,
                        contentScale = contentScale,
                        colorFilter = colorFilter
                    )
                }
            }

            is ImageState.SuccessBitmap -> {
                Image(
                    bitmap = (imageState as ImageState.SuccessBitmap)
                        .bitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    colorFilter = colorFilter
                )
            }

            is ImageState.SuccessDrawable -> {
                Image(
                    painter = painterResource(id = (imageState as ImageState.SuccessDrawable).drawableRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    colorFilter = colorFilter
                )
            }

            is ImageState.Error -> {
                errorPainter?.let {
                    Image(
                        painter = it,
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale,
                        colorFilter = colorFilter,
                    )
                }
            }
        }
    }
}

private suspend fun loadNetworkImage(
    urlString: String,
    resources: Resources
): ImageState.SuccessBitmap = withContext(Dispatchers.IO) {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.doInput = true
    connection.connect()

    val inputStream = connection.inputStream.use { it.readBytes() }

    // Process image on background thread
    val bitmap = if (urlString.endsWith(".svg", ignoreCase = true)) {
        val svg = SVG.getFromString(String(inputStream))
        renderSvgToBitmap(svg, resources)
    } else {
        BitmapFactory.decodeByteArray(inputStream, 0, inputStream.size)
    }

    // Update cache on background thread
    Log.d("AsyncImage", "Image cached for URL: $urlString, bitmap size: $bitmap")
    ImageCache.put(urlString, bitmap)

    ImageState.SuccessBitmap(bitmap)
}

private fun renderSvgToBitmap(svg: SVG, resources: Resources): Bitmap {
    // Get device density metrics
    val density = resources.displayMetrics.density
    val densityDpi = resources.displayMetrics.densityDpi

    // Calculate scaled dimensions using density
    val baseWidth = svg.documentWidth.takeIf { it > 0 } ?: (resources.displayMetrics.widthPixels / density)
    val baseHeight = svg.documentHeight.takeIf { it > 0 } ?: (resources.displayMetrics.heightPixels / density)

    // Enforce maximum bitmap size
    val maxSize = 4096
    val scaleFactor = minOf(1f, maxSize / max(baseWidth * density, baseHeight * density))
    val scaledWidth = (baseWidth * density * scaleFactor).toInt()
    val scaledHeight = (baseHeight * density * scaleFactor).toInt()

    // Create high-quality bitmap configuration
    val bitmap = createBitmap(
        scaledWidth,
        scaledHeight,
        Bitmap.Config.ARGB_8888
    ).apply {
        setDensity(densityDpi)
    }

    // Render with density-aware scaling
    Canvas(bitmap).apply {
        this.density = densityDpi
        svg.setDocumentWidth(scaledWidth.toFloat())
        svg.setDocumentHeight(scaledHeight.toFloat())
        try {
            svg.renderToCanvas(this)
        } catch (e: Exception) {
            Log.e("SVG_RENDER", "Error rendering SVG: ${e.message}")
        }
    }
    return bitmap
}

private sealed class ImageState {
    data object Loading : ImageState()
    data class SuccessBitmap(val bitmap: Bitmap) : ImageState()
    data class SuccessDrawable(@DrawableRes val drawableRes: Int) : ImageState()
    data object Error : ImageState()
}
