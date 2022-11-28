package com.coffilation.app.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coffilation.app.R
import com.coffilation.app.databinding.FragmentMapBinding
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.diffutil.AsyncListDifferDataSource
import com.coffilation.app.util.dpToPx
import com.coffilation.app.view.delegate.ErrorItemDelegate
import com.coffilation.app.view.delegate.LoadingItemDelegate
import com.coffilation.app.view.delegate.PublicCollectionsListItemDelegate
import com.coffilation.app.view.delegate.SearchButtonItemDelegate
import com.coffilation.app.view.delegate.SearchInputItemDelegate
import com.coffilation.app.view.delegate.SearchSuggestionItemDelegate
import com.coffilation.app.view.delegate.UserCollectionItemDelegate
import com.coffilation.app.view.delegate.UserCollectionsHeaderItemDelegate
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.viewstate.MainViewState
import com.coffilation.app.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * @author pvl-zolotov on 29.10.2022
 */
class MainFragment : Fragment() {

    private var binding: FragmentMapBinding? = null
    private val viewModel: MainViewModel by viewModel()

    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    private lateinit var dataSource: AsyncListDifferDataSource<AdapterItem>
    private var minBottomSheetHeightAllowed: MainViewState.BottomSheetState? = null

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
        binding?.mapview?.map?.apply {
            logo.setPadding(Padding(dpToPx(requireContext(), 10f), dpToPx(requireContext(), 110f)))
            //isFastTapEnabled = true
            addTapListener { geoObjectTapEvent ->
                /*val t = geoObjectTapEvent.geoObject.descriptionText
                Toast.makeText(requireContext(), t, Toast.LENGTH_SHORT).show()
                true*/
                val selectionMetadata = geoObjectTapEvent
                    .geoObject
                    .metadataContainer
                    .getItem(GeoObjectSelectionMetadata::class.java)

                if (selectionMetadata != null) {
                    selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
                }
                selectionMetadata != null
            }
            addInputListener(object : InputListener {

                override fun onMapTap(map: Map, point: Point) {

                }

                override fun onMapLongTap(map: Map, point: Point) {

                }
            })
        }

        val dataAdapter = DataSourceAdapter(
            SearchButtonItemDelegate(
                onSearchClick = {
                    val visibleRegion = binding?.mapview?.map?.visibleRegion
                    visibleRegion?.also {
                        val boundingBox = BoundingBox(
                            it.bottomLeft,
                            it.topRight
                        )
                        viewModel.changeModeToSearch(boundingBox)
                    }
                },
                onUserClick = {}
            ),
            LoadingItemDelegate(),
            ErrorItemDelegate(),
            PublicCollectionsListItemDelegate({}),
            UserCollectionsHeaderItemDelegate {
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<EditCollectionFragment>(R.id.fragment_container_view)
                    addToBackStack(null)
                }
            },
            UserCollectionItemDelegate({}),
            SearchInputItemDelegate(
                onInputChanged = viewModel::setSearchQuery,
                onSearchStart = {}
            ),
            SearchSuggestionItemDelegate(viewModel::applySearchSuggestion)
        )
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = dataAdapter
        }
        dataSource = AsyncListDifferDataSource.createFor(dataAdapter)
        dataAdapter.data = dataSource

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (
                    newState == BottomSheetBehavior.STATE_COLLAPSED &&
                    minBottomSheetHeightAllowed == MainViewState.BottomSheetState.FULLSCREEN
                ) {
                    viewModel.changeModeToCollections()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        }
        var bottomSheetBehavior: BottomSheetBehavior<*>? = null
        binding?.bottomSheet?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(it).also { behavior ->
                bottomSheetCallback?.also(behavior::addBottomSheetCallback)
            }
        }

        /*requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                this.handleOnBackPressed()
            }
        }*/

        setFragmentResultListener(REQUEST_KEY_EDIT_COLLECTION) { requestKey, bundle ->
            if (bundle.getBoolean(KEY_USER_COLLECTIONS_CHANGED)) {
                viewModel.updateUserCollections()
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            dataSource.setItems(state.items)
            minBottomSheetHeightAllowed = state.minBottomSheetHeightAllowed

            if (
                bottomSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED &&
                minBottomSheetHeightAllowed == MainViewState.BottomSheetState.FULLSCREEN
            ) {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.bottomSheet?.let {
            val bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetCallback?.also(bottomSheetBehavior::removeBottomSheetCallback)
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

        const val REQUEST_KEY_EDIT_COLLECTION = "requestKeyEditCollection"
        const val KEY_USER_COLLECTIONS_CHANGED = "userCollectionsChanged"
    }
}
