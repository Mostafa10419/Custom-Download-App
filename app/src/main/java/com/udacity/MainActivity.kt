package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

// Notification ID.
private const val NOTIFICATION_ID = 0

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(
            CHANNEL_ID,
            getString(R.string.notification_title)
        )

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager


        custom_button.setOnClickListener {
            when (download.checkedRadioButtonId) {
                download1.id -> {
                    URL = "https://github.com/bumptech/glide"
                    download()
                    notificationManager.sendNotification(
                        getString(R.string.glide),
                        this
                    )
                }
                download2.id -> {
                    URL =
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                    download()
                    notificationManager.sendNotification(
                        getString(R.string.project),
                        this
                    )
                }
                download3.id -> {
                    URL = "https://github.com/square/retrofit"
                    download()
                    notificationManager.sendNotification(
                        getString(R.string.retrofit),
                        this
                    )
                }
                else -> Toast.makeText(this, R.string.select, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }

    private fun NotificationManager.sendNotification(
        fileName: String,
        applicationContext: Context
    ) {
        // Create the content intent for the notification, which launches
        // this activity
        // TODO: Step 1.11 create intent
        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtra("fileName", fileName)
        // TODO: Step 1.12 create PendingIntent
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        action = NotificationCompat.Action(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            pendingIntent
        )

        // TODO: Step 2.0 add style
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(getString(R.string.download))

        // TODO: Step 1.2 get an instance of NotificationCompat.Builder
        // Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )

            // TODO: Step 1.3 set title, text and icon to builder
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(
                applicationContext
                    .getString(R.string.notification_title)
            )
            .setContentText(fileName + " " + getString(R.string.download))

            // TODO: Step 1.13 set content intent
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

            // TODO: Step 2.1 add style to builder
            .setStyle(bigTextStyle)

            .addAction(action)

            // TODO: Step 2.5 set priority
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        // TODO: Step 1.4 call notify
        notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
