package com.example.quakereport

import java.util.*

class Quake(magnitude: Double,city: String, time: Long,details: String) {
    private val mMagnitude = magnitude
    private val mCity = city
    private val mTime = time
    private val mDetails = details

    fun getMagnitude(): Double{
        return mMagnitude
    }

    fun getLocation(): String{
        return mCity
    }

    fun getTimeInMilliSeconds(): Long{
        return mTime
    }

    fun getDeatailsLink(): String{
        return mDetails
    }
}