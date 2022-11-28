package com.coffilation.app.network

import com.coffilation.app.data.RefreshTokenData
import com.coffilation.app.data.TokensResult
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 23.10.2022
 */
interface AuthRepository {

    suspend fun refreshToken(refreshTokenData: RefreshTokenData): UseCaseResult<TokensResult>
}

class AuthRepositoryImpl(private val authApi: AuthApi) : AuthRepository {

    override suspend fun refreshToken(refreshTokenData: RefreshTokenData): UseCaseResult<TokensResult> {
        return try {
            val result = authApi.refreshToken(refreshTokenData)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
