package com.example.sunnyweather.logic.network

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>()

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun searchPlaces(query:String) = placeService.searchPlaces(query).await()

    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealTimeWeather(lng, lat).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine {
            continuation ->
                enqueue(object: Callback<T>{
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        val body = response.body()
                        if(body != null) continuation.resume(body)
                        else continuation.resumeWithException(
                            RuntimeException("reponse body is null")
                        )
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                })
        }
    }



}