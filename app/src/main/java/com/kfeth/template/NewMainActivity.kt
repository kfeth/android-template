package com.kfeth.template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kfeth.template.ui.NewsApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsApp()
        }
    }
}