package com.c241ps447.prodswing.view.activity.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.c241ps447.prodswing.databinding.ActivitySignInBinding
import com.c241ps447.prodswing.view.activity.main.MainActivity
import com.c241ps447.prodswing.view.activity.signup.SignUpActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        binding.apply {
            signUpTextView.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                finish()
            }

            loginButton.setOnClickListener {
                showLoading(true)
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                showLoading(false)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressIndicator.visibility =
            View.VISIBLE else binding.progressIndicator.visibility = View.GONE
    }
}