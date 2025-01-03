package com.techbeloved.media

enum class PlayerState {
    Ready,

    /**
     * The player must be prepared before playback can be done
     */
    Idle,
    Buffering,
    Ended,
}
