package com.appify.appifymain.viewModel

import androidx.lifecycle.ViewModel
import com.appify.appifymain.repository.AppifyRepository

class AppifyViewModel(
    private val repositoryInstance: AppifyRepository
) : ViewModel() {
    init {
        repositoryInstance.setUpFirebaseHelper(); repositoryInstance.getAppInfoList()
    }

    fun getAppInfoLiveData() = repositoryInstance.appInfoLiveData()

    fun sendAppRequest(
        appName: String, userName: String, email: String, contactNo: String
    ) = repositoryInstance.sendAppRequest(appName, userName, email, contactNo)

    fun getAppRequestLiveData() = repositoryInstance.getAppRequestLiveData()

    fun clearRequestStates() = repositoryInstance.clearRequestStates()
}