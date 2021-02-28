package com.symphony.projjal.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView

class SymphonyRecyclerView : RecyclerView {
    private val mOnItemTouchDispatcher = OnItemTouchDispatcher()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        @Nullable attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        @Nullable attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.addOnItemTouchListener(mOnItemTouchDispatcher)
    }

    override fun addOnItemTouchListener(listener: OnItemTouchListener) {
        mOnItemTouchDispatcher.addListener(listener)
    }

    override fun removeOnItemTouchListener(listener: OnItemTouchListener) {
        mOnItemTouchDispatcher.removeListener(listener)
    }

    private class OnItemTouchDispatcher : OnItemTouchListener {
        private val mListeners: MutableList<OnItemTouchListener> = ArrayList()
        private val mTrackingListeners: MutableSet<OnItemTouchListener> = LinkedHashSet()

        @Nullable
        private var mInterceptingListener: OnItemTouchListener? = null
        fun addListener(listener: OnItemTouchListener) {
            mListeners.add(listener)
        }

        fun removeListener(listener: OnItemTouchListener) {
            mListeners.remove(listener)
            mTrackingListeners.remove(listener)
            if (mInterceptingListener === listener) {
                mInterceptingListener = null
            }
        }

        override fun onInterceptTouchEvent(
            recyclerView: RecyclerView,
            event: MotionEvent
        ): Boolean {
            val action = event.action
            for (listener in mListeners) {
                val intercepted = listener.onInterceptTouchEvent(recyclerView, event)
                if (action == MotionEvent.ACTION_CANCEL) {
                    mTrackingListeners.remove(listener)
                    continue
                }
                if (intercepted) {
                    mTrackingListeners.remove(listener)
                    event.action = MotionEvent.ACTION_CANCEL
                    for (trackingListener in mTrackingListeners) {
                        trackingListener.onInterceptTouchEvent(recyclerView, event)
                    }
                    event.action = action
                    mTrackingListeners.clear()
                    mInterceptingListener = listener
                    return true
                } else {
                    mTrackingListeners.add(listener)
                }
            }
            return false
        }

        override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {
            if (mInterceptingListener == null) {
                return
            }
            mInterceptingListener!!.onTouchEvent(recyclerView, event)
            val action = event.action
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                mInterceptingListener = null
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            for (listener in mListeners) {
                listener.onRequestDisallowInterceptTouchEvent(disallowIntercept)
            }
        }
    }
}
