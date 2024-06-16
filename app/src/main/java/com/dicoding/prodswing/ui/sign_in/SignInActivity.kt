package com.dicoding.prodswing.ui.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.dicoding.prodswing.databinding.ActivitySigninBinding
import com.dicoding.prodswing.model.User
import com.dicoding.prodswing.ui.main.MainActivity
import com.dicoding.prodswing.ui.sign_up.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        observeLoading()
        initView()
    }

    private fun initView() {
        binding.signinButton.setClickListener {
            signIn()
        }
        binding.signupText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn() {
        val emailOrUsername = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        var isValid  = true

        if (emailOrUsername.isEmpty()) {
            binding.inputEmail.error = "Email or Username is required"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.inputPassword.error = "Password is required"
            isValid = false
        }

        if (isValid) {
            isLoading.value = true

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val user = findUserByEmailOrUsername(emailOrUsername)
                    firebaseAuth.signInWithEmailAndPassword(user?.email.orEmpty(), password).addOnCompleteListener {
                        isLoading.postValue(false)
                        if (it.isSuccessful) {
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            Toast.makeText(this@SignInActivity, "Email/Username or password is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(SignInActivity::class.java.simpleName, "Error signing in", e)
                    runOnUiThread {
                        isLoading.value = false
                        Toast.makeText(this@SignInActivity, "Email/Username or password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    suspend fun findUserByEmailOrUsername(emailOrUsername: String): User? {
        val firebaseFirestore = FirebaseFirestore.getInstance()

        // Query by email
        val emailQuery = firebaseFirestore.collection("users")
            .whereEqualTo("email", emailOrUsername)
            .get()
            .await()
            .toObjects(User::class.java)

        // Query by username
        val usernameQuery = firebaseFirestore.collection("users")
            .whereEqualTo("username", emailOrUsername)
            .get()
            .await()
            .toObjects(User::class.java)

        // Combine results
        val combinedResults = (emailQuery + usernameQuery).distinct()

        // Return the first result or null if no match is found
        return combinedResults.firstOrNull()
    }

    private fun observeLoading() {
        isLoading.observe(this) {
            binding.signinButton.setLoading(it)
        }
    }
}