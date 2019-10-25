package com.example.apprendiendo

import android.content.Context
import android.widget.ListView

interface InterfaceClient{
    fun create()
    fun getApplications(listView: ListView, context: Context)
    fun getCourses(application: String, listView: ListView, context: Context)
}