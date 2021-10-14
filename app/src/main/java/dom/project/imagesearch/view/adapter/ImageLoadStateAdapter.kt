package dom.project.imagesearch.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import dom.project.imagesearch.R
import dom.project.imagesearch.databinding.ItemLoadStateImageBinding

class ImageLoadStateAdapter(private val view: View, private val retry: () -> Unit): LoadStateAdapter<ImageLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ImageLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState, view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ImageLoadStateViewHolder {
        return ImageLoadStateViewHolder.create(parent, retry)
    }

}

class ImageLoadStateViewHolder(
    private val binding: ItemLoadStateImageBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.reload.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState, view: View) {
        if (loadState is LoadState.Error) {
            //todo create snackbar
            loadState.error.localizedMessage

        }
        binding.loading.isVisible = loadState is LoadState.Loading
        binding.reload.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ImageLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_load_state_image, parent, false)
            val binding = ItemLoadStateImageBinding.bind(view)
            return ImageLoadStateViewHolder(binding, retry)
        }
    }
}