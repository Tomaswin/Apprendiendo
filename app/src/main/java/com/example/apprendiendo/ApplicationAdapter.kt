package com.example.apprendiendo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ApplicationAdapter(private val context: Context, private val dataSource: ArrayList<ApplicationModel>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_application, parent, false)
        // Get title element
        val titleTextView = rowView.findViewById(R.id.tittle_application) as TextView

        // Get subtitle element
        val subtitleTextView = rowView.findViewById(R.id.course_count) as TextView

        // Get detail element
        val detailTextView = rowView.findViewById(R.id.number_count) as TextView

        // Get thumbnail element
        val thumbnailImageView = rowView.findViewById(R.id.recipe_list_thumbnail) as ImageView

        val recipe = getItem(position) as ApplicationModel

        titleTextView.text = recipe.title
        subtitleTextView.text = recipe.description
        detailTextView.text = recipe.course_count

        if(recipe.image != ""){
            Picasso.with(context).load(recipe.image + ".png").placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)
        }
        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}