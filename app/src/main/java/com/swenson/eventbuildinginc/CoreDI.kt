package com.swenson.eventbuildinginc

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.swenson.eventbuildinginc.data.EventRepository
import com.swenson.eventbuildinginc.data.local.EventDao
import com.swenson.eventbuildinginc.data.local.EventsDb
import com.swenson.eventbuildinginc.data.remote.EventsApi
import com.swenson.eventbuildinginc.domain.FormatAmountUseCase
import com.swenson.eventbuildinginc.util.Constants.BASE_URL
import com.swenson.eventbuildinginc.util.Constants.DB_NAME
import com.swenson.eventbuildinginc.util.IoDispatcher
import com.swenson.eventbuildinginc.util.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class CoreDI {
    companion object {
        private const val TIMEOUT = 5L

        @Provides
        fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Provides
        fun provideNetworkRequestBuilder(): NetworkRequest.Builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        @Provides
        fun provideLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        @Provides
        fun provideGsonConverter(): Gson {
            return GsonBuilder().create()
        }

        @Provides
        fun provideRetrofitBuilder(
            okHttpClient: OkHttpClient,
            gson: Gson
        ): Retrofit.Builder {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
        }

        @Provides
        fun provideOkHttpClient(
            loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()

        @Provides
        fun provideRestService(retrofitBuilder: Retrofit.Builder): EventsApi {
            return retrofitBuilder.baseUrl(BASE_URL)
                .build()
                .create(EventsApi::class.java)
        }

        @Provides
        fun provideAppDatabase(@ApplicationContext appContext: Context) =
            Room.databaseBuilder(
                appContext,
                EventsDb::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration().build()

        @Provides
        fun provideMovieDao(database: EventsDb) = database.eventsDao()

        @IoDispatcher
        @Provides
        fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @MainDispatcher
        @Provides
        fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

        @Provides
        fun provideAmountFormatterUseCase() = FormatAmountUseCase()

        @Provides
        fun provideRepository(
            dao: EventDao, api: EventsApi,
            formatAmountUseCase: FormatAmountUseCase,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): EventRepository = EventRepository(dao, api, formatAmountUseCase, ioDispatcher)
    }
}

