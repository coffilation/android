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
import com.coffilation.app.models.CollectionType
import com.coffilation.app.models.ColorData
import com.coffilation.app.models.GradientData
import com.coffilation.app.databinding.FragmentEditCollectionBinding
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
                val collectionAddData = CollectionAddData(
                    name = title.text.toString(),
                    type = when (accessSwitch.checkedButtonId) {
                        R.id.button_public -> CollectionType.PUBLIC
                        R.id.button_private -> CollectionType.PRIVATE
                        else -> throw IllegalArgumentException("Unexpected view id")
                    },
                    description = description.text.toString(),
                    gradient = getGradient(this)
                )
                viewModel.viewModelScope.launch {
                    viewModel.addCollection(collectionAddData)
                    setFragmentResult(REQUEST_KEY_EDIT_COLLECTION, bundleOf(KEY_USER_COLLECTIONS_CHANGED to true))
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun getGradient(binding: FragmentEditCollectionBinding): GradientData {
        return when (binding.colorPicker.checkedRadioButtonId) {
            R.id.gradient_red -> GradientData(
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_red_start)),
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_red_end))
            )
            R.id.gradient_yellow -> GradientData(
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_yellow_start)),
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_yellow_end))
            )
            R.id.gradient_green -> GradientData(
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_green_start)),
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_green_end))
            )
            R.id.gradient_blue -> GradientData(
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_blue_start)),
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_blue_end))
            )
            R.id.gradient_violet -> GradientData(
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_violet_start)),
                ColorData.newInstance(ContextCompat.getColor(requireContext(), R.color.gradient_violet_end))
            )
            else -> throw IllegalArgumentException("Unexpected view id")
        }
    }
}
