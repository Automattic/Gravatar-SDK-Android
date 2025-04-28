package com.gravatar.restapi.infrastructure

import android.os.Build
import com.squareup.moshi.adapter
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Headers.Builder
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import java.io.File
import java.io.IOException
import java.net.URLConnection
import java.util.Locale
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal val EMPTY_REQUEST: RequestBody = ByteArray(0).toRequestBody()

internal open class ApiClient(
    internal val baseUrl: String,
    internal val client: Call.Factory = defaultClient,
) {
    internal companion object {
        protected const val CONTENT_TYPE: String = "Content-Type"
        protected const val ACCEPT: String = "Accept"
        protected const val AUTHORIZATION: String = "Authorization"
        protected const val JSON_MEDIA_TYPE: String = "application/json"
        protected const val FORM_DATA_MEDIA_TYPE: String = "multipart/form-data"
        protected const val FORM_URL_ENC_MEDIA_TYPE: String = "application/x-www-form-urlencoded"
        protected const val XML_MEDIA_TYPE: String = "application/xml"
        protected const val OCTET_MEDIA_TYPE: String = "application/octet-stream"
        protected const val TEXT_MEDIA_TYPE: String = "text/plain"

        internal var accessToken: String? = null

        @JvmStatic
        internal val defaultClient: OkHttpClient by lazy {
            builder.build()
        }

        @JvmStatic
        internal val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    }

    /**
     * Guess Content-Type header from the given byteArray (defaults to "application/octet-stream").
     *
     * @param byteArray The given file
     * @return The guessed Content-Type
     */
    protected fun guessContentTypeFromByteArray(byteArray: ByteArray): String {
        val contentType = try {
            URLConnection.guessContentTypeFromStream(byteArray.inputStream())
        } catch (io: IOException) {
            "application/octet-stream"
        }
        return contentType
    }

    /**
     * Guess Content-Type header from the given file (defaults to "application/octet-stream").
     *
     * @param file The given file
     * @return The guessed Content-Type
     */
    protected fun guessContentTypeFromFile(file: File): String {
        val contentType = URLConnection.guessContentTypeFromName(file.name)
        return contentType ?: "application/octet-stream"
    }

    /**
     * Adds a File to a MultipartBody.Builder
     * Defined a helper in the requestBody method to not duplicate code
     * It will be used when the content is a FORM_DATA_MEDIA_TYPE and the body of the PartConfig is a File
     *
     * @param name The field name to add in the request
     * @param headers The headers that are in the PartConfig
     * @param file The file that will be added as the field value
     * @return The method returns Unit but the new Part is added to the Builder that the extension function is applying on
     * @see requestBody
     */
    protected fun MultipartBody.Builder.addPartToMultiPart(name: String, headers: Map<String, String>, file: File) {
        val partHeaders = headers.toMutableMap() +
            ("Content-Disposition" to "form-data; name=\"$name\"; filename=\"${file.name}\"")
        val fileMediaType = guessContentTypeFromFile(file).toMediaTypeOrNull()
        addPart(
            partHeaders.toHeaders(),
            file.asRequestBody(fileMediaType),
        )
    }

    /**
     * Adds any type to a MultipartBody.Builder
     * Defined a helper in the requestBody method to not duplicate code
     * It will be used when the content is a FORM_DATA_MEDIA_TYPE and the body of the PartConfig is not a File.
     *
     * @param name The field name to add in the request
     * @param headers The headers that are in the PartConfig
     * @param obj The field name to add in the request
     * @return The method returns Unit but the new Part is added to the Builder that the extension function is applying on
     * @see requestBody
     */
    protected fun <T> MultipartBody.Builder.addPartToMultiPart(name: String, headers: Map<String, String>, obj: T?) {
        val partHeaders = headers.toMutableMap() +
            ("Content-Disposition" to "form-data; name=\"$name\"")
        addPart(
            partHeaders.toHeaders(),
            parameterToString(obj).toRequestBody(null),
        )
    }

    protected inline fun <reified T> requestBody(content: T, mediaType: String?): RequestBody = when {
        content is ByteArray -> content.toRequestBody(
            (mediaType ?: guessContentTypeFromByteArray(content)).toMediaTypeOrNull(),
        )
        content is File -> content.asRequestBody(
            (mediaType ?: guessContentTypeFromFile(content)).toMediaTypeOrNull(),
        )
        mediaType == FORM_DATA_MEDIA_TYPE ->
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    // content's type *must* be Map<String, PartConfig<*>>
                    @Suppress("UNCHECKED_CAST")
                    (content as Map<String, PartConfig<*>>).forEach { (name, part) ->
                        when (part.body) {
                            is File -> addPartToMultiPart(name, part.headers, part.body)
                            is List<*> -> {
                                part.body.forEach {
                                    if (it is File) {
                                        addPartToMultiPart(name, part.headers, it)
                                    } else {
                                        addPartToMultiPart(name, part.headers, it)
                                    }
                                }
                            }
                            else -> addPartToMultiPart(name, part.headers, part.body)
                        }
                    }
                }.build()
        mediaType == FORM_URL_ENC_MEDIA_TYPE -> {
            FormBody.Builder().apply {
                // content's type *must* be Map<String, PartConfig<*>>
                @Suppress("UNCHECKED_CAST")
                (content as Map<String, PartConfig<*>>).forEach { (name, part) ->
                    add(name, parameterToString(part.body))
                }
            }.build()
        }
        mediaType == null || mediaType.startsWith("application/") && mediaType.endsWith("json") ->
            if (content == null) {
                EMPTY_REQUEST
            } else {
                Serializer.moshi.adapter(T::class.java).toJson(content)
                    .toRequestBody((mediaType ?: JSON_MEDIA_TYPE).toMediaTypeOrNull())
            }
        mediaType == XML_MEDIA_TYPE -> throw UnsupportedOperationException("xml not currently supported.")
        mediaType == TEXT_MEDIA_TYPE && content is String ->
            content.toRequestBody(TEXT_MEDIA_TYPE.toMediaTypeOrNull())
        // TODO: this should be extended with other serializers
        else -> throw UnsupportedOperationException(
            "requestBody currently only supports JSON body, text body, byte body and File body.",
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    protected inline fun <reified T : Any?> responseBody(response: Response, mediaType: String? = JSON_MEDIA_TYPE): T? {
        val body = response.body
        if (body == null) {
            return null
        } else if (T::class.java == Unit::class.java) {
            // No need to parse the body when we're not interested in the body
            // Useful when API is returning other Content-Type
            return null
        } else if (T::class.java == File::class.java) {
            // return tempFile
            val contentDisposition = response.header("Content-Disposition")

            val fileName = if (contentDisposition != null) {
                // Get filename from the Content-Disposition header.
                val pattern = Pattern.compile("filename=['\"]?([^'\"\\s]+)['\"]?")
                val matcher = pattern.matcher(contentDisposition)
                if (matcher.find()) {
                    matcher.group(1)
                        ?.replace(".*[/\\\\]", "")
                        ?.replace(";", "")
                } else {
                    null
                }
            } else {
                null
            }

            var prefix: String?
            val suffix: String?
            if (fileName == null) {
                prefix = "download"
                suffix = ""
            } else {
                val pos = fileName.lastIndexOf(".")
                if (pos == -1) {
                    prefix = fileName
                    suffix = null
                } else {
                    prefix = fileName.substring(0, pos)
                    suffix = fileName.substring(pos)
                }
                // Files.createTempFile requires the prefix to be at least three characters long
                if (prefix.length < 3) {
                    prefix = "download"
                }
            }

            val tempFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                java.nio.file.Files.createTempFile(prefix, suffix).toFile()
            } else {
                @Suppress("DEPRECATION")
                createTempFile(prefix, suffix)
            }
            tempFile.deleteOnExit()
            body.byteStream().use { inputStream ->
                tempFile.outputStream().use { tempFileOutputStream ->
                    inputStream.copyTo(tempFileOutputStream)
                }
            }
            return tempFile as T
        }

        return when {
            mediaType == null || (mediaType.startsWith("application/") && mediaType.endsWith("json")) -> {
                val bodyContent = body.string()
                if (bodyContent.isEmpty()) {
                    return null
                }
                Serializer.moshi.adapter<T>().fromJson(bodyContent)
            }
            mediaType == OCTET_MEDIA_TYPE -> body.bytes() as? T
            mediaType == TEXT_MEDIA_TYPE -> body.string() as? T
            else -> throw UnsupportedOperationException(
                "responseBody currently only supports JSON body, text body and byte body.",
            )
        }
    }

    protected fun <T> updateAuthParams(requestConfig: RequestConfig<T>) {
        if (requestConfig.headers[AUTHORIZATION].isNullOrEmpty()) {
            accessToken?.let { accessToken ->
                requestConfig.headers[AUTHORIZATION] = "Bearer $accessToken"
            }
        }
        if (requestConfig.headers[AUTHORIZATION].isNullOrEmpty()) {
            accessToken?.let { accessToken ->
                requestConfig.headers[AUTHORIZATION] = "Bearer $accessToken "
            }
        }
    }

    protected suspend inline fun <reified I, reified T : Any> request(requestConfig: RequestConfig<I>): ApiResponse<T> {
        val httpUrl = baseUrl.toHttpUrlOrNull() ?: throw IllegalStateException("baseUrl is invalid.")

        // take authMethod from operation
        updateAuthParams(requestConfig)

        val url = httpUrl.newBuilder()
            .addEncodedPathSegments(requestConfig.path.trimStart('/'))
            .apply {
                requestConfig.query.forEach { query ->
                    query.value.forEach { queryValue ->
                        addQueryParameter(query.key, queryValue)
                    }
                }
            }.build()

        // take content-type/accept from spec or set to default (application/json) if not defined
        if (requestConfig.body != null && requestConfig.headers[CONTENT_TYPE].isNullOrEmpty()) {
            requestConfig.headers[CONTENT_TYPE] = JSON_MEDIA_TYPE
        }
        if (requestConfig.headers[ACCEPT].isNullOrEmpty()) {
            requestConfig.headers[ACCEPT] = JSON_MEDIA_TYPE
        }
        val headers = requestConfig.headers

        if (headers[ACCEPT].isNullOrEmpty()) {
            throw kotlin.IllegalStateException("Missing Accept header. This is required.")
        }

        val contentType = if (headers[CONTENT_TYPE] != null) {
            // TODO: support multiple CONTENT_TYPE options here.
            (headers[CONTENT_TYPE] as String).substringBefore(";").lowercase(Locale.US)
        } else {
            null
        }

        val request = when (requestConfig.method) {
            RequestMethod.DELETE -> Request.Builder().url(url).delete(requestBody(requestConfig.body, contentType))
            RequestMethod.GET -> Request.Builder().url(url)
            RequestMethod.HEAD -> Request.Builder().url(url).head()
            RequestMethod.PATCH -> Request.Builder().url(url).patch(requestBody(requestConfig.body, contentType))
            RequestMethod.PUT -> Request.Builder().url(url).put(requestBody(requestConfig.body, contentType))
            RequestMethod.POST -> Request.Builder().url(url).post(requestBody(requestConfig.body, contentType))
            RequestMethod.OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
        }.apply {
            val headersBuilder = Headers.Builder()
            headers.forEach { header ->
                headersBuilder.add(header.key, header.value)
            }
            this.headers(headersBuilder.build())
        }.build()

        val response: Response = suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)
            continuation.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            })
        }

        val accept = response.header(CONTENT_TYPE)?.substringBefore(";")?.lowercase(Locale.US)

        // TODO: handle specific mapping types. e.g. Map<int, Class<?>>
        @Suppress("UNNECESSARY_SAFE_CALL")
        return response.use {
            val rawBody: ResponseBody? = it.body
            when {
                it.isSuccessful -> ApiResponse(
                    rawResponse = response,
                    body = responseBody(it, accept),
                    errorBody = null,
                )
                else -> ApiResponse(
                    rawResponse = it,
                    body = null,
                    errorBody = rawBody?.let { body -> buffer(body) },
                )
            }
        }
    }

    protected fun parameterToString(value: Any?): String = when (value) {
        null -> ""
        is Array<*> -> toMultiValue(value, "csv").toString()
        is Iterable<*> -> toMultiValue(value, "csv").toString()
        else -> value.toString()
    }

    @Throws(IOException::class)
    private fun buffer(body: ResponseBody): ResponseBody {
        val buffer = Buffer()
        body.source().readAll(buffer)
        return buffer.asResponseBody(body.contentType(), body.contentLength())
    }
}
