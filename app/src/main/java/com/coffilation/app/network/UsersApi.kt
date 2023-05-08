package com.coffilation.app.network

import com.coffilation.app.data.UserData
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.data.UserSignUpResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author pvl-zolotov on 15.10.2022
 */
interface UsersApi {

    @POST("users/")
    suspend fun signUp(
        @Body signUpData: UserSignUpData
    ): UserSignUpResult

    @GET("users/me/")
    suspend fun me(): UserData
}
