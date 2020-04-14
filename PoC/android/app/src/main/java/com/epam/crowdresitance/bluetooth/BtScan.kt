// =========================================================================
// Copyright 2019 EPAM Systems, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// =========================================================================
package com.epam.crowdresitance.bluetooth

import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Handler
import android.util.Log
import com.epam.crowdresitance.bluetooth.BtConfig.Companion.CHARACTERISTIC_UUID
import com.epam.crowdresitance.bluetooth.BtConfig.Companion.SERVICE_UUID
import com.epam.crowdresitance.repo.BtDiscoveredDevices
import com.epam.crowdresitance.repo.BtScanResult

class BtScan {
    private val TAG: String = BtScan::class.java.simpleName
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null
    val macAdresses: MutableSet<String> = mutableSetOf()
    lateinit var context: Context
    var issuedMac: String? = null
    val handler = Handler()

    fun init(context: Context) {
        this.context = context
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    fun startScanning() {
        if (scanCallback == null) {
            Log.d(TAG, "Starting Scanning")
            scanCallback = SampleScanCallback()
            bluetoothLeScanner!!.startScan(buildScanFilters(), buildScanSettings(), scanCallback)
        }
    }

    fun stopScanning() {
        Log.d(TAG, "Stopping Scanning")
        bluetoothLeScanner!!.stopScan(scanCallback)
        scanCallback = null
    }

    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilters: MutableList<ScanFilter> = ArrayList()
        val builder = ScanFilter.Builder()
        builder.setServiceUuid(SERVICE_UUID)
        scanFilters.add(builder.build())
        return scanFilters
    }

    private fun buildScanSettings(): ScanSettings {
        val builder = ScanSettings.Builder()
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        return builder.build()
    }

    private inner class SampleScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult?>) {
            super.onBatchScanResults(results)
            for (result in results) {
                if (result != null) {
                    processResult(result)
                }
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                processResult(result)
            }
        }

        private fun processResult(result: ScanResult?) {
            if (result != null) {
                val btDevice = result.toBtScanResult()
                BtDiscoveredDevices.add(btDevice)
                if (!macAdresses.contains(btDevice.macAddress)) {
                    if (btDevice.macAddress == issuedMac) {
                        val delayMillis: Long = 4000
                        handler.postDelayed(
                            {
                                result.device.connectGatt(
                                    context,
                                    false,
                                    BtGattConnectCallback(btDevice),
                                    BluetoothDevice.TRANSPORT_LE
                                )
                            }
                            , delayMillis)

                    } else {
                        result.device.connectGatt(
                            context,
                            false,
                            BtGattConnectCallback(btDevice)
                        )
                    }
                }

            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan failed with error: $errorCode")
        }
    }

    private inner class BtGattConnectCallback(val btDevice: BtScanResult) :
        BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothAdapter.STATE_CONNECTED) {
                gatt?.discoverServices()
            }
            val errorCode133 = 133
            if (newState == errorCode133) {
                gatt?.close()
                issuedMac = btDevice.macAddress
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            val service = gatt?.getService(SERVICE_UUID.uuid)
            val characteristic =
                service?.getCharacteristic(CHARACTERISTIC_UUID.uuid)
            gatt?.readCharacteristic(characteristic)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            val charString = characteristic?.value?.toString(Charsets.UTF_8)
            if (charString != null) {
                macAdresses.add(btDevice.macAddress)
                btDevice.userId = charString
                BtDiscoveredDevices.add(btDevice)
                if (btDevice.macAddress != issuedMac) {
                    gatt?.close()
                }
            }
        }

    }
}

private fun ScanResult.toBtScanResult(): BtScanResult {
    return BtScanResult(device.address, timestampNanos, "")
}
