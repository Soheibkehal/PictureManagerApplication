package com.example.picturemanagersoheib.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.picturemanagersoheib.ui.fragments.AlbumFragment
import com.example.picturemanagersoheib.ui.fragments.ImageFragment
import com.example.picturemanagersoheib.utils.CategoryContentType
import com.example.picturemanagersoheib.utils.SessionManager


private const val NUM_TABS = 2

class UserPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var userId :Int = SessionManager().fetchUserId()!!.toInt()

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ImageFragment.newInstance(userId, CategoryContentType.IMAGE)
            1 -> return AlbumFragment.newInstance(userId)
        }
        return ImageFragment.newInstance(userId, CategoryContentType.IMAGE)
    }
}

