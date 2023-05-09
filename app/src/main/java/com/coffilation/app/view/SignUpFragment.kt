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
import com.coffilation.app.models.UserSignUpData
import com.coffilation.app.databinding.FragmentSignUpBinding
import com.coffilation.app.util.changeUrlSpanClickAction
import com.coffilation.app.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author pvl-zolotov on 15.10.2022
 */
class SignUpFragment : Fragment() {

    private var binding: FragmentSignUpBinding? = null
    private val signUpViewModel: SignUpViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.submit?.setOnClickListener {
            submit()
        }
        binding?.signInSuggest?.apply {
            movementMethod = LinkMovementMethod.getInstance()
            text = resources.getText(R.string.sign_in_link).changeUrlSpanClickAction {
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<SignInFragment>(R.id.fragment_container_view)
                }
            }
        }

        signUpViewModel.action.observe(viewLifecycleOwner) {
            when (it) {
                is SignUpViewModel.Action.ShowError -> {
                    if (it.message != null) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
                    }
                }
                SignUpViewModel.Action.ShowSignInScreen -> {
                    parentFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<SignInFragment>(R.id.fragment_container_view)
                    }
                }
            }
        }
    }

    private fun submit() {
        val userSignUpData = UserSignUpData(
            username = binding?.username?.text.toString(),
            password = binding?.password?.text.toString(),
            rePassword = binding?.repassword?.text.toString(),
        )
        signUpViewModel.viewModelScope.launch {
            signUpViewModel.submit(userSignUpData)
        }
    }
}
