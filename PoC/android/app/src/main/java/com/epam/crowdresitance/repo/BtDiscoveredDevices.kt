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
package com.epam.crowdresitance.repo

import android.os.Handler
import android.os.Looper
import com.epam.crowdresitance.ui.ScanResultAdapter

object BtDiscoveredDevices {
    private val scanResults: ArrayList<BtScanResult> = ArrayList()
    private var adapter : ScanResultAdapter? = null

    fun add(result :BtScanResult){
        if(scanResults.find { it -> it.macAddress == result.macAddress } == null){
            scanResults.add(result)
        }
        Handler(Looper.getMainLooper()).post {
            adapter?.add(result)
            adapter?.notifyDataSetChanged()
        }
    }

    fun registerAdapter(adapter : ScanResultAdapter?){
        this.adapter = adapter
    }

    fun unregisterAdapter(){
        this.adapter = null
    }
}