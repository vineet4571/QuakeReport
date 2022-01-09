package com.example.quakereport

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.sql.Date
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import kotlin.math.floor
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity


class QuakeAdapter(context: Context, resource: Int, quakeReport: ArrayList<Quake>) :
    ArrayAdapter<Quake>(context, 0, quakeReport) {
    @SuppressLint("QueryPermissionsNeeded")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.quake, parent, false)
        }

        val currentQuake = getItem(position)

        if (currentQuake != null) {
            val magnitude: TextView? = listItemView?.findViewById(R.id.magnitude_text_view)
            if (magnitude != null) {

                val magnitudeCircle: GradientDrawable = magnitude.background as GradientDrawable
                val magnitudeColor = getMagnitudeColor(currentQuake.getMagnitude())
                magnitudeCircle.setColor(magnitudeColor)
                magnitude.text = formatMagnitude(currentQuake.getMagnitude())
            }

            val location = currentQuake.getLocation()
            val idx = location.indexOf("of")

            var locationOffset = idx.plus(2).let { location.subSequence(0, it) }
            var primaryLocation = idx.plus(2).let { location.subSequence(it, location.length) }

            if (!location.contains("of")) {
                locationOffset = ""
                primaryLocation = location
            }

            val coords: TextView? = listItemView?.findViewById(R.id.city_text_view1)
            if (coords != null) {
                coords.text = locationOffset
            }

            val city: TextView? = listItemView?.findViewById(R.id.city_text_view2)
            if (city != null) {
                city.text = primaryLocation
            }

            val dateObject = currentQuake.let { Date(it.getTimeInMilliSeconds()) }

            val date: TextView? = listItemView?.findViewById(R.id.date)
            if (date != null) {
                date.text = formatDate(dateObject)
            }

            val time: TextView? = listItemView?.findViewById(R.id.time)
            if (time != null) {
                time.text = formatTime(dateObject)
            }
        }

        return listItemView!!
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    @SuppressLint("SimpleDateFormat")
    private fun formatDate(dateObject: Date): String? {
        val dateFormat = SimpleDateFormat("LLL dd, yyyy")
        return dateFormat.format(dateObject)
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    @SuppressLint("SimpleDateFormat")
    private fun formatTime(dateObject: Date): String? {
        val timeFormat = SimpleDateFormat("h:mm a")
        return timeFormat.format(dateObject)
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private fun formatMagnitude(magnitude: Double): String? {
        val magnitudeFormat = DecimalFormat("0.0")
        return magnitudeFormat.format(magnitude)
    }

    private fun getMagnitudeColor(magnitude: Double): Int {

        val magnitudeColorResourceId: Int = when (floor(magnitude).toInt()) {
            0, 1 -> R.color.magnitude1;
            2 -> R.color.magnitude2;
            3 -> R.color.magnitude3;
            4 -> R.color.magnitude4;
            5 -> R.color.magnitude5;
            6 -> R.color.magnitude6;
            7 -> R.color.magnitude7;
            8 -> R.color.magnitude8;
            9 -> R.color.magnitude9;
            else -> R.color.magnitude10plus;
        }
        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }


}