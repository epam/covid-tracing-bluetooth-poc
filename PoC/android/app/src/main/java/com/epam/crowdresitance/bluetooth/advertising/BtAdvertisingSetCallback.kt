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

import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class BtAdvertisingSetCallback(private var currentAdvertisingSet: AdvertisingSet?) : AdvertisingSetCallback() {
    private val LOG_TAG = "BT"+this.javaClass.name

    override fun onAdvertisingSetStarted(
        advertisingSet: AdvertisingSet?,
        txPower: Int,
        status: Int
    ) {
        super.onAdvertisingSetStarted(advertisingSet, txPower, status)
        Log.i(LOG_TAG, "onAdvertisingSetStarted(): txPower: $txPower status: $status")
        currentAdvertisingSet = advertisingSet
        Log.d(LOG_TAG, "DATA ${advertisingSet.toString()}")
    }

}