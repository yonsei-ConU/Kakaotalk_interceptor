package com.nokakao.interceptor.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

/**
 * Returns whether this app's [NotificationListenerService] component is enabled in secure settings.
 */
fun Context.isNotificationListenerEnabled(listenerClass: Class<*>): Boolean {
    val component = ComponentName(this, listenerClass)
    val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        ?: return false
    return flat.split(":")
        .mapNotNull { ComponentName.unflattenFromString(it) }
        .any { it == component }
}
