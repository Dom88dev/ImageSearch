package dom.project.imagesearch.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dom.project.imagesearch.R
import dom.project.imagesearch.base.ItemClickListener
import dom.project.imagesearch.databinding.ItemImageBinding
import dom.project.imagesearch.model.remote.dto.Document

class ImageAdapter(private val listener: ItemClickListener) :
    PagingDataAdapter<Document, ImageAdapter.ImageItemViewHolder>(IMAGE_COMPARATOR) {

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        getItem(position)?.let { data ->
            val thumbnail = Glide.with(holder.itemView).load(data.thumbnail)

            Glide.with(holder.itemView)
                .load(data.imgSrc)
                .placeholder(R.drawable.app_icon)
                .thumbnail(thumbnail)
                .transform(CenterCrop(), RoundedCorners(5))
                .into(holder.binding.item)

            holder.binding.item.setOnClickListener {
                listener.onClickItem(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        return ImageItemViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ImageItemViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<Document>() {
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean =
                oldItem.imgSrc == newItem.imgSrc

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean =
                oldItem == newItem

        }
    }
}