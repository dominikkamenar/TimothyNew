package com.timothy

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import java.io.*
import java.util.*


class ImagePickerActivity : AppCompatActivity() {

    private lateinit var removeBgBtn : Button
    private lateinit var img : ImageView
    private lateinit var img2 : ImageView
    private lateinit var img3 : ImageView
    private lateinit var go_to_activity : Button

    private var image = ""
    private var imagePicked = ""

    private val REQUEST_CODE = 100

    var uri1: Uri? = null
    var bit: Bitmap? = null

    var i = 0;

    private val imageResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { uri ->
                img.setImageURI(uri)
                uri1 = uri

                removeBg()
            }
        }

    val context = this

    var already = false
    var counter = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        val w: Window = this@ImagePickerActivity.getWindow()
        MainActivity.changeStatusBarContrastStyle(w, false)
        setContentView(R.layout.activity_image_picker)

        val intent = intent
        image = intent.getStringExtra("image")!!
        imagePicked = intent.getStringExtra("imagePicked")!!

        //checkPermission()
        //requestPermission()

        //imageResult.launch("image/*")

        //pickFromGallery()

        removeBgBtn = findViewById(R.id.removeBgBtn);
        img = findViewById(R.id.img);
        go_to_activity = findViewById(R.id.go_to_activity);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);

        img.setImageURI(Uri.parse(imagePicked))

        removeBg()

        /*removeBgBtn.setOnClickListener {
            removeBg()
        }*/
    }

    private fun pickFromGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            REQUEST_CODE
        )
    }

    private fun verifyPermissions() {
        Log.d(
            TAG,
            "verifyPermissions: asking user for permissions"
        )
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    permissions[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //crop image
            } else {
                ActivityCompat.requestPermissions(
                    this@ImagePickerActivity,
                    permissions,
                    REQUEST_CODE
                )
            }
        } else {
            //CROP IMAGE
        }
    }

    private fun removeBg() {

        val intent1 = Intent(this, ImageCombinerActivity::class.java)

        img.invalidate()
        BackgroundRemover.bitmapForProcessing(
            img.drawable.toBitmap(),
            true,
            object : OnBackgroundChangeListener {
                override fun onSuccess(bitmap: Bitmap) {
                    img.setImageBitmap(bitmap)
                    bit = bitmap
                    img.buildDrawingCache()
                    val bmap: Bitmap = img.getDrawingCache()

                    //createImageFromBitmap(overlayBitmapToCenter(img2.drawable.toBitmap(400, 400), img.drawable.toBitmap(300, 300))!!)

                    if (i == 2) {
                        val overlayBitmap = Bitmap.createBitmap(img.width, img.height, bitmap.config)
                        val canvas = Canvas(overlayBitmap)
                        canvas.drawBitmap(bmap, Matrix(), null)

                        img.setImageBitmap(bmap)

                        createImageFromBitmap(img.drawable.toBitmap(img.width, img.height))

                        intent1.putExtra("image", image)
                        startActivity(intent1)
                        finish()
                    } else {
                        i += 1;
                        removeBg()
                    }
                }

                override fun onFailed(exception: Exception) {
                    Toast.makeText(this@ImagePickerActivity, "Error Occurred :(", Toast.LENGTH_SHORT).show()
                }

            })
    }

    fun takeScreenshot(): Bitmap? {
        val rootView: View = findViewById<View>(android.R.id.content).rootView
        rootView.setDrawingCacheEnabled(true)
        return rootView.getDrawingCache()
    }

    fun saveBitmap(bitmap: Bitmap) {
        var fileName: String? = "myImage"
        val fos: FileOutputStream
        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE)
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }
    }

    fun overlayBitmapToCenter(bitmap1: Bitmap, bitmap2: Bitmap): Bitmap? {
        val bitmap1Width = bitmap1.width
        val bitmap1Height = bitmap1.height
        val bitmap2Width = bitmap2.width
        val bitmap2Height = bitmap2.height
        val marginLeft = (bitmap1Width * 0.5 - bitmap2Width * 0.5).toFloat()
        val marginTop = (bitmap1Height * 0.5 - bitmap2Height * 0.5).toFloat()
        val overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.config)
        val canvas = Canvas(overlayBitmap)
        canvas.drawBitmap(bitmap1, Matrix(), null)
        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null)
        return overlayBitmap
    }

    fun createImageFromBitmap(bitmap: Bitmap): String? {
        var fileName: String? = "myImage" //no .png or .jpg needed
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

            val fo: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            fo.write(bytes.toByteArray())
            // remember close file output
            fo.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            fileName = null
        }
        return fileName
    }

    private fun saveImage(img: ImageView) {
        val image: Uri
        val contentResolver = contentResolver
        image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            System.currentTimeMillis().toString() + ".jpg"
        )
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*")
        val uri = contentResolver.insert(image, contentValues)
        try {
            val bitmapDrawable = img.getDrawable() as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            val outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri)!!)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Objects.requireNonNull(outputStream)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage(img)
            }
        }
    }

    protected fun getImageData(img: Bitmap): Bitmap {
        val w = img.width
        val h = img.height
        val data = IntArray(w * h)
        img.getPixels(data, 0, w, 0, 0, w, h)
        return img
    }

    fun changeStatusBarContrastStyle(window: Window, lightIcons: Boolean) {
        val decorView = window.decorView
        if (lightIcons) {
            // Draw light icons on a dark background color
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            // Draw dark icons on a light background color
            decorView.systemUiVisibility =
                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}