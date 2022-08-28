package com.example.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.activities.AddHappyPlaceActivity
import com.example.happyplaces.activities.MainActivity
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.happy_places_item_list.view.*
import org.w3c.dom.Text

class HappyPlacesAdapter(private val context : Context,
                         private val list : ArrayList<HappyPlaceModel>):RecyclerView.Adapter<HappyPlacesAdapter.MyViewHolder>(){
    //click event
    private var onClickListenerForItem : onClickListener? =null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textviewTitle :TextView = view.findViewById(R.id.tvItemTitle)
        val textviewDescription : TextView = view.findViewById(R.id.tvItemDescription)
        val circleImage :ImageView = view.findViewById(R.id.ivCircularImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.happy_places_item_list, parent, false )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        holder.circleImage.setImageURI(Uri.parse(model.image))
        holder.textviewTitle.text = model.title
        holder.textviewDescription.text = model.description

        holder.itemView.setOnClickListener{
            if (onClickListenerForItem!= null){
                onClickListenerForItem!!.onClick(position, model)
            }
        }

    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode : Int){
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    //click event
    fun setOnClickListener(onClickListener: onClickListener){
        this.onClickListenerForItem = onClickListener
    }
    //click event
    interface onClickListener{
        fun onClick(position: Int, model: HappyPlaceModel)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}