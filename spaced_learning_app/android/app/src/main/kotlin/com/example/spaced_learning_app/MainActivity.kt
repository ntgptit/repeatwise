package com.example.spaced_learning_app

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import com.example.spaced_learning_app.device.DeviceOptimizationPlugin
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log

class MainActivity : FlutterActivity() {
    private val TAG = "MainActivity"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        try {
            flutterEngine.plugins.add(DeviceOptimizationPlugin())
            Log.i(TAG, "DeviceOptimizationPlugin registered successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error registering DeviceOptimizationPlugin: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request critical permissions when app starts
        checkCriticalPermissions()
    }

    private fun checkCriticalPermissions() {
        try {
            // Check battery optimization for apps that need reliable alarms/notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    Log.i(TAG, "App is not ignoring battery optimizations")
                    // Consider showing a prompt to the user first instead of directly opening settings
                    // Intent().also { intent ->
                    //     intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    //     intent.data = android.net.Uri.parse("package:$packageName")
                    //     startActivity(intent)
                    // }
                }
            }

            // Check exact alarm permission for Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.i(TAG, "App cannot schedule exact alarms")
                    // Consider showing a prompt to the user first instead of directly opening settings
                    // Intent().also { intent ->
                    //     intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    //     intent.data = android.net.Uri.parse("package:$packageName")
                    //     startActivity(intent)
                    // }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking permissions: ${e.message}")
        }
    }
}
