package com.michlig.fetchrewardsexercise.view

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fetchrewardsexercise.databinding.ActivityMainBinding
import com.michlig.fetchrewardsexercise.view_model.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val binding get() = _binding
    private lateinit var _binding: ActivityMainBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkForInternetConnection()


        //observer that updates recycler once data is retrieved
        mainActivityViewModel.dataList.observe(this){
            binding.listRecycler.apply{
                adapter = ListRecyclerViewAdapter(it)
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.HORIZONTAL))
            }
        }
    }

    //function to check for internet, otherwise display dialog
    private fun checkForInternetConnection(){
        if(mainActivityViewModel.internetConnected(this)) {
            lifecycleScope.launch(Dispatchers.Default) {
                mainActivityViewModel.retrieveJsonFromWeb()
            }
        }else{
            val noInternetDialog = AlertDialog.Builder(this)
            noInternetDialog.setTitle("No Internet Available")
                .setMessage("You must be connected to the internet to retrieve data")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    checkForInternetConnection()
                }
                .show()
        }
    }


//    private fun parseJsonData(){
//
//        Log.e("debugging", jsonData!!.length().toString())
//        val size = jsonData!!.length()
//        var entry: JSONObject?
//
//        for(i in 0 until size){
//            entry = jsonData!!.get(i) as JSONObject
//            val listID = entry.getInt("listId")
//            val id = entry.getInt("id")
//            val name: String? = entry.optString("name")
//            if(name != null && name != "null" && name.isNotBlank()){
//                if(!entryList.containsKey(listID)){
//                    entryList[listID] = ListIdEntry(listID, mutableListOf(ListEntry(id, listID, name)))
//                }else{
//                    entryList[listID]!!.listEntries.add(ListEntry(id, listID, name))
//                }
//            }
//        }
//    }
//
//    private fun getDataFromWeb()


}