package com.coffilation.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coffilation.app.data.UserSignUpData
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 15.10.2022
 */
class SignUpViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    suspend fun submit(userSignUpData: UserSignUpData) {
        val result = usersRepository.signUp(userSignUpData)
        when (result) {
            is UseCaseResult.Success -> {
                Log.v("test", result.data.username)
            }
            is UseCaseResult.Error -> Log.v("test", "error")
        }
    }
}
