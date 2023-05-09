package com.coffilation.app.view.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.coffilation.app.models.PointData
import com.coffilation.app.databinding.ItemSearchResultsListBinding
import com.coffilation.app.util.delegate.BindingAdapterDelegate
import com.coffilation.app.util.DataSourceAdapter
import com.coffilation.app.util.RatioLayoutManager
import com.coffilation.app.util.data.ListDataSource
import com.coffilation.app.util.viewholder.BindingViewHolder
import com.coffilation.app.view.item.AdapterItem
import com.coffilation.app.view.item.SearchResultItem
import com.coffilation.app.view.item.SearchResultsListItem

/**
 * @author pvl-zolotov on 26.11.2022
 */
class SearchResultsListItemDelegate(
    onPointClick: (PointData) -> Unit,
) : BindingAdapterDelegate<SearchResultsListItem, AdapterItem, ItemSearchResultsListBinding>(
    SearchResultsListItem::class.java,
    ItemSearchResultsListBinding::inflate
) {

    private val adapter = DataSourceAdapter(SearchResultItemDelegate(onPointClick))

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): BindingViewHolder<ItemSearchResultsListBinding> {
        return super.onCreateViewHolder(inflater, parent).apply {
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)
            PagerSnapHelper().attachToRecyclerView(binding.recyclerView)
        }
    }

    override fun onBindViewHolder(
        item: SearchResultsListItem,
        viewHolder: BindingViewHolder<ItemSearchResultsListBinding>,
        payloads: List<Any>
    ) {
        val binding = viewHolder.binding
        binding.recyclerView.layoutManager = RatioLayoutManager(
            binding.root.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        adapter.data = ListDataSource(item.points.map { SearchResultItem(it) })
        adapter.notifyDataSetChanged()

        /*if (item.selectedPoint != null) {
            binding.recyclerView.smoothScrollToPosition(item.points.indexOf(item.selectedPoint))
        }*/

    }
}
