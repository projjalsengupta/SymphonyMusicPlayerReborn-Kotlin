package com.symphony.projjal.utils

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.symphony.projjal.customviews.SymphonyImageView

object FragmentUtils {
    data class SharedImageView(val view: SymphonyImageView)

    fun addFragment(
        activity: AppCompatActivity,
        id: Int,
        newFragment: Fragment,
        fragmentName: String,
        sharedImageView: SharedImageView? = null
    ) {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        if (sharedImageView != null) {
            fragmentTransaction.setReorderingAllowed(true)
            fragmentTransaction.addSharedElement(
                sharedImageView.view,
                sharedImageView.view.transitionName
            )
        }
        fragmentTransaction.replace(id, newFragment)
        fragmentTransaction.addToBackStack(fragmentName)
        fragmentTransaction.commit()
    }
}