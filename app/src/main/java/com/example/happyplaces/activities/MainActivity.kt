package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.adapters.HappyPlacesAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import com.example.happyplaces.utils.SwipeToDeleteCallBack
import com.example.happyplaces.utils.SwipeToEditCallBack
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
        var dbHandler : DatabaseHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler  = DatabaseHandler(this)

        fabAddHappyPlace.setOnClickListener{
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE )
        }
        getHappyPlacesListFromLocalDB()
    }

    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler  = DatabaseHandler(this)
        var getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()


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

        //click event
        placesAdapter.setOnClickListener(object :HappyPlacesAdapter.onClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
               var intent=  Intent(this@MainActivity, HappyPlaceDetailsActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallBack(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }

        val deleteSwipeHandler = object  : SwipeToDeleteCallBack(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)

        val deleteItemHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemHelper.attachToRecyclerView(rvHappyPlacesList)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }else{
                Log.i("Activity", "Cancelled or Back pressed")
            }
        }
    }

    companion object{

        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS : String? = "extra_place_details"

    }
}

