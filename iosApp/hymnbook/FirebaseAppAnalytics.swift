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

    func setDefaultParams(params: [String: String]) {
        Analytics.setDefaultEventParameters(params)
    }

}

extension TrackingEvent {
    func firebaseEvent() -> String {
        return switch self {
        case .screenview:  AnalyticsEventScreenView
        case .actionsearch: AnalyticsEventSearch
        case .actionselectitem: AnalyticsEventSelectItem
        case .actionshare: AnalyticsEventShare
        case .viewcontent: AnalyticsEventSelectContent
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
        case .screenclass: AnalyticsParameterScreenClass
        case .searchterm: AnalyticsParameterSearchTerm
        case .content: AnalyticsParameterContent
        case .contenttype: AnalyticsParameterContentType
        case .method: AnalyticsParameterMethod
        default: ""
        }
    }
}
