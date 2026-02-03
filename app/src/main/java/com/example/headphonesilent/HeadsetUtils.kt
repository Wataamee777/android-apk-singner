package com.example.headphonesilent

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager

object HeadsetUtils {
    fun isHeadsetConnected(context: Context): Boolean {
        val audioManager = context.getSystemService(AudioManager::class.java)
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        return devices.any { device ->
            when (device.type) {
                AudioDeviceInfo.TYPE_WIRED_HEADSET,
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                AudioDeviceInfo.TYPE_USB_HEADSET,
                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> true
                else -> false
            }
        }
    }
}
