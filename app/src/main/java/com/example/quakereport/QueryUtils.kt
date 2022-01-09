package com.example.quakereport

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset


class QueryUtils(SAMPLE_JSON_RESPONSE: String) {
    companion object {
        private fun extractFeatureFromJson(earthquakeJSON: String): ArrayList<Quake> {

            // Create an empty ArrayList that we can start adding earthquakes to
            val earthquakes: ArrayList<Quake> = ArrayList()

            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try {
                val obj = JSONObject(earthquakeJSON)
                val features = obj.getJSONArray("features")

                for (feature in 0 until features.length()) {
                    val objectTemp = features.getJSONObject(feature)
                    val objects = objectTemp.getJSONObject("properties")

                    val detailsLink = objects.get("url").toString()
                    val magnitude: Double = objects.getDouble("mag")
                    val time: Long = objects.getLong("time")
                    earthquakes.add(
                        Quake(
                            magnitude,
                            objects["place"].toString(),
                            time,
                            detailsLink
                        )
                    )
                }
                // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
                // build up a list of Earthquake objects with the corresponding data.
            } catch (e: JSONException) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e)
            }

            // Return the list of earthquakes
            return earthquakes
        }


        /**
         * Returns new URL object from the given string URL.
         */
        @SuppressLint("RestrictedApi")
        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            } catch (e: MalformedURLException) {
                Log.e(LOG_TAG, "Problem building the URL ", e)
            }
            return url
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        @SuppressLint("RestrictedApi")
        @Throws(IOException::class)
        private fun makeHttpRequest(url: URL?): String? {
            var jsonResponse = ""

            // If the URL is null, then return early.
            if (url == null) {
                return jsonResponse
            }
            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000
                urlConnection.connectTimeout = 15000
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.responseCode)
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e)
            } finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }
            return jsonResponse
        }

        /**
         * Convert the [InputStream] into a String which contains the
         * whole JSON response from the server.
         */
        @Throws(IOException::class)
        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line: String = reader.readLine()
                while (true) {
                    output.append(line)
                    val p: String? = reader.readLine() ?: break
                    line = p!!
                }
            }
            return output.toString()
        }

        /**
         * Query the USGS dataset and return a list of [Quake] objects.
         */
        @SuppressLint("RestrictedApi")
        fun fetchEarthquakeData(requestUrl: String?): List<Quake>? {
            // Create URL object
            val url = createUrl(requestUrl!!)

            // Perform HTTP request to the URL and receive a JSON response back
            var jsonResponse: String? = null
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e)
            }

            // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s

            // Return the list of {@link Earthquake}s
            return extractFeatureFromJson(jsonResponse!!)
        }
    }

}