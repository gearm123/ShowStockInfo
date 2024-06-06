package com.comsign.showstockprice.network


data class GetStockInfoApiState(
    var status: GetStockDownloadStatus, val data: ArrayList<String?>?, val message: String?
) {

    companion object {

        // In case of Success,set status as
        // Success and data as the response
        fun <T> success(data : ArrayList<String?>?): GetStockInfoApiState {
            return GetStockInfoApiState(GetStockDownloadStatus.SUCCESS, data, null)
        }

        // In case of failure ,set state to Error ,
        // add the error message,set data to null
        fun <T> error(msg: String): GetStockInfoApiState {
            return GetStockInfoApiState(GetStockDownloadStatus.ERROR, null, msg)
        }

        // When the call is loading set the state
        // as Loading and rest as null
        fun <T> loading(): GetStockInfoApiState {
            return GetStockInfoApiState(GetStockDownloadStatus.LOADING, null, null)
        }
    }
}

enum class GetStockDownloadStatus {
    SUCCESS,
    ERROR,
    LOADING
}