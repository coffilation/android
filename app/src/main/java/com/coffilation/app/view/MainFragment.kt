package com.coffilation.app.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coffilation.app.R
import com.coffilation.app.databinding.FragmentMapBinding
import com.coffilation.app.models.CollectionData
import com.coffilation.app.models.PointData
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.OnEndReachedListener
import com.coffilation.app.util.diffutil.AsyncListDifferDataSource
import com.coffilation.app.util.dpToPx
import com.coffilation.app.util.getBoundingBox
import com.coffilation.app.util.hideKeyboard
import com.coffilation.app.util.setBottomPadding
import com.coffilation.app.util.toMapPoint
import com.coffilation.app.view.delegate.CollectionInfoItemDelegate
import com.coffilation.app.view.delegate.DragHandleItemDelegate
import com.coffilation.app.view.delegate.EmptyItemDelegate
import com.coffilation.app.view.delegate.ErrorItemDelegate
import com.coffilation.app.view.delegate.LoadingItemDelegate
import com.coffilation.app.view.delegate.PointCollectionItemDelegate
import com.coffilation.app.view.delegate.PointInfoItemDelegate
import com.coffilation.app.view.delegate.PublicCollectionsListItemDelegate
import com.coffilation.app.view.delegate.SearchButtonItemDelegate
import com.coffilation.app.view.delegate.SearchButtonWithNavigationItemDelegate
import com.coffilation.app.view.delegate.SearchInputItemDelegate
import com.coffilation.app.view.delegate.SearchResultsListItemDelegate
import com.coffilation.app.view.delegate.SearchSuggestionItemDelegate
import com.coffilation.app.view.delegate.UserCollectionItemDelegate
import com.coffilation.app.view.delegate.UserCollectionsHeaderItemDelegate
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.viewstate.MainViewState
import com.coffilation.app.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Float.max


/**
 * @author pvl-zolotov on 29.10.2022
 */
class MainFragment : Fragment() {

    private var binding: FragmentMapBinding? = null
    private val viewModel: MainViewModel by viewModel()

    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null
    private lateinit var autoLoadingListener: OnEndReachedListener

    private lateinit var dataSource: AsyncListDifferDataSource<AdapterItem>
    private var bottomSheetConfig: MainViewState.BottomSheetConfig? = null
    private var savedMapObjects: List<PlacemarkMapObject>? = null

    private val mapObjectTapListener = MapObjectTapListener { mapObject: MapObject, point: Point ->
        val pointData = mapObject.userData as PointData
        zoomToMapPoint(pointData)
        viewModel.selectPointInList(pointData)
        true
    }

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

        binding?.mapview?.map?.move(
            CameraPosition(Point(59.9386, 30.3141), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.3f),
            null
        )
        binding?.mapview?.map?.apply {
            logo.setPadding(Padding(dpToPx(requireContext(), 10f), dpToPx(requireContext(), 110f)))
        }

