package com.c241ps447.prodswing.view.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.c241ps447.prodswing.databinding.ActivitySignUpBinding
import com.c241ps447.prodswing.view.signin.SignInActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        binding.apply {

            signUpButton.setOnClickListener {
                showLoading(true)
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                showLoading(false)
                finish()
            }

            gologinTextView.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressIndicator.visibility =
            View.VISIBLE else binding.progressIndicator.visibility = View.GONE
    }
}