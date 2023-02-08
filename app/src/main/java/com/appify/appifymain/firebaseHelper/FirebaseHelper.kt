package com.appify.appifymain.firebaseHelper

import androidx.lifecycle.MutableLiveData
import com.appify.appifymain.models.AppInfo
import com.appify.appifymain.models.AppRequest
import com.google.firebase.database.*

class FirebaseHelper {
    companion object {
        private lateinit var firebaseDatabase: FirebaseDatabase
        private val appInfoListLiveData: MutableLiveData<ArrayList<AppInfo>> = MutableLiveData()
        private val appRequestStatusLiveData: MutableLiveData<String> = MutableLiveData()

        fun setUpFirebaseHelper() {
            firebaseDatabase = FirebaseDatabase.getInstance()
        }

        fun getAppInfoList() {
            firebaseDatabase.goOnline()
            firebaseDatabase.reference.child("apps").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val currentAppsList: ArrayList<AppInfo> = arrayListOf()
                        for (eachApp in dataSnapshot.children) {
                            eachApp.getValue(AppInfo::class.java)?.let { currentAppsList.add(it) }
                        }
                        appInfoListLiveData.postValue(currentAppsList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // this is response for error
                    }
                }
            )
        }

        fun sendAppRequest(
            appName: String,
            userName: String,
            email: String,
            contactNo: String
        ) {
            appRequestStatusLiveData.postValue("Request Sent")
            firebaseDatabase.goOnline()
            firebaseDatabase.reference.child("requests").child(appName)
                .child(userName).setValue(AppRequest(userName, email, contactNo))
                .addOnSuccessListener { appRequestStatusLiveData.postValue("Request Success") }
                .addOnCanceledListener { appRequestStatusLiveData.postValue("Request Failure") }
                .addOnFailureListener { appRequestStatusLiveData.postValue("Request Failure") }
        }

        fun getAppInfoLiveData() = appInfoListLiveData

        fun getAppRequestLiveData() = appRequestStatusLiveData

        fun clearRequestStates() {
            appRequestStatusLiveData.postValue("Requests Cleared")
        }
    }
}