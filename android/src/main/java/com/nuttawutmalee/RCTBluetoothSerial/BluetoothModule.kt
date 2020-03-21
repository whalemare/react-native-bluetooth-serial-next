package com.nuttawutmalee.RCTBluetoothSerial

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.*
import me.aflak.bluetooth.Bluetooth
import me.aflak.bluetooth.interfaces.BluetoothCallback
import me.aflak.bluetooth.interfaces.DeviceCallback
import java.util.*
import kotlin.collections.HashMap

/**
 * @since 2020
 * @author Anton Vlasov - whalemare
 */
class BluetoothModule(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {
    override fun getName(): String {
        return "RCTBluetoothSerial"
    }

    private val bluetooth = Bluetooth(context)

    private val connectedDevices = HashMap<Address, BluetoothDevice>()
    private val connectionCallbacks = HashMap<Address, Promise>()

    private val readers = HashMap<Address, Promise>()

    init {
        bluetooth.setBluetoothCallback(object : BluetoothCallback {
            override fun onBluetoothTurningOn() {
                Log.d("Bluetooth", "onBluetoothTurningOn")
            }

            override fun onBluetoothOn() {
                Log.d("Bluetooth", "onBluetoothOn")
            }

            override fun onBluetoothTurningOff() {
                Log.d("Bluetooth", "onBluetoothTurningOff")
            }

            override fun onBluetoothOff() {
                Log.d("Bluetooth", "onBluetoothOff")
            }

            override fun onUserDeniedActivation() {
                Log.d("Bluetooth", "onUserDeniedActivation")
            }
        })
        bluetooth.setReader(LampReader::class.java)
        bluetooth.setDeviceCallback(object : DeviceCallback {
            override fun onDeviceConnected(device: BluetoothDevice) {
                connectionCallbacks[device.address]?.let {
                    it.resolve(device.toWritableMap())
                    connectedDevices[device.address] = device
                    connectionCallbacks.remove(device.address)
                }
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, message: String) {
                Log.d("Bluetooth", "onDeviceDisconnected")
                connectedDevices.remove(device.address)
            }

            override fun onMessage(message: ByteArray) {
                Log.d("Bluetooth", "onMessage bytes = ${message}; content = ${String(message)}")
                readers.forEach { (address, promise) ->
                    val array: WritableArray = WritableNativeArray()
                    for (i in message.indices) {
                        array.pushInt(message[i].toInt())
                    }
                    Log.d("Bluetooth", "onMessage resolve = ${array}")
                    promise.resolve(array)
                }
            }

            override fun onError(errorCode: Int) {
                Log.d("Bluetooth", "onError $errorCode")
            }

            override fun onConnectError(device: BluetoothDevice, message: String) { //                onConnectError(device, message);
                Log.d("Bluetooth", "onConnectError")
                connectionCallbacks[device.address]?.let {
                    it.reject(message, UnknownError(message))
                    connectionCallbacks.remove(device.address)
                }
            }
        })
        bluetooth.onStart()
//        bluetooth.setCallbackOnUI(reactApplicationContext.currentActivity)
    }

    @ReactMethod
    fun requestEnable(promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun enable(promise: Promise) {
        bluetooth.enable()
        promise.resolve(true)
    }

    @ReactMethod
    fun disable(promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun isEnabled(promise: Promise) {
        promise.resolve(bluetooth.isEnabled)
    }

    @ReactMethod
    fun list(promise: Promise) {
        val collection = Arguments.createArray()
        bluetooth.pairedDevices.map { it.toWritableMap() }.forEach { collection.pushMap(it) }
        promise.resolve(collection)
    }

    @ReactMethod
    fun listUnpaired(promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun cancelDiscovery(promise: Promise) {
        bluetooth.removeDiscoveryCallback()
    }

    @ReactMethod
    fun pairDevice(id: String, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun unpairDevice(id: String, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun connect(id: String?, promise: Promise?) {
        connectionCallbacks[id!!] = promise!!
        bluetooth.connectToAddress(id)
    }

    @ReactMethod
    fun disconnect(id: String?, promise: Promise) {
//        var id = id
//        if (id == null) {
//            id = mBluetoothService.getFirstDeviceAddress()
//        }
//        if (RCTBluetoothSerialModule.D) Log.d(RCTBluetoothSerialPackage.TAG, "Disconnect from device id $id")
//        if (id != null) {
//            mBluetoothService.stop(id)
//        }
//        promise.resolve(true)
    }

    @ReactMethod
    fun disconnectAll(promise: Promise) {
//        mBluetoothService.stopAll()
//        promise.resolve(true)
    }

    @ReactMethod
    fun isConnected(id: String?, promise: Promise) {
        if (connectedDevices[id] == null) {
            promise.resolve(false)
        } else {
            promise.resolve(true)
        }
    }

    @ReactMethod
    fun writeToDevice(message: String, id: String?, promise: Promise) {
        val bytes = Base64.decode(message, Base64.DEFAULT)
        Log.d("Bluetooth", "writeToDevice base64: $message, bytes: ${Arrays.toString(bytes)}")

        val device = connectedDevices[id]
        if (device != null) {
            bluetooth.send(bytes)
            promise.resolve(true)
        } else {
            promise.reject("Device not connected", IllegalArgumentException())
        }
    }

    @ReactMethod
    fun readFromDevice(id: String?, promise: Promise) {
        Log.d("Bluetooth", "readFromDevice ${id}")
        readers[id!!] = promise
    }

    @ReactMethod
    fun readUntilDelimiter(delimiter: String?, id: String?, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun withDelimiter(delimiter: String, id: String?, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun clear(id: String?, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun available(id: String?, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())

    }

    @ReactMethod
    fun setAdapterName(newName: String?, promise: Promise) {
        promise.reject("Not implemented", NotImplementedError())
    }

    @ReactMethod
    fun setServices(services: ReadableArray?, includeDefaultServices: Boolean?, promise: Promise) {
        val updated = Arguments.createArray()
        promise.resolve(updated)
    }

    @ReactMethod
    fun getServices(promise: Promise) {
        val services = Arguments.createArray()
        promise.resolve(services)
    }

    @ReactMethod
    fun restoreServices(promise: Promise) {
        val services = Arguments.createArray()
        promise.resolve(services)
    }

    // @Override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bluetooth.onActivityResult(requestCode, resultCode)
    }

    // @Override
//    fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
//        if (RCTBluetoothSerialModule.D) Log.d(RCTBluetoothSerialPackage.TAG, "On activity result request: $requestCode, result: $resultCode")
//        if (requestCode == RCTBluetoothSerialModule.REQUEST_ENABLE_BLUETOOTH) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (RCTBluetoothSerialModule.D) Log.d(RCTBluetoothSerialPackage.TAG, "User enabled Bluetooth")
//                if (mEnabledPromise != null) {
//                    mEnabledPromise.resolve(true)
//                    mEnabledPromise = null
//                }
//            } else {
//                if (RCTBluetoothSerialModule.D) Log.d(RCTBluetoothSerialPackage.TAG, "User did not enable Bluetooth")
//                if (mEnabledPromise != null) {
//                    mEnabledPromise.reject(Exception("User did not enable Bluetooth"))
//                    mEnabledPromise = null
//                }
//            }
//        }
//        if (requestCode == RCTBluetoothSerialModule.REQUEST_PAIR_DEVICE) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (RCTBluetoothSerialModule.D) Log.d(RCTBluetoothSerialPackage.TAG, "Pairing ok")
//            } else {
//                if (RCTBluetoothSerialModule.D) Log.d(RCTBluetoothSerialPackage.TAG, "Pairing failed")
//            }
//        }
//    }
}