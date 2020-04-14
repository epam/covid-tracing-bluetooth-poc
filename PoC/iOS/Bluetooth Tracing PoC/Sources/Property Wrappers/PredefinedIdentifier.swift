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

import Foundation

/*
 Property wrapper for predefined service and characteristic identifiers that stored in Info.plist
 Android contains the same service and characteristic identifiers
 */

@propertyWrapper
struct PredefinedIdentifier {
    enum Key: String {
        case service = "Service UUID"
        case characteristic = "Characteristic UUID"
    }

    var wrappedValue: String {
        get {
            return identifier
        }
    }

    private let identifier: String

    init(_ key: Key) {
        guard let path = Bundle.main.path(forResource: "Info", ofType: "plist"),
            let properties = NSDictionary(contentsOfFile: path) as? [String: Any],
            let value = properties[key.rawValue] as? String else {

                preconditionFailure("Failed to load identifier")
        }

        identifier = value
    }
}
