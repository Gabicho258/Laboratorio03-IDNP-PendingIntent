package com.example.batteryshowinfo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.batteryshowinfo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), BatteryListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var batteryReceiver: BatteryReceiver
    private val STATE_APP = "StateAppMainActivity"

    // Variables interfaz
    private lateinit var currentStateBattery: TextView
    private lateinit var energySourceBattery: TextView
    private lateinit var percentageBattery: TextView
    private lateinit var batteryImage: ImageView
    private lateinit var generalStateBattery: TextView
    private lateinit var temperatureBattery: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // animation
        val mainLayout = binding.mainLayout
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_and_translate)
        mainLayout.startAnimation(animation)

        // Se establece conexion con los componentes de la interfaz
        currentStateBattery = binding.currentStateBattery
        energySourceBattery = binding.energySourceBattery
        percentageBattery = binding.percentageBattery
        batteryImage = binding.batteryImage
        generalStateBattery = binding.generalStateBattery
        temperatureBattery = binding.temperatureBattery

        intent?.let {
            if (it.hasExtra("isCharging")) {
                val isCharging = it.getBooleanExtra("isCharging", false)
                val energySource = it.getStringExtra("energySourceBattery") ?: "Unknown"
                val batteryPct = it.getFloatExtra("batteryPct", 0f)
                val batteryState = it.getStringExtra("batteryState") ?: "Unknown"
                val batteryTemperature = it.getIntExtra("batteryTemperature", 0)

                onBatteryInfoReceived(
                    isCharging,
                    energySource,
                    batteryPct,
                    batteryState,
                    batteryTemperature
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(STATE_APP, "Start")

        // Configurar el PendingIntent para el AlarmManager
        val intent = Intent(this, BatteryReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast( // uso getBroadcast
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Programar el AlarmManager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            60 * 1000, // equivale a 1 minuto
            pendingIntent
        )
    }

    override fun onStop() {
        super.onStop()
        Log.d(STATE_APP, "Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(STATE_APP, "Destroy")

    }

    override fun onBatteryInfoReceived(
        isCharging: Boolean,
        energySourceBattery: String,
        batteryPct: Float,
        batteryState: String,
        batteryTemperature: Int
    ) {
        // Mostrar datos en interfaz
        currentStateBattery.text =
            when {
                isCharging -> "El dispositivo está cargando"
                else -> "El dispositivo no está cargando"
            }
        batteryImage.setImageResource(
            when {
                batteryPct.toInt() == 0 -> R.drawable.baseline_battery_0_bar_24
                batteryPct.toInt() in 1..20 -> R.drawable.baseline_battery_2_bar_24
                batteryPct.toInt() in 21 .. 40 -> R.drawable.baseline_battery_3_bar_24
                batteryPct.toInt() in 41 .. 60 -> R.drawable.baseline_battery_4_bar_24
                batteryPct.toInt() in 61 .. 80 -> R.drawable.baseline_battery_5_bar_24
                batteryPct.toInt() in 81 .. 99 -> R.drawable.baseline_battery_6_bar_24
                else -> R.drawable.baseline_battery_full_24
            }
        )
        this.energySourceBattery.text = energySourceBattery
        percentageBattery.text = "${batteryPct.toInt()}%"
        generalStateBattery.text = batteryState
        temperatureBattery.text = "$batteryTemperature °C"

    }
}