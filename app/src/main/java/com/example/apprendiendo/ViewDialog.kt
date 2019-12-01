package com.example.apprendiendo

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget


class ViewDialog(activity: Activity){
    var activity: Activity = activity
    lateinit var dialog: Dialog

    fun showDialog() {

        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.loading_spinner)

        //...initialize the imageView form infalted layout
        val gifImageView = dialog.findViewById<ImageView>(R.id.custom_loading_imageView)

        /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
        val imageViewTarget = GlideDrawableImageViewTarget(gifImageView)

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        Glide.with(activity)
            .load(R.drawable.spinner)
            .placeholder(R.drawable.spinner)
            .centerCrop()
            .crossFade()
            .into(imageViewTarget)

        dialog.show()
    }

    fun hideDialog() {
        dialog.dismiss()
    }

}