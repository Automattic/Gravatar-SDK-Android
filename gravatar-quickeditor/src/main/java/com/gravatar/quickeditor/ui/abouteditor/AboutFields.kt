package com.gravatar.quickeditor.ui.abouteditor

internal data class AboutFields(
    val personal: PersonalFields,
    val professional: ProfessionalFields,
) {
    companion object {
        val EMPTY: AboutFields = AboutFields(
            personal = PersonalFields(
                displayName = AboutInputField.Personal.DisplayName(value = ""),
                aboutMe = AboutInputField.Personal.AboutMe(value = ""),
                pronunciation = AboutInputField.Personal.Pronunciation(value = ""),
                pronouns = AboutInputField.Personal.Pronouns(value = ""),
                location = AboutInputField.Personal.Location(value = ""),
            ),
            professional = ProfessionalFields(
                jobTitle = AboutInputField.Professional.JobTitle(value = ""),
                company = AboutInputField.Professional.Company(value = ""),
            ),
        )
    }

    fun update(field: AboutInputField): AboutFields {
        return when (field) {
            is AboutInputField.Personal.DisplayName ->
                copy(personal = personal.copy(displayName = field))

            is AboutInputField.Personal.AboutMe ->
                copy(personal = personal.copy(aboutMe = field))

            is AboutInputField.Personal.Pronunciation ->
                copy(personal = personal.copy(pronunciation = field))

            is AboutInputField.Personal.Pronouns ->
                copy(personal = personal.copy(pronouns = field))

            is AboutInputField.Personal.Location ->
                copy(personal = personal.copy(location = field))

            is AboutInputField.Professional.JobTitle ->
                copy(professional = professional.copy(jobTitle = field))

            is AboutInputField.Professional.Company ->
                copy(professional = professional.copy(company = field))
        }
    }
}

internal data class PersonalFields(
    val displayName: AboutInputField.Personal.DisplayName,
    val aboutMe: AboutInputField.Personal.AboutMe,
    val pronunciation: AboutInputField.Personal.Pronunciation,
    val pronouns: AboutInputField.Personal.Pronouns,
    val location: AboutInputField.Personal.Location,
)

internal data class ProfessionalFields(
    val jobTitle: AboutInputField.Professional.JobTitle,
    val company: AboutInputField.Professional.Company,
)

internal sealed class AboutInputField {
    abstract val value: String
    open val maxLines: Int = 1
    abstract val order: Int

    sealed class Personal : AboutInputField() {
        data class DisplayName(
            override val value: String,
        ) : Personal() {
            override val order: Int = 0
        }

        data class AboutMe(
            override val value: String,
        ) : Personal() {
            override val order: Int = 1
            override val maxLines: Int = 4
        }

        data class Pronunciation(
            override val value: String,
        ) : Personal() {
            override val order: Int = 2
        }

        data class Pronouns(
            override val value: String,
        ) : Personal() {
            override val order: Int = 3
        }

        data class Location(
            override val value: String,
        ) : Personal() {
            override val order: Int = 4
        }
    }

    sealed class Professional : AboutInputField() {
        data class JobTitle(
            override val value: String,
        ) : Professional() {
            override val order: Int = 0
        }

        data class Company(
            override val value: String,
        ) : Professional() {
            override val order: Int = 1
        }
    }
}
