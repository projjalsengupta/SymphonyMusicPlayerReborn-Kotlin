package com.symphony.bottomnavfragmenthelper

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.symphony.colorutils.ColorUtils.adjustAlpha
import com.symphony.colorutils.ColorUtils.contrastColor

class BottomNavFragmentHelper(
    val activity: AppCompatActivity,
    val fragmentContainerId: Int,
    val bottomNavigation: BottomNavigationView,
    val fragmentMap: Map<Int, Fragment>,
    val savedInstanceState: Bundle?,
    val defaultFragment: Int
) {
    fun setUp() {
        bottomNavigation.setOnNavigationItemSelectedListener {
            fragmentMap[it.itemId]?.let { it1 -> replaceFragment(it1) }
            true
        }
        if (savedInstanceState == null) {
            if (fragmentMap[defaultFragment] == null) {
                replaceFragment(fragmentMap.values.toTypedArray()[0])
            } else {
                fragmentMap[defaultFragment]?.let { replaceFragment(it) }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(fragmentContainerId, fragment)
        fragmentTransaction.commit()
    }

    fun setBottomNavigationColor(colorPrimary: Int, colorAccent: Int) {
        bottomNavigation.setBackgroundColor(colorPrimary)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colors = intArrayOf(
            colorAccent,
            adjustAlpha(contrastColor(colorPrimary), 0.5f)
        )

        val colorStateList = ColorStateList(states, colors)
        bottomNavigation.itemIconTintList = colorStateList
        bottomNavigation.itemTextColor = colorStateList
    }
}