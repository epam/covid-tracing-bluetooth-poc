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
 The view that mirrors log messages and provides actions to share them or clear logger 
 */

class ViewController: UIViewController, LoggerDelegate {
    @IBOutlet private weak var textView: UITextView!

    @StoredIdentifier(.user) private var userIdentifier: String

    // MARK: - Lifecycle

    override func viewDidLoad() {
        super.viewDidLoad()

        title = userIdentifier

        textView.text = Logger.shared.logs

        Logger.shared.delegate = self
    }

    // MARK: - Actions

    @IBAction private func actionBarButtonItemDidTap(_ sender: UIBarButtonItem) {
        let items = [textView.text as Any]
        let controller = UIActivityViewController(activityItems: items, applicationActivities: nil)
        present(controller, animated: true)
    }

    @IBAction private func trashBarButtonItemDidTap(_ sender: UIBarButtonItem) {
        textView.text = nil

        Logger.shared.clear()
    }

    // MARK: - LoggerDelegate

    func logDidAppend(with message: String) {
        DispatchQueue.main.async {
            self.textView.text += message
        }
    }
}
