package pt.isec.a21280348.bigmath

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.CaseMap.Title
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import pt.isec.a21280348.bigmath.databinding.ActivityProfileBinding
import java.io.File

class ProfileActivity : AppCompatActivity() {

    companion object {
        private val TAG = "ProfileActivity"
        private const val GALLERY = 1
        private const val CAMERA  = 2
    }

    lateinit var binding : ActivityProfileBinding
    private var actionBar: ActionBar? = null

    private var mode = GALLERY
    private var imagePath : String? = null
    private var permissionsGranted = false
        set(value) {
            field = value
            binding.btnChooseImage.isEnabled = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar
        actionBar?.setTitle(R.string.profile)

        binding.btnTakePhoto.apply {
            mode = CAMERA
            setOnClickListener {
                if(checkCameraHardware(this.context))
                    takePhoto_v2()
                else{
                    Toast.makeText(this.context,"Your device doesn't support Camara actions!",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnChooseImage.apply {
            setOnClickListener { chooseImage_v3() }
        }

        verifyPermissions_v3()
        updatePreview()

    }

    fun updatePreview() {
        if (imagePath != null)
            setPic(binding.profileImage, imagePath!!)
        else
            binding.profileImage.background = ResourcesCompat.getDrawable(resources,
                R.drawable.profile_picture, //android.R.drawable.ic_menu_report_image,
                null)
    }

    // para a v3
    var startActivityForContentResult = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        Log.i(TAG, "startActivityForContentResult: ")

        imagePath = uri?.let { createFileFromUri(this, it) }
        updatePreview()
    }

    fun chooseImage_v3() {
        mode = CAMERA
        Log.i(TAG, "chooseImage_v3: ")
        startActivityForContentResult.launch("image/*")
    }

    var startActivityForTakePhotoResult = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        Log.i(TAG, "startActivityForTakePhotoResult: $success")
        if (!success)
            imagePath = null
        updatePreview()
    }

    fun takePhoto_v2() {
        mode = CAMERA
        imagePath = getTempFilename(this)
        Log.i(TAG, "takePhoto: $imagePath")
        startActivityForTakePhotoResult.launch(
            FileProvider.getUriForFile( this,
            "pt.isec.a21280348.bigmath.android.fileprovider", File(imagePath)
            ))
    }


    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionsGranted = isGranted
    }


    fun verifyPermissions_v3() {
        Log.i(TAG, "verifyPermissions_v3: ")
        if (mode == CAMERA) {
            permissionsGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionsGranted)
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            return
        }
        //mode == GALLERY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionsGranted)
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            return
        }
        // GALLERY, versões < API33
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED /*||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED */
        ) {
            permissionsGranted = false
            requestPermissionsLauncher.launch(
                arrayOf(
                    //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else
            permissionsGranted = true
    }

    val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
        permissionsGranted = grantResults.values.any { it }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }


}