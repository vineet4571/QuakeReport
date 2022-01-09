package com.example.quakereport

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(){

    /** URL for earthquake data from the USGS dataset  */
    private val USGS_REQUEST_URL =
        "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&limit=25"
    lateinit var mEarthquakeAdapter: QuakeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val earthquakeListView = findViewById<ListView>(R.id.list)
        mEarthquakeAdapter = QuakeAdapter(this,android.R.layout.simple_list_item_1, ArrayList())
        earthquakeListView.adapter = mEarthquakeAdapter

        earthquakeListView.onItemClickListener =
            OnItemClickListener { _, _, position, _ -> // Find the current earthquake that was clicked on
                val currentEarthquake: Quake? = mEarthquakeAdapter.getItem(position)

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                val earthquakeUri: Uri = Uri.parse(currentEarthquake?.getDeatailsLink())

                // Create a new intent to view the earthquake URI
                val websiteIntent = Intent(Intent.ACTION_VIEW, earthquakeUri)

                // Send the intent to launch a new activity
                startActivity(websiteIntent)
            }

        // Start the AsyncTask to fetch the earthquake data

        class EarthquakeAsyncTask : AsyncTask<String, Void, List<Quake>>() {
            override fun doInBackground(vararg urls: String): List<Quake>? {
                // Don't perform the request if there are no URLs, or the first URL is null.
                if(urls.isEmpty()){
                    return null
                }

                return QueryUtils.fetchEarthquakeData(urls[0])
            }

            override fun onPostExecute(result: List<Quake>?) {
                if(result!=null && result.isNotEmpty()){
                    mEarthquakeAdapter.addAll(result)
                }
            }

        }

        val task = EarthquakeAsyncTask()
        task.execute(USGS_REQUEST_URL)
    }
}