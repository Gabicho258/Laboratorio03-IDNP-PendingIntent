package com.example.batteryshowinfo

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// PendingIntent inicializa el broadcast sin paramenros
class BatteryReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Obtener informacíon de la batería
        val currentState: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = currentState == BatteryManager.BATTERY_STATUS_CHARGING || currentState == BatteryManager.BATTERY_STATUS_FULL

        // Ver el tipo de fuente de carga
        val chargePlug: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        // Selecciona el tipo de fuente de carga
        val energySourceBattery = when {
            // las variables actuan como booleano, la que sea true retornará el string señalado
            usbCharge -> "USB"
            acCharge -> "AC"
            else -> "No charging"
        }
        // Se obtiene el % de la batería
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct: Float = level * 100 / scale.toFloat()

        // Obtener información del estado de la batería
        val batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val batteryState = when (batteryHealth) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "En buen estado"
            BatteryManager.BATTERY_HEALTH_COLD -> "Fría"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Muerta"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Sobrecalentada"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Sobre voltaje"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Fallo no especificado"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Desconocido"
            else -> "Desconocido"
        }

        // Obtener temperatura - Se retorna en centesimas de grados por ello se divide sobre 10
        val batteryTemperature: Int = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10

        // Se crea un Intent para enviar los datos a MainActivity
        val dataIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("isCharging", isCharging)
            putExtra("energySourceBattery", energySourceBattery)
            putExtra("batteryPct", batteryPct)
            putExtra("batteryState", batteryState)
            putExtra("batteryTemperature", batteryTemperature)
        }

        // Se crea el PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            dataIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Enviar el PendingIntent
        try {
            pendingIntent.send() // Esto activa la MainActivity con el intent
        } catch (e: PendingIntent.CanceledException) {
            Log.e("BatteryReceiver", "PendingIntent was canceled.", e)
        }

        Log.d("BatteryReceiver", "Datos de la batería enviados")

    }
}