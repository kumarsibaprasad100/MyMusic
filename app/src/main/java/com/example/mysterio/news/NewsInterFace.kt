package com.example.mysterio.news

import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response


interface NewsInterFace {
    @GET("/v2/everything")
    fun getNews(
        @Query("q") about: String,
        @Query("apiKey") apiKey: String
    ): Observable<Response<ResponseBody>>

}