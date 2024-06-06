package com.comsign.showstockprice.network

import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.API_CLIENT_CONNECT_TIME_OUT_SECONDS
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.API_CLIENT_READ_TIME_OUT_SECONDS
import com.comsign.showstockprice.network.ApiClientGetPasses.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiAdapter {

    val getStockInfo: ApiServiceGetStockInfo =
        Retrofit.Builder().baseUrl(BASE_URL).client(generateOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiServiceGetStockInfo::class.java)

    private fun generateOkHttpClient(): OkHttpClient {
         return OkHttpClient.Builder()
             .addInterceptor { chain ->
                 val request = chain.request().newBuilder()
                     .addHeader("accept", "application/json, text/plain")
                     .addHeader("accept-language", "he-IL")
                     .addHeader("if-none-match", "60ad6b3699e9480aac45f900265d53cb:dtagent10211210318124316H6Ek")
                     .addHeader("origin", "https://maya.tase.co.il")
                     .addHeader("priority", "u=1, i")
                     .addHeader("referer", "https://maya.tase.co.il/")
                     .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24")
                     .addHeader("sec-ch-ua-mobile", "?1")
                     .addHeader("x-maya-with", "allow")
                     .addHeader("user-agent", " Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Mobile Safari/537.36")
                     .addHeader("sec-ch-ua-platform", "Android")
                     .addHeader("sec-fetch-dest", "empty")
                     .addHeader("sec-fetch-mode", "cors")

                 chain.proceed(request.build()) }
            .readTimeout(API_CLIENT_READ_TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(API_CLIENT_CONNECT_TIME_OUT_SECONDS, TimeUnit.SECONDS).build()
    }

}