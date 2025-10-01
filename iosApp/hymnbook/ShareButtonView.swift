import SwiftUI
import UIKit
import shared

struct ShareButtonView {

    func share(_ text: String) {
        let activityViewController = UIActivityViewController(
            activityItems: [text], applicationActivities: nil
        )

        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let rootViewController = windowScene.windows.first?.rootViewController {

            var topController = rootViewController
            while let presentedViewController = topController.presentedViewController {
                topController = presentedViewController
            }
            topController.present(activityViewController, animated: true, completion: nil)
        }
    }
}

public class SwiftInteropImpl: SwiftInterop {
    public func shareData(data: ShareAppData) {
        let shareButtonView = ShareButtonView()
        let text = "\(data.text)\n\(data.url)"
        shareButtonView.share(text)
    }
}
