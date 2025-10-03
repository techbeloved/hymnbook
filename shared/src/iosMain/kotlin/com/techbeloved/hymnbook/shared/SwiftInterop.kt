package com.techbeloved.hymnbook.shared

import com.techbeloved.hymnbook.shared.songshare.ShareAppData
import platform.UIKit.UIView

public interface SwiftInterop {
    public fun shareData(data: ShareAppData, sourceView: UIView)
}
