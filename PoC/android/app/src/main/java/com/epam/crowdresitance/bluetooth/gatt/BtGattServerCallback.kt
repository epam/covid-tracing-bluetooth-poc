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

import android.bluetooth.*
import android.util.Log
import com.epam.crowdresitance.bluetooth.BtConfig.Companion.userIdData


class BtGattServerCallback() : BluetoothGattServerCallback() {
    var gattServer: BluetoothGattServer? = null

    private val TAG: String = BtGattServerCallback::class.java.simpleName

    override fun onConnectionStateChange(
        device: BluetoothDevice?,
        status: Int,
        newState: Int
    ) {
        super.onConnectionStateChange(device, status, newState)
        Log.d(TAG, status.toString())
    }

    override fun onCharacteristicReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
        val response = userIdData()
        gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, response)
    }
}