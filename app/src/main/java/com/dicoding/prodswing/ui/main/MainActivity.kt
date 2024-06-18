package com.dicoding.prodswing.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.prodswing.R
import com.dicoding.prodswing.databinding.ActivityMainBinding
import com.dicoding.prodswing.ui.profile.SavedFragment
import com.dicoding.prodswing.ui.sign_in.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar
//        supportActionBar?.hide()

        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SentimentFragment())
                .commit()
        }

        // Handle floating action button click

        firebaseAuth = FirebaseAuth.getInstance()

        setupNavBar()
        checkUser()
    }

    private fun setupNavBar() {
        binding.apply {
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.logout -> {
                        // Handle more item (inside overflow menu) press
                        signOut()
                        true
                    }

                    else -> false
                }
            }
            // Set background to null
            bottomNavigationView.background = null
            bottomNavigationView.setOnItemSelectedListener { item ->
                var selectedFragment: Fragment? = null

                when (item.itemId) {
                    R.id.nav_sentiment -> selectedFragment = SentimentFragment()
                    R.id.nav_category -> selectedFragment = HomeFragment()
                    R.id.nav_saved -> selectedFragment = SavedFragment()
                }

                if (selectedFragment != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit()
                }
                true
            }
        }
    }

    private fun checkUser() {
        lifecycleScope.launch {
            firebaseAuth.currentUser?.reload()?.await()
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                // User not logged in
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "User is logged out", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }

    private fun signOut() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(this@MainActivity)

            firebaseAuth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
            finishAffinity()
        }
    }
}
