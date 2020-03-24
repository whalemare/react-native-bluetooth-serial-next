package com.nuttawutmalee.RCTBluetoothSerial.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.nuttawutmalee.RCTBluetoothSerial.Address
import com.nuttawutmalee.RCTBluetoothSerial.RCTBluetoothSerialModule

/**
 * @since 2020
 * @author Anton Vlasov - whalemare
 */
sealed class AppEvent {
    class read(private val writableArray: WritableArray): AppEvent() {

        override fun getParams(): WritableArray {
            return writableArray
        }
    }

    class connectionLost(private val address: Address): AppEvent() {
        override fun getParams(): ReadableMap {
            val params = Arguments.createMap()
            params.putString("address", address)
            return params
        }
    }

    class error(private val errorCode: Int): AppEvent() {
        override fun getParams(): Int {
            return errorCode
        }

    }

    abstract fun getParams(): Any
}