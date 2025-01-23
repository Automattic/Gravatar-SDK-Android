package com.gravatar.quickeditor.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.gravatar.quickeditor.ui.GravatarQuickEditorActivity.GravatarEditorActivityArguments
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.editor.bottomsheet.GravatarQuickEditorBottomSheet
import kotlinx.parcelize.Parcelize

/**
 * Activity that hosts the [GravatarQuickEditorBottomSheet] composable.
 * This activity is used to show the Gravatar Quick Editor in a bottom sheet.
 *
 * @see GravatarEditorActivityArguments
 */
@Deprecated(
    message = "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("GravatarQuickEditor.show()"),
)
public class GravatarQuickEditorActivity : AppCompatActivity() {
    private var avatarHasChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        val arguments = requireNotNull(intent.parcelable<GravatarEditorActivityArguments>(EXTRA_QE_ARGUMENTS))
        setContent {
            GravatarQuickEditorBottomSheet(
                gravatarQuickEditorParams = arguments.gravatarQuickEditorParams,
                authenticationMethod = arguments.authenticationMethod,
                onAvatarSelected = { avatarHasChanged = true },
                onDismiss = { finishWithResult() },
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AVATAR_HAS_CHANGED_KEY, avatarHasChanged)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        avatarHasChanged = savedInstanceState.getBoolean(AVATAR_HAS_CHANGED_KEY)
    }

    private fun finishWithResult() {
        val resultIntent = Intent().apply {
            putExtra(
                ACTIVITY_RESULT,
                if (avatarHasChanged) {
                    RESULT_AVATAR_SELECTED
                } else {
                    RESULT_DISMISSED
                },
            )
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun finish() {
        super.finish()
        if (SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    /**
     * Arguments for the [GravatarQuickEditorActivity].
     *
     * [gravatarQuickEditorParams] The parameters to configure the Quick Editor.
     * [authenticationMethod] The method used for authentication with the Gravatar REST API.
     */
    @Parcelize
    public class GravatarEditorActivityArguments(
        public val gravatarQuickEditorParams: GravatarQuickEditorParams,
        public val authenticationMethod: AuthenticationMethod,
    ) : Parcelable

    internal companion object {
        const val EXTRA_QE_ARGUMENTS: String = "qeArguments"
        private const val AVATAR_HAS_CHANGED_KEY: String = "avatarHasChanged"

        const val ACTIVITY_RESULT: String = "activityResult"
        const val RESULT_DISMISSED: Int = 1000
        const val RESULT_AVATAR_SELECTED: Int = 1001
    }
}

/**
 * Activity result contract to get the result from the [GravatarQuickEditorActivity].
 *
 * @see GravatarQuickEditorResult
 * @see GravatarEditorActivityArguments
 */
@Deprecated(
    message = "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("GravatarQuickEditor.show()"),
)
public class GetQuickEditorResult :
    ActivityResultContract<GravatarEditorActivityArguments, GravatarQuickEditorResult?>() {
    override fun createIntent(context: Context, input: GravatarEditorActivityArguments): Intent {
        return Intent(context, GravatarQuickEditorActivity::class.java).apply {
            putExtra(GravatarQuickEditorActivity.EXTRA_QE_ARGUMENTS, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GravatarQuickEditorResult? {
        return when (intent?.getIntExtra(GravatarQuickEditorActivity.ACTIVITY_RESULT, -1)) {
            GravatarQuickEditorActivity.RESULT_AVATAR_SELECTED -> GravatarQuickEditorResult.AVATAR_SELECTED
            GravatarQuickEditorActivity.RESULT_DISMISSED -> GravatarQuickEditorResult.DISMISSED
            else -> null
        }
    }
}

/**
 * Result enum for the [GravatarQuickEditorActivity].
 */
@Deprecated(
    message = "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("GravatarQuickEditor.show()"),
)
public enum class GravatarQuickEditorResult {
    AVATAR_SELECTED,
    DISMISSED,
}

private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}
