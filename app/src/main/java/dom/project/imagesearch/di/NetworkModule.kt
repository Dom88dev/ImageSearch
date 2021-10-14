package dom.project.imagesearch.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dom.project.imagesearch.BuildConfig
import dom.project.imagesearch.model.remote.api.KakaoApi
import dom.project.imagesearch.utills.KAKAO_BASE_URL
import dom.project.imagesearch.utills.KAKAO_REST_API_KEY
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L


val networkModule = module {

    factory { Cache(androidApplication().cacheDir, 10L * 1024 * 1024) }

    factory { createGson() }

    factory { provideInterceptor() }

    single { provideRetrofit(get(), get(), get()) }

    single(createdAtStart = false) { get<Retrofit>().create(KakaoApi::class.java) }

}

fun createGson(): Gson {
    return GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.000+z")
        .create()
}

fun provideInterceptor(): Interceptor {
    return Interceptor { chain ->
        chain.proceed(chain.request().newBuilder().apply {
            header("Accept", "application/json")
            header("Authorization", "KakaoAK $KAKAO_REST_API_KEY")
        }.build())
    }
}


fun provideRetrofit(cache: Cache, interceptor: Interceptor, gson: Gson): Retrofit {
    val client = OkHttpClient.Builder().apply {
        cache(cache)
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
        addInterceptor(interceptor = interceptor)
        addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        })

    }.build()
    return Retrofit.Builder()
        .baseUrl(KAKAO_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
}







