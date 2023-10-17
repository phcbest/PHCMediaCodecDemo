package com.gowow.phcmediacodecdemo

import android.content.Intent
import android.os.Bundle
import com.gowow.phcmediacodecdemo.base.BaseActivity
import com.gowow.phcmediacodecdemo.databinding.ActivityMainBinding
import com.gowow.phcmediacodecdemo.frameExtraction.FrameExtractionActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnGotoFrameExtraction.setOnClickListener {
            startActivity(Intent(this@MainActivity, FrameExtractionActivity::class.java))
        }
    }
}