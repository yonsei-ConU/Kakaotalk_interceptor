package com.nokakao.interceptor

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nokakao.interceptor.data.local.MessageEntity
import com.nokakao.interceptor.logging.InterceptorLogEntry
import com.nokakao.interceptor.notification.KakaoNotificationListener
import com.nokakao.interceptor.ui.MainViewModel
import com.nokakao.interceptor.ui.theme.NoKakaoTheme
import com.nokakao.interceptor.util.isNotificationListenerEnabled
import java.text.DateFormat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as NotificationApp
        val dao = app.database.messageDao()

        setContent {
            NoKakaoTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModel.Factory(dao, app),
                )
                val messages by viewModel.messages.collectAsStateWithLifecycle()
                val logEntries by viewModel.logEntries.collectAsStateWithLifecycle()
                val isConnected by viewModel.isListenerConnected.collectAsStateWithLifecycle()
                val lastPkg by viewModel.lastPackageName.collectAsStateWithLifecycle()

                HomeScreen(
                    isListenerConnected = isConnected,
                    lastPackageName = lastPkg,
                    logEntries = logEntries,
                    messages = messages,
                    onOpenNotificationSettings = {
                        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    onDeleteAllMessages = {
                        viewModel.deleteAllMessages()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    isListenerConnected: Boolean,
    lastPackageName: String?,
    logEntries: List<InterceptorLogEntry>,
    messages: List<MessageEntity>,
    onOpenNotificationSettings: () -> Unit,
    onDeleteAllMessages: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionGranted by remember {
        mutableStateOf(context.isNotificationListenerEnabled(KakaoNotificationListener::class.java))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted =
                    context.isNotificationListenerEnabled(KakaoNotificationListener::class.java)
                
                if (permissionGranted) {
                    // Try to nudge the system to bind the service if it's not already
                    NotificationListenerService.requestRebind(
                        ComponentName(context, KakaoNotificationListener::class.java)
                    )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Intercepted messages") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onDeleteAllMessages) {
                Icon(Icons.Default.Delete, contentDescription = "Delete all messages")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                if (!permissionGranted) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Grant notification access so the listener can capture Kakao Talk alerts.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Button(onClick = onOpenNotificationSettings) {
                            Text("Open notification listener settings")
                        }
                    }
                } else if (!isListenerConnected) {
                    Text(
                        text = "Permission granted, but service is NOT bound. Try toggling the permission off and on in settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else {
                    Text(
                        text = "Listener enabled and active — Kakao notifications are saved then cleared from the shade.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                Text(
                    text = "Activity log",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (logEntries.isEmpty()) {
                item {
                    Text(
                        text = "No events yet. Send a Kakao message to see listener activity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(logEntries, key = { "log_${it.id}" }) { entry ->
                    LogEntryRow(entry)
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Saved messages",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            items(messages, key = { "msg_${it.id}" }) { entity ->
                MessageCard(entity)
            }
        }
    }
}

@Composable
private fun LogEntryRow(entry: InterceptorLogEntry) {
    val timeFormat = remember { DateFormat.getTimeInstance(DateFormat.MEDIUM) }
    Text(
        text = "[${timeFormat.format(entry.timestampMs)}] ${entry.message}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun MessageCard(entity: MessageEntity) {
    val formatter = remember { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            if (!entity.chatRoomName.isNullOrBlank()) {
                Text(
                    text = entity.chatRoomName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(entity.senderName, style = MaterialTheme.typography.titleMedium)
            Text(entity.messageContent, style = MaterialTheme.typography.bodyMedium)
            Text(
                formatter.format(entity.timestamp),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
