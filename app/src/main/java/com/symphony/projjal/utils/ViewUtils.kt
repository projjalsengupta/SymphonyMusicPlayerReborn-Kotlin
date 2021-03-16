package com.symphony.projjal.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.view.Display
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding


object ViewUtils {
    fun bottomFitsSystemWindows(view: View, context: Context?, orientation: Int) {
        if (context == null) {
            return
        }
        view.doOnPreDraw {
            view.updatePadding(bottom = getNavigationBarHeight(context, orientation))
        }
    }

    fun topFitsSystemWindows(
        view: View,
        context: Context?,
        orientation: Int
    ) {
        if (context == null) {
            return
        }
        view.doOnPreDraw {
            view.updatePadding(top = getStatusBarHeight(context, orientation))
        }
    }

    fun fitsSystemWindows(view: View, context: Context?, orientation: Int) {
        if (context == null) {
            return
        }
        view.doOnPreDraw {
            view.updatePadding(
                top = getStatusBarHeight(context, orientation),
                bottom = getNavigationBarHeight(context, orientation)
            )
        }
    }

    private fun getWindowInsetsSize(context: Context): Point? {
        val appUsableSize = getAppUsableScreenSize(context)
        val realScreenSize = getRealScreenSize(context)

        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }

        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else Point()
    }

    private fun getAppUsableScreenSize(context: Context): Point {
        val size = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int
        val height: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val windowInsets: WindowInsets = windowMetrics.windowInsets

            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom

            val b = windowMetrics.bounds
            width = b.width() - insetsWidth
            height = b.height() - insetsHeight

            size.x = width
            size.y = height
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getSize(size)
        }
        return size
    }

    private fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.display
        } else {
            @Suppress("DEPRECATION")
            display = windowManager.defaultDisplay
        }
        val size = Point()
        display?.getRealSize(size)
        return size
    }

    fun getNavigationBarHeight(context: Context, orientation: Int): Int {
        val size = getWindowInsetsSize(context)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return size?.x ?: 0
        } else {
            return (size?.y?.minus(getStatusBarHeight(context, orientation))) ?: 0
        }
    }

    fun getStatusBarHeight(context: Context, orientation: Int): Int {
        val resources: Resources = context.resources
        val id: Int = resources.getIdentifier(
            if (orientation == Configuration.ORIENTATION_PORTRAIT) "status_bar_height" else "status_bar_height_landscape",
            "dimen",
            "android"
        )
        return if (id > 0) {
            resources.getDimensionPixelSize(id)
        } else 0
    }
}