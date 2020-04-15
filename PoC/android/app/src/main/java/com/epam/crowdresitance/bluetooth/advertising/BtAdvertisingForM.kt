// =========================================================================
// Copyright 2020 EPAM Systems, Inc.
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

import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings


class BtAdvertisingForM : BtAdvertising() {

    private lateinit var callback: BtAdvertiseCallback

    override fun startAdvertising() {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
            .setConnectable(true)
            .build()
        val data: AdvertiseData = buildAdvertiseData()
        callback = BtAdvertiseCallback()
        advertiser.startAdvertising(settings, data, callback )
    }

    override fun stopAdvertising() {
        advertiser.stopAdvertising(callback)
    }

}