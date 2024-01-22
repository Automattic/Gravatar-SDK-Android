package com.gravatar

import com.gravatar.di.container.GravatarSdkContainer
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class GravatarApiTest {
    private lateinit var gravatarApi: GravatarApi
    private var gravatarSdkContainer = mockk<GravatarSdkContainer>()
    private var gravatarApiService = mockk<GravatarApiService>()

    @Before
    fun setUp() {
        gravatarSdkContainer = mockk<GravatarSdkContainer>()
        gravatarApiService = mockk<GravatarApiService>(relaxed = true)
        mockkObject(GravatarSdkContainer)
        every { GravatarSdkContainer.instance } returns gravatarSdkContainer
        every { gravatarSdkContainer.getGravatarApiService(any()) } returns gravatarApiService

        gravatarApi = GravatarApi()
    }

    @Test
    fun `given an file, email and accessToken when uploading avatar then Gravatar service is invoked`() {
        gravatarApi.uploadGravatar(File("avatarFile"), "email", "accessToken", mockk())
        verify(exactly = 1) {
            gravatarApiService.uploadImage(
                "Bearer accessToken",
                withArg { assertTrue(it.headers?.values("Content-Disposition").toString().contains("account")) },
                withArg {
                    assertTrue(
                        with(it.headers?.values("Content-Disposition").toString()) {
                            contains("filedata") && contains("avatarFile")
                        },
                    )
                },
            )
        }
    }
}
