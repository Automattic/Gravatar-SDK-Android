package com.gravatar.quickeditor.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a set of fields that can be shown in the "About" section of the QuickEditor.
 *
 * @property value The value of the about input field.
 */
@JvmInline
@Parcelize
public value class AboutInputField(
    public val value: String,
) : Parcelable {
    public companion object {
        /**
         * The user’s display name.
         */
        @JvmStatic
        public val DisplayName: AboutInputField = AboutInputField("display_name")

        /**
         * A short biography or description about the user.
         */
        @JvmStatic
        public val AboutMe: AboutInputField = AboutInputField("about_me")

        /**
         * A phonetic pronunciation of the user’s name.
         */
        @JvmStatic
        public val Pronunciation: AboutInputField = AboutInputField("pronunciation")

        /**
         * The pronouns the user identifies with (e.g., she/her, they/them).
         */
        @JvmStatic
        public val Pronouns: AboutInputField = AboutInputField("pronouns")

        /**
         * The user's geographic location.
         */
        @JvmStatic
        public val Location: AboutInputField = AboutInputField("location")

        /**
         * The user's current job title or role.
         */
        @JvmStatic
        public val JobTitle: AboutInputField = AboutInputField("job_title")

        /**
         * The company or organization the user is affiliated with.
         */
        @JvmStatic
        public val Company: AboutInputField = AboutInputField("company")

        /**
         * User's first name. This is only provided in authenticated API requests.
         */
        @JvmStatic
        public val FirstName: AboutInputField = AboutInputField("first_name")

        /**
         * User's last name. This is only provided in authenticated API requests.
         */
        @JvmStatic
        public val LastName: AboutInputField = AboutInputField("last_name")

        /**
         * A convenience set representing all possible about info fields.
         */
        @JvmStatic
        public val all: Set<AboutInputField> = setOf(
            DisplayName,
            AboutMe,
            Pronunciation,
            Pronouns,
            Location,
            JobTitle,
            Company,
            FirstName,
            LastName,
        )

        /**
         * A subset of fields that are personal.
         */
        @JvmStatic
        public val personal: Set<AboutInputField> = setOf(
            DisplayName,
            AboutMe,
            Pronunciation,
            Pronouns,
            Location,
        )

        /**
         * A subset of fields that are professional or work-related.
         */
        @JvmStatic
        public val professional: Set<AboutInputField> = setOf(
            JobTitle,
            Company,
        )

        /**
         * A convenience set representing extra fields.
         */
        @JvmStatic
        public val extra: Set<AboutInputField> = setOf(
            FirstName,
            LastName,
        )
    }

    internal val isPersonal: Boolean
        get() = personal.contains(this)

    internal val isProfessional: Boolean
        get() = professional.contains(this)

    internal val isExtra: Boolean
        get() = extra.contains(this)

    internal val order: Int
        get() = when (this) {
            DisplayName -> 0
            AboutMe -> 1
            Pronunciation -> 2
            Pronouns -> 3
            Location -> 4
            JobTitle -> 100
            Company -> 101
            FirstName -> 200
            LastName -> 201
            else -> -1
        }
}
