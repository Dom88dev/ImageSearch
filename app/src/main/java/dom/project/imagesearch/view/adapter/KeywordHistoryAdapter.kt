package dom.project.imagesearch.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dom.project.imagesearch.base.ItemClickListener
import dom.project.imagesearch.databinding.ItemSearchedKeywordBinding
import dom.project.imagesearch.model.local.entity.SearchedKeyword

class KeywordHistoryAdapter(private val listener: ItemClickListener) :
    PagingDataAdapter<SearchedKeyword, KeywordHistoryAdapter.KeywordViewHolder>(KEYWORD_COMPARATOR) {

    inner class KeywordViewHolder(val binding: ItemSearchedKeywordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        getItem(position)?.let {
            holder.binding.keywordSearched.text = it.keyword
            holder.binding.keywordSearched.setOnClickListener { _ ->
                listener.onClickItem(it.keyword)
            }
            holder.binding.delete.setOnClickListener { _ ->
                listener.onClickItem(it)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder =
        KeywordViewHolder(
            ItemSearchedKeywordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    companion object {
        private val KEYWORD_COMPARATOR = object : DiffUtil.ItemCallback<SearchedKeyword>() {
            override fun areItemsTheSame(
                oldItem: SearchedKeyword,
                newItem: SearchedKeyword
            ): Boolean =
                oldItem.keyword == newItem.keyword

            override fun areContentsTheSame(
                oldItem: SearchedKeyword,
                newItem: SearchedKeyword
            ): Boolean =
                oldItem == newItem

        }
    }
}