package com.gravatar.demoapp.ui.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalSheetProperties
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import com.composeunstyled.Thumb
import com.composeunstyled.ToggleSwitch
import com.gravatar.quickeditor.ui.editor.AboutInputField

@Composable
internal fun AboutFieldsBottomSheet(
    aboutFields: Set<AboutInputField>,
    onFieldsChanged: (Set<AboutInputField>) -> Unit,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        state = rememberModalBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
        ),
        onDismiss = onDismiss,
        properties = ModalSheetProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Scrim(
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
        )
        Sheet(
            modifier = Modifier
                .imePadding()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .padding(
                    WindowInsets.navigationBars
                        .only(WindowInsetsSides.Vertical)
                        .asPaddingValues(),
                ),
        ) {
            Surface(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                tonalElevation = 1.dp,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AboutInputField.all.forEach { field ->
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                        ) {
                            val fieldsChecked = aboutFields.contains(field)

                            val animatedColor by animateColorAsState(
                                if (fieldsChecked) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.inversePrimary
                                },
                            )
                            Text(
                                text = field.translatedValue(LocalContext.current),
                                modifier = Modifier
                                    .weight(1f),
                            )
                            ToggleSwitch(
                                toggled = fieldsChecked,
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = RoundedCornerShape(100),
                                contentPadding = PaddingValues(2.dp),
                                onToggled = { toggled ->
                                    if (toggled) {
                                        onFieldsChanged(aboutFields + field)
                                    } else {
                                        onFieldsChanged(aboutFields - field)
                                    }
                                },
                                thumb = {
                                    Thumb(
                                        shape = CircleShape,
                                        color = animatedColor,
                                        modifier = Modifier.shadow(elevation = 4.dp, CircleShape),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun AboutInputField.translatedValue(context: Context): String {
    return when (this) {
        AboutInputField.DisplayName -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_display_name
        AboutInputField.AboutMe -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_about_me
        AboutInputField.Pronouns -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_pronouns
        AboutInputField.Pronunciation -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_pronunciation
        AboutInputField.Location -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_location
        AboutInputField.JobTitle -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_job_title
        AboutInputField.Company -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_company
        AboutInputField.FirstName -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_first_name
        AboutInputField.LastName -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_last_name
        else -> com.gravatar.quickeditor.R.string.gravatar_qe_about_field_label_display_name
    }.let { context.getString(it) }
}

@Preview
@Composable
private fun AboutFieldsBottomSheetPreview() {
    AboutFieldsBottomSheet(
        aboutFields = setOf(
            AboutInputField.DisplayName,
            AboutInputField.AboutMe,
            AboutInputField.Pronouns,
            AboutInputField.Pronunciation,
            AboutInputField.Location,
            AboutInputField.JobTitle,
            AboutInputField.Company,
            AboutInputField.FirstName,
            AboutInputField.LastName,
        ),
        onFieldsChanged = {},
    )
}
