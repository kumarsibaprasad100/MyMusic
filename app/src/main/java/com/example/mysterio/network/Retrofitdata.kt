package com.example.mysterio.network

import com.example.mysterio.news.NewsInterFace
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Retrofitdata {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/") // Replace with your base URL
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsInterFace::class.java)
}