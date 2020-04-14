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
package com.epam.crowdresitance.bluetooth.advertising

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.BluetoothLeAdvertiser
import com.epam.crowdresitance.bluetooth.BtConfig.Companion.SERVICE_UUID

abstract class BtAdvertising {
    protected lateinit var advertiser : BluetoothLeAdvertiser

    fun init() {
        advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    }

    protected fun buildAdvertiseData(): AdvertiseData {
        val dataBuilder = AdvertiseData.Builder()
        dataBuilder.addServiceUuid(SERVICE_UUID)
        return dataBuilder.build()
    }

    abstract fun startAdvertising()
    abstract fun stopAdvertising()

}