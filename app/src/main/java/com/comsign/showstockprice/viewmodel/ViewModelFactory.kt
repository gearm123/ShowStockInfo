package com.comsign.showstockprice.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comsign.showstockprice.data.StockRepo

class ViewModelFactory
    (
    private val application: Application,
    private val activity: AppCompatActivity,
    private val repository: StockRepo,

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            StockViewModel(application, activity, repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}