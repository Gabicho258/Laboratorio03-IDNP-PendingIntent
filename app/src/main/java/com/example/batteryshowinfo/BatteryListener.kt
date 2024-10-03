package com.example.batteryshowinfo

interface BatteryListener {
    fun onBatteryInfoReceived (isCharging: Boolean, energySourceBattery: String, batteryPct: Float,
                               batteryState: String, batteryTemperature: Int)
}