package com.jenil.f1comp.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

object LegalConstants {
    const val PRIVACY_POLICY_URL = "https://pitwall-apex.vercel.app/privacy"
    const val TERMS_CONDITIONS_URL = "https://pitwall-apex.vercel.app/terms"
}

fun openCustomTabUrl(context: Context, url: String) {
    try {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()
        customTabsIntent.launchUrl(context, url.toUri())
    } catch (_: Exception) {
        openWebUrl(context, url)
    }
}

fun openWebUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Cannot open web browser", Toast.LENGTH_SHORT).show()
    }
}
