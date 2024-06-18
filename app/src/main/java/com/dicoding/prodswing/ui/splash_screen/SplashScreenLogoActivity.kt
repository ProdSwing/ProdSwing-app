package com.dicoding.prodswing.ui.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.dicoding.prodswing.databinding.ActivitySplashScreenLogoBinding
import com.dicoding.prodswing.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenLogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenLogoBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivitySplashScreenLogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null) {
            navigateToMainActivity()
        } else {
            navigateToSplashScreenActivity()
        }
    }

    private fun navigateToSplashScreenActivity() {
        lifecycleScope.launch {
            delay(SPLASH_SCREEN_DURATION)
            val intent = Intent(this@SplashScreenLogoActivity, SplashScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToMainActivity() {
        lifecycleScope.launch {
            delay(SPLASH_SCREEN_DURATION)
            val intent = Intent(this@SplashScreenLogoActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val SPLASH_SCREEN_DURATION = 2000L
    }
}