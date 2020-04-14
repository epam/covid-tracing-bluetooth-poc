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
package com.epam.crowdresitance.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.epam.crowdresitance.R
import com.epam.crowdresitance.bluetooth.BtConfig
import com.epam.crowdresitance.service.BtService


class AdvertiserFragment : Fragment(), View.OnClickListener {
    private var switch: Switch? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_advertiser, container, false)
        switch = view.findViewById(R.id.advertise_switch)
        switch!!.setOnClickListener(this)
        val myId: TextView = view.findViewById(R.id.myId)
        myId.text = getString(R.string.my_id, BtConfig.userIdData().toString(Charsets.UTF_8))
        return view
    }

    override fun onResume() {
        super.onResume()
        switch!!.isChecked = BtService.running

    }

    override fun onClick(v: View) {
        val on = (v as Switch).isChecked
        if (on) {
            startAdvertising()
        } else {
            stopAdvertising()
        }
    }

    private fun startAdvertising() {
        val context = activity
        if (context != null) {
            BtService.startService(context)
        }
    }

    private fun stopAdvertising() {
        val context = activity
        context?.stopService(getServiceIntent(context))
        switch!!.isChecked = false
    }

    companion object {
        private fun getServiceIntent(c: Context): Intent {
            return Intent(c, BtService::class.java)
        }
    }
}