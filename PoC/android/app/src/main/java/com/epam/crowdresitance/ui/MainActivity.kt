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

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.epam.crowdresitance.R
import com.epam.crowdresitance.bluetooth.BtConfig

class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_LOCATION = 22
        const val REQUEST_ENABLE_BT: Int = 21
    }

    private val permissions = arrayOf(ACCESS_FINE_LOCATION)

    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BtConfig.generateUserIdData(baseContext)
        if (savedInstanceState == null) {
            initAppAndPermissions()
        }
    }

    private fun initAppAndPermissions() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled) {
                if (bluetoothAdapter.isMultipleAdvertisementSupported) {
                    if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        setup()
                    } else {
                        requestLocationPermission()
                    }
                }
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        } else {
            Log.e("BT", "Adapter is null")
        }
    }

    private fun setup() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val scannerFragment = ScannerFragment()

        scannerFragment.setBluetoothAdapter(bluetoothAdapter)
        transaction.replace(R.id.scanner_fragment_container, scannerFragment)

        val advertiserFragment = AdvertiserFragment()
        transaction.replace(R.id.advertiser_fragment_container, advertiserFragment)

        transaction.commit()

    }

    private fun requestLocationPermission() {
        requestPermissions(permissions, PERMISSION_REQUEST_LOCATION)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
                        setup()
                    }
                } else {
                    finish()
                }
                super.onActivityResult(requestCode, resultCode, data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setup()
            } else {
                Log.e("BT", "Location permission needed for bluetooth")
            }
        }
    }
}