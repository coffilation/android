package com.coffilation.app.network;

import com.coffilation.app.data.UserData
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.data.UserSignUpResult
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 15.10.2022
 */
interface UsersRepository {

    suspend fun signUp(userSignUpData: UserSignUpData): UseCaseResult<UserSignUpResult>

    suspend fun me(): UseCaseResult<UserData>
}

class UsersRepositoryImpl(private val usersApi: UsersApi) : UsersRepository {

    override suspend fun signUp(userSignUpData: UserSignUpData): UseCaseResult<UserSignUpResult> {
        return try {
            val result = usersApi.signUp(userSignUpData)
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
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
}
