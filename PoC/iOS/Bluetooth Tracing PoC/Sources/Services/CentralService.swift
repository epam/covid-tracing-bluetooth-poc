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
 Service that works as a central, scans for peripherals with the predefined identifier,
 discovers service's characteristic with the predefined identifier,
 reads a value (user identifier), logs it and displays it as a local notification

 Message for keys 'Privacy - Bluetooth Always Usage Description' should be added to Info.plist
 Background mode 'Uses Bluetooth LE accessories' should be enable in the Capabilities of the project
*/

class CentralService: NSObject, CBCentralManagerDelegate, CBPeripheralDelegate {
    private var centralManager: CBCentralManager!
    private var discoveredPeripherals = Set<CBPeripheral>()
    private var connectedPeripherals = Set<CBPeripheral>()

    @PredefinedIdentifier(.service) private var serviceIdentifier: String
    @PredefinedIdentifier(.characteristic) private var characteristicIdentifier: String

    override init() {
        super.init()

        centralManager = CBCentralManager(delegate: self, queue: .main)
        
        NotificationCenter.default.addObserver(forName: .RestartBluetooth, object: nil, queue: nil) { _ in
            self.stopScan()
            self.startScan()
        }
    }

    private func startScan() {
        let services = [serviceIdentifier.cbuuid]

        let options: [String: Any] = [CBCentralManagerScanOptionAllowDuplicatesKey: NSNumber(value: true),
                                      CBCentralManagerScanOptionSolicitedServiceUUIDsKey: services]

        centralManager.scanForPeripherals(withServices: services, options: options)

        Logger.shared.logMessage("CM did start scan\n")
    }

    private func stopScan() {
        centralManager.stopScan()

        Logger.shared.logMessage("CM did stop scan\n")
    }

    // MARK: - CBCentralManagerDelegate

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            Logger.shared.logMessage("CM scan for peripherals with service \(serviceIdentifier)\n")

            startScan()
        default:
            Logger.shared.logMessage("CM stop scan\n")

            stopScan()

        }
    }

    func centralManager(_ central: CBCentralManager,
                        didDiscover peripheral: CBPeripheral,
                        advertisementData: [String : Any],
                        rssi RSSI: NSNumber) {

        Logger.shared.logMessage("CM did discover peripheral \(peripheral.identifier)\n")

        discoveredPeripherals.insert(peripheral)

        central.connect(peripheral, options: nil)
    }

    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        Logger.shared.logMessage("CM did connect peripheral \(peripheral.identifier)\n")

        discoveredPeripherals.remove(peripheral)
        connectedPeripherals.insert(peripheral)

        peripheral.delegate = self
        peripheral.discoverServices([serviceIdentifier.cbuuid])
    }

    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        Logger.shared.logMessage("CM did fail to discover peripheral \(peripheral.identifier)\n")

        discoveredPeripherals.remove(peripheral)
    }

    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        Logger.shared.logMessage("CM did disconnect peripheral \(peripheral.identifier)\n")

        connectedPeripherals.remove(peripheral)
    }

    // MARK: - CBPeripheralDelegate

    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        guard error == nil, let service = peripheral.services?.first else {
            Logger.shared.logMessage("P \(peripheral.identifier) did discover services\n")

            centralManager.cancelPeripheralConnection(peripheral)

            return
        }

        peripheral.discoverCharacteristics([characteristicIdentifier.cbuuid], for: service)
    }

    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        guard error == nil, let characteristic = service.characteristics?.first else {
            Logger.shared.logMessage("P \(peripheral.identifier) did discover characteristics\n")

            centralManager.cancelPeripheralConnection(peripheral)

            return
        }

        peripheral.readValue(for: characteristic)
    }

    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        defer {
            centralManager.cancelPeripheralConnection(peripheral)
        }

        guard error == nil, let data = characteristic.value, let value = String(data: data, encoding: .utf8) else {
            Logger.shared.logMessage("P \(peripheral.identifier) did update value\n")

            return
        }

        var message = "Identifier -> \(value)\n"
        message += "Date -> \(Date.now.format())\n"
        message += "My battery status -> \(UIDevice.current.batteryStatus)\n"
        Logger.shared.logMessage(message)

        NotificationService.shared.show(message: "Found another device: \(value)")
    }
}
