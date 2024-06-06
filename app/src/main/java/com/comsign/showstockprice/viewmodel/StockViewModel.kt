package com.comsign.showstockprice.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.comsign.showstockprice.data.StockRepo
import com.comsign.showstockprice.network.GetStockInfoApiState
import kotlinx.coroutines.launch

class StockViewModel(
    private val application: Application,
    private val activity: AppCompatActivity,
    private val stockRepo: StockRepo,
) : AndroidViewModel(application) {
    private var getStockInfoApiState: MutableLiveData<GetStockInfoApiState> = MutableLiveData()


    fun getStockInfoApiState(): LiveData<GetStockInfoApiState> {
        return getStockInfoApiState
    }

    fun getStockInfo(stockId: String) {
        viewModelScope.launch {
            stockRepo.getStockInfo(
                stockId,
                getStockInfoApiState,
            )
        }
    }
}