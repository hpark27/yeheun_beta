package com.beta.yeheun

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.beta.yeheun.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    // check if permissions are allowed
    private var num: Int = 0
    private lateinit var googleSignInClient: GoogleSignInClient

    private val login = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(applicationContext, getString(R.string.login_signin_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Permission checking activity
        setPermission()

        // initialize authentication
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // google login
        binding.signin.setOnClickListener {
            if(num==1){
                val signInIntent = googleSignInClient.signInIntent
                login.launch(signInIntent)
            }else{
                Toast.makeText(this@LoginActivity, getString(R.string.permission_request), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    /**
     * Set up tedpermission
     */
    private fun setPermission() {
        val permission = object: PermissionListener {
            // permission is granted
            override fun onPermissionGranted() {
                num = 1
            }

            //permission is denied
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // toast message
                Toast.makeText(this@LoginActivity, getString(R.string.permission_request), Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setDeniedMessage(getString(R.string.permission_request))
            .setGotoSettingButtonText(getString(R.string.permission_setting))
            .setPermissions(android.Manifest.permission.INTERNET)
            .check()
    }

    /**
     * When succeed to login, go to  SubMainActivity
     */
    private fun updateUI(currentuser: FirebaseUser?){
        if(currentuser != null){
            val main = Intent(this, MainActivity::class.java)
            startActivity(main)
            finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }
}