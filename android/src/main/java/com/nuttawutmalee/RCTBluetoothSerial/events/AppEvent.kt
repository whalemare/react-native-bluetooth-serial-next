package com.nuttawutmalee.RCTBluetoothSerial.events

import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray

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

    abstract fun getParams(): Any
}