package com.example.apprendiendo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService




class ContactFragment(val title: String) : Fragment() {
    var interfaceClient: InterfaceClient = RestClientCall()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        interfaceClient.create()
        // Get the custom view for this fragment layout
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        val tv = view.findViewById<EditText>(R.id.title)
        tv.hint = title
        val btn = view.findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val from = "app@Apprendiendo.app"
            val subject = "App"
            val to = "app@Apprendiendo.app"
            val content = tv.text.toString()
            var response = interfaceClient.sendEmail(from,subject,to,content)

            if(response != null){
                getFragmentManager()?.beginTransaction()?.remove(getFragmentManager()?.findFragmentById(R.id.flContent)!!)?.commit()
                val imm =
                    activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(getView()!!.windowToken, 0)
            }
        }
        return view
    }
}
