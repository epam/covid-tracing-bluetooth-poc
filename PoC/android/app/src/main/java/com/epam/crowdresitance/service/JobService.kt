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

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService

@RequiresApi(Build.VERSION_CODES.O)
class JobService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        BtService.startService(baseContext)
    }

    companion object {
        private const val JOB_ID = 0x01
        fun enqueueWork(context: Context?, work: Intent?) {
            enqueueWork(context!!, JobService::class.java, JOB_ID, work!!)
        }
    }
}