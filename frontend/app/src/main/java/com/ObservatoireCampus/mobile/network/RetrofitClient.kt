package com.ObservatoireCampus.mobile.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.20:8080/"  // typo corrigee : http:// pas http:/

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val campusApi: CampusApi by lazy { retrofit.create(CampusApi::class.java) }
    val parkingApi: ParkingApi by lazy { retrofit.create(ParkingApi::class.java) }
    val stationTBApi: StationTBApi by lazy { retrofit.create(StationTBApi::class.java) }
    val stationVApi: StationVApi by lazy { retrofit.create(StationVApi::class.java) }
    val freeVehicleApi: FreeVehicleApi by lazy { retrofit.create(FreeVehicleApi::class.java) }
    val weatherApi: WeatherApi by lazy { retrofit.create(WeatherApi::class.java) }
    val airQualityApi: AirQualityApi by lazy { retrofit.create(AirQualityApi::class.java) }
    val stationTerApi: StationTerApi by lazy { retrofit.create(StationTerApi::class.java) }
    val geocodingApi: GeocodingApi by lazy { retrofit.create(GeocodingApi::class.java) }

    val itineraryApi: ItineraryApi by lazy { retrofit.create(ItineraryApi::class.java) }

}