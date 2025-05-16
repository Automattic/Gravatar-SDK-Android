package com.gravatar.quickeditor.ui.abouteditor

internal data class AboutFields(
    val personal: PersonalFields,
    val professional: ProfessionalFields,
) {
    companion object {
        val EMPTY: AboutFields = AboutFields(
            personal = PersonalFields(
                displayName = AboutEditorField.Personal.DisplayName(value = ""),
                aboutMe = AboutEditorField.Personal.AboutMe(value = ""),
                pronunciation = AboutEditorField.Personal.Pronunciation(value = ""),
                pronouns = AboutEditorField.Personal.Pronouns(value = ""),
                location = AboutEditorField.Personal.Location(value = ""),
            ),
            professional = ProfessionalFields(
                jobTitle = AboutEditorField.Professional.JobTitle(value = ""),
                company = AboutEditorField.Professional.Company(value = ""),
            ),
        )
    }

    fun update(field: AboutEditorField): AboutFields {
        return when (field) {
            is AboutEditorField.Personal.DisplayName ->
                copy(personal = personal.copy(displayName = field))

            is AboutEditorField.Personal.AboutMe ->
                copy(personal = personal.copy(aboutMe = field))

            is AboutEditorField.Personal.Pronunciation ->
                copy(personal = personal.copy(pronunciation = field))

            is AboutEditorField.Personal.Pronouns ->
                copy(personal = personal.copy(pronouns = field))

            is AboutEditorField.Personal.Location ->
                copy(personal = personal.copy(location = field))

            is AboutEditorField.Professional.JobTitle ->
                copy(professional = professional.copy(jobTitle = field))

            is AboutEditorField.Professional.Company ->
                copy(professional = professional.copy(company = field))
        }
    }
}

internal data class PersonalFields(
    val displayName: AboutEditorField.Personal.DisplayName,
    val aboutMe: AboutEditorField.Personal.AboutMe,
    val pronunciation: AboutEditorField.Personal.Pronunciation,
    val pronouns: AboutEditorField.Personal.Pronouns,
    val location: AboutEditorField.Personal.Location,
)

internal data class ProfessionalFields(
    val jobTitle: AboutEditorField.Professional.JobTitle,
    val company: AboutEditorField.Professional.Company,
)

internal sealed class AboutEditorField {
    abstract val value: String
    open val maxLines: Int = 1
    abstract val visible: Boolean
    abstract val order: Int

    sealed class Personal : AboutEditorField() {
        data class DisplayName(
            override val value: String,
            override val visible: Boolean = true,
        ) : Personal() {
            override val order: Int = 0
        }

        data class AboutMe(
            override val value: String,
            override val visible: Boolean = true,
        ) : Personal() {
            override val order: Int = 1
            override val maxLines: Int = 4
        }

        data class Pronunciation(
            override val value: String,
            override val visible: Boolean = true,
        ) : Personal() {
            override val order: Int = 2
        }

        data class Pronouns(
            override val value: String,
            override val visible: Boolean = true,
        ) : Personal() {
            override val order: Int = 3
        }

        data class Location(
            override val value: String,
            override val visible: Boolean = true,
        ) : Personal() {
            override val order: Int = 4
        }
    }

    sealed class Professional : AboutEditorField() {
        data class JobTitle(
            override val value: String,
            override val visible: Boolean = true,
        ) : Professional() {
            override val order: Int = 0
        }

        data class Company(
            override val value: String,
            override val visible: Boolean = true,
        ) : Professional() {
            override val order: Int = 1
        }
    }
}
