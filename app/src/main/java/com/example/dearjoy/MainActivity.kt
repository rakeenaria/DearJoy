package com.example.dearjoy

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.dearjoy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

/*
* Rakeen Aria Alireza 22523252
* Muhammad Ikhsan Nurhadi 22523002
* Umaymatun Az-Zauraa' 22523283
* Novera Prestiana Putri 22523222
* */

/*
* email: joy@gmail.com
* password: enjoythemoment
* */

class MainActivity : AppCompatActivity(),
    Home.OnFragmentInteractionListener,
    EscapeRoom.OnFragmentInteractionListener,
    Settings.OnFragmentInteractionListener,
    Journal.OnFragmentInteractionListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        preferences = Preferences(this)

        if(!preferences.prefStatus){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        replaceFragment(Home())
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsetsCompat.Type.statusBars())
        } else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

        }

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)

            }
        }

        binding.bottomNav.setOnItemSelectedListener{

            when(it.itemId){
                R.id.home -> {
                    replaceFragment(Home())
                    setBottomNavIndicator(true)
                }
                R.id.stat -> {
                    replaceFragment(Stat())
                    setBottomNavIndicator(true)
                }
                R.id.shop -> {
                    replaceFragment(Shop())
                    setBottomNavIndicator(true)
                }
                R.id.settings -> {
                    replaceFragment(Settings())
                    setBottomNavIndicator(true)
                }

                else ->{

                }
            }

            true
        }
    }

    override fun onGoToHome() {
        replaceFragment(Home())
        setBottomNavIndicator(true) // Pastikan indikator navigasi bawah aktif
    }

    override fun onGoToAvatar() {
        replaceFragment(Avatar())
        setBottomNavIndicator(false)
    }

    override fun onGoToEscapeRoom() {
        replaceFragment(EscapeRoom())
        setBottomNavIndicator(false)
    }

    override fun onGoToJournal() {
        replaceFragment(Journal())
        setBottomNavIndicator(false)
    }

    override fun onGoToFeelNFill() {
        replaceFragment(FeelNFill())
        setBottomNavIndicator(false)
    }

    override fun onGoToPopIt() {
        replaceFragment(PopIt())
        setBottomNavIndicator(false)
    }

    override fun onGoToSignIn(){
        preferences.prefClear()

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setBottomNavIndicator(isActive: Boolean) {
        if (isActive) {
            binding.bottomNav.itemActiveIndicatorColor = ContextCompat.getColorStateList(this, R.color.pale_green)
        } else {
            binding.bottomNav.itemActiveIndicatorColor = ContextCompat.getColorStateList(this, R.color.transparent)
        }
    }
    @SuppressLint("ResourceType")

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}