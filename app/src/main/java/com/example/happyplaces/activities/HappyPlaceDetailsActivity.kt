package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_happpy_place_details.*

class HappyPlaceDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happpy_place_details)

        var happyPlaceDetailModel : HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetailModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (happyPlaceDetailModel != null){
            setSupportActionBar(toolbarHappyPlaceDetails)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title  = happyPlaceDetailModel.title

            toolbarHappyPlaceDetails.setNavigationOnClickListener{
                onBackPressed()
            }

            ivHappyPlaceDetailsImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            tvDescriptionHappyPlaceDetails.text = happyPlaceDetailModel.description
            tvLocationHappyPlaceDetails.text = happyPlaceDetailModel.location
        }
    }
}