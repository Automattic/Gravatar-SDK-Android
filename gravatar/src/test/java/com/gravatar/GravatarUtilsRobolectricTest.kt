package com.gravatar

import android.net.Uri
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.IllegalArgumentException

@RunWith(RobolectricTestRunner::class)
class GravatarUtilsRobolectricTest {
    @Test
    fun `emailAddressToGravatarUrl must not add any query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            emailAddressToGravatarUrl("example@example.com"),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl supports size from 1 to 2048`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?s=1",
            emailAddressToGravatarUrl("example@example.com", size = 1),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl supports size 2048`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?s=2048",
            emailAddressToGravatarUrl("example@example.com", size = 2048),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl fails on size strictly greater than 2048`() {
        assertThrows(IllegalArgumentException::class.java) {
            emailAddressToGravatarUrl(
                "https://example.com/avatar/oiresntioes",
                size = 2049,
            )
        }
    }

    @Test
    fun `emailAddressToGravatarUrl must set the size but no other query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?s=1000",
            emailAddressToGravatarUrl("example@example.com", size = 1000),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl must add default avatar but no other query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=monsterid",
            emailAddressToGravatarUrl("example@example.com", defaultAvatarImage = DefaultAvatarImage.MONSTER),
        )
    }

    @Test
    fun `emailAddressToGravatarUri must add size and default avatar query params`() {
        assertEquals(
            Uri.parse(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&s=42",
            ),
            emailAddressToGravatarUri("example@example.com", 42, DefaultAvatarImage.IDENTICON),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl must add all supported parameters`() {
        assertEquals(
            Uri.parse(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=robohash&s=42&r=x&f=y",
            ),
            emailAddressToGravatarUri("example@example.com", 42, DefaultAvatarImage.ROBOHASH, ImageRating.X, true),
        )
    }

    @Test
    fun `rewrite gravatar url must replace size and default`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            gravatarImageUrlToGravatarImageUrl(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&s=42",
            ),
        )
    }

    @Test
    fun `rewrite gravatar url must add all supported parameters`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=identicon&s=42&r=pg&f=y",
            gravatarImageUrlToGravatarImageUrl(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66",
                42,
                DefaultAvatarImage.IDENTICON,
                ImageRating.ParentalGuidance,
                true,
            ),
        )
    }

    @Test
    fun `rewrite gravatar url must remove size and default if no parameter given`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            gravatarImageUrlToGravatarImageUrl(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&s=42",
            ),
        )
    }

    @Test
    fun `rewrite host on gravatar urls and drop parameters`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            gravatarImageUrlToGravatarImageUrl(
                "https://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&s=42",
            ),
        )
    }

    @Test
    fun `rewrite host on gravatar urls and set parameters`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=identicon&s=42",
            gravatarImageUrlToGravatarImageUrl(
                "https://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&s=42",
                42,
                DefaultAvatarImage.IDENTICON,
            ),
        )
    }

    @Test
    fun `rewrite gravatar url fails on a non gravatar URL`() {
        assertThrows(IllegalArgumentException::class.java) {
            gravatarImageUrlToGravatarImageUrl(
                "https://example.com/avatar/oiresntioes",
            )
        }
    }
}
