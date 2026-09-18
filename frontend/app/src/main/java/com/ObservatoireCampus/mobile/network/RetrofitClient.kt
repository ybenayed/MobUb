package com.ObservatoireCampus.mobile.network

import android.content.Context
import com.ObservatoireCampus.mobile.data.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.178.47.195:8080/"

    private lateinit var tokenManager: TokenManager

    /**
     * A appeler UNE SEULE FOIS, avant tout appel reseau (voir MainActivity.onCreate).
     * Necessaire car TokenManager depend d'un Context Android, indisponible
     * directement dans un objet singleton comme RetrofitClient.
     */
    fun init(context: Context) {
        if (!::tokenManager.isInitialized) {
            tokenManager = TokenManager(context.applicationContext)
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        check(::tokenManager.isInitialized) {
            "RetrofitClient.init(context) doit etre appele avant tout appel reseau"
        }
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // AJOUT : manquait completement
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }

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
    val batimentApi: BatimentApi by lazy { retrofit.create(BatimentApi::class.java) }

    // Pratique pour AuthRepository, qui a aussi besoin du TokenManager
    // (pour lire isLoggedIn() / logout() sans dupliquer le SharedPreferences).
    fun getTokenManager(): TokenManager = tokenManager
    val searchHistoryApi: SearchHistoryApi by lazy { retrofit.create(SearchHistoryApi::class.java) }

    val adminUserApi: AdminUserApi by lazy { retrofit.create(AdminUserApi::class.java) }
    val adminInfrastructureApi: AdminInfrastructureApi by lazy { retrofit.create(AdminInfrastructureApi::class.java) }

    val adminLegendApi: AdminLegendApi by lazy { retrofit.create(AdminLegendApi::class.java) }
}