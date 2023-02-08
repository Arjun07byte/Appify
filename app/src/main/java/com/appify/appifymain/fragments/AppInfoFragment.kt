package com.appify.appifymain.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appify.appifymain.MainActivity
import com.appify.appifymain.R
import com.appify.appifymain.viewModel.AppifyViewModel
import com.bumptech.glide.Glide

class AppInfoFragment : Fragment() {
    private val args: AppInfoFragmentArgs by navArgs()
    private lateinit var viewModelInstance: AppifyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelInstance = (activity as MainActivity).myViewModel
        setUpAppInfoContent(view); setUpRequestStateListener(); setUpSubmitButton(view)

        val expandedView: View = view.findViewById(R.id.view_Expanded)
        setUpSharedTransition(); setUpSwipeDownGesture(expandedView)
    }

    // function to set up the App Info Content
    private fun setUpAppInfoContent(view: View) {
        val textViewAppName: TextView = view.findViewById(R.id.textView_appInfoAppName)
        val textViewAppDesc: TextView = view.findViewById(R.id.textView_appInfoAppDesc)
        val textViewAppRating: TextView = view.findViewById(R.id.textView_appInfoRating)
        val textViewAppReviewCount: TextView = view.findViewById(R.id.textView_appInfoReviewCount)
        val textViewAppPrice: TextView = view.findViewById(R.id.textView_appInfoPrice)
        val imageViewAppPicture: ImageView = view.findViewById(R.id.imageView_normalAppInfo)
        val textViewRatingHeading: TextView = view.findViewById(R.id.textView_appRatingHeading)

        val currAppInfo = args.passedAppInfo
        currAppInfo.apply {
            textViewAppName.text = this?.name; textViewAppDesc.text = this?.description
            Glide.with(this@AppInfoFragment).load(this?.appInfoPicture).into(imageViewAppPicture)

            if (this?.name == "Custom App") {
                textViewAppPrice.visibility = View.GONE; textViewAppRating.visibility = View.GONE
                textViewAppReviewCount.visibility = View.GONE; textViewRatingHeading.text =
                    "See your Ideas come Real"
            } else {
                textViewAppRating.text =
                    view.context.getString(R.string.rating_text, this?.rating.toString())
                textViewAppPrice.text =
                    view.context.getString(R.string.price_text, this?.price.toString())
                textViewAppReviewCount.text =
                    view.context.getString(R.string.review_text, this?.reviewCount.toString())
            }
        }
    }

    // function to set up the Request States Listener
    // and display dialogs accordingly
    private fun setUpRequestStateListener() {
        val requestStatusDialogBuilder = AlertDialog.Builder(this.requireContext())

        val successDialog = requestStatusDialogBuilder.create(); successDialog.setCancelable(false)
        val failureDialog = requestStatusDialogBuilder.create(); failureDialog.setCancelable(false)
        val progressDialog =
            requestStatusDialogBuilder.create(); progressDialog.setCancelable(false)

        val requestSuccessView = layoutInflater.inflate(R.layout.layout_success_request, null)
        requestSuccessView.findViewById<Button>(R.id.button_successRequest)
            .setOnClickListener { findNavController().popBackStack(); successDialog.dismiss() }
        successDialog.setView(requestSuccessView)

        val requestFailureView = layoutInflater.inflate(R.layout.layout_failure_request, null)
        requestFailureView.findViewById<Button>(R.id.button_failureRequest)
            .setOnClickListener { failureDialog.dismiss() }
        failureDialog.setView(requestFailureView)

        val requestProgressView = layoutInflater.inflate(R.layout.layout_processing_request, null)
        progressDialog.setView(requestProgressView)

        viewModelInstance.getAppRequestLiveData().observe(this.viewLifecycleOwner) {
            when (it) {
                "Request Success" -> {
                    failureDialog.dismiss(); progressDialog.dismiss()
                    successDialog.show()
                }
                "Request Failure" -> {
                    progressDialog.dismiss(); successDialog.dismiss()
                    failureDialog.show()
                }
                "Request Sent" -> {
                    failureDialog.dismiss(); successDialog.dismiss()
                    progressDialog.show()
                }
            }
        }
    }

    // setting up the Submit Button which verifies
    // email phone and send request for App Purchase
    private fun setUpSubmitButton(view: View) {
        val textViewSubmit: TextView = view.findViewById(R.id.button_submitAppInfo)
        val editTextUserName: EditText = view.findViewById(R.id.editText_userName)
        val editTextEmail: EditText = view.findViewById(R.id.editText_userEmail)
        val editTextContact: EditText = view.findViewById(R.id.editText_contactNo)

        textViewSubmit.setOnClickListener {
            if (editTextUserName.text.trim()
                    .isNotEmpty() && isValidEmail(editTextEmail.text.trim()) && isValidPhone(
                    editTextContact.text.trim()
                )
            ) {
                viewModelInstance.sendAppRequest(
                    args.passedAppInfo?.name ?: "null",
                    editTextUserName.text.trim().toString(),
                    editTextEmail.text.trim().toString(),
                    editTextContact.text.trim().toString()
                )
            } else {
                val alertDialog = AlertDialog.Builder(this.requireContext()).create()
                var errorText = "Invalid "
                errorText += if (editTextUserName.text.trim().isEmpty()) {
                    "Name Entered"
                } else if (!isValidEmail(editTextEmail.text.trim())) {
                    "Email Entered"
                } else {
                    "Contact No. Entered"
                }
                val errorView = layoutInflater.inflate(R.layout.layout_invalid_fields_entered, null)
                errorView.findViewById<TextView>(R.id.textView_invalidFields).text = errorText
                errorView.findViewById<Button>(R.id.button_invalidFields).setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.setView(errorView); alertDialog.show(); alertDialog.setCancelable(false)
            }
        }
    }

    // function to set up the shared transition of the
    // expanded view and the TextView
    private fun setUpSharedTransition() {
        val incomingTransition = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        val exitTransition = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        incomingTransition.doOnEnd { makeLightIconStatusBar() }; exitTransition.doOnStart { makeDarkIconStatusBar() }
        sharedElementReturnTransition = exitTransition; sharedElementEnterTransition =
            incomingTransition
    }

    // function to check whether the entered phone no is
    // valid or not based on a regex defined in Java Library
    private fun isValidPhone(givenPhone: CharSequence): Boolean {
        return Patterns.PHONE.matcher(givenPhone).matches()
    }

    // function to check whether the entered email is
    // valid or not based on a regex defined in Java Library
    private fun isValidEmail(givenEmail: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(givenEmail).matches()
    }

    // functions to change the status bar of the device
    // according to the fragment in which the user is currently active
    private fun makeDarkIconStatusBar() {
        val currActivityWindow = requireActivity().window; currActivityWindow.statusBarColor =
            requireView().context.getColor(R.color.white100)
        currActivityWindow.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun makeLightIconStatusBar() {
        val currActivityWindow = requireActivity().window; currActivityWindow.statusBarColor =
            requireView().context.getColor(R.color.brandColor)
        currActivityWindow.decorView.systemUiVisibility = 0
    }

    // Setting up the swipe down listener on the Expanded
    // view and popping the fragment out
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSwipeDownGesture(expandedView: View) {
        var currY = 0f
        expandedView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    currY = event.y - currY
                    if (currY > 0) {
                        findNavController().popBackStack()
                    }
                }
            }
            true
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewModelInstance.clearRequestStates()
    }
}