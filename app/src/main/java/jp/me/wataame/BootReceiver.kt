package jp.me.wataame

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action ||
            Intent.ACTION_MY_PACKAGE_REPLACED == intent.action
        ) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, HeadsetMonitorService::class.java),
            )
        }
    }
}
