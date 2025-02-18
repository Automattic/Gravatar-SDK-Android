package com.gravatar.quickeditor

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.gravatar.quickeditor.data.AcceptedLanguageInterceptor
import com.gravatar.quickeditor.data.FileUtils
import com.gravatar.quickeditor.data.ImageDownloader
import com.gravatar.quickeditor.data.datastore.createEncryptedFileWithFallbackReset
import com.gravatar.quickeditor.data.repository.AvatarRepository
import com.gravatar.quickeditor.data.storage.AvatarStorage
import com.gravatar.quickeditor.data.storage.DataStoreProfileStorage
import com.gravatar.quickeditor.data.storage.DataStoreTokenStorage
import com.gravatar.quickeditor.data.storage.InMemoryTokenStorage
import com.gravatar.quickeditor.data.storage.ProfileStorage
import com.gravatar.quickeditor.data.storage.TokenStorage
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
import io.github.osipxd.security.crypto.createEncrypted
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

public class QuickEditorContainer private constructor(
    private val context: Context,
) {
    public companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: QuickEditorContainer

        public fun init(context: Context): QuickEditorContainer {
            instance = QuickEditorContainer(context)
            return instance
        }

        internal fun getInstance(): QuickEditorContainer {
            check(::instance.isInitialized) {
                "QuickEditorContainer is not initialized. Call init() first."
            }
            return instance
        }
    }

//    private val tokenDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createEncrypted(
//        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
//    ) {
//        context.createEncryptedFileWithFallbackReset(name = "quick-editor-preferences")
//    }

    private val profileDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    ) {
        context.preferencesDataStoreFile("quick-editor-profile")
    }

    internal val dataStoreTokenStorage: TokenStorage by lazy {
        InMemoryTokenStorage()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AcceptedLanguageInterceptor(context))
            .build()
    }

    private var useInMemoryTokenStorage = false

    internal val inMemoryTokenStorage: InMemoryTokenStorage by lazy {
        InMemoryTokenStorage()
    }

    internal val tokenStorage: TokenStorage
        get() = if (useInMemoryTokenStorage) inMemoryTokenStorage else dataStoreTokenStorage

    internal val profileStorage: ProfileStorage by lazy {
        DataStoreProfileStorage(dataStore = profileDataStore, dispatcher = Dispatchers.IO)
    }

    private val avatarService: AvatarService by lazy {
        AvatarService(okHttpClient)
    }

    internal val profileService: ProfileService by lazy {
        ProfileService()
    }

    internal val fileUtils: FileUtils by lazy {
        FileUtils(context)
    }

    private val avatarStorage: AvatarStorage by lazy {
        AvatarStorage()
    }

    internal val avatarRepository: AvatarRepository
        get() = AvatarRepository(
            avatarService = avatarService,
            tokenStorage = tokenStorage,
            avatarStorage = avatarStorage,
            dispatcher = Dispatchers.IO,
        )

    internal val imageDownloader: ImageDownloader by lazy { ImageDownloader(context = context) }

    internal fun useInMemoryTokenStorage() {
        useInMemoryTokenStorage = true
    }

    internal fun resetUseInMemoryTokenStorage() {
        useInMemoryTokenStorage = false
    }
}
