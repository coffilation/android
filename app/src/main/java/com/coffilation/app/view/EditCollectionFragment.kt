package com.coffilation.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.coffilation.app.R
import com.coffilation.app.data.CollectionAddData
import com.coffilation.app.data.CollectionType
import com.coffilation.app.data.ColorData
import com.coffilation.app.data.GradientData
import com.coffilation.app.databinding.FragmentEditCollectionBinding
import com.coffilation.app.util.setBackStackData
import com.coffilation.app.view.MainFragment.Companion.KEY_USER_COLLECTIONS_CHANGED
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

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding?.run {
            toolbar.setupWithNavController(navController, appBarConfiguration)
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
                    setBackStackData(KEY_USER_COLLECTIONS_CHANGED, true)
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
