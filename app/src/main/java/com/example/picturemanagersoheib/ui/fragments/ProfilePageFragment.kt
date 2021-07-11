package com.example.picturemanagersoheib.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.ui.adapters.UserPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator





class ProfilePageFragment() : Fragment() {

    private val userArray = arrayOf(
        "Images",
        "Albums"
    )

    private var mContext: Context? = null

    private lateinit var tabLayout :TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var userPageAdapter: UserPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.mContext = activity
        return inflater.inflate(R.layout.user_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout = view.findViewById(R.id.tab_layout)
        userPageAdapter = UserPageAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = userPageAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = userArray[position]
        }.attach()
    }
}