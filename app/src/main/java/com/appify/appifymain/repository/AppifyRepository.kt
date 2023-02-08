package com.appify.appifymain.repository

import com.appify.appifymain.firebaseHelper.FirebaseHelper

class AppifyRepository {
    fun setUpFirebaseHelper() = FirebaseHelper.setUpFirebaseHelper()
    fun getAppInfoList() = FirebaseHelper.getAppInfoList()
    fun appInfoLiveData() = FirebaseHelper.getAppInfoLiveData()
    fun sendAppRequest(
        appName: String,
        userName: String,
        email: String,
        contactNo: String
    ) =
        FirebaseHelper.sendAppRequest(appName, userName, email, contactNo)

    fun getAppRequestLiveData() = FirebaseHelper.getAppRequestLiveData()
    fun clearRequestStates() = FirebaseHelper.clearRequestStates()
}