package com.gravatar.quickeditor.ui.abouteditor

import android.view.Surface
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gravatar.quickeditor.ui.abouteditor.components.AboutSection
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.extensions.QESnackbarHost
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AboutEditor(
    quickEditorParams: GravatarQuickEditorParams,
    viewModel: AboutEditorViewModel = viewModel(
        factory = AboutEditorViewModelFactory(quickEditorParams),
    ),
) {
    val snackState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    Surface {
        Box(modifier = Modifier.wrapContentSize()) {
            AboutEditor(
                uiState = uiState,
                onValueChange = { aboutField ->
                    viewModel.onEvent(AboutEditorEvent.OnAboutFieldUpdated(aboutField))
                },
            )
            QESnackbarHost(
                modifier = Modifier
                    .align(Alignment.BottomStart),
                hostState = snackState,
            )
        }
    }
}

@Composable
internal fun AboutEditor(uiState: AboutEditorUiState, onValueChange: (AboutInputField) -> Unit) {
    Surface {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .height(300.dp)
                        .fillMaxWidth(),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            else -> {
                AboutSection(
                    aboutFields = uiState.aboutFields,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onValueChange = onValueChange,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditorLoadedPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutEditor(
                uiState = AboutEditorUiState(
                    aboutFields = AboutFields(
                        personal = PersonalFields(
                            aboutMe = AboutInputField.Personal.AboutMe(value = "My description"),
                            displayName = AboutInputField.Personal.DisplayName(value = "John Doe"),
                            location = AboutInputField.Personal.Location(value = "San Francisco, CA"),
                            pronunciation = AboutInputField.Personal.Pronunciation(value = "John Doe"),
                            pronouns = AboutInputField.Personal.Pronouns(value = "he/him"),
                        ),
                        professional = ProfessionalFields(
                            company = AboutInputField.Professional.Company(value = "Automattic"),
                            jobTitle = AboutInputField.Professional.JobTitle(value = "Software Engineer"),
                        ),
                    ),
                ),
                onValueChange = { },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AboutEditorLoadingPreview() {
    GravatarTheme {
        Box(modifier = Modifier.padding(10.dp)) {
            AboutEditor(
                uiState = AboutEditorUiState(
                    aboutFields = AboutFields.EMPTY,
                    isLoading = true,
                ),
                onValueChange = { },
            )
        }
    }
}
