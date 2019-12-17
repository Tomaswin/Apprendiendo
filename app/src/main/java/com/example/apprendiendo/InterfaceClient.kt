package com.example.apprendiendo

import android.content.Context
import android.widget.ListView
import javax.security.auth.Subject

interface InterfaceClient{
    fun create()
    fun getApplications(listView: ListView, context: Context, viewDialog: ViewDialog)
    fun getCourses(application: String, listView: ListView, context: Context, viewDialog: ViewDialog)
    fun getSteps(course: String, context: Context): ArrayList<StepsModel>
    fun sendEmail(from: String, subject: String, to: String, content: String)
}