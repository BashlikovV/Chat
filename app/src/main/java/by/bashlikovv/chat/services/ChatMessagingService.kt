package by.bashlikovv.chat.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import by.bashlikovv.chat.R

const val READ_ACTION = "by.bashlikovv.chat.services.ACTION_MESSAGE_READ"
const val REPLY_ACTION = "by.bashlikovv.chat.services.ACTION_MESSAGE_REPLY"
const val CONVERSATION_ID = "conversation_id"
const val EXTRA_VOICE_REPLY = "extra_voice_reply"

class ChatMessagingService : Service() {

    private val mMessenger = Messenger(IncomingHandler())
    private lateinit var mNotificationManager: NotificationManagerCompat

    override fun onBind(intent: Intent): IBinder {
        return mMessenger.binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    //START_STICKY -- service will recreated when possible(after kill)
    //START_NOT_STICKY -- Android systems won`t recreate service if it has resources
    //START_REDELIVER_INTENT -- START_STICKY + service again will receive 
    // all startService calls

    private fun createIntent(conversationId: Int, action: String): Intent {
        return Intent().apply {
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            setAction(action)
            putExtra(CONVERSATION_ID, conversationId)
        }
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun sendNotification(
        conversationId: Int,
        message: String,
        participant: String,
        timestamp: Long
    ) {
        // A pending Intent for reads
        val readPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            conversationId,
            createIntent(conversationId, READ_ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build a RemoteInput for receiving voice input in a Car Notification
        val remoteInput = RemoteInput.Builder(EXTRA_VOICE_REPLY)
            .setLabel("Reply by voice")
            .build()

        // Building a Pending Intent for the reply action to trigger
        val replyIntent = PendingIntent.getBroadcast(
            applicationContext,
            conversationId,
            createIntent(conversationId, REPLY_ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create the UnreadConversation and populate it with the participant name,
        // read and reply intents.
        val unreadConversationBuilder = NotificationCompat.CarExtender.UnreadConversation.Builder(participant)
            .setLatestTimestamp(timestamp)
            .setReadPendingIntent(readPendingIntent)
            .setReplyAction(replyIntent, remoteInput)

        val builder = NotificationCompat.Builder(applicationContext)
            // Set the application notification icon:
            //.setSmallIcon(R.drawable.notification_icon)

            // Set the large icon, for example a picture of the other recipient of the message
            //.setLargeIcon(personBitmap)

            .setContentText(message)
            .setWhen(timestamp)
            .setContentTitle(participant)
            .setContentIntent(readPendingIntent)
            .extend(
                NotificationCompat.CarExtender()
                    .setUnreadConversation(unreadConversationBuilder.build())
            )

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val action = NotificationCompat.Action.Builder(
                R.drawable.message,
                "Reply by voice",
                replyIntent
            ).build()
            builder.addAction(action)
            return
        }
        mNotificationManager.notify(conversationId, builder.build())
    }

    /**
     * Handler of incoming messages from clients.
     */
    @SuppressLint("HandlerLeak")
    internal inner class IncomingHandler : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            sendNotification(1, "This is a sample message", "John Doe", System.currentTimeMillis())
        }
    }
}
