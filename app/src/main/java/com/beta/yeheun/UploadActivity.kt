package com.beta.yeheun

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beta.yeheun.databinding.ActivityUploadBinding

class UploadActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // back
        binding.back.setOnClickListener {
            val submain = Intent(this, MainActivity::class.java)
            startActivity(submain)
            finish()
        }

        // url
        binding.url.setOnClickListener {
            val url = Intent(this, UrlUploadActivity::class.java)
            startActivity(url)
        }

        // posting
        binding.posting.setOnClickListener {
            val offering = Intent(this, PostingUploadActivity::class.java)
            startActivity(offering)
        }

        // url edit
        binding.urlEdit.setOnClickListener {
            val urledit = Intent(this, UrlEditActivity::class.java)
            startActivity(urledit)
        }

        // notice
        binding.postingEdit.setOnClickListener {
            val postingedit = Intent(this, PostingEditActivity::class.java)
            startActivity(postingedit)
        }
    }

    // back button override
    override fun onBackPressed() {
        super.onBackPressed()

        val main = Intent(this, MainActivity::class.java)
        startActivity(main)
        finishAffinity()
    }
}
