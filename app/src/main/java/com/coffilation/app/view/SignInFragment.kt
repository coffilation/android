package com.coffilation.app.view

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.viewModelScope
import com.coffilation.app.R
import com.coffilation.app.data.UserSignInData
import com.coffilation.app.databinding.FragmentSignInBinding
import com.coffilation.app.util.changeUrlSpanClickAction
import com.coffilation.app.viewmodel.SignInViewModel
import com.coffilation.app.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author pvl-zolotov on 15.10.2022
 */
class SignInFragment : Fragment() {

    private var binding: FragmentSignInBinding? = null
    private val signInViewModel: SignInViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.submit?.setOnClickListener {
            submit()
        }
        binding?.signUpSuggest?.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = resources.getText(R.string.sign_up_link).changeUrlSpanClickAction {
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<SignUpFragment>(R.id.fragment_container_view)
                }
            }
        }

        signInViewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                SignInViewModel.Action.PasswordError -> {
                    Toast.makeText(requireContext(), R.string.password_error, Toast.LENGTH_LONG).show()
                }
                SignInViewModel.Action.ShowMainScreen -> {
                    parentFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<MainFragment>(R.id.fragment_container_view)
                    }
                }
            }
        }
    }

    private fun submit() {
        val userSignInData = UserSignInData(
            username = binding?.username?.text.toString(),
            password = binding?.password?.text.toString(),
        )
        signInViewModel.viewModelScope.launch {
            signInViewModel.submit(userSignInData)
            userViewModel.saveUserInfo()
            signInViewModel.showMainScreen()
        }
    }
}
