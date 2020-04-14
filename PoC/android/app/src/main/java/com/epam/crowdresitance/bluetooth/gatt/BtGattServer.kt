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
package com.epam.crowdresitance.bluetooth.gatt

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context
import com.epam.crowdresitance.bluetooth.BtConfig.Companion.CHARACTERISTIC_UUID
import com.epam.crowdresitance.bluetooth.BtConfig.Companion.SERVICE_UUID


class BtGattServer {
    private lateinit var gattServer: BluetoothGattServer

    fun init(context: Context) {
        val bluetoothGattServerCallback =
            BtGattServerCallback()

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        gattServer =
            bluetoothManager.openGattServer(context, bluetoothGattServerCallback)
        bluetoothGattServerCallback.gattServer = gattServer
        startGattService()
    }

    private fun startGattService(){
        val service =
            BluetoothGattService(SERVICE_UUID.uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val characteristic = BluetoothGattCharacteristic(
            CHARACTERISTIC_UUID.uuid,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        service.addCharacteristic(characteristic)
        gattServer.addService(service)
    }

    fun close() {
        gattServer.close()
    }
}