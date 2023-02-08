package com.appify.appifymain

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.appify.appifymain.repository.AppifyRepository
import com.appify.appifymain.viewModel.AppifyViewModel

class MainActivity : AppCompatActivity() {
    lateinit var myViewModel: AppifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen(); super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myViewModel = AppifyViewModel(AppifyRepository())
    }
}