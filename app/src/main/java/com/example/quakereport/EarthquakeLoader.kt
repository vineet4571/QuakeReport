package com.example.quakereport

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

class EarthquakeLoader(context: Context,url: String) : AsyncTaskLoader<List<Quake>>(context) {

    private val mUrl = url

    override fun onStartLoading() {
        forceLoad()
    }


    override fun loadInBackground(): List<Quake>? {
        return QueryUtils.fetchEarthquakeData(mUrl)

    }

}