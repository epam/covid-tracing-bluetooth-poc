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

import Foundation

/*
 Property wrapper for user defaults that create unique user identifier for the first time
 and provide it for every application's launch
 */

@propertyWrapper
struct StoredIdentifier {
    enum Key: String {
        case user = "User UUID"
    }

    var wrappedValue: String {
        get {
            return identifier
        }
    }

    private let identifier: String

    init(_ key: Key) {
        let storage: UserDefaults = .standard
        if let value = storage.value(forKey: key.rawValue) as? String {
            identifier = value
        } else {
            identifier = UUID().uuidString
            storage.set(identifier, forKey: key.rawValue)
        }
    }
}
