package com.coffilation.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coffilation.app.R
import com.coffilation.app.databinding.FragmentMapBinding
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.diffutil.AsyncListDifferDataSource
import com.coffilation.app.util.getBackStackData
import com.coffilation.app.view.delegate.ErrorItemDelegate
import com.coffilation.app.view.delegate.LoadingItemDelegate
import com.coffilation.app.view.delegate.PublicCollectionsListItemDelegate
import com.coffilation.app.view.delegate.SearchButtonItemDelegate
import com.coffilation.app.view.delegate.UserCollectionItemDelegate
import com.coffilation.app.view.delegate.UserCollectionsHeaderItemDelegate
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * @author pvl-zolotov on 29.10.2022
 */
class MainFragment : Fragment() {

    private var binding: FragmentMapBinding? = null
    private val viewModel: MainViewModel by viewModel()

    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null
    private var bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED

    private lateinit var dataSource: AsyncListDifferDataSource<AdapterItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MapKitFactory.initialize(requireContext())

        /*binding?.mapview?.map?.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )*/

        val dataAdapter = DataSourceAdapter(
            SearchButtonItemDelegate({}, {}),
            LoadingItemDelegate(),
            ErrorItemDelegate(),
            PublicCollectionsListItemDelegate({}),
            UserCollectionsHeaderItemDelegate {
                findNavController(this).navigate(R.id.action_MainFragment_to_EditCollectionFragment)
            },
            UserCollectionItemDelegate({}),
        )
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = dataAdapter
        }
        dataSource = AsyncListDifferDataSource.createFor(dataAdapter)
        dataAdapter.data = dataSource

        if (getBackStackData<Boolean>(KEY_USER_COLLECTIONS_CHANGED) == true) {
            viewModel.updateUserCollections()
        }

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                bottomSheetState = newState
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }
        binding?.bottomSheet?.let {
            val bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetBehavior.state = bottomSheetState
            bottomSheetCallback?.also(bottomSheetBehavior::addBottomSheetCallback)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            dataSource.setItems(state.items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.bottomSheet?.let {
            val bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetCallback?.also(bottomSheetBehavior::addBottomSheetCallback)
        }
    }

    override fun onStop() {
        binding?.mapview?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding?.mapview?.onStart()
    }

    companion object {

        const val KEY_USER_COLLECTIONS_CHANGED = "userCollectionsChanged"
    }
}
