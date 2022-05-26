package com.dicoding.picodiploma.loginwithanimation.AddStory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.api.helper
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.picodiploma.loginwithanimation.room.ResultResponseStory
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.listStory.ListStoryAppActivity
import com.google.android.gms.location.FusedLocationProviderClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var user: UserModel
    private var getFile: File? = null
    private var result: Bitmap? = null
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        user = intent.getParcelableExtra(EXTRA_USER)!!

        binding.btnCameraX.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImageStory() }

        binding.etDescription.setOnClickListener { validateDescription() }

        editTextListener()
        enableButtonUpload()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }

    override fun onBackPressed() {
        val moveToListStoryActivity =
            Intent(this@AddStoryActivity, ListStoryAppActivity::class.java)
        moveToListStoryActivity.putExtra(ListStoryAppActivity.EXTRA_USER, user)
        startActivity(moveToListStoryActivity)
        finish()
    }

    private fun startCameraX() {
        launcherIntentCameraX.launch(Intent(this, CameraStoryActivity::class.java))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            result =
                helper.rotateBitmap(
                    BitmapFactory.decodeFile(getFile?.path),
                    isBackCamera
                )
        }
        binding.imgVPreview.setImageBitmap(result)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = helper.uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.imgVPreview.setImageURI(selectedImg)
        }
    }

    private fun validateDescription(): Boolean {
        val emailInput: String = binding.etDescription.text.toString().trim()
        return if (emailInput.isEmpty()) {
            binding.etDescription.error = "Field can't be empty"
            false
        } else {
            binding.etDescription.error = null
            true
        }
    }

    private fun enableButtonUpload() {
        binding.btnUpload.isEnabled =
            binding.etDescription.text.toString().isNotEmpty() &&
                    binding.etDescription.text.toString().length >= 0
    }

    private fun editTextListener() {
        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                enableButtonUpload()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun uploadImageStory() {
        when {
            binding.etDescription.text.toString().isEmpty() -> {
                binding.etDescription.error = getString(R.string.no_attach_file)
            }
            getFile != null -> {
                val file = helper.reduceFileImage(getFile as File)
                val description = binding.etDescription.text.toString()
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                var lat: RequestBody? = null
                var lon: RequestBody? = null
                if (location != null) {
                    lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                    lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                }

                viewModel.uploadImage(user.token, description, imageMultipart, lat, lon)
                    .observe(this) {
                        if (it != null) {
                            when (it) {
                                is ResultResponseStory.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is ResultResponseStory.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    helper.showToast(this, getString(R.string.upload_success))
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.information))
                                        setMessage(getString(R.string.upload_success))
                                        setPositiveButton(getString(R.string.continue_)) { _, _ ->
                                            binding.progressBar.visibility = View.GONE
                                            val moveToListStoryActivity =
                                                Intent(this@AddStoryActivity, ListStoryAppActivity::class.java)
                                            moveToListStoryActivity.putExtra(ListStoryAppActivity.EXTRA_USER, user)
                                            startActivity(moveToListStoryActivity)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }
                                    //finish()
                                }
                                is ResultResponseStory.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    AlertDialog.Builder(this).apply {
                                        setTitle(getString(R.string.information))
                                        setMessage(getString(R.string.upload_failed) + ", ${it.error}")
                                        setPositiveButton(getString(R.string.continue_)) { _, _ ->
                                            binding.progressBar.visibility = View.GONE
                                        }
                                        create()
                                        show()
                                    }
                                }
                            }
                        }
                    }
            }
            else -> {
                helper.showToast(this@AddStoryActivity, getString(R.string.no_attach_file))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    location = it
                    Log.d(TAG, "Lat : ${it.latitude}, Lon : ${it.longitude}")
                } else {
                    helper.showToast(this, getString(R.string.enable_gps_permission))
                    binding.switchLocationPermission.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        Log.d(TAG, "$it")
        if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getMyLocation()
        } else binding.switchLocationPermission.isChecked = false
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private const val TAG = "AddStoryActivity"
        const val EXTRA_USER = "user"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}