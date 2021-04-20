package com.intoverflown.pos.ui.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intoverflown.pos.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private var binding: ActivityNotificationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }
}