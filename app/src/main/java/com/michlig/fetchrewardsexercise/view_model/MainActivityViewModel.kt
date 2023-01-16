package com.michlig.fetchrewardsexercise.view_model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.michlig.fetchrewardsexercise.model.ListEntry
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    val dataList: LiveData<List<List<ListEntry>>> get() = _dataList
    private var _dataList = MutableLiveData<List<List<ListEntry>>>()

    val error: LiveData<Boolean> get() = _error
    private var _error = MutableLiveData<Boolean>()

    fun setError(boolean: Boolean){
        _error.postValue(boolean)
    }

    //function to retrieve JSON data from web endpoint
    fun retrieveJsonFromWeb(){
        var text = ""
        var connection: HttpURLConnection? = null
        var bufferedReader: BufferedReader? = null
        try {
            //open connection to web url
            val jsonURL = URL(JSON_URL)
            connection = jsonURL.openConnection() as HttpURLConnection
            connection.connect()

            //read in data from url
            val stream = connection.inputStream
            bufferedReader = BufferedReader(InputStreamReader(stream))
            text = bufferedReader.lines().collect(Collectors.joining(""))
        }catch(e: Exception){
            Log.e(TAG, e.toString())
            setError(true)
        }finally{
            connection?.disconnect()
            bufferedReader?.close()
        }
        if(text.isNotBlank()){
            try{
                parseJsonData(JSONArray(text))
            }catch(e: JSONException){
                Log.e(TAG, e.toString())
                setError(true)
            }

        }else{
            setError(true)
        }
    }

    //function to parse JSON data into a list of list of ListEntries to be displayed by
    //nested recyclerViews
    private fun parseJsonData(json: JSONArray){
        val size = json.length()

        //iterate to determine how many list IDs are in the JSON
        var maxID = 0
        var curID: Int
        var entry: JSONObject?
        for(i in 0 until size){
            entry = json.get(i) as JSONObject
            curID = entry.getInt("listId")
            if(curID > maxID){
                maxID = curID
            }
        }

        //create list of lists of correct size
        val list = List(maxID){ mutableListOf<ListEntry>()}


        //iterate to parse JSON into an array list of lists that can be used as a backing
        //for a recyclerView
        for(i in 0 until size) {
            entry = json.get(i) as JSONObject
            val listID = entry.getInt("listId")
            val id = entry.getInt("id")

            val name: String? = if(entry.isNull("name")){
                null
            }else {
                entry.optString("name")
            }

            if (name != null && name.isNotBlank()) {
                list[listID - 1].add(ListEntry(id, listID, name))
            }
        }
        _dataList.postValue(list)
    }

    //check if the device has an internet connection
    fun internetConnected(context: Context): Boolean{
        val connectivityManager: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        return if(connectivityManager != null){
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        }else{
            false
        }
    }

    companion object{
        const val TAG = "MainActivityViewModel"
        const val JSON_URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
    }
}