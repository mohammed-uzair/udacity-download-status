package com.udacity.screens

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.webkit.URLUtil.isValidUrl
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.Util.toast
import com.udacity.util.cancelNotifications
import com.udacity.util.sendNotification
import com.udacity.util.updateStatus

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0
    private var selectedDownloadFilePosition = -1
    private lateinit var notificationManager: NotificationManager
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
        }

        setToolbar()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        initializeViews()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.content.waveButton.resetButton()
    }

    private val radioCheckedListener =
        RadioGroup.OnCheckedChangeListener { _, _ -> //Set the custom download URL edit text to empty
            if (binding.content.editText.text!!.isNotBlank()) {
                binding.content.editText.setText("")
            }

            selectedDownloadFilePosition =
                when (binding.content.filesDownloadRadioGroup.checkedRadioButtonId) {
                    R.id.glide_radio_button -> 0
                    R.id.current_repo_radio_button -> 1
                    R.id.retrofit_radio_button -> 2
                    else -> -1
                }

            if (binding.content.waveButton.isEnabled) {
                binding.content.waveButton.resetButton()
            }
        }

    private fun initializeViews() {
        binding.content.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                selectedDownloadFilePosition = -1
                binding.content.filesDownloadRadioGroup.setOnCheckedChangeListener(null)
                binding.content.filesDownloadRadioGroup.clearCheck()
                binding.content.filesDownloadRadioGroup.setOnCheckedChangeListener(
                    radioCheckedListener
                )

                if (binding.content.waveButton.isEnabled || selectedDownloadFilePosition == -1) {
                    binding.content.waveButton.resetButton()
                }
            }

        })

        binding.content.filesDownloadRadioGroup.setOnCheckedChangeListener(radioCheckedListener)

        binding.content.waveButton.setOnClickListener {
            //If no file is provided in the URL and none is selected from the predefined, show alert
            downloadButtonClicked()
        }
    }

    private fun downloadButtonClicked() {
        if (isAnyViewSelected()) {
            if (selectedDownloadFilePosition > -1 && isValidUrl(URLs[selectedDownloadFilePosition])) {
                url = URLs[selectedDownloadFilePosition]
                download(URLs[selectedDownloadFilePosition])
            } else if (binding.content.editText.text!!.isNotBlank() && isValidUrl(
                    binding.content.editText.text.toString().trim()
                )
            ) {
                url = binding.content.editText.text.toString().trim()
                download(binding.content.editText.text.toString().trim())
            } else {
                toast(getString(R.string.error_invalid_download_url))
            }
        } else {
            toast(getString(R.string.error_none_selected))
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun isAnyViewSelected() =
        selectedDownloadFilePosition > -1 || binding.content.editText.text.toString().isNotBlank()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                toast("Download Completed")

                // Update the notification
                notificationManager.updateStatus(applicationContext, url, "Completed")

                binding.content.waveButton.setCompleted()
            }
        }
    }

    private fun download(url: String) {
        binding.content.waveButton.startLoading()

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        showNotification(url)
    }

    private fun showNotification(url: String) {
        //Cancel previous notifications
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(
            url,
            applicationContext
        )
    }

    companion object {
        private val URLs = arrayOf(
            "https://github.com/bumptech/glide",
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
            "https://github.com/square/retrofit"
        )
    }
}