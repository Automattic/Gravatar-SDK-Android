package com.gravatar.events

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import com.gravatar.models.UserProfile
import com.gravatar.utils.getDisplayName
import com.gravatar.utils.getPrimaryEmail

public fun saveContact(context: Context, profile: UserProfile) {
    val intentInsertEdit = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
        type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
    }

    intentInsertEdit.apply {
        // Add name
        putExtra(ContactsContract.Intents.Insert.NAME, profile.getDisplayName())

        // Add primary email as work email
        putExtra(ContactsContract.Intents.Insert.EMAIL, profile.getPrimaryEmail())
        putExtra(
            ContactsContract.Intents.Insert.EMAIL_TYPE,
            ContactsContract.CommonDataKinds.Email.TYPE_WORK,
        )

        // Add first phone number as work phone number
        putExtra(ContactsContract.Intents.Insert.PHONE, profile.phoneNumbers.firstOrNull()?.value)
        putExtra(
            ContactsContract.Intents.Insert.PHONE_TYPE,
            ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
        )
    }
    context.startActivity(intentInsertEdit)
    // activityLauncher.launch(intentInsertEdit)
}
