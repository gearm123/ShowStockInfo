package com.comsign.showstockprice.data

import androidx.lifecycle.MutableLiveData
import com.comsign.showstockprice.network.ApiAdapter
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.HTTP_BAD_GATEWAY_CODE
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.HTTP_BAD_REQUEST_CODE
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.HTTP_INTERNAL_SERVER_ERROR_CODE
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.HTTP_NOT_FOUND_CODE
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.HTTP_UNAUTHORIZED_CODE
import com.comsign.showstockprice.network.GetStockDownloadStatus
import com.comsign.showstockprice.network.GetStockInfoApiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockRepo {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    suspend fun getStockInfo(
        stockId: String,
        downloadApiState: MutableLiveData<GetStockInfoApiState>,
    ) {
        withContext(defaultDispatcher) {
            ApiAdapter.getStockInfo.get(stockId)
                ?.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>, response: Response<ResponseBody>
                    ) {
                        response.also {
                            if (response.isSuccessful) {
                                var uiResponse: ArrayList<String?> = ArrayList()
                                handleResponse(response, uiResponse)
                                downloadApiState.postValue(
                                    GetStockInfoApiState(
                                        GetStockDownloadStatus.SUCCESS,
                                        uiResponse,
                                        ""
                                    )
                                )
                            } else {
                                downloadApiState.postValue(
                                    GetStockInfoApiState(
                                        GetStockDownloadStatus.ERROR,
                                        null,
                                        handleHttpResponseError(response.code())
                                    )
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        downloadApiState.postValue(
                            GetStockInfoApiState(
                                GetStockDownloadStatus.ERROR, null, t.message
                            )
                        )
                    }
                })
        }
    }

    private fun handleHttpResponseError(errorResponseCode: Int): String {
        when (errorResponseCode) {
            HTTP_BAD_REQUEST_CODE -> {
                return "Bad Request (400)"
            }

            HTTP_UNAUTHORIZED_CODE -> {
                return "Unauthorized (401)"

            }

            HTTP_NOT_FOUND_CODE -> {
                return "Not Found (403)"
            }

            HTTP_INTERNAL_SERVER_ERROR_CODE -> {
                return "Internal Server Error (500)"
            }

            HTTP_BAD_GATEWAY_CODE -> {
                return "BadGateway (502)"
            }

            else -> {
                return "Server Response code:$errorResponseCode"
            }
        }

    }

    private fun handleResponse(response: Response<ResponseBody>, uiResponse: ArrayList<String?>) {
        if (response.body() != null) {
            val responseJson = JSONObject(response.body()!!.string())
            uiResponse.add(getStockBaseRate(responseJson))
            uiResponse.add(getStockLastRate(responseJson))
            uiResponse.add(getStockChangePercent(responseJson))
        }

    }

    private fun getStockLastRate(responseJson: JSONObject): String? {
        return responseJson.getString("LastRate")
    }

    private fun getStockBaseRate(responseJson: JSONObject): String? {
        return responseJson.getString("BaseRate")
    }

    private fun getStockChangePercent(responseJson: JSONObject): String? {
        return responseJson.getString("PercentageChange")
    }
}