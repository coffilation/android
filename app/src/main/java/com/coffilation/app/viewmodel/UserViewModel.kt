package com.coffilation.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coffilation.app.network.UsersRepository
import com.coffilation.app.storage.PrefRepository
import com.coffilation.app.util.UseCaseResult

/**
 * @author pvl-zolotov on 23.10.2022
 */
class UserViewModel(
    private val usersRepository: UsersRepository,
    private val prefRepository: PrefRepository
) : ViewModel() {

    suspend fun saveUserInfo() {
        val result = usersRepository.me()
        when (result) {
            is UseCaseResult.Success -> {
                prefRepository.putUserId(result.data.id)
                prefRepository.putUsername(result.data.username)
            }
            is UseCaseResult.Error -> Log.v("test", "error")
        }
    }

    /*suspend fun getUserInfo() {
        val userInfo = UserData(
            id = prefRepository.getUserId(),
            username = prefRepository.getUsername()
        )
    }*/
}
