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
import CoreBluetooth

/*
 Service that works as a peripheral, advertises service identifier
 and provides characteristic with user identifier as a value of it

 Message for keys 'Privacy - Bluetooth Peripheral Usage Description' should be added to Info.plist
 Background mode 'Act as a Bluetooth LE accessories' should be enable in the Capabilities of the project
 */

class PeripheralService: NSObject, CBPeripheralManagerDelegate {
    private var peripheralManager: CBPeripheralManager!
    
    @PredefinedIdentifier(.service) private var serviceIdentifier: String
    @PredefinedIdentifier(.characteristic) private var characteristicIdentifier: String
    
    @StoredIdentifier(.user) private var userIdentifier: String

    override init() {
        super.init()

        peripheralManager = CBPeripheralManager(delegate: self, queue: .main)

        NotificationCenter.default.addObserver(forName: .RestartBluetooth, object: nil, queue: nil) { _ in
            self.stopAdvertising()
            self.startAdvertising()
        }
    }
    
    private func startAdvertising() {
        peripheralManager.removeAllServices()

        let data = Data(userIdentifier.utf8)
        let characteristic = CBMutableCharacteristic(type: characteristicIdentifier.cbuuid,
                                                     properties: .read,
                                                     value: data,
                                                     permissions: .readable)

        let service = CBMutableService(type: serviceIdentifier.cbuuid, primary: true)
        service.characteristics = [characteristic]

        peripheralManager.add(service)
        peripheralManager.startAdvertising([CBAdvertisementDataServiceUUIDsKey: [serviceIdentifier.cbuuid]])

        Logger.shared.logMessage("PM did start advertising\n")
    }

    private func stopAdvertising() {
        peripheralManager.stopAdvertising()

        Logger.shared.logMessage("PM did stop advertising\n")
    }

    // MARK: - CBPeripheralManagerDelegate

    func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        switch peripheral.state {
        case .poweredOn:
            Logger.shared.logMessage("PM start advertising\n")

            startAdvertising()
        default:
            Logger.shared.logMessage("PM stop advertising\n")

            stopAdvertising()
        }
    }
}
