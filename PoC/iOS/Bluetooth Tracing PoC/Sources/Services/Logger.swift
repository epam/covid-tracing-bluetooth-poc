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

protocol LoggerDelegate: class {
    func logDidAppend(with message: String)
}

/*
 Singleton that writes log messages to the disc
 and provides computed property 'logs' to get all messages
 and method 'clear' to remove file from the disc
*/

class Logger {
    static var shared = Logger()

    weak var delegate: LoggerDelegate?

    var logs: String? {
        try? String(contentsOf: url, encoding: .utf8)
    }

    private let url: URL

    private init() {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        url = urls[0].appendingPathComponent("log.txt")
    }

    func clear() {
        try? FileManager.default.removeItem(at: url)
    }

    func logMessage(_ message: String) {
        defer {
            delegate?.logDidAppend(with: message)
        }

        let data = Data(message.utf8)

        guard FileManager.default.fileExists(atPath: url.path) else {
            try? data.write(to: url)

            return
        }

        let handler = try! FileHandle(forWritingTo: url)
        handler.seekToEndOfFile()
        handler.write(data)
        handler.closeFile()
    }
}
