package com.dicoding.prodswing.ui.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.dicoding.prodswing.R
import com.dicoding.prodswing.data.firebase.model.User
import com.dicoding.prodswing.databinding.ActivitySigninBinding
import com.dicoding.prodswing.ui.main.MainActivity
import com.dicoding.prodswing.ui.sign_up.SignUpActivity
import com.dicoding.prodswing.util.await
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth = Firebase.auth
        firebaseFirestore = FirebaseFirestore.getInstance()

        observeLoading()
        initView()
    }

    private fun initView() {
        binding.signinButton.setClickListener {
            signIn()
        }
        binding.googleSignInButton.setOnClickListener {
            googleSignIn()
        }
        binding.signupText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun googleSignIn() {
        val credentialManager =
            CredentialManager.create(this) //import from androidx.CredentialManager
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    //import from androidx.CredentialManager
                    request = request,
                    context = this@SignInActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Process Login dengan Firebase Auth
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {

        if (firebaseUser != null ) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
        }

    }

    private fun signIn() {
        val emailOrUsername = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        var isValid = true

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
                    firebaseAuth.signInWithEmailAndPassword(user?.email.orEmpty(), password)
                        .addOnCompleteListener {
                            isLoading.postValue(false)
                            if (it.isSuccessful) {
                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finishAffinity()
                            } else {
                                Toast.makeText(
                                    this@SignInActivity,
                                    "Email/Username or password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } catch (e: Exception) {
                    Log.e(SignInActivity::class.java.simpleName, "Error signing in", e)
                    runOnUiThread {
                        isLoading.value = false
                        Toast.makeText(
                            this@SignInActivity,
                            "Email/Username or password is incorrect",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private suspend fun findUserByEmailOrUsername(emailOrUsername: String): User? {
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

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}