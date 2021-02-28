package com.symphony.projjal.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
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

    fun topFitsSystemWindows(view: View, context: Context?, orientation: Int) {
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

    fun getNavigationBarHeight(context: Context, orientation: Int): Int {
        val resources: Resources = context.resources
        val id: Int = resources.getIdentifier(
            if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
            "dimen",
            "android"
        )
        return if (id > 0) {
            resources.getDimensionPixelSize(id)
        } else 0
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