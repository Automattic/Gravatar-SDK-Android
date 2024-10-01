package com.gravatar

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
import java.net.URL

class AvatarUrlTest {
    @Test
    fun `AvatarUrl created via an email address must not add any query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(Email("example@example.com")).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an email address must set the size but no other query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?size=1000",
            AvatarUrl(
                Email("example@example.com"),
                AvatarQueryOptions { preferredSize = 1000 },
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an email address must add default avatar but no other query param if not set`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?default=monsterid",
            AvatarUrl(
                Email("example@example.com"),
                AvatarQueryOptions { defaultAvatarOption = MonsterId },
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an email address must add size and default avatar query params`() {
        assertEquals(
            URL(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?default=identicon&size=42",
            ),
            AvatarUrl(
                Email("example@example.com"),
                AvatarQueryOptions {
                    preferredSize = 42
                    defaultAvatarOption = Identicon
                },
            ).url(),
        )
    }

    @Test
    fun `AvatarUrl created via an email address must add all supported parameters`() {
        assertEquals(
            URL(
                "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                    "970a1e66?default=robohash&size=42&rating=x&forcedefault=y",
            ),
            AvatarUrl(
                Email("example@example.com"),
                AvatarQueryOptions {
                    preferredSize = 42
                    defaultAvatarOption = RoboHash
                    rating = X
                    forceDefaultAvatar = true
                },
            ).url(),
        )
    }

    @Test
    fun `AvatarUrl created via an URL must replace size and default`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(
                URL(
                    "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?default=identicon&size=42",
                ),
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an URL must add all supported parameters`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?default=identicon&size=42&rating=pg&forcedefault=y",
            AvatarUrl(
                URL(
                    "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66",
                ),
                AvatarQueryOptions {
                    preferredSize = 42
                    defaultAvatarOption = Identicon
                    rating = ParentalGuidance
                    forceDefaultAvatar = true
                },
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an URL must remove size and default if no parameter given`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(
                URL(
                    "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?default=identicon&size=42",
                ),
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an URL must keep gravatar host and path but drop parameters`() {
        assertEquals(
            "http://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66",
            AvatarUrl(
                URL(
                    "http://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?default=identicon",
                ),
            ).url().toString(),
        )
    }

    @Test
    fun `keep host on gravatar dot com urls and set parameters`() {
        assertEquals(
            "https://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?default=identicon&size=42",
            AvatarUrl(
                URL(
                    "https://gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?default=identicon&size=42",
                ),
                AvatarQueryOptions {
                    preferredSize = 42
                    defaultAvatarOption = Identicon
                },
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an URL must keep gravatar host and set parameters`() {
        assertEquals(
            "https://1.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?default=identicon&size=42",
            AvatarUrl(
                URL(
                    "https://1.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe" +
                        "970a1e66?default=identicon&size=42",
                ),
                AvatarQueryOptions {
                    preferredSize = 42
                    defaultAvatarOption = Identicon
                },
            ).url().toString(),
        )
    }

    @Test
    fun `AvatarUrl created via an URL fails on a non gravatar URL`() {
        assertThrows(IllegalArgumentException::class.java) {
            AvatarUrl(
                URL(
                    "https://example.com/avatar/oiresntioes",
                ),
            )
        }
    }

    @Test
    fun `AvatarUrl created via an URL fails on a non gravatar hash URL`() {
        assertThrows(IllegalArgumentException::class.java) {
            AvatarUrl(
                URL(
                    "https://gravatar.com/",
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
    fun `AvatarUrl created via an email address supports custom url and encode them`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?default=https%3A%2F%2Fexample.com%2F%3Fencoded%3Dtrue%26please%3Dyes",
            AvatarUrl(
                Email("example@example.com"),
                AvatarQueryOptions {
                    defaultAvatarOption = CustomUrl("https://example.com/?encoded=true&please=yes")
                },
            ).url().toString(),
        )
    }

    @Test
    fun `force default avatar false must generate forcedefault=n`() {
        assertEquals(
            "https://www.gravatar.com/avatar/31c5543c1734d25c7206f5fd591525d0295bec6fe84ff82f946a34fe970a1e66" +
                "?forcedefault=n",
            AvatarUrl(
                Email("example@example.com"),
                AvatarQueryOptions {
                    forceDefaultAvatar = false
                },
            ).url().toString(),
        )
    }
}
