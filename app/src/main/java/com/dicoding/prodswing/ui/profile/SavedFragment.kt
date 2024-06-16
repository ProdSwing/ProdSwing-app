package com.dicoding.prodswing.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.dicoding.prodswing.R
import com.dicoding.prodswing.databinding.EditProfileBinding
import com.dicoding.prodswing.data.firebase.model.User
import com.dicoding.prodswing.ui.sign_in.SignInActivity
import com.dicoding.prodswing.util.await
import com.dicoding.prodswing.util.createCustomTempFile
import com.dicoding.prodswing.util.uriToFile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SavedFragment : Fragment() {

    private var _binding: EditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage

    private var firebaseUser: FirebaseUser? = null
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    private var imageFile: File? = null
    private var currentImageFileUri: Uri? = null

    private val imageChooser =
        registerForActivityResult(ImageChooserActivityResultContract()) { pair ->
            if (pair.first) {
                pair.second?.let { imageUri ->
                    imageFile?.let {
                        imageFile = uriToFile(imageUri, it, requireContext())
                        binding.profileImage.load(it) {
                            transformations(CircleCropTransformation())
                        }
                    }
                } ?: kotlin.run {
                    imageFile?.let {
                        binding.profileImage.load(it) {
                            transformations(CircleCropTransformation())
                        }
                    }
                }
            }
        }
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                createFileForCameraImageCapture()
                launchImageChooser(currentImageFileUri)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), android.Manifest.permission.CAMERA
                )
            ) {
                Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
            } else {
                launchImageChooser(null)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseUser = firebaseAuth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = EditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()
        observeLoading()
        fetchProfile()
        initView()
    }

    private fun observeLoading() {
        isLoading.observe(viewLifecycleOwner) { loading ->
            binding.saveButton.setLoading(loading)
        }
    }

    private fun initView() {
        binding.saveButton.setClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            var isValid = true

            if (email.isBlank()) {
                binding.emailEditText.error = "Email adress is required"
                isValid = false
            }

            if (username.isBlank()) {
                binding.usernameEditText.error = "Username is required"
                isValid = false
            }

            if (password.isBlank()) {
                binding.passwordEditText.error = "Please enter your current password"
                isValid = false
            }

            if (isValid) {
                updateProfile()
            }
        }
        binding.profileImage.setOnClickListener {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun updateProfile() {
        Log.d(TAG, "updateProfile: Updating profile")

        val username = binding.usernameEditText.text.toString().trim()
        val fullName = binding.fullNameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        isLoading.value = true
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val currentUser = firebaseFirestore.collection("users")
                    .document(firebaseUser?.uid.orEmpty())
                    .get().await().toObject(User::class.java)

                val isUsernameChanged = currentUser?.username != username
                val isEmailChanged = currentUser?.email != email

                val isUsernameAvailable = if (isUsernameChanged) {
                    firebaseFirestore.collection("users")
                        .whereEqualTo("username", username)
                        .get().await().isEmpty
                } else true

                val isEmailAvailable = if (isEmailChanged) {
                    firebaseFirestore.collection("users")
                        .whereEqualTo("email", email)
                        .get().await().isEmpty
                } else true

                val isPasswordCorrect = try {
                    val credential = EmailAuthProvider.getCredential(firebaseUser?.email.orEmpty(), password)
                    firebaseAuth.currentUser?.reauthenticate(credential)?.await()
                    true
                } catch (e: Exception) {
                    Log.e(TAG, "updateProfile: Incorrect password", e)
                    false
                }

                if (isUsernameAvailable && isEmailAvailable && isPasswordCorrect) {
                    Log.d(TAG, "updateProfile: Username and email are available")

                    if (isEmailChanged) {
                        Log.i(TAG, "updateProfile: Updating email")

                        firebaseUser?.updateEmail(email)?.await()
                    }

                    if (imageFile != null && imageFile?.exists() == true) {
                        Log.i(TAG, "updateProfile: Updating profile picture")

                        val imageUrl = firebaseStorage.getReference("profile_pictures/${firebaseUser?.uid}")
                            .putFile(Uri.fromFile(imageFile)).await().storage.downloadUrl.await().toString()
                        firebaseFirestore.collection("users").document(firebaseUser?.uid.orEmpty())
                            .update("profilePicture", imageUrl).await()
                        firebaseAuth.currentUser?.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(imageUrl)).build()
                        )?.await()
                    }

                    Log.i(TAG, "updateProfile: Updating user data")
                    firebaseFirestore.collection("users").document(firebaseUser?.uid.orEmpty())
                        .update(
                            mapOf(
                                "username" to username,
                                "fullName" to fullName,
                                "email" to email,
                                "phone" to phone
                            )
                        ).await()

                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "updateProfile: Username or email is already taken")

                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        if (!isUsernameAvailable) {
                            binding.usernameEditText.error = "Username is already taken"
                        }
                        if (!isEmailAvailable) {
                            binding.emailEditText.error = "Email is already taken"
                        }
                        if (!isPasswordCorrect) {
                            binding.passwordEditText.error = "Incorrect password"
                        }
                    }
                    return@launch
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    Log.e(TAG, "Error updating profile", e)
                    Toast.makeText(context, "Error updating profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createFileForCameraImageCapture() {
        if (imageFile == null) {
            imageFile = try {
                createCustomTempFile(requireContext())
            } catch (e: Exception) {
                Log.e(TAG, "createFileForCameraImageCapture: Unable to create image file", e)
                null
            }
        }

        imageFile?.let {
            currentImageFileUri =
                FileProvider.getUriForFile(requireContext(), requireActivity().packageName, it)
        } ?: kotlin.run { currentImageFileUri = null }
    }

    private fun launchImageChooser(cameraUri: Uri?) {
        imageChooser.launch(
            Triple(
                cameraUri,
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                "Image Chooser"
            )
        )
    }

    private fun checkUser() {
        if (firebaseUser == null) {
            Toast.makeText(context, "User is logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finishAffinity()
        }
    }

    private fun fetchProfile() {
        isLoading.value = true
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user =
                    firebaseFirestore.collection("users").document(firebaseUser?.uid.orEmpty())
                        .get().await()
                val profile = user.toObject(User::class.java)

                withContext(Dispatchers.Main) {
                    Log.d(TAG, "Profile: $profile")
                    isLoading.value = false

                    binding.fullNameEditText.setText(profile?.fullName)
                    binding.usernameEditText.setText(profile?.username)
                    binding.emailEditText.setText(profile?.email)
                    binding.phoneEditText.setText(profile?.phone)

                    val circularProgressDrawable = CircularProgressDrawable(requireContext())
                    circularProgressDrawable.strokeWidth = 5f
                    circularProgressDrawable.centerRadius = 30f
                    circularProgressDrawable.start()

                    binding.profileImage.load(profile?.profilePicture) {
                        crossfade(true)
                        placeholder(circularProgressDrawable)
                        transformations(CircleCropTransformation())
                        error(R.drawable.ic_placeholder_avatar)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    Log.e(TAG, "Error fetching profile", e)
                    Toast.makeText(context, "Error fetching profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "SavedFragment"
    }
}
