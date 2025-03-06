package fr.isen.delcasse.isensmartcompanion.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.delcasse.isensmartcompanion.data.Course

class SharedPreferencesHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("SelectedCourses", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCourses(courses: List<Course>) {
        val json = gson.toJson(courses)
        prefs.edit().putString("selected_courses", json).apply()
    }

    fun getCourses(): List<Course> {
        val json = prefs.getString("selected_courses", null) ?: return emptyList()
        val type = object : TypeToken<List<Course>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearCourses() {
        prefs.edit().remove("selected_courses").apply()
    }
}
