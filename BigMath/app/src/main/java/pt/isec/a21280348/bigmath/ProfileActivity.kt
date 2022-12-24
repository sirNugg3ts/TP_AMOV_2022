package pt.isec.a21280348.bigmath

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.text.CaseMap.Title
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import com.google.android.material.snackbar.Snackbar
import pt.isec.a21280348.bigmath.databinding.ActivityProfileBinding
import java.io.ByteArrayOutputStream
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
        var title = "Profile"
        actionBar?.setTitle(title)

        binding.btnTakePhoto.apply {
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

        binding.btnSubmit.setOnClickListener{
            val username : String = binding.usernameTV.text.toString()
            if(username.isNullOrEmpty() || username.isBlank()){ //if didnt inserted a username
                Snackbar.make(binding.root,"Please insert the username!",Snackbar.LENGTH_SHORT).show()
            }else{//if was inserted a username
                val sharedPref = this.getSharedPreferences(getString(R.string.user_file_key),Context.MODE_PRIVATE) ?: return@setOnClickListener
                val drawable = binding.profileImage.drawable as BitmapDrawable
                val imageBitmap = drawable.bitmap
                if(imageBitmap != null) {//if have an image save it

                    //transform the image to base64 string
                    val encondeImage = getEnconded64FromBitmap(imageBitmap)

                    //save in SharedPrefs the base64 image encoded
                    with(sharedPref.edit()){
                        putString(getString(R.string.imageIdent),encondeImage)
                        apply()
                    }

                }
                //save in SharedPrefs the username inserted
                with(sharedPref.edit()){//save username
                    putString(getString(R.string.usernameIdent),username)
                    apply()
                }

                //goes to menu
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }

        verifyPermissions_v3()
        updatePreview()

        val sharedPref = this.getSharedPreferences(getString(R.string.user_file_key),Context.MODE_PRIVATE) ?: return
        if(binding.usernameTV.text.toString().isNullOrEmpty()) {//if the fields are empty fill it

            //read image and username from the SharedPrefs
            val readedName = sharedPref.getString(getString(R.string.usernameIdent), "")
            val readedImage= sharedPref.getString(getString(R.string.imageIdent), "")

            //decode the string fonded
            val decoded64 = Base64.decode(readedImage,Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decoded64,0,decoded64.size)

            //use the data stored.
            binding.profileImage.setImageBitmap(bitmap)
            binding.usernameTV.setText(readedName)
        }
        /*
        var colorDrawable = ColorDrawable(Color.parseColor("#0F9D58"))
        actionBar?.setBackgroundDrawable(colorDrawable)
        Toast.makeText(this, "Circular Image View " + "without using any library", Toast.LENGTH_LONG).show()*/

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
        /*uri?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor !=null && cursor.moveToFirst())
                    imagePath = cursor.getString(0)
                updatePreview()
        }*/
        imagePath = uri?.let {createFileFromUri(this, it)}
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

    fun getEnconded64FromBitmap(bitmap: Bitmap): String{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream)
        val byteFormat = stream.toByteArray()

        return Base64.encodeToString(byteFormat,Base64.NO_WRAP)
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