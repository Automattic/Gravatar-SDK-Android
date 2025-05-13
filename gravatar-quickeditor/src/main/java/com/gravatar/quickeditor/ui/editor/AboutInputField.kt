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
    }
}
