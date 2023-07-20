package com.example.mysterio.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.*
import androidx.lifecycle.ViewModel
import com.example.mysterio.news.newsmodel.NewsResponse
import com.example.mysterio.network.Retrofitdata
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class NewsViewModel : ViewModel() {

    val myResponse: MutableLiveData<NewsResponse> = MutableLiveData()

    fun getNews(query: String) {
        val news = Retrofitdata.retrofit.getNews(
            if (query.isEmpty())"india" else query,
            "631729be2bd84e5faf097775ebf4c461"
        )
        news.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Log.i("TAG", "onError: " + e.message)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Response<ResponseBody>) {
                    if (t.code() == 200) {
                        val responseString = t.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val response: NewsResponse = Gson().fromJson(
                            jsonObject.toString(),
                            NewsResponse::class.java
                        )
                        Log.i("TAG", "onNext: " + response)
                        myResponse.postValue(response)
                    }
                }

            })


    }
}