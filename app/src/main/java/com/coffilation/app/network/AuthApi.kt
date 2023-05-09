package com.coffilation.app.network

import com.coffilation.app.models.RefreshTokenData
import com.coffilation.app.models.UserSignInData
import com.coffilation.app.models.TokensResult
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author pvl-zolotov on 23.10.2022
 */
interface AuthApi {

    @POST("auth/jwt/create/")
    suspend fun signIn(
        @Body signUpData: UserSignInData
    ): TokensResult

    @POST("auth/jwt/refresh/")
    suspend fun refreshToken(
        @Body refreshTokenData: RefreshTokenData
    ): TokensResult
}
