//
// Created by Kennedy Odife on 06.07.25.
// Copyright (c) 2025 techbeloved. All rights reserved.
//

import Foundation
import shared
import FirebaseAnalytics

class FirebaseAppAnalytics: AppAnalytics {
    func track(bundle: TrackingBundle) {
        let parameters = bundle.params.map { (param: TrackingParam, value: String) in
            return (param.firebaseParam(), value)
        }
        let parametersDict = Dictionary(uniqueKeysWithValues: parameters)
        Analytics.logEvent(bundle.event.firebaseEvent(), parameters: parametersDict)
    }

}

extension TrackingEvent {
    func firebaseEvent() -> String {
        return switch self {
        case .screenview:  AnalyticsEventScreenView
        case .actionsearch: AnalyticsEventSearch
        case .actionselectitem: AnalyticsEventSelectItem
        case .actionshare: AnalyticsEventShare
        default: ""
        }
    }
}

extension TrackingParam {
    func firebaseParam() -> String {
        return switch self {
        case .itemid: AnalyticsParameterItemID
        case .itemcategory: AnalyticsParameterItemCategory
        case .itemname: AnalyticsParameterItemName
        case .screenname: AnalyticsParameterScreenName
        case .searchterm: AnalyticsParameterSearchTerm
        case .content: AnalyticsParameterContent
        case .contenttype: AnalyticsParameterContentType
        default: ""
        }
    }
}
