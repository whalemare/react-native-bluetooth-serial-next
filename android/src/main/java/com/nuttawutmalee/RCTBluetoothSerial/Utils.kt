package com.nuttawutmalee.RCTBluetoothSerial

import android.bluetooth.BluetoothDevice
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

/**
 * @since 2020
 * @author Anton Vlasov - whalemare
 */

typealias Address = String

/**
 * Convert BluetoothDevice into WritableMap
 *
 * @param device Bluetooth device
 */
fun BluetoothDevice.toWritableMap(): WritableMap {
    val params = Arguments.createMap()
    params.putString("name", this.name)
    params.putString("address", this.address)
    params.putString("id", this.address)
    if (this.bluetoothClass != null) {
        params.putInt("class", this.bluetoothClass.deviceClass)
    }
    return params
}