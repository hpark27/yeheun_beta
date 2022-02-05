package com.beta.yeheun

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.beta.yeheun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    // initial banner position
    // 인덱스값이 아이템 수의 절반, 딱 중간쯤에서 시작하도록 해 앞뒤 어디로 이동해도 무한대처럼 보이게 함
    private var currentPosition = Int.MAX_VALUE / 2
    // list of banner
    private var list = mutableListOf(R.drawable.yeheun, R.drawable.worship, R.drawable.mission_trip,
            R.drawable.goods, R.drawable.meeting)
    // Firebase oath
    private lateinit var auth: FirebaseAuth
    // announcement content
    private lateinit var content: String
    // bulletin uri
    private lateinit var bulletin: String
    // application uri
    private lateinit var application: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // connect banner layout using adapter
        binding.bannerSubLayout.adapter = ViewPagerAdapter(list)
        // define current position
        binding.bannerSubLayout.setCurrentItem(currentPosition, true)
        // initialize navigation drawer
        binding.mainNavigation.setNavigationItemSelectedListener(this)

        // initialize authentication
        auth = FirebaseAuth.getInstance()
        // initialize the reference to firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Admin").child("MainNotice")

        ref.get().addOnSuccessListener {
            content = it.value.toString()

            if(content.isNotEmpty()){
                // set alarm symbol visible
                binding.announceContentText.text=content

            }else{
                val content = resources.getString(R.string.main_announce_none)
                binding.announceContentText.text= content
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
        }

        // change current banner position number
        binding.bannerSubLayout.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val num = position%5+1
                    val numText = num.toString()
                    binding.indicatorStart.text = numText
                }
            })
        }

        // open drawer
        binding.menu.setOnClickListener {
            binding.mainDrawer.openDrawer(GravityCompat.START)
        }

        // change the visibility of announcement content
        // and rotate arrow
        binding.moreLayout.setOnClickListener {
            if(binding.announceContent.visibility == View.VISIBLE){
                binding.announceContent.visibility = View.GONE
                binding.announceArrow.setImageResource(R.drawable.down_arrow)
            }else{
                binding.announceContent.visibility = View.VISIBLE
                binding.announceArrow.setImageResource(R.drawable.up_arrow)
            }
        }

        // read announce content
        binding.announceMore.setOnClickListener {
            val notice = Intent(this, NoticeViewActivity::class.java)
            startActivity(notice)
        }

        // read bulletin
        binding.bulletin.setOnClickListener {
            readBulletin()
        }

        // offering
        binding.offering.setOnClickListener {
            val offering = Intent(this, OfferingActivity::class.java)
            startActivity(offering)
        }

        // goods
        binding.goods.setOnClickListener {
            val goods = Intent(this, GoodsViewActivity::class.java)
            startActivity(goods)
        }

        // open google form
        binding.application.setOnClickListener {
            openGoogleform()
        }

        // open Youtube app
        binding.youtube.setOnClickListener {
            openYoutube()
        }

        // open Instagram app
        binding.instagram.setOnClickListener {
            openInstagram()
        }
    }

    /**
     * Navigation menu item is selected
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit -> login()

            R.id.logout -> logOut()
        }

        // close navigation bar
        binding.mainDrawer.closeDrawers()

        return true
    }

    /**
     *  Get Pdf Url
     */
    private fun readBulletin(){
        // reference to firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Admin").child("Bulletin")

        ref.get().addOnSuccessListener {
            bulletin = it.value.toString()

            if(bulletin.isEmpty()){
                Toast.makeText(applicationContext, getString(R.string.main_bulletin_empy), Toast.LENGTH_SHORT).show()
            }else{
                val read = Intent(Intent.ACTION_VIEW, Uri.parse(bulletin))
                startActivity(read)
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, getString(R.string.main_bulletin_fail), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     *  Open Google Form
     */
    private fun openGoogleform(){
        // reference to firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Admin").child("Application")

        ref.get().addOnSuccessListener {
            application = it.value.toString()

            if(application.isEmpty()){
                Toast.makeText(applicationContext,getString(R.string.main_googleform_empy), Toast.LENGTH_SHORT).show()
            }else{
                val read = Intent(Intent.ACTION_VIEW, Uri.parse(application))
                startActivity(read)
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext,getString(R.string.main_googleform_fail), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     *  Go to login activity
     */
    private fun login(){
        val login = Intent(this, SubLoginActivity::class.java)
        startActivity(login)
    }

    /**
     * Logout intent
     */
    private fun logOut(){
        // get current user
        var user = auth.currentUser

        if(user!=null){
            auth.signOut()

            // get current user
            user = auth.currentUser

            if(user == null){
                Toast.makeText(applicationContext,"Logout successful",Toast.LENGTH_SHORT).show()
            }else{
                Toast.
                makeText(applicationContext,"Something went wrong\nPlease try again",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(applicationContext,"Please sign in first",Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Open Youtube App
     */
    private fun openYoutube(){
        // get string from string resource
        val themarks = getString(R.string.lolmc_youtube)
        // get uri from string
        val themarksUri: Uri = Uri.parse(themarks)
        // intent
        val themarksIntent = Intent(Intent.ACTION_VIEW,themarksUri)
        // app package name
        val youtubePackage = getString(R.string.youtube_package)
        // app chooser title
        val appOpenTitle: String = getString(R.string.app_open)
        themarksIntent.setPackage(youtubePackage)
        // try catch with youtube package

        try {
            // when youtube app is installed
            startActivity(themarksIntent)
        } catch (e: ActivityNotFoundException) { // when youtube app does not exist
            // webview intent
            val themarksWebIntent = Intent(Intent.ACTION_VIEW, themarksUri)
            // create app chooser
            val appChooserYoutube: Intent =
                    Intent.createChooser(themarksWebIntent, appOpenTitle)
            startActivity(appChooserYoutube)
        }
    }

    /**
     * Open Instagram App
     */
    private fun openInstagram(){
        // get string from string resource
        val instagram = getString(R.string.lolmc_instagram)
        // get uri from string
        val instagramUri: Uri = Uri.parse(instagram)
        // intent
        val instagramIntent = Intent(Intent.ACTION_VIEW, instagramUri)
        // instagram package name
        val instagramPackage = getString(R.string.instagram_package)
        // app chooser title
        val appOpenTitle: String = getString(R.string.app_open)
        instagramIntent.setPackage(instagramPackage)
        // try catch with instagram package
        try {
            // when youtube app is installed
            startActivity(instagramIntent)
        } catch (e: ActivityNotFoundException) { // when instagram app does not exist
            // webview intent
            val instagramWebIntent = Intent(Intent.ACTION_VIEW, instagramUri)
            // create app chooser
            val appChooserInstagram: Intent =
                    Intent.createChooser(instagramWebIntent, appOpenTitle)
            startActivity(appChooserInstagram)
        }
    }
}