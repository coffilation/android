package com.coffilation.app.network

import com.coffilation.app.data.UserSignInData
import com.coffilation.app.data.TokensResult
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 23.10.2022
 */
interface SignInRepository {

    suspend fun signIn(userSignInData: UserSignInData): UseCaseResult<TokensResult>
}

class SignInRepositoryImpl(private val authApi: AuthApi) : SignInRepository {

    override suspend fun signIn(userSignInData: UserSignInData): UseCaseResult<TokensResult> {
        return try {
            val result = authApi.signIn(userSignInData)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
