package com.gravatar

import android.net.Uri
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GravatarUtilsRobolectricTest {
    @Test
    fun gravatarUrl_isCorrect() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            emailAddressToGravatarUrl("example@example.com"),
        )
    }

    @Test
    fun gravatarUrlWithSizeParameter_isCorrect() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?size=42",
            emailAddressToGravatarUrl("example@example.com", size = 42),
        )
    }

    @Test
    fun gravatarUrlWithBigSizeParameter_isCorrect() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?size=1000",
            emailAddressToGravatarUrl("example@example.com", size = 1000),
        )
    }

    @Test
    fun gravatarUrlWithAvatarDefaultImageParameter_isCorrect() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=monsterid",
            emailAddressToGravatarUrl("example@example.com", defaultAvatarImage = DefaultAvatarImage.MONSTER),
        )
    }

    @Test
    fun gravatarUrlWithAvatarDefaultImageAndSizeParameter_isCorrect() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=monsterid&size=42",
            emailAddressToGravatarUrl("example@example.com", DefaultAvatarImage.MONSTER, 42),
        )
    }

    @Test
    fun gravatarUriWithAvatarDefaultImageAndSizeParameter_isCorrect() {
        assertEquals(
            Uri.parse(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&size=42",
            ),
            emailAddressToGravatarUri("example@example.com", DefaultAvatarImage.IDENTICON, 42),
        )
    }
}
