package com.coffilation.app.network

import com.coffilation.app.data.RefreshTokenData
import com.coffilation.app.data.UserSignInData
import com.coffilation.app.data.TokensResult
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author pvl-zolotov on 23.10.2022
 */
interface AuthApi {

    @POST("auth/login")
    suspend fun signIn(
        @Body signUpData: UserSignInData
    ): TokensResult

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshTokenData: RefreshTokenData
    ): TokensResult
}
