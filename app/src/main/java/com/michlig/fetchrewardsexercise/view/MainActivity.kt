package com.michlig.fetchrewardsexercise.view

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fetchrewardsexercise.R
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
                addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            }
        }

        //observer that displays error message if data is not retrieved
        mainActivityViewModel.error.observe(this){
            if(it) {
                val errorDialog = AlertDialog.Builder(this)
                errorDialog.setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_message))
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->
                        mainActivityViewModel.setError(false)
                        checkForInternetConnection()

                    }
                    .show()
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
            noInternetDialog.setTitle(getString(R.string.no_internet_available))
                .setMessage(getString(R.string.internet_connection_needed))
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    checkForInternetConnection()
                }
                .show()
        }
    }
}