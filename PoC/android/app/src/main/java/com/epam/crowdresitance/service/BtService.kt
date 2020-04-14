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
package com.epam.crowdresitance.service

import android.app.*
import android.app.Notification.Builder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.epam.crowdresitance.R
import com.epam.crowdresitance.bluetooth.BtConfig
import com.epam.crowdresitance.bluetooth.BtScan
import com.epam.crowdresitance.bluetooth.advertising.BtAdvertising
import com.epam.crowdresitance.bluetooth.advertising.BtAdvertisingForM
import com.epam.crowdresitance.bluetooth.advertising.BtAdvertisingForO
import com.epam.crowdresitance.bluetooth.gatt.BtGattServer
import com.epam.crowdresitance.ui.MainActivity


class BtService : Service() {
    companion object {
        var running: Boolean = false

        fun startService(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(getServiceIntent(context))
            } else {
                context.startService(getServiceIntent(context))
            }
        }

        private fun getServiceIntent(context: Context): Intent {
            return Intent(context, BtService::class.java)
        }

    }

    private val TAG: String = BtService::class.java.simpleName
    private val channelId = "ForegroundServiceChannel"
    private val foregroundNotificationId = 332

    private var btAdvertiser: BtAdvertising? = null
    private var btGattServer: BtGattServer? = null
    private var btScan: BtScan? = null

    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "onBind is called - should be started service")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        running = true
        goForeground()
        BtConfig.generateUserIdData(baseContext)
        initialize()
        startBtServices()
        super.onCreate()
    }

    override fun onDestroy() {
        running = false
        stopBtServices()
        stopForeground(true)
        super.onDestroy()
    }

    private fun stopBtServices() {
        btAdvertiser?.stopAdvertising()
        btGattServer?.close()
        btScan?.stopScanning()
    }

    private fun startBtServices() {
        btScan?.startScanning()
        btAdvertiser?.startAdvertising()
    }

    private fun initialize() {
        btAdvertiser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            BtAdvertisingForO()
        } else {
            BtAdvertisingForM()
        }
        btAdvertiser?.init()
        btGattServer = BtGattServer()
        btGattServer?.init(baseContext)
        btScan = BtScan()
        btScan?.init(this.baseContext)
    }

    @Suppress("DEPRECATION")
    private fun goForeground() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Builder(this, channelId)
                .setContentTitle(getString(R.string.service_title))
                .setContentText(getString(R.string.service_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            Builder(this)
                .setContentTitle(getString(R.string.service_title))
                .setContentText(getString(R.string.service_text))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build()
        }
        startForeground(foregroundNotificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Bluetooth Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

}