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
package com.epam.crowdresitance.ui


import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.epam.crowdresitance.R
import com.epam.crowdresitance.repo.BtScanResult
import java.util.concurrent.TimeUnit


class ScanResultAdapter internal constructor(
    private val context: Context,
    private val inflater: LayoutInflater
) :
    BaseAdapter() {
    private val btScanResults: ArrayList<BtScanResult> = ArrayList()
    override fun getCount(): Int {
        return btScanResults.size
    }

    override fun getItem(position: Int): Any {
        return btScanResults[position]
    }

    override fun getItemId(position: Int): Long {
        return btScanResults[position].macAddress.hashCode().toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, currentView: View?, parent: ViewGroup?): View? {
        var view: View? = currentView
        if (view == null) {
            view = inflater.inflate(R.layout.listitem_scanresult, null)
        }
        val deviceNameView = view!!.findViewById(R.id.device_name) as TextView
        val deviceAddressView = view.findViewById(R.id.device_address) as TextView
        val lastSeenView = view.findViewById(R.id.last_seen) as TextView
        val scanResult: BtScanResult = btScanResults[position]
        var name: String? = scanResult.userId
        if (name.isNullOrBlank()) {
            name = context.resources.getString(R.string.no_name)
        }
        deviceNameView.text = name
        deviceAddressView.text = scanResult.macAddress
        lastSeenView.text = getTimeSinceString(
            context,
            scanResult.lastSeen
        )
        return view
    }

    private fun getPosition(address: String): Int {
        var position = -1
        for (i in 0 until btScanResults.size) {
            if (btScanResults[i].macAddress == address) {
                position = i
                break
            }
        }
        return position
    }

    fun add(scanResult: BtScanResult) {
        val existingPosition = getPosition(scanResult.macAddress)
        if (existingPosition >= 0) {
            if (btScanResults[existingPosition].userId.isBlank()) {
                btScanResults[existingPosition] = scanResult
            } else {
                scanResult.userId = btScanResults[existingPosition].userId
                btScanResults[existingPosition] = scanResult
            }
        } else {
            btScanResults.add(scanResult)
        }
    }

    fun clear() {
        btScanResults.clear()
    }

    companion object {
        fun getTimeSinceString(context: Context, timeNanoseconds: Long): String {
            var lastSeenText: String =
                context.resources.getString(R.string.last_seen) + " "
            val timeSince =
                SystemClock.elapsedRealtimeNanos() - timeNanoseconds
            val secondsSince: Long =
                TimeUnit.SECONDS.convert(timeSince, TimeUnit.NANOSECONDS)
            if (secondsSince < 5) {
                lastSeenText += context.getResources().getString(R.string.just_now)
            } else if (secondsSince < 60) {
                lastSeenText += "$secondsSince " + context.getResources()
                    .getString(R.string.seconds_ago)
            } else {
                val minutesSince: Long =
                    TimeUnit.MINUTES.convert(secondsSince, TimeUnit.SECONDS)
                lastSeenText += if (minutesSince < 60) {
                    if (minutesSince == 1L) {
                        "$minutesSince " + context.getResources()
                            .getString(R.string.minute_ago)
                    } else {
                        "$minutesSince " + context.getResources()
                            .getString(R.string.minutes_ago)
                    }
                } else {
                    val hoursSince: Long =
                        TimeUnit.HOURS.convert(minutesSince, TimeUnit.MINUTES)
                    if (hoursSince == 1L) {
                        "$hoursSince " + context.getResources()
                            .getString(R.string.hour_ago)
                    } else {
                        "$hoursSince " + context.getResources()
                            .getString(R.string.hours_ago)
                    }
                }
            }
            return lastSeenText
        }
    }

}