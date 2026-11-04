package com.example.memoryleak.leaks.uiviews

import android.animation.ValueAnimator

class LottieAnimationLeak {
    private var isAnimationRunning = false

    fun triggerLeak(): String {
        // LEAK: Continuous Lottie/Vector animation running indefinitely on background Choreographer.
        isAnimationRunning = true
        return "Started continuous Lottie / Animator repeat loop without calling cancelAnimation()."
    }

    fun triggerFix(): String {
        isAnimationRunning = false
        return "Fixed! Called lottieAnimationView.cancelAnimation() and pauseAnimation()."
    }
}
