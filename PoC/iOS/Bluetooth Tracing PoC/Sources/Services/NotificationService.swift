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

extension Notification.Name {
    static let RestartBluetooth = Notification.Name(rawValue: "RestartBluetoothNotification")
}

/*
 Service that provides authorisation for local notifications and displaying notification messages
 with actions for restarting Bluetooth stack (immediately or by schedule)
 */

class NotificationService: NSObject, UNUserNotificationCenterDelegate {
    private enum Constants {
        static let backgroundRefreshInterval = 3600.0
        static let restartNotificationTimeInterval = 300.0
        static let acitonNotificationHour = 16

        static let categoryRestart = "com.tazetdinov.notification.category.restart"
        static let categoryShowRestart = "com.tazetdinov.notification.category.show-restart"
        static let actionRestart = "com.tazetdinov.notification.action.force-restart"
        static let actionShowRestart = "com.tazetdinov.notification.action.show-restart"
    }

    static let shared = NotificationService()
    
    private override init() {
        super.init()

        UNUserNotificationCenter.current().delegate = self
    }
    
    func authorize() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { _, _ in }
    }

    func show(message: String) {
        setupCategories()

        let content = UNMutableNotificationContent()
        content.categoryIdentifier = Constants.categoryRestart
        content.title = "BT message"
        content.body = message
        content.sound = .default

        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: nil)
        UNUserNotificationCenter.current().add(request)
    }
    
    private func scheduleActionNotification() {
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: Constants.restartNotificationTimeInterval,
                                                        repeats: false)

        showRestartNotification(trigger: trigger)
    }

    private func showRestartNotification(trigger: UNNotificationTrigger) {
        setupCategories()

        let content = UNMutableNotificationContent()
        content.categoryIdentifier = Constants.categoryRestart
        content.title = "Workaround for BT Background"
        content.body = "Let's restart bluetooth stack?"
        content.sound = .default

        let request = UNNotificationRequest(identifier: Constants.categoryRestart, content: content, trigger: trigger)
        UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
    }
    
    private func setupCategories() {
        UIApplication.shared.setMinimumBackgroundFetchInterval(Constants.backgroundRefreshInterval)

        let options: UNNotificationCategoryOptions = [.customDismissAction,
                                                      .hiddenPreviewsShowTitle,
                                                      .hiddenPreviewsShowSubtitle]
        
        let restartAction = UNNotificationAction(identifier: Constants.actionRestart, title: "Restart BT")
        let restartActionCategory = UNNotificationCategory(identifier: Constants.categoryRestart,
                                                           actions: [restartAction],
                                                           intentIdentifiers: [],
                                                           options: options)
        
        let showAction = UNNotificationAction(identifier: Constants.actionShowRestart, title: "Schedule restart BT")
        let showActionCategory = UNNotificationCategory(identifier: Constants.categoryRestart,
                                                        actions: [showAction],
                                                        intentIdentifiers: [],
                                                        options: options)

        UNUserNotificationCenter.current().setNotificationCategories([restartActionCategory, showActionCategory])
    }

    // MARK: - UNUserNotificationCenterDelegate

    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {

        switch response.actionIdentifier {
        case Constants.actionRestart:
            NotificationCenter.default.post(name: .RestartBluetooth, object: self)
        case Constants.actionShowRestart:
            self.scheduleActionNotification()
        default:
            break
        }

        completionHandler()
    }
}
