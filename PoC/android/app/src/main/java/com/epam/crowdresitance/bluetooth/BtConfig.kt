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
package com.epam.crowdresitance.bluetooth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.provider.Settings
import java.util.*
import kotlin.math.absoluteValue

/**
 * This companion object stores uuids for service and characteristic
 * additionally it generates identifier of the user
 * TODO change for your own implementation
 */
class BtConfig {
    companion object {
        val SERVICE_UUID: ParcelUuid = ParcelUuid
            .fromString("0000b81d-0000-1000-8000-698065770001")
        val CHARACTERISTIC_UUID: ParcelUuid = ParcelUuid
            .fromString("0000b81d-0000-1000-8000-698065770002")
        private var userId: ByteArray = ByteArray(0)

        @SuppressLint("HardwareIds")
        @Suppress("DEPRECATION")
        fun generateUserIdData(context: Context) {
            val devIDShort = ("35" +
                    Build.BOARD.length % 10
                    + Build.BRAND.length % 10
                    + Build.CPU_ABI.length % 10
                    + Build.DEVICE.length % 10
                    + Build.MANUFACTURER.length % 10
                    + Build.MODEL.length % 10
                    + Build.PRODUCT.length % 10)

            val androidId =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID)

            val uuid = UUID(devIDShort.hashCode().toLong(), androidId.hashCode().toLong())
            userId =
                uuid.leastSignificantBits.xor(uuid.mostSignificantBits).absoluteValue.toString()
                    .toByteArray()
        }

        fun userIdData() = userId

    }
}