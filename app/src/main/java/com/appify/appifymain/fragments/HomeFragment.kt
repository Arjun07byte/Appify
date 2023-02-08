package com.appify.appifymain.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.appify.appifymain.MainActivity
import com.appify.appifymain.R
import com.appify.appifymain.adapters.AppInfoAdapter
import com.appify.appifymain.models.AppInfo
import com.appify.appifymain.viewModel.AppifyViewModel

class HomeFragment : Fragment() {
    private lateinit var viewToExpand: View
    private lateinit var viewModelInstance: AppifyViewModel
    private lateinit var makeItMineText: TextView
    private lateinit var layoutLoading: LinearLayout
    private lateinit var appInfoViewPager: ViewPager2
    private val homeFragmentAdapter = AppInfoAdapter()
    private var currAppToDisplay = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelInstance = (activity as MainActivity).myViewModel

        setUpViewVariables(view)    // setting up all the viewVariables

        // Setting up the ViewPager
        setUpViewPager(homeFragmentAdapter)

        // Setting up the Listener to listen the data
        // coming from the Firebase RD
        viewModelInstance.getAppInfoLiveData().observe(this.viewLifecycleOwner) {
            if (it != null) {
                homeFragmentAdapter.submitList(it); appInfoViewPager.setCurrentItem(
                    currAppToDisplay,
                    false
                )
                layoutLoading.visibility = View.GONE; appInfoViewPager.visibility = View.VISIBLE
                makeItMineText.setText(R.string.make_it_mine_text); setUpSwipeUpGesture()
            } else {
                layoutLoading.visibility = View.VISIBLE; appInfoViewPager.visibility = View.GONE
                makeItMineText.setText(R.string.please_wait_text); disableSwipeUpGesture()
            }
        }

        setUpArrowAnimation()       // setting up the arrow above the make it mine button
        setUpSwipeUpGesture()       // setting up the swipeUp Gesture on Make it Mine button
    }

    private fun setUpViewVariables(view: View) {
        view.apply {
            makeItMineText = findViewById(R.id.textView_makeItMine); appInfoViewPager =
            findViewById(R.id.viewPager_homeFragment)
            viewToExpand = findViewById(R.id.view_toExpand); layoutLoading =
            findViewById(R.id.layout_loadingIndicator)

        }
    }

    private fun setUpViewPager(homeFragmentAdapter: AppInfoAdapter) {
        appInfoViewPager.apply {
            adapter = homeFragmentAdapter; offscreenPageLimit = 3
            clipToPadding = false; clipChildren = false
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(getTransformer())
        }
    }

    private fun getTransformer(): CompositePageTransformer {
        val newTransformer = CompositePageTransformer()
        newTransformer.addTransformer(MarginPageTransformer(40))
        newTransformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }

        return newTransformer
    }

    private fun setUpArrowAnimation() {
        val upDownAnimation =
            AnimationUtils.loadAnimation(this.requireContext(), R.anim.anim_up_down)
        upDownAnimation.apply {
            duration = 1000; repeatCount = Animation.INFINITE; repeatMode = Animation.REVERSE
        }
        val imageViewUpArrow: ImageView = requireView().findViewById(R.id.imageView_upArrow_home)
        imageViewUpArrow.startAnimation(upDownAnimation)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSwipeUpGesture() {
        var currY = 0f
        viewToExpand.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    currY = event.y - currY
                    if (currY < 0) transitFragment()
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disableSwipeUpGesture() {
        viewToExpand.setOnTouchListener { _, _ ->
            false
        }
    }

    private fun transitFragment() {
        currAppToDisplay = appInfoViewPager.currentItem

        makeItMineText = requireView().findViewById(R.id.textView_makeItMine)
        val transitionExtra = FragmentNavigatorExtras(
            viewToExpand to "viewExpanded", makeItMineText to "makeItMineExpanded"
        )
        val navigationDirections =
            HomeFragmentDirections.actionHomeFragmentToAppInfoFragment(getCurrentAppInfo())
        findNavController().navigate(
            navigationDirections, transitionExtra
        )
    }

    private fun getCurrentAppInfo(): AppInfo? {
        return homeFragmentAdapter.getItemAtIndex(appInfoViewPager.currentItem)
    }
}