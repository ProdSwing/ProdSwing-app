package com.c241ps447.prodswing.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.c241ps447.prodswing.R
import com.c241ps447.prodswing.ViewModelFactory
import com.c241ps447.prodswing.databinding.ActivityMainBinding
import com.c241ps447.prodswing.view.fragment.AccountFragment
import com.c241ps447.prodswing.view.fragment.MainFragment
import com.c241ps447.prodswing.view.signin.SignInActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
        loadFragment(MainFragment())

        setupView()
    }

    private fun setupView() {
        binding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.logout -> {
                        //handle logout

                        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                        true
                    }

                    else -> false
                }
            }

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.sentiment -> {
                        loadFragment(MainFragment())
                        true

                    }

                    R.id.account -> {
                        loadFragment(AccountFragment())
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }
}