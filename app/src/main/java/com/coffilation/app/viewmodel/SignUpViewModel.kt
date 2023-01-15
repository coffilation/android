package com.coffilation.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.util.SingleLiveEvent
import com.coffilation.app.util.UseCaseResult
import retrofit2.HttpException

/**
 * @author pvl-zolotov on 15.10.2022
 */
class SignUpViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    val action = SingleLiveEvent<Action>()

    suspend fun submit(userSignUpData: UserSignUpData) {
        val result = usersRepository.signUp(userSignUpData)
        when (result) {
            is UseCaseResult.Success -> {
                action.value = Action.ShowSignInScreen
            }
            is UseCaseResult.Error -> {
                if (
                    result.exception is HttpException &&
                    result.exception.response()?.errorBody()?.string()?.contains(passwordsErrorText) == true
                ) {
                    action.value = Action.RepasswordError
                } else {
                    action.value = Action.LoginError
                }
            }
        }
    }

    sealed class Action {

        object ShowSignInScreen : Action()

        object RepasswordError : Action()

        object LoginError : Action()
    }

    companion object {

        const val passwordsErrorText = "Passwords doesn't match"
    }
}
