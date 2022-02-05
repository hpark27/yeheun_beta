package com.beta.yeheun

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.beta.yeheun.databinding.ActivityUrluploadBinding
import java.text.SimpleDateFormat
import java.util.*

class UrlUploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUrluploadBinding
    // firebase database
    private lateinit var dataBase: FirebaseDatabase
    // firebase firestore
    private lateinit var fireStore: FirebaseFirestore
    // determine where to upload the url
    private var num: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUrluploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase database
        dataBase = FirebaseDatabase.getInstance()
        // initialize firestore
        fireStore = FirebaseFirestore.getInstance()

        // back
        binding.back.setOnClickListener {
            finish()
        }

        // upload bulletin URL
        binding.bulletinUpload.setOnClickListener {
            val url = binding.bulletinText.text.toString()
            num = 1
            uploadUrl(url,num)
        }

        // upload application URL
        binding.applicationUpload.setOnClickListener {
            val url = binding.applicationText.text.toString()
            num = 2
            uploadUrl(url,num)
        }

        // upload announcement
        binding.announceUpload.setOnClickListener {
            val title = binding.announceTextTitle.text.toString()
            val content = binding.announceTextContent.text.toString()
            uploadAnnounce(title,content)
        }

        // upload main announcement
        binding.noticeUpload.setOnClickListener {
            val content = binding.noticeText.text.toString()
            uploadMainAnnounce(content)
        }
    }

    /**
     * Upload URL to firebase
     */
    private fun uploadUrl(url: String, num: Int){
        if(num==1){ // bulletin
            if(url.isEmpty()){
                Toast.makeText(applicationContext, getString(R.string.urlupload_empty), Toast.LENGTH_SHORT).show()
                binding.bulletinText.requestFocus()
            }else{
                // reference to firebase database. Specific user database (*uid)
                dataBase.reference.child("Admin").child("Bulletin").setValue(url).
                addOnCompleteListener {
                    Toast.makeText(applicationContext,getString(R.string.uploaded), Toast.LENGTH_SHORT).show()
                    binding.bulletinText.text=null
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }

        if(num==2){ // application
            if(url.isEmpty()){
                Toast.makeText(applicationContext, getString(R.string.urlupload_empty), Toast.LENGTH_SHORT).show()
                binding.applicationText.requestFocus()
            }else{
                // reference to firebase database. Specific user database (*uid)
                dataBase.reference.child("Admin").child("Application").setValue(url).
                addOnCompleteListener {
                    Toast.makeText(applicationContext,getString(R.string.uploaded), Toast.LENGTH_SHORT).show()
                    binding.applicationText.text=null
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Upload announce on firebasestore
     */
    private fun uploadAnnounce(title: String, content: String){
        // current date
        val currentDate = Calendar.getInstance().time
        // convert date into string
        val date = SimpleDateFormat("yyyy. MM. dd", Locale.getDefault()).format(currentDate)
        // firestore document name
        val fileName = "$title $date"

        when {
            title.isEmpty() -> {
                Toast.makeText(applicationContext, getString(R.string.upload_title), Toast.LENGTH_SHORT).show()
                binding.announceTextTitle.requestFocus()
            }
            content.isEmpty() -> { // when content is empty
                Toast.makeText(applicationContext, getString(R.string.upload_content), Toast.LENGTH_SHORT).show()
                binding.announceTextContent.requestFocus()
            }
            else -> {
                val noticeMap = hashMapOf(
                    "title" to title,
                    "content" to content,
                    "date" to date)

                fireStore.collection("Notice").document(fileName).set(noticeMap)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, getString(R.string.uploaded), Toast.LENGTH_SHORT).show()
                        // clear edit text and request focus
                        binding.announceTextTitle.text = null
                        binding.announceTextContent.text = null
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    /**
     * Upload main announce on firebasestore
     */
    private fun uploadMainAnnounce(content: String){
        if(content.isEmpty()){ // when content is empty
            Toast.makeText(applicationContext, getString(R.string.upload_content), Toast.LENGTH_SHORT).show()
            binding.noticeText.requestFocus()
        }else{
            // reference to firebase database. Specific user database (*uid)
            dataBase.reference.child("Admin").child("MainNotice").setValue(content).
            addOnCompleteListener {
                Toast.makeText(applicationContext,getString(R.string.uploaded), Toast.LENGTH_SHORT).show()
                binding.noticeText.text=null
            }.addOnFailureListener {
                Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }
}