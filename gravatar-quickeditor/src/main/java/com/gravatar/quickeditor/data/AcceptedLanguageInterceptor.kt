package com.gravatar.quickeditor.data

import android.content.Context
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.math.BigDecimal
import java.util.Locale

internal class AcceptedLanguageInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addAcceptedLanguageHeader(context.languagesList.asLocaleList.acceptedLanguageHeader).build(),
        )
    }

    private fun Request.Builder.addAcceptedLanguageHeader(languages: String) = addHeader("Accept-Language", languages)

    private val List<Locale>.acceptedLanguageHeader: String
        get() {
            var weight = BigDecimal(1)
            return map { it.language }.reduce { accumulator, language ->
                weight -= BigDecimal(0.1)
                "$accumulator,$language;q=${weight.toFloat()}"
            }
        }
}

internal val LocaleListCompat.asLocaleList: List<Locale>
    get() = buildList {
        if (this@asLocaleList.size() > 0) {
            for (index in 0 until this@asLocaleList.size()) {
                add(this@asLocaleList.get(index))
            }
        }
    }.filterNotNull()

internal val Context.languagesList: LocaleListCompat
    get() = LocaleManagerCompat.getApplicationLocales(this).takeIf { localeListCompat ->
        localeListCompat.size() > 0
    } ?: LocaleManagerCompat.getSystemLocales(this)
