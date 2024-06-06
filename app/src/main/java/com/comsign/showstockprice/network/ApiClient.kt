package com.comsign.showstockprice.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClientGetPasses {

    companion object {
        const val HTTP_BAD_GATEWAY_CODE = 502
        const val HTTP_INTERNAL_SERVER_ERROR_CODE = 500
        const val HTTP_NOT_FOUND_CODE = 403
        const val HTTP_UNAUTHORIZED_CODE = 401
        const val HTTP_BAD_REQUEST_CODE = 400
        const val API_CLIENT_READ_TIME_OUT_SECONDS: Long = 30
        const val API_CLIENT_CONNECT_TIME_OUT_SECONDS: Long = 30
        const val BASE_URL = "https://mayaapi.tase.co.il"
    }
}

interface ApiServiceGetStockInfo {
    @GET(
        "/api/foreignetf/tradedata"
    )
    fun get(@Query("etfId") stockId: String): Call<ResponseBody>?
}