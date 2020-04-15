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

import UIKit
import UserNotifications

/*
 The start point of the application that configures all services and logs all lifecycle events
 */

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    private var peripheralService: PeripheralService!
    private var centralService: CentralService!

    // MARK: - UIApplicationDelegate

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        Logger.shared.logMessage("\nA did finish launching\n")

        UIDevice.current.isBatteryMonitoringEnabled = true

        peripheralService = PeripheralService()
        centralService = CentralService()
        
        NotificationService.shared.authorize()

        return true
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        Logger.shared.logMessage("A did become active\n")
    }

    func applicationWillResignActive(_ application: UIApplication) {
        Logger.shared.logMessage("A will resign active\n")
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        Logger.shared.logMessage("A did enter background\n")
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        Logger.shared.logMessage("A will enter foreground\n")
    }

    func applicationWillTerminate(_ application: UIApplication) {
        Logger.shared.logMessage("A will terminate\n")
    }
}
