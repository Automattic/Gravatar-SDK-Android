package com.gravatar

import android.net.Uri
import com.gravatar.DefaultAvatarOption.CustomUrl
import com.gravatar.DefaultAvatarOption.Identicon
import com.gravatar.DefaultAvatarOption.MonsterId
import com.gravatar.DefaultAvatarOption.RoboHash
import com.gravatar.ImageRating.ParentalGuidance
import com.gravatar.ImageRating.X
import com.gravatar.types.Email
import com.gravatar.types.sha256Hash
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AvatarUrlTest {
    @Test
    fun `emailAddressToGravatarUrl must not add any query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(Email("example@example.com")).uri().toString(),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl must set the size but no other query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?s=1000",
            AvatarUrl(Email("example@example.com")).uri(AvatarQueryOptions(preferredSize = 1000)).toString(),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl must add default avatar but no other query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=monsterid",
            AvatarUrl(Email("example@example.com")).uri(AvatarQueryOptions(defaultAvatarOption = MonsterId)).toString(),
        )
    }

    @Test
    fun `emailAddressToGravatarUri must add size and default avatar query params`() {
        assertEquals(
            Uri.parse(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=identicon&s=42",
            ),
            AvatarUrl(Email("example@example.com")).uri(AvatarQueryOptions(42, Identicon)),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl must add all supported parameters`() {
        assertEquals(
            Uri.parse(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?d=robohash&s=42&r=x&f=y",
            ),
            AvatarUrl(Email("example@example.com")).uri(AvatarQueryOptions(42, RoboHash, X, true)),
        )
    }

    @Test
    fun `rewrite gravatar url must replace size and default`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(
                Uri.parse(
                    "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?d=identicon&s=42",
                ),
            ).uri().toString(),
        )
    }

    @Test
    fun `rewrite gravatar url must add all supported parameters`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=identicon&s=42&r=pg&f=y",
            AvatarUrl(
                Uri.parse(
                    "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66",
                ),
            ).uri(
                AvatarQueryOptions(
                    42,
                    Identicon,
                    ParentalGuidance,
                    true,
                ),
            ).toString(),
        )
    }

    @Test
    fun `rewrite gravatar url must remove size and default if no parameter given`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(
                Uri.parse(
                    "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?d=identicon&s=42",
                ),
            ).uri().toString(),
        )
    }

    @Test
    fun `keep url scheme on gravatar urls and drop parameters`() {
        assertEquals(
            "http://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(
                Uri.parse(
                    "http://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?d=identicon",
                ),
            ).uri().toString(),
        )
    }

    @Test
    fun `keep host on gravatar dot com urls and set parameters`() {
        assertEquals(
            "https://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=identicon&s=42",
            AvatarUrl(
                Uri.parse(
                    "https://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?d=identicon&s=42",
                ),
            ).uri(AvatarQueryOptions(42, Identicon)).toString(),
        )
    }

    @Test
    fun `keep host on 1 dot gravatar dot com urls and set parameters`() {
        assertEquals(
            "https://1.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=identicon&s=42",
            AvatarUrl(
                Uri.parse(
                    "https://1.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?d=identicon&s=42",
                ),
            ).uri(AvatarQueryOptions(42, Identicon)).toString(),
        )
    }

    @Test
    fun `rewrite gravatar url fails on a non gravatar URL`() {
        assertThrows(IllegalArgumentException::class.java) {
            AvatarUrl(
                Uri.parse(
                    "https://example.com/avatar/oiresntioes",
                ),
            )
        }
    }

    @Test
    fun `hashing an input string with sha256 returns a valid hex string`() {
        assertEquals(
            "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
            "Hello World!".sha256Hash(),
        )
    }

    @Test
    fun `hashing a valid email address returns the expected hex string`() {
        assertEquals(
            "31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            Email("example@example.com").hash().toString(),
        )
    }

    @Test
    fun `hashing an email address trim left most empty spaces`() {
        assertEquals(
            Email("   example@example.com").hash().toString(),
            Email("example@example.com").hash().toString(),
        )
    }

    @Test
    fun `hashing an email address trim right most empty spaces`() {
        assertEquals(
            Email("example@example.com  ").hash().toString(),
            Email("example@example.com").hash().toString(),
        )
    }

    @Test
    fun `hashing an email address trim left and right most empty spaces`() {
        assertEquals(
            Email("    example@example.com   ").hash().toString(),
            Email("example@example.com").hash().toString(),
        )
    }

    @Test
    fun `hashing an email address lowercase inputs`() {
        assertEquals(
            Email("example@EXAMPLE.com").hash().toString(),
            Email("example@example.com").hash().toString(),
        )
    }

    @Test
    fun `hashing an email address lowercase inputs and trim left and right most empty spaces`() {
        assertEquals(
            Email(" EXample@EXAMPLE.com  ").hash().toString(),
            Email("example@example.com").hash().toString(),
        )
    }

    @Test
    fun `emailAddressToGravatarUrl supports custom url and encode them`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?d=https%3A%2F%2Fexample.com%2F%3Fencoded%3Dtrue%26please%3Dyes",
            AvatarUrl(Email("example@example.com")).uri(
                AvatarQueryOptions(
                    defaultAvatarOption = CustomUrl("https://example.com/?encoded=true&please=yes"),
                ),
            ).toString(),
        )
    }
}
