package com.udacity.screens

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        const val TAG = "DetailsActivity"
        const val INTENT_EXTRA_FILE_NAME = "INTENT_EXTRA_FILE_NAME"
        const val INTENT_EXTRA_STATUS = "INTENT_EXTRA_STATUS"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.apply {
            lifecycleOwner = this@DetailActivity
        }

        setSupportActionBar(binding.toolbar)

        if (intent != null) {
            val fileName = intent.getStringExtra(INTENT_EXTRA_FILE_NAME)
            val status = intent.getStringExtra(INTENT_EXTRA_STATUS)
            if (fileName != null) {
                binding.content.fileNameText.text = fileName
            }
            if (status != null) {
                binding.content.statusText.text = status
            }
        }

        binding.content.openDownloadsButton.setOnClickListener {
            startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        }

        binding.content.button.setOnClickListener { onBackPressed() }
    }
}