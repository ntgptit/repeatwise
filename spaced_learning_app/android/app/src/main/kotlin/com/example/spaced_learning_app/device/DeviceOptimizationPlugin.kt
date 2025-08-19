package com.example.spaced_learning_app.device

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class DeviceOptimizationPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private var activity: android.app.Activity? = null
    private val TAG = "DeviceOptimizationPlugin"

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.example.spaced_learning_app.device/optimization")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "requestExactAlarmPermission" -> result.success(requestExactAlarmPermission())
            "disableSleepingApps" -> result.success(disableSleepingApps())
            "requestBatteryOptimization" -> result.success(requestBatteryOptimization())
            "getDeviceInfo" -> result.success(getDeviceInfo())
            "hasExactAlarmPermission" -> result.success(hasExactAlarmPermission())
            "isIgnoringBatteryOptimizations" -> result.success(isIgnoringBatteryOptimizations())
            else -> result.notImplemented()
        }
    }

    private fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.canScheduleExactAlarms()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking exact alarm permission: ${e.message}")
                false
            }
        } else {
            true // Permissions not required before Android S (API 31)
        }
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val packageName = context.packageName
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                pm.isIgnoringBatteryOptimizations(packageName)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking battery optimization status: ${e.message}")
                false
            }
        } else {
            true // Not applicable before Android M (API 23)
        }
    }

    private fun requestExactAlarmPermission(): Boolean {
        if (activity == null) {
            Log.e(TAG, "Activity is null, cannot request permission")
            return false
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    activity?.startActivity(intent)
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting exact alarm permission: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun disableSleepingApps(): Boolean {
        if (activity == null) {
            Log.e(TAG, "Activity is null, cannot open settings")
            return false
        }

        return try {
            val manufacturer = Build.MANUFACTURER.lowercase()
            val intent = when {
                manufacturer.contains("samsung") -> Intent().apply {
                    component = android.content.ComponentName(
                        "com.samsung.android.lool",
                        "com.samsung.android.sm.ui.battery.BatteryActivity"
                    )
                }
                manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> Intent().apply {
                    component = android.content.ComponentName(
                        "com.miui.powerkeeper",
                        "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
                    )
                }
                manufacturer.contains("huawei") -> Intent().apply {
                    component = android.content.ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                    )
                }
                manufacturer.contains("oppo") -> Intent().apply {
                    component = android.content.ComponentName(
                        "com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
                    )
                }
                manufacturer.contains("vivo") -> Intent().apply {
                    component = android.content.ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }
                else -> Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            }.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity?.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to general battery settings if specific manufacturer settings failed
            try {
                val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                activity?.startActivity(intent)
                true
            } catch (e2: Exception) {
                Log.e(TAG, "Error disabling sleeping apps: ${e2.message}")
                e2.printStackTrace()
                false
            }
        }
    }

    private fun requestBatteryOptimization(): Boolean {
        if (activity == null) {
            Log.e(TAG, "Activity is null, cannot request battery optimization")
            return false
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = context.packageName
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:$packageName")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    activity?.startActivity(intent)
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting battery optimization: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun getDeviceInfo(): Map<String, Any> {
        val info = HashMap<String, Any>()
        info["manufacturer"] = Build.MANUFACTURER ?: "Unknown"
        info["model"] = Build.MODEL ?: "Unknown"
        info["sdkVersion"] = Build.VERSION.SDK_INT
        info["brand"] = Build.BRAND ?: "Unknown"
        info["device"] = Build.DEVICE ?: "Unknown"

        try {
            info["hasVibrator"] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.hasVibrator() ?: false
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.hasVibrator() ?: false
            }
        } catch (e: Exception) {
            info["hasVibrator"] = false
            Log.e(TAG, "Error checking vibrator: ${e.message}")
        }

        info["hasExactAlarmPermission"] = hasExactAlarmPermission()
        info["isIgnoringBatteryOptimizations"] = isIgnoringBatteryOptimizations()

        return info
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}
