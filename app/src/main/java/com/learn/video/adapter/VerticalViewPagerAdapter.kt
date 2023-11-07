package com.fmt.mvi.learn.video.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter
import com.fmt.mvi.learn.video.fragment.VideoItemFragment

class VerticalViewPagerAdapter(fm: FragmentManager) : PagerAdapter() {
    private val fragmentManager: FragmentManager
    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentPrimaryItem: Fragment? = null
    private var urlList: List<String>? = null
    fun setUrlList(urlList: List<String>?) {
        this.urlList = urlList
    }

    init {
        fragmentManager = fm
    }

    override fun getCount(): Int {
        return urlList!!.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mCurTransaction == null) {
            mCurTransaction = fragmentManager.beginTransaction()
        }
        val fragment = VideoItemFragment()
        if (urlList != null && urlList!!.isNotEmpty()) {
            val bundle = Bundle()
            if (position >= urlList!!.size) {
                bundle.putString(VideoItemFragment.URL, urlList!![position % urlList!!.size])
            } else {
                bundle.putString(VideoItemFragment.URL, urlList!![position])
            }
            fragment.arguments=bundle
        }
        mCurTransaction?.add(
            container.id, fragment,
            makeFragmentName(container.id, position)
        )
        fragment.userVisibleHint=false
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (mCurTransaction == null) {
            mCurTransaction = fragmentManager.beginTransaction()
        }
        (`object` as Fragment?)?.let { mCurTransaction!!.detach(it) }
        (`object` as Fragment?)?.let { mCurTransaction!!.remove(it) }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    private fun makeFragmentName(viewId: Int, position: Int): String {
        return "android:switcher:$viewId$position"
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment: Fragment? = `object` as Fragment?
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                mCurrentPrimaryItem!!.setUserVisibleHint(false)
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true)
                fragment.setUserVisibleHint(true)
            }
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitNowAllowingStateLoss()
            mCurTransaction = null
        }
    }



}