package com.coffilation.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.viewModelScope
import com.coffilation.app.R
import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.models.GradientData
import com.coffilation.app.databinding.FragmentEditCollectionBinding
import com.coffilation.app.util.toColorHexString
import com.coffilation.app.view.MainFragment.Companion.KEY_USER_COLLECTIONS_CHANGED
import com.coffilation.app.view.MainFragment.Companion.REQUEST_KEY_EDIT_COLLECTION
import com.coffilation.app.viewmodel.EditCollectionViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author pvl-zolotov on 29.10.2022
 */
class EditCollectionFragment : Fragment() {

    private var binding: FragmentEditCollectionBinding? = null
    private val viewModel: EditCollectionViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCollectionBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
            toolbar.setNavigationIcon(R.drawable.ic_back)
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            toolbar.title = ""
            buttonDone.setOnClickListener {
                val gradient = getGradient(this)
                val collectionAddData = CollectionAddData.newInstance(
                    name = title.text.toString(),
                    isPrivate = when (accessSwitch.checkedButtonId) {
                        R.id.button_public -> false
                        R.id.button_private -> true
                        else -> throw IllegalArgumentException("Unexpected view id")
                    },
                    description = description.text.toString().takeIf { it.isNotEmpty() },
                    gradientData = gradient
                )
                viewModel.viewModelScope.launch {
                    viewModel.addCollection(collectionAddData)
                    setFragmentResult(REQUEST_KEY_EDIT_COLLECTION, bundleOf(KEY_USER_COLLECTIONS_CHANGED to true))
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun getGradient(binding: FragmentEditCollectionBinding): GradientData? {
        return when (binding.colorPicker.checkedRadioButtonId) {
            R.id.gradient_red -> GradientData(
                ContextCompat.getColor(requireContext(), R.color.gradient_red_start).toColorHexString(),
                ContextCompat.getColor(requireContext(), R.color.gradient_red_end).toColorHexString()
            )
            R.id.gradient_yellow -> GradientData(
                ContextCompat.getColor(requireContext(), R.color.gradient_yellow_start).toColorHexString(),
                ContextCompat.getColor(requireContext(), R.color.gradient_yellow_end).toColorHexString()
            )
            R.id.gradient_green -> GradientData(
                ContextCompat.getColor(requireContext(), R.color.gradient_green_start).toColorHexString(),
                ContextCompat.getColor(requireContext(), R.color.gradient_green_end).toColorHexString()
            )
            R.id.gradient_blue -> GradientData(
                ContextCompat.getColor(requireContext(), R.color.gradient_blue_start).toColorHexString(),
                ContextCompat.getColor(requireContext(), R.color.gradient_blue_end).toColorHexString()
            )
            R.id.gradient_violet -> GradientData(
                ContextCompat.getColor(requireContext(), R.color.gradient_violet_start).toColorHexString(),
                ContextCompat.getColor(requireContext(), R.color.gradient_violet_end).toColorHexString()
            )
            else -> null
        }
    }
}