        val dataAdapter = DataSourceAdapter(
            DragHandleItemDelegate(),
            SearchButtonItemDelegate(
                onSearchClick = {
                    getBoundingBox()?.also(viewModel::changeModeToSearch)
                },
                onUserClick = {}
            ),
            LoadingItemDelegate(),
            ErrorItemDelegate(viewModel::onRetryPressed),
            EmptyItemDelegate(),
            PublicCollectionsListItemDelegate(
                onCollectionClick = { viewModel.changeModeToCollectionView(it, null) },
                onRetryClick = viewModel::onPublicCollectionsListRetryPressed,
                autoLoadingListener = OnEndReachedListener(3, viewModel::onPublicCollectionsListEndReached)
            ),
            UserCollectionsHeaderItemDelegate {
                openEditCollectionFragment(null)
            },
            UserCollectionItemDelegate {
                viewModel.changeModeToCollectionView(it, null)
            },
            SearchInputItemDelegate(
                onInputChanged = viewModel::setSearchQuery,
                onSearchStart = {
                    getBoundingBox()?.also(viewModel::showSearchResults)
                },
                onBackClick = viewModel::goToPreviousMode,
            ),
            SearchSuggestionItemDelegate { suggest ->
                getBoundingBox()?.also { boundingBox ->
                    viewModel.applySearchSuggestion(suggest, boundingBox)
                }
            },
            SearchButtonWithNavigationItemDelegate(
                onSearchClick = {
                    getBoundingBox()?.also(viewModel::changeModeToSearch)
                },
                onBackClick = viewModel::goToPreviousMode,
            ),
            SearchResultsListItemDelegate(viewModel::changeModeToPointView, ::zoomToMapPoint),
            PointInfoItemDelegate(viewModel::goToPreviousMode),
            PointCollectionItemDelegate(viewModel::onCollectionPointModified),
            CollectionInfoItemDelegate(
                onEditCollectionClick = ::openEditCollectionFragment,
                onRemoveCollectionClick = viewModel::removeCollection,
                onBackClick = viewModel::goToPreviousMode,
            ),
        )
        autoLoadingListener = OnEndReachedListener(3, viewModel::onUserCollectionsListEndReached)

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = dataAdapter
            addOnScrollListener(autoLoadingListener)
        }
        dataSource = AsyncListDifferDataSource.createFor(dataAdapter)
        dataAdapter.data = dataSource

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (
                    newState == BottomSheetBehavior.STATE_COLLAPSED &&
                    bottomSheetConfig?.minHeight == MainViewState.BottomSheetState.FULLSCREEN
                ) {
                    viewModel.goToPreviousMode()
                }
                if (
                    newState == BottomSheetBehavior.STATE_EXPANDED &&
                    bottomSheetConfig?.minHeight == MainViewState.BottomSheetState.PEEKED_MEDIUM
                ) {
                    viewModel.goToPreviousMode()
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!viewModel.goToPreviousMode()) {
                requireActivity().finish()
            }
        }

        setFragmentResultListener(REQUEST_KEY_EDIT_COLLECTION) { requestKey, bundle ->
            val mode = bundle.getString(KEY_COLLECTIONS_SAVING_MODE)
            when (mode) {
                COLLECTIONS_CREATED -> viewModel.updateUserCollections()
                COLLECTIONS_EDITED -> {
                    viewModel.updateUserCollections()
                    (bundle.getSerializable(KEY_COLLECTIONS_SAVING_RESULT) as? CollectionData)?.also { collection ->
                        viewModel.changeModeToCollectionView(collection, null)
                    }
                }
            }
        }

        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                MainViewModel.Action.ShowSignIn -> {
                    parentFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<SignInFragment>(R.id.fragment_container_view)
                    }
                }
                MainViewModel.Action.ShowPointModifyError -> {
                    Toast.makeText(requireContext(), R.string.point_collection_modify_error, Toast.LENGTH_LONG).show()
                }
                MainViewModel.Action.ShowRemoveCollectionError -> {
                    Toast.makeText(requireContext(), R.string.collection_remove_error, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            dataSource.items = state.items
            binding?.recyclerView?.adapter?.notifyDataSetChanged()
            autoLoadingListener.reset(state.autoLoadingEnabled)
            binding?.recyclerView?.addOnLayoutChangeListener(autoLoadingListener)

            bottomSheetConfig = state.bottomSheetConfig

            if (!state.allowShowKeyboard) {
                hideKeyboard(requireActivity())
            }

            binding?.bottomSheet?.post {
                binding?.bottomSheet?.requestLayout()
                binding?.bottomSheet?.invalidate()

                if (
                    bottomSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED &&
                    bottomSheetConfig?.minHeight == MainViewState.BottomSheetState.FULLSCREEN
                ) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
                if (
                    bottomSheetConfig?.minHeight == MainViewState.BottomSheetState.PEEKED_COMPACT ||
                    bottomSheetConfig?.minHeight == MainViewState.BottomSheetState.PEEKED_MEDIUM
                ) {
                    (context?.let { bottomSheetConfig?.minHeight?.valueOf(it) } as? UiBottomSheetState.Peeked)?.height?.also {
                        bottomSheetBehavior?.peekHeight = it
                        binding?.mapview?.apply {
                            setBottomPadding(width().toFloat(), height().toFloat(), it.toFloat())
                        }
                    }
                    if (
                        bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED &&
                        bottomSheetConfig?.maxHeight != MainViewState.BottomSheetState.FULLSCREEN
                    ) {
                        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

            binding?.mapview?.map?.apply {
                savedMapObjects?.forEach {
                    it.removeTapListener(mapObjectTapListener)
                    mapObjects.remove(it)
                }
                mapObjects.clear()
                val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.ic_location)
                val selectedImageProvider = ImageProvider.fromResource(requireContext(), R.drawable.ic_location_selected)
                val points = state?.points
                points?.forEach { pointData ->
                    mapObjects.addPlacemark(pointData.toMapPoint(), imageProvider).apply {
                        userData = pointData
                        addTapListener(mapObjectTapListener)
                    }
                }
                if (points?.isNotEmpty() == true) {
                    if (state.selectedPoint == null) {
                        points.map { it.toMapPoint() }.getBoundingBox().also { boundingBox ->
                            val cameraPosition = cameraPosition(boundingBox)
                            val newCameraPosition = CameraPosition(
                                cameraPosition.target,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt
                            )
                            move(
                                newCameraPosition,
                                Animation(Animation.Type.SMOOTH, 0.3f),
                                null
                            )
                        }
                    } else {
                        zoomToMapPoint(state.selectedPoint)
                    }
                }
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

    private fun openEditCollectionFragment(collection: CollectionData?) {
        val fragment = EditCollectionFragment()
        collection?.also { data ->
            fragment.arguments = EditCollectionFragment.createArgs(data)
        }
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container_view, fragment, EditCollectionFragment.TAG)
            addToBackStack(null)
        }
    }

    private fun zoomToMapPoint(pointData: PointData) {
        binding?.mapview?.map?.apply {
            val animation = Animation(Animation.Type.SMOOTH, 0.3f)
            move(
                CameraPosition(pointData.toMapPoint(), max(cameraPosition.zoom, MIN_ZOOM), cameraPosition.azimuth, cameraPosition.tilt),
                animation,
                null
            )
        }
    }

    private fun getBoundingBox(): BoundingBox? {
        return binding?.mapview?.map?.visibleRegion?.let {
            BoundingBox(
                it.bottomLeft,
                it.topRight
            )
        }
    }

    private fun MainViewState.BottomSheetState.valueOf(context: Context): UiBottomSheetState {
        return when (this) {
            MainViewState.BottomSheetState.FULLSCREEN -> UiBottomSheetState.Expanded
            MainViewState.BottomSheetState.PEEKED_COMPACT -> UiBottomSheetState.Peeked(dpToPx(context, 100f))
            MainViewState.BottomSheetState.PEEKED_MEDIUM -> UiBottomSheetState.Peeked(dpToPx(context, 240f))
        }
    }

    private sealed class UiBottomSheetState {

        object Expanded : UiBottomSheetState()
        class Peeked(val height: Int) : UiBottomSheetState()
    }

    companion object {

        const val REQUEST_KEY_EDIT_COLLECTION = "requestKeyEditCollection"
        const val KEY_COLLECTIONS_SAVING_MODE = "collectionsSavingMode"
        const val KEY_COLLECTIONS_SAVING_RESULT = "collectionsSavingResult"
        const val COLLECTIONS_CREATED = "collectionCreated"
        const val COLLECTIONS_EDITED = "collectionEdited"
        const val MIN_ZOOM = 13f
    }
}
