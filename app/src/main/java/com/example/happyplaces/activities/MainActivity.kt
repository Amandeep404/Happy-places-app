package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.R
import com.example.happyplaces.adapters.HappyPlacesAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabAddHappyPlace.setOnClickListener{
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }
        getHappyPlacesListFromLocalDB()
    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler  = DatabaseHandler(this)
        val getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        if (getHappyPlaceList.size > 0){
           tvNoRecordsAvailable.visibility = View.INVISIBLE
            rvHappyPlacesList.visibility = View.VISIBLE
            setUpHappyPlacesRecyclerView(getHappyPlaceList)
        }else{
            tvNoRecordsAvailable.visibility = View.VISIBLE
            rvHappyPlacesList.visibility = View.INVISIBLE
        }

    }

    private fun setUpHappyPlacesRecyclerView(happyPlaceList : ArrayList<HappyPlaceModel>){
        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        rvHappyPlacesList.adapter = placesAdapter
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == HAPPY_PLACES_RESULT){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }else{
                Log.i("Activity", "Cancelled or Back pressed")
            }
        }
    }

    companion object{
        const val HAPPY_PLACES_RESULT = 1

    }
}

