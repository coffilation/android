package com.coffilation.app.util

import com.coffilation.app.domain.PublicCollectionsInteractor
import com.coffilation.app.models.RefreshTokenData
import com.coffilation.app.network.AuthApi
import com.coffilation.app.network.AuthRepository
import com.coffilation.app.network.AuthRepositoryImpl
import com.coffilation.app.network.CollectionRepositoryImpl
import com.coffilation.app.network.CollectionsApi
import com.coffilation.app.network.CollectionsRepository
import com.coffilation.app.network.SearchApi
import com.coffilation.app.network.SearchRepository
import com.coffilation.app.network.SearchRepositoryImpl
import com.coffilation.app.network.SignInRepository
import com.coffilation.app.network.SignInRepositoryImpl
import com.coffilation.app.network.UsersApi
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.network.UsersRepositoryImpl
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.storage.PrefRepositoryImpl
import com.coffilation.app.viewmodel.EditCollectionViewModel
import com.coffilation.app.viewmodel.MainViewModel
import com.coffilation.app.viewmodel.SignInViewModel
import com.coffilation.app.viewmodel.SignUpViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author pvl-zolotov on 15.10.2022
 */
const val API_BASE_URL = "https://backend.test.coffilation.ru/"
const val API_SEARCH_BASE_URL = "https://search.test.coffilation.ru/"

val usersModule = module {
    single {
        createWebService<UsersApi>(
            okHttpClient = createHttpClient(prefRepository = get(), authRepository = get()),
            baseUrl = API_BASE_URL
        )
    }
    factory<UsersRepository> { UsersRepositoryImpl(usersApi = get(), prefRepository = get()) }
    viewModel { SignUpViewModel(usersRepository = get()) }
}

val signInModule = module {
    single {
        createWebService<AuthApi>(
            okHttpClient = createHttpClient(prefRepository = get(), authRepository = get()),
            baseUrl = API_BASE_URL
        )
    }
    factory<SignInRepository> { SignInRepositoryImpl(authApi = get()) }
    factory<UsersRepository> { UsersRepositoryImpl(usersApi = get(), prefRepository = get()) }
    viewModel { SignInViewModel(signInRepository = get(), prefRepository = get()) }
}

val collectionsModule = module {
    single {
        createWebService<CollectionsApi>(
            okHttpClient = createHttpClient(prefRepository = get(), authRepository = get()),
            baseUrl = API_BASE_URL
        )
    }
    factory<CollectionsRepository> { CollectionRepositoryImpl(collectionsApi = get()) }
    factory { PublicCollectionsInteractor(collectionsRepository = get()) }
    viewModel { EditCollectionViewModel(collectionsRepository = get()) }
    viewModel { MainViewModel(collectionsRepository = get(), publicCollectionsInteractor = get(), searchRepository = get(), usersRepository = get()) }
}

val searchModule = module {
    single {
        createWebService<SearchApi>(
            okHttpClient = createHttpClient(prefRepository = get(), authRepository = get()),
            baseUrl = API_SEARCH_BASE_URL
        )
    }
    factory<SearchRepository> { SearchRepositoryImpl(searchApi = get()) }
}

val authModule = module {
    single {
        createWebService<AuthApi>(
            okHttpClient = createHttpClient(prefRepository = get(), authRepository = null),
            baseUrl = API_BASE_URL
        )
    }
    factory<AuthRepository> { AuthRepositoryImpl(authApi = get()) }
}

val prefModule = module {
    factory<PrefRepository> { PrefRepositoryImpl(context = androidContext()) }
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, baseUrl: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(okHttpClient)
        .build()
    return retrofit.create(T::class.java)
}

fun createHttpClient(prefRepository: PrefRepository, authRepository: AuthRepository?): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .authenticator { route, response ->
            val refreshToken = runBlocking {
                prefRepository.getRefreshToken()
            }
            if (refreshToken.isNotEmpty()) {
                val newTokens = runBlocking {
                    authRepository?.refreshToken(RefreshTokenData(refreshToken))
                }
                if (newTokens is UseCaseResult.Success) {
                    runBlocking { prefRepository.putAccessToken(newTokens.data.access) }
                    runBlocking { prefRepository.putRefreshToken(newTokens.data.refresh) }
                    return@authenticator response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.data.access}")
                        .build()
                }
            }
            return@authenticator null
        }
        .addInterceptor { chain ->
            val token = runBlocking {
                prefRepository.getAccessToken()
            }
            val newRequest = chain.request().newBuilder()
                .apply {
                    if (token.isNotEmpty()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }
                .build()
            chain.proceed(newRequest)
        }
        .build()
}
