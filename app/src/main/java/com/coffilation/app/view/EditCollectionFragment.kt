package com.coffilation.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.viewModelScope
import com.coffilation.app.R
import com.coffilation.app.models.CollectionAddData
import com.coffilation.app.models.GradientData
import com.coffilation.app.databinding.FragmentEditCollectionBinding
import com.coffilation.app.models.CollectionData
import com.coffilation.app.view.MainFragment.Companion.COLLECTIONS_CREATED
import com.coffilation.app.view.MainFragment.Companion.COLLECTIONS_EDITED
import com.coffilation.app.view.MainFragment.Companion.KEY_COLLECTIONS_SAVING_MODE
import com.coffilation.app.view.MainFragment.Companion.KEY_COLLECTIONS_SAVING_RESULT
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

            val collectionToEdit = getParams()
            if (collectionToEdit != null) {
                title.setText(collectionToEdit.name)
                if (collectionToEdit.isPrivate) {
                    accessSwitch.check(R.id.button_private)
                } else {
                    accessSwitch.check(R.id.button_public)
                }
                description.setText(collectionToEdit.description.orEmpty())
                colorPicker.check(getColorPickerValue(collectionToEdit.getGradientData()))
            }

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
                if (collectionToEdit != null) {
                    viewModel.viewModelScope.launch {
                        viewModel.editCollection(collectionToEdit.id, collectionAddData)
                    }
                } else {
                    viewModel.viewModelScope.launch {
                        viewModel.addCollection(collectionAddData)
                    }
                }
            }
        }

        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                is EditCollectionViewModel.Action.CollectionCreationFinished -> {
                    setFragmentResult(
                        REQUEST_KEY_EDIT_COLLECTION,
                        bundleOf(
                            KEY_COLLECTIONS_SAVING_MODE to COLLECTIONS_CREATED,
                            KEY_COLLECTIONS_SAVING_RESULT to action.collection
                        )
                    )
                    parentFragmentManager.popBackStack()
                }
                is EditCollectionViewModel.Action.CollectionEditingFinished -> {
                    setFragmentResult(
                        REQUEST_KEY_EDIT_COLLECTION,
                        bundleOf(
                            KEY_COLLECTIONS_SAVING_MODE to COLLECTIONS_EDITED,
                            KEY_COLLECTIONS_SAVING_RESULT to action.collection
                        )
                    )
                    parentFragmentManager.popBackStack()
                }
                EditCollectionViewModel.Action.ShowSavingError -> {
                    Toast.makeText(requireContext(), R.string.saving_error, Toast.LENGTH_LONG).show()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun getGradient(binding: FragmentEditCollectionBinding): GradientData? {
        val context = requireContext()
        return when (binding.colorPicker.checkedRadioButtonId) {
            R.id.gradient_red -> GradientData.getRed(context)
            R.id.gradient_yellow -> GradientData.getYellow(context)
            R.id.gradient_green -> GradientData.getGreen(context)
            R.id.gradient_blue -> GradientData.getBlue(context)
            R.id.gradient_violet -> GradientData.getViolet(context)
            else -> null
        }
    }

    private fun getColorPickerValue(gradientData: GradientData?): Int {
        val context = requireContext()
        return when (gradientData) {
            GradientData.getRed(context) -> R.id.gradient_red
            GradientData.getYellow(context) -> R.id.gradient_yellow
            GradientData.getGreen(context) -> R.id.gradient_green
            GradientData.getBlue(context) -> R.id.gradient_blue
            GradientData.getViolet(context) -> R.id.gradient_violet
            else -> DEFAULT_GRADIENT_ID
        }
    }

    private fun getParams(): CollectionData? {
        return arguments?.getSerializable(ARG_PARAMS) as? CollectionData
    }

    companion object {

        const val TAG = "EditCollectionFragment"
        private const val ARG_PARAMS = "params"
        private val DEFAULT_GRADIENT_ID = R.id.gradient_yellow

        @JvmStatic
        fun createArgs(params: CollectionData): Bundle {
            return Bundle().apply { putSerializable(ARG_PARAMS, params) }
        }
    }
}
