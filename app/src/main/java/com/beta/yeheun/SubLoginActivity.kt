package com.beta.yeheun

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.beta.yeheun.databinding.ActivitySubloginBinding

class SubLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubloginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Permission checking activity
        setPermission()

        // go back
        binding.back.setOnClickListener{
            finish()
        }

        // go to manage activity
        binding.login.setOnClickListener {
            logIn()
        }
    }

    /**
     * Set up tedpermission
     */
    private fun setPermission() {
        val permission = object: PermissionListener {
            // permission is granted
            override fun onPermissionGranted() {
                // toast message
                Toast.makeText(this@SubLoginActivity, getString(R.string.permission_grant),Toast.LENGTH_SHORT).show()
            }

            //permission is denied
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // toast message
                Toast.makeText(this@SubLoginActivity, getString(R.string.permission_request), Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
                .setPermissionListener(permission)
                .setDeniedMessage(getString(R.string.permission_request))
                .setGotoSettingButtonText(getString(R.string.permission_setting))
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
    }

    /**
     *  Login
     */
    private fun logIn(){
        // get password
        val text = binding.password.text.toString()

        // reference to firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Admin").child("Password")

        ref.get().addOnSuccessListener {
            val password = it.value.toString()

            if(text.isEmpty()){
                Toast.makeText(applicationContext, getString(R.string.login_password_empty), Toast.LENGTH_SHORT).show()
                binding.password.requestFocus()
            }else {
                if(text == password){
                    val manage = Intent(this, UploadActivity::class.java)

                    startActivity(manage)
                    finish()
                }else{
                    Toast.makeText(applicationContext,getString(R.string.login_password_error),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}