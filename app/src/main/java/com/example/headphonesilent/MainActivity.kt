package com.example.headphonesilent

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.statusText)
        val startButton = findViewById<Button>(R.id.startButton)
        val dndButton = findViewById<Button>(R.id.dndButton)

        updateStatus(statusText)

        startButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermissionIfNeeded()
            }
            ContextCompat.startForegroundService(
                this,
                Intent(this, HeadsetMonitorService::class.java),
            )
            updateStatus(statusText)
        }

        dndButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus(findViewById(R.id.statusText))
    }

    private fun updateStatus(statusText: TextView) {
        val connected = HeadsetUtils.isHeadsetConnected(this)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            statusText.setText(R.string.status_needs_dnd_access)
            return
        }
        val label = if (connected) {
            R.string.status_connected
        } else {
            R.string.status_disconnected
        }
        statusText.setText(label)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001,
            )
        }
    }
}
