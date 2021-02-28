package com.symphony.projjal.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

object FragmentUtils {
    fun addFragment(
        activity: AppCompatActivity,
        id: Int,
        newFragment: Fragment,
        fragmentName: String
    ) {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(id, newFragment)
        fragmentTransaction.addToBackStack(fragmentName)
        fragmentTransaction.commit()
    }
}