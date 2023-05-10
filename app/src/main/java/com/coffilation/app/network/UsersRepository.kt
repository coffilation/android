package com.coffilation.app.network

import com.coffilation.app.models.UserData
import com.coffilation.app.models.UserSignUpData
import com.coffilation.app.models.UserSignUpError
import com.coffilation.app.models.UserSignUpResult
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.UseCaseResult
import com.google.gson.Gson
import retrofit2.HttpException

/**
 * @author pvl-zolotov on 15.10.2022
 */
interface UsersRepository {

    suspend fun signUp(userSignUpData: UserSignUpData): UseCaseResult<UserSignUpResult>

    suspend fun me(): UseCaseResult<UserData>

    suspend fun isAuthenticationSaved(): Boolean
}

class UsersRepositoryImpl(private val usersApi: UsersApi, private val prefRepository: PrefRepository) : UsersRepository {

    override suspend fun signUp(userSignUpData: UserSignUpData): UseCaseResult<UserSignUpResult> {
        return try {
            val result = usersApi.signUp(userSignUpData)
            UseCaseResult.Success(result)
        } catch (ex: HttpException) {
            val message = Gson().fromJson(ex.response()?.errorBody()?.string(), UserSignUpError::class.java)
            UseCaseResult.Error(ex, message.getMessages())
        } catch (ex: java.lang.Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun me(): UseCaseResult<UserData> {
        return try {
            val result = usersApi.me()
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(ex)
        }
    }

    override suspend fun isAuthenticationSaved(): Boolean {
        return prefRepository.getRefreshToken().isNotEmpty()
    }
}
