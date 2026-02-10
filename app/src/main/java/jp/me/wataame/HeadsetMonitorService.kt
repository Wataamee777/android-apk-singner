package jp.me.wataame

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class HeadsetMonitorService : Service() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateRingerMode(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(receiver, createIntentFilter())
        startForeground(NOTIFICATION_ID, buildNotification())
        updateRingerMode(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateRingerMode(this)
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createIntentFilter(): IntentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_HEADSET_PLUG)
        addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
        addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }

    private fun buildNotification(): Notification {
        val channelId = "headset_monitor"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.status_title),
                NotificationManager.IMPORTANCE_LOW,
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.status_title))
            .setContentText(getString(R.string.status_unknown))
            .setSmallIcon(android.R.drawable.stat_sys_headset)
            .setOngoing(true)
            .build()
    }

    private fun updateRingerMode(context: Context) {
        val audioManager = context.getSystemService(AudioManager::class.java)
        val connected = HeadsetUtils.isHeadsetConnected(context)
        if (connected) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        } else {
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 100
    }
}
