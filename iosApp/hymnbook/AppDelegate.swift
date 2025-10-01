import SwiftUI
import FirebaseCore
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        let iosInjector = IosInjector.init()
        iosInjector.setAnalytics(appAnalytics: FirebaseAppAnalytics())
        iosInjector.setSwiftInterop(swiftInterop: SwiftInteropImpl())

        return true
    }

    func application(_ application: UIApplication,
                     continue userActivity: NSUserActivity,
                     restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        // Get URL components from the incoming user activity.
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              let incomingURL = userActivity.webpageURL,
              let components = NSURLComponents(url: incomingURL, resolvingAgainstBaseURL: true)
        else {
            return false
        }


        // Check for specific URL components that you need.
        DeeplinkHandler.init().setDeeplink(deeplink: incomingURL.absoluteString)
        return true
    }
}
