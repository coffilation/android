package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.coffilation.app.databinding.ItemSearchResultsListBinding
import com.coffilation.app.models.PointData
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.RatioLayoutManager
import com.coffilation.app.util.UserScrollListener
import com.coffilation.app.util.data.ListDataSource
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.SearchResultItem
import com.coffilation.app.view.item.SearchResultsListItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class SearchResultsListItemDelegate(
    onPointClick: (PointData) -> Unit,
    private val onScrolled: (PointData) -> Unit,
) : BindingAdapterDelegate<SearchResultsListItem, AdapterItem, ItemSearchResultsListBinding>(
    SearchResultsListItem::class.java,
    ItemSearchResultsListBinding::inflate
) {

    private val adapter = DataSourceAdapter(SearchResultItemDelegate(onPointClick))
    private lateinit var onScrollListener: UserScrollListener

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemSearchResultsListBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = RatioLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            PagerSnapHelper().attachToRecyclerView(binding.recyclerView)

            onScrollListener = UserScrollListener {
                getBoundData<List<PointData>>()?.also { points ->
                    val pos = (binding.recyclerView.layoutManager as RatioLayoutManager).findFirstCompletelyVisibleItemPosition()
                    if (pos != -1) {
                        onScrolled.invoke(points[pos])
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(
        item: SearchResultsListItem,
        viewHolder: BindingViewHolder<ItemSearchResultsListBinding>,
        payloads: List<Any>
    ) {
        viewHolder.binding.recyclerView.addOnScrollListener(onScrollListener)
        val binding = viewHolder.binding
        val points = item.points
        viewHolder.setBoundData(points)
        adapter.data = ListDataSource(points.map { SearchResultItem(it) })
        adapter.notifyDataSetChanged()
        val pos = points.indexOf(item.scrollToPoint).takeIf { it != -1 }
        pos?.also { binding.recyclerView.smoothScrollToPosition(it) }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val binding = ((holder as? BindingViewHolder<*>)?.binding as? ItemSearchResultsListBinding)
        binding?.recyclerView?.removeOnScrollListener(onScrollListener)
    }
}
