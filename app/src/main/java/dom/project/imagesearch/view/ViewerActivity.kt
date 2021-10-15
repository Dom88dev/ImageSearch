package dom.project.imagesearch.view

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dom.project.imagesearch.base.BaseActivity
import dom.project.imagesearch.databinding.ActivityViewerBinding
import dom.project.imagesearch.model.remote.dto.Document
import java.text.SimpleDateFormat
import java.util.*

class ViewerActivity : BaseActivity<ActivityViewerBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        intent.extras?.let {
            it.getParcelable<Document>("data")?.let { data ->

                SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA).let { sdf ->
                    binding.info.text = "출처 : ${data.provenance}\n일시 : ${sdf.format(data.dateTime)}"
                }

                Glide.with(this).load(data.imgSrc).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                }).into(binding.image)
            }
        }
    }

    override fun initViewBinding() {
        binding = ActivityViewerBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        finishAfterTransition()
    }
}