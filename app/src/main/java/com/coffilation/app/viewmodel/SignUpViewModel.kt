package com.coffilation.app.viewmodel

import androidx.lifecycle.ViewModel
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.util.SingleLiveEvent
import com.coffilation.app.util.UseCaseResult
import com.coffilation.app.util.format

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
                action.value = Action.ShowError(result.message?.format())
            }
        }
    }

    sealed class Action {

        object ShowSignInScreen : Action()

        class ShowError(val message: String?) : Action()
    }
}
