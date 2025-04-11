package com.tugasmobile.inventory.utils


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

        // Offset posisi awal (dari bawah)
        val offsetPx = dpToPx(100f, context)

        // Tahap 1: Buat semua item menghilang dengan animasi
        for (i in 0 until childCount) {
            val child = container.getChildAt(i)
            child.alpha = 0f
            child.translationY = offsetPx
        }

        // Tahap 2: Setelah delay, munculkan semua item satu per satu dari bawah
        Handler(Looper.getMainLooper()).postDelayed({
            for (i in 0 until childCount) {
                val child = container.getChildAt(i)
                child.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay((i * 50).toLong()) // efek delay berurutan
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }, 350) // tunggu animasi hilang selesai
    }
    fun animateRecyclerItem(view: View, position: Int) {
        view.alpha = 0f
        view.translationY = 100f // offset dari bawah

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay((position * 30).toLong()) // delay berdasarkan posisi
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

}