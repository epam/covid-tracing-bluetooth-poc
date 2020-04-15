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
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetParameters
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class BtAdvertisingForO : BtAdvertising() {

    private lateinit var callback: BtAdvertisingSetCallback

    @RequiresApi(Build.VERSION_CODES.O)
    override fun startAdvertising() {
        val parameters = (AdvertisingSetParameters.Builder())
            .setLegacyMode(true)
            .setConnectable(true)
            .setScannable(true)
            .setInterval(AdvertisingSetParameters.INTERVAL_MEDIUM)
            .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_LOW)
            .build()

        val data: AdvertiseData = buildAdvertiseData()

        val currentAdvertisingSet: AdvertisingSet? = null
        callback =
            BtAdvertisingSetCallback(
                currentAdvertisingSet
            )
        advertiser.startAdvertisingSet(parameters, data, null, null, null, callback)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun stopAdvertising() {
        advertiser.stopAdvertisingSet(callback)
    }
}