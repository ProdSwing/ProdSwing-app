package com.dicoding.prodswing.ui.sign_up

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.dicoding.prodswing.databinding.ActivitySignupBinding
import com.dicoding.prodswing.data.firebase.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        initView()
        observeLoading()
    }

    private fun observeLoading() {
        isLoading.observe(this) { isLoading ->
            binding.signupButton.setLoading(isLoading)
        }
    }

    private fun initView() {
        binding.apply {
            signinText.setOnClickListener {
                finish()
            }

            signupButton.setClickListener {
                signUp()
            }
        }
    }

    private fun signUp() {
        val username = binding.inputUsername.text.toString().trim().lowercase()
        val email = binding.inputEmail.text.toString().trim().lowercase()
        val password = binding.inputPassword.text.toString().trim()
        var isValid = true

        if (username.isEmpty()) {
            binding.inputUsername.error = "Username is required"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.inputEmail.error = "Email is required"
            isValid = false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Invalid email address"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.inputPassword.error = "Password is required"
            isValid = false
        }

        if (password.length < 6) {
            binding.inputPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (isValid) {
            isLoading.value = true

            lifecycleScope.launch(Dispatchers.IO) {
                val isUsernameAvailable = firebaseFirestore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                    .isEmpty
                val isEmailAvailable = firebaseFirestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .await()
                    .isEmpty

                if (isUsernameAvailable && isEmailAvailable) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val firebaseUser = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                            firebaseUser.user?.let { user ->
                                val userData = User(
                                    id = user.uid,
                                    email = email,
                                    username = username
                                )
                                firebaseFirestore.collection("users").document(firebaseUser.user?.uid.orEmpty()).set(userData).await()

                                runOnUiThread {
                                    Toast.makeText(this@SignUpActivity, "User created successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(SignUpActivity::class.java.simpleName, "Failed to create user: ${e.message}", e)
                            firebaseAuth.currentUser?.delete()
                            runOnUiThread {
                                isLoading.value = false
                                Toast.makeText(this@SignUpActivity, "Failed to create user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        isLoading.value = false
                        if (!isUsernameAvailable) {
                            binding.inputUsername.error = "Username is already taken"
                        }
                        if (!isEmailAvailable) {
                            binding.inputEmail.error = "Email is already taken"
                        }
                    }
                }
            }
        }
    }
}