package com.coffilation.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coffilation.app.data.UserSignInData
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.network.AuthRepository
import com.coffilation.app.network.SignInRepository
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.SingleLiveEvent
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 15.10.2022
 */
class SignInViewModel(
    private val signInRepository: SignInRepository,
    private val prefRepository: PrefRepository
) : ViewModel() {

    val action = SingleLiveEvent<Action>()

    suspend fun submit(userSignInData: UserSignInData) {
        val result = signInRepository.signIn(userSignInData)
        when (result) {
            is UseCaseResult.Success -> {
                prefRepository.putAccessToken(result.data.access)
                prefRepository.putRefreshToken(result.data.refresh)
            }
            is UseCaseResult.Error -> {
                action.value = Action.PasswordError
            }
        }
    }

    fun showMainScreen() {
        action.value = Action.ShowMainScreen
    }

    sealed class Action {

        object ShowMainScreen : Action()

        object PasswordError : Action()
    }
}
