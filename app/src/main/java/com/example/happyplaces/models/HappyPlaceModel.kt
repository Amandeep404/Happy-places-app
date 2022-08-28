package com.example.happyplaces.models

import java.io.Serializable

data class HappyPlaceModel(
    val id : Int = 0,
    val title : String = "",
    val image : String = "",
    val description : String = "",
    val date : String= "",
    val location : String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0
):Serializable