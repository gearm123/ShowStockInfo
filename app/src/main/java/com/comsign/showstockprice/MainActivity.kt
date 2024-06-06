package com.comsign.showstockprice

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.comsign.showstockprice.data.StockRepo
import com.comsign.showstockprice.network.GetStockDownloadStatus
import com.comsign.showstockprice.viewmodel.StockViewModel
import com.comsign.showstockprice.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {
    private val DEFAULT_STOCK_ID: String = "1183441"

    private lateinit var stockViewModel: StockViewModel

    private lateinit var stockSearchLayout: ConstraintLayout
    private lateinit var stockInfoLayout: ConstraintLayout
    private lateinit var stockUnitsInfoLayout: ConstraintLayout

    private lateinit var searchStockButton: Button
    private lateinit var searchStockEdt: EditText
    private lateinit var stockValue: TextView
    private lateinit var stockPercent: TextView
    private lateinit var refreshButton: Button
    private lateinit var stockNumEdt: EditText
    private lateinit var fullCashValue: TextView
    private lateinit var dailyProfit: TextView

    private lateinit var sharedPref: SharedPreferences

    private var currentStockValue: Double = 0.0
    private var baseStockValue: Double = 0.0
    private var dailyPercentageChange: Double = 0.0
    private var numStockHolding: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getPreferences(Context.MODE_PRIVATE)
        setupViewModel()
        doObserveWork()
        setUpViews()
    }

    private fun setupViewModel() {
        stockViewModel = ViewModelProvider(
            this, ViewModelFactory(application, this, StockRepo())
        )[StockViewModel::class.java]
    }

    private fun updateStockInfo() {
        val stockId: String? = sharedPref.getString("stock_id", DEFAULT_STOCK_ID)
        if (stockId != null) {
            stockViewModel.getStockInfo(stockId)
        }
    }

    private fun setUpViews() {
        stockSearchLayout = findViewById(R.id.stock_search_layout)
        stockInfoLayout = findViewById(R.id.stock_info_layout)

        searchStockButton =
            stockSearchLayout.findViewById(R.id.stock_id_search)
        searchStockEdt =
            stockSearchLayout.findViewById(R.id.stock_id_edt)
        setUpSearchClickListener()
        setUpSearchStockEdt()



        stockPercent =
            stockInfoLayout.findViewById(R.id.stock_percent_change)
        stockValue =
            stockInfoLayout.findViewById(R.id.stock_value)
        stockPercent =
            stockInfoLayout.findViewById(R.id.stock_percent_change)

        refreshButton =
            stockInfoLayout.findViewById(R.id.refresh_button)
        refreshButton.setOnClickListener {
            updateStockInfo()
        }

        stockUnitsInfoLayout = stockInfoLayout.findViewById(R.id.stock_units_info_layout)
        stockNumEdt =
            stockUnitsInfoLayout.findViewById(R.id.stock_number_edt)
        setUpStockNumEdtListener()


        fullCashValue =
            stockUnitsInfoLayout.findViewById(R.id.full_cash_value)

        dailyProfit = findViewById(R.id.daily_profit)


    }

    private fun doObserveWork() {

        stockViewModel.getStockInfoApiState().observe(this) {
            if (it != null && it.status == GetStockDownloadStatus.SUCCESS) {
                val baseRate: String? = it.data?.get(0)
                val lastRate: String? = it.data?.get(1)
                val percentChange: String? = it.data?.get(2)
                stockValue.text = "Last Rate ${lastRate}"
                stockPercent.text = "Percent Change ${percentChange} %"
                if (lastRate != null) {
                    currentStockValue = lastRate.toDouble()

                }
                if (baseRate != null) {
                    baseStockValue = baseRate.toDouble()
                }
                if (percentChange != null) {
                    dailyPercentageChange = percentChange.toDouble()
                }
                stockUnitsInfoLayout.visibility = View.VISIBLE
            } else if (it.status == GetStockDownloadStatus.LOADING) {
            } else {
                stockInfoLayout.visibility = View.GONE
                stockSearchLayout.visibility = View.VISIBLE
                createToast(
                    this, "Error getting stock value!" + ": ${it.message}", Toast.LENGTH_LONG
                ).show()
            }


        }


    }

    private fun setUpSearchClickListener() {
        searchStockButton.setOnClickListener {
            stockSearchLayout.visibility = View.GONE
            stockInfoLayout.visibility = View.VISIBLE
            val stockId: String = searchStockEdt.text.toString()
            with(sharedPref.edit()) {
                putString("stock_id", stockId)
                apply()
            }
            updateStockInfo()
        }
    }

    private fun setUpSearchStockEdt() {
        searchStockEdt.setText(sharedPref.getString("stock_id", DEFAULT_STOCK_ID).toString())
    }

    private fun setUpStockNumEdtListener() {
        stockNumEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val numStockValueStr: String = s.toString()
                try {
                    numStockHolding = numStockValueStr.toInt()
                    val fullCash: Int = (numStockHolding * currentStockValue.toInt())
                    fullCashValue.text = "Current Net Work Of Stock: ${fullCash / 100}"
                    val valueDiff: Double = currentStockValue - baseStockValue
                    val profit: Int = (valueDiff * dailyPercentageChange).toInt() * numStockHolding
                    dailyProfit.text = "Daily Profit: ${profit / 100}"
                    fullCashValue.visibility = View.VISIBLE
                    dailyProfit.visibility = View.VISIBLE
                } catch (e: Exception) {
                    createToast(this@MainActivity, "invalid input", Toast.LENGTH_SHORT)
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun createToast(activity: Activity, text: String, duration: Int): Toast {
        return Toast.makeText(activity, text, duration)
    }
}