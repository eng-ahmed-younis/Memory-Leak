package com.example.memoryleak.leaks.uiviews

import android.animation.ValueAnimator


class AnimatorLeak {
    private var activeAnimator: ValueAnimator? = null

    fun triggerLeak(): String {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                // LEAK: AnimatorUpdateListener holding implicit reference to outer class running infinitely without cancel().
            }
        }
        activeAnimator = animator
        animator.start()
        return "Started INFINITE ValueAnimator without calling cancel() when component was destroyed."
    }

    fun triggerFix(): String {
        activeAnimator?.let {
            it.removeAllUpdateListeners()
            it.cancel()
            activeAnimator = null
        }
        return "Fixed! Called animator.removeAllUpdateListeners() and animator.cancel()."
    }
}
