package com.gravatar

import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class GravatarApiTest {
    @get:Rule
    var gravatarSdkTest = GravatarSdkContainerRule()

    private lateinit var gravatarApi: GravatarApi

    @Before
    fun setUp() {
        gravatarApi = GravatarApi()
    }

    @Test
    fun `given an file, email and accessToken when uploading avatar then Gravatar service is invoked`() {
        gravatarApi.uploadGravatar(File("avatarFile"), "email", "accessToken", mockk())
        verify(exactly = 1) {
            gravatarSdkTest.gravatarApiServiceMock.uploadImage(
                "Bearer accessToken",
                withArg {
                    assertTrue(
                        it.headers?.values("Content-Disposition").toString().contains("account"),
                    )
                },
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
