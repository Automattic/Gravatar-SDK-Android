package com.gravatar.quickeditor.ui.editor

import org.junit.Assert.assertEquals
import org.junit.Test

class QuickEditorScopeOptionTest {
    @Test
    fun `avatarPicker DSL creates same result as static method`() {
        // Using static method
        val staticResult = QuickEditorScopeOption.avatarPicker(
            AvatarPickerConfiguration(AvatarPickerContentLayout.Vertical),
        )

        // Using DSL
        val dslResult = QuickEditorScopeOption.avatarPicker {
            contentLayout = AvatarPickerContentLayout.Vertical
        }

        assertEquals(staticResult, dslResult)
    }

    @Test
    fun `aboutEditor DSL creates same result as static method`() {
        // Using static method
        val staticResult = QuickEditorScopeOption.aboutEditor(
            AboutEditorConfiguration(setOf(AboutInputField.DisplayName, AboutInputField.FirstName)),
        )

        // Using DSL
        val dslResult = QuickEditorScopeOption.aboutEditor {
            fields = setOf(AboutInputField.DisplayName, AboutInputField.FirstName)
        }

        assertEquals(staticResult, dslResult)
    }

    @Test
    fun `avatarAndAbout DSL creates same result as static method`() {
        // Using static method
        val staticResult = QuickEditorScopeOption.avatarAndAbout(
            AvatarPickerAndAboutEditorConfiguration(
                contentLayout = AvatarPickerContentLayout.Vertical,
                fields = setOf(AboutInputField.DisplayName, AboutInputField.FirstName),
                initialPage = AvatarPickerAndAboutEditorConfiguration.Page.AboutEditor,
            ),
        )

        // Using DSL
        val dslResult = QuickEditorScopeOption.avatarAndAbout {
            contentLayout = AvatarPickerContentLayout.Vertical
            fields = setOf(AboutInputField.DisplayName, AboutInputField.FirstName)
            initialPage = AvatarPickerAndAboutEditorConfiguration.Page.AboutEditor
        }

        assertEquals(staticResult, dslResult)
    }

    @Test
    fun `avatarPicker DSL with default values creates same result as static method with default values`() {
        // Using static method with default values
        val staticResult = QuickEditorScopeOption.avatarPicker()

        // Using DSL with default values
        val dslResult = QuickEditorScopeOption.avatarPicker {}

        assertEquals(staticResult, dslResult)
    }

    @Test
    fun `aboutEditor DSL with default values creates same result as static method with default values`() {
        // Using static method with default values
        val staticResult = QuickEditorScopeOption.aboutEditor()

        // Using DSL with default values
        val dslResult = QuickEditorScopeOption.aboutEditor {}

        assertEquals(staticResult, dslResult)
    }

    @Test
    fun `avatarAndAbout DSL with default values creates same result as static method with default values`() {
        // Using static method with default values
        val staticResult = QuickEditorScopeOption.avatarAndAbout()

        // Using DSL with default values
        val dslResult = QuickEditorScopeOption.avatarAndAbout {}

        assertEquals(staticResult, dslResult)
    }
}
