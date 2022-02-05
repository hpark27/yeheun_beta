package com.beta.yeheun

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.beta.yeheun.databinding.ActivityPostinguploadBinding
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class PostingUploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostinguploadBinding
    // firebase firestore
    private lateinit var fireStore: FirebaseFirestore
    // image file url
    private var url: String = ""
    // firebase download url
    private var downloadUrl: String = ""

    // pick image for offering
    private val getOfferingContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            url = it.data?.data.toString()

            if(url.isEmpty()){
                Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
            }else{
                // show image on imageview
                Glide.with(this).load(url).into(binding.qr)
            }
        }
    }

    // pick image for goods
    private val getGoodsContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            url = it.data?.data.toString()

            if(url.isEmpty()){
                Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
            }else{
                // show image on imageview
                Glide.with(this).load(url).into(binding.goodsProfile)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostinguploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize Firebase Database
        fireStore = FirebaseFirestore.getInstance()

        // back
        binding.back.setOnClickListener {
            finish()
        }

        // pick image
        binding.qr.setOnClickListener {
            val pickImage = Intent(Intent.ACTION_PICK)
            pickImage.type = "image/*"

            getOfferingContent.launch(pickImage)
        }

        // upload offering information
        binding.offeringUpload.setOnClickListener {
            val id = binding.id.text.toString()

            when {
                id.isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.postingupload_offering),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.id.requestFocus()
                }
                else -> {
                    val fileName = getString(R.string.offering_id)
                    offeringUpload(fileName, id)
                }
            }
        }

        // pick image
        binding.goodsProfile.setOnClickListener {
            val pickImage = Intent(Intent.ACTION_PICK)
            pickImage.type = "image/*"

            getGoodsContent.launch(pickImage)
        }

        // upload goods information
        binding.goodsUpload.setOnClickListener {
            val fileName = binding.goodsName.text.toString()
            val price = binding.goodsPrice.text.toString()

            when {
                fileName.isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.goodsupload_name),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.goodsName.requestFocus()
                }
                price.isEmpty() -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.goodsupload_price),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.goodsPrice.requestFocus()
                }
                else -> {
                    goodsUpload(fileName, price)
                }
            }
        }
    }

    /**
     *  Upload offering information
     */
    private fun offeringUpload(fileName: String, id: String){
        // get firebase storage reference
        val ref = FirebaseStorage.getInstance().getReference("Venmo/$fileName")
        // upload task
        val upload: StorageTask<*>

        if(url.isNotEmpty()){
            val uri = Uri.parse(url)
            upload = ref.putFile(uri)

            upload.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                // get image download url from firebase
                downloadUrl = task.result.toString()

                val offeringMap = hashMapOf(
                    "id" to id,
                    "url" to downloadUrl)

                fireStore.collection("Offering").document(fileName).set(offeringMap)
                    .addOnCompleteListener {
                        Toast.makeText(applicationContext, getString(R.string.uploaded), Toast.LENGTH_SHORT).show()
                        // clear edit text
                        binding.id.text = null
                        binding.qr.setImageResource(android.R.color.transparent)
                    }
            }
        }else{
            Toast.makeText(applicationContext, getString(R.string.goodsupload_image), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Upload goods information
     */
    /**
     *  Upload post
     */
    private fun goodsUpload(fileName: String, price: String){
        // get firebase storage reference
        val ref = FirebaseStorage.getInstance().getReference("Goods/$fileName")
        // upload task
        val upload: StorageTask<*>

        if(url.isNotEmpty()){
            val uri = Uri.parse(url)
            upload = ref.putFile(uri)

            upload.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                // get image download url from firebase
                downloadUrl = task.result.toString()

                val goodsMap = hashMapOf(
                    "name" to fileName,
                    "price" to price,
                    "image" to downloadUrl)

                fireStore.collection("Goods").document(fileName).set(goodsMap)
                    .addOnCompleteListener {
                        Toast.makeText(applicationContext, getString(R.string.uploaded), Toast.LENGTH_SHORT).show()

                        binding.goodsName.text = null
                        binding.goodsPrice.text = null
                        binding.goodsProfile.setImageResource(android.R.color.transparent)
                    }
            }
        }else{
            Toast.makeText(applicationContext, getString(R.string.goodsupload_image), Toast.LENGTH_SHORT).show()
        }
    }
}