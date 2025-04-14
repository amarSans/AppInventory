package com.muammar.inventory.utils


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

object AnimationHelper {

    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun animateItems(container: ViewGroup, context: Context) {
        val childCount = container.childCount

        val offsetPx = dpToPx(100f, context)

        for (i in 0 until childCount) {
            val child = container.getChildAt(i)
            child.alpha = 0f
            child.translationY = offsetPx
        }

        Handler(Looper.getMainLooper()).postDelayed({
            for (i in 0 until childCount) {
                val child = container.getChildAt(i)
                child.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay((i * 50).toLong())
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }, 350)
    }
    fun animateRecyclerItem(view: View, position: Int) {
        view.alpha = 0f
        view.translationY = 100f

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay((position * 30).toLong())
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

}