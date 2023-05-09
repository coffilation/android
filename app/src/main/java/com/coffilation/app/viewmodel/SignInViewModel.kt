package com.coffilation.app.viewmodel

import androidx.lifecycle.ViewModel
import com.coffilation.app.models.UserSignInData
import com.coffilation.app.network.SignInRepository
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.SingleLiveEvent
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.util.format

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
                action.value = Action.ShowMainScreen
            }
            is UseCaseResult.Error -> {
                action.value = Action.ShowError(result.message?.format())
            }
        }
    }

    sealed class Action {

        object ShowMainScreen : Action()

        class ShowError(val message: String?) : Action()
    }
}
