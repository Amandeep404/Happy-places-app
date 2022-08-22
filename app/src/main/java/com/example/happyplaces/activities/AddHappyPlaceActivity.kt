package com.example.happyplaces.activities

import  android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.happyplaces.R
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happpy_place.*
import org.w3c.dom.Text
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), MultiplePermissionsListener {

    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0
    private var defaultDate : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happpy_place)

        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        etDateAddHappyPlaces.setText( DateFormat.getDateInstance().format(Date()) )

        setSupportActionBar(toolbarAddHappyPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarAddHappyPlace.setNavigationOnClickListener{
            onBackPressed()
        }

        etDateAddHappyPlaces.setOnClickListener{
            popUpDatePicker()
        }

        tvAddImageAddHappyPlaces?.setOnClickListener{
            Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(this)
                .check()

        }

        saveBtnAddHappyPlaces.setOnClickListener{
            saveDataOnClickSaveBtn()
        }


    }

    private fun popUpDatePicker(){
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this,{view, year, monthOfYear, dayOfMonth ->
            defaultDate= (dayOfMonth.toString() + "."  +  (monthOfYear+1) + "."+ year)
            etDateAddHappyPlaces.setText(defaultDate)
        },
            year,
            month,
            day
        )
        datePicker.show()

    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if (report!!.areAllPermissionsGranted()){
            selectAndCaptureImage()
        }
    }

    override fun onPermissionRationaleShouldBeShown(
        permissions: MutableList<PermissionRequest>?,
        token: PermissionToken?
    ) {

        showRationalDialog("Needs Permission", "The App needs this permission to add image")
    }

    private fun showRationalDialog(title:String, message:String){
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel"){ dialog ,_->
                dialog.dismiss()
            }
            .setPositiveButton("Go To Settings"){_,_->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS )
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
        builder.create().show()
    }

    private fun selectAndCaptureImage(){
        val dialog :AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogItems = arrayOf("Select photo from Gallery", "Capture photo from Camera")
        with(dialog){
            setTitle("Select Action")
            setItems(dialogItems){ _, which ->
                when(which){
                    0 -> choosePhotoFromGallery()
                    1 -> takePictureFromCamera()

                }
            }
            show()
        }
    }

    private fun choosePhotoFromGallery(){
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }
    private fun takePictureFromCamera(){
        val gallery = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(gallery, camera)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == pickImage ){
                if (data!=null){
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap =MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        ibAddHappyPlaces.background = null
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        ibAddHappyPlaces.setImageBitmap(selectedImageBitmap)

                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load image from gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                if (requestCode == camera){
                    val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap
                    ibAddHappyPlaces.background = null
                    saveImageToInternalStorage = saveImageToInternalStorage(thumbNail)
                    ibAddHappyPlaces.setImageBitmap(thumbNail)
                }
                }
        }

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }
    private fun saveDataOnClickSaveBtn(){
        if (etTitleAddHappyPlaces.text.isNullOrBlank()
            || etDescriptionAddHappyPlaces.text.isNullOrBlank()
            || etLocationAddHappyPlaces.text.isNullOrBlank()
            || saveImageToInternalStorage==null){
            Toast.makeText(this, "Make sure you fill all details", Toast.LENGTH_SHORT).show()
        }else{
            val happyPlaceModel = HappyPlaceModel(
                0,
                etTitleAddHappyPlaces.toString(),
                saveImageToInternalStorage.toString(),
                etDescriptionAddHappyPlaces.toString(),
                etDateAddHappyPlaces.toString(),
                etLocationAddHappyPlaces.toString(),
                mLatitude,
                mLongitude
            )
            val dbHandler = DatabaseHandler(this)
            val addHappyPlace = dbHandler.addHappyPlaces(happyPlaceModel)

            if(addHappyPlace > 0){
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }


    companion object{
        private const val pickImage = 1
        private const val camera = 2
        private const val IMAGE_DIRECTORY = "Happy Places Images"
    }
}