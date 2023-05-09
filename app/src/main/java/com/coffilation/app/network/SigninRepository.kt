package com.coffilation.app.network

import com.coffilation.app.models.UserSignInData
import com.coffilation.app.models.TokensResult
import com.coffilation.app.models.UserSignInError
import com.coffilation.app.util.UseCaseResult
import com.google.gson.Gson
import retrofit2.HttpException
import java.lang.Exception

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
        } catch (ex: HttpException) {
            val message = Gson().fromJson(ex.response()?.errorBody()?.string(), UserSignInError::class.java)
            UseCaseResult.Error(ex, message.getMessages())
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }
}
