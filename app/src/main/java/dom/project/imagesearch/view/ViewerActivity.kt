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
import dom.project.imagesearch.utills.createSnackBarMessage
import java.text.SimpleDateFormat
import java.util.*

class ViewerActivity : BaseActivity<ActivityViewerBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        intent.extras?.let {
            it.getParcelable<Document>("data")?.let { data ->

                var textAdditionalInfo = ""
                if (data.provenance.isNotBlank()) textAdditionalInfo += "출처 : ${data.provenance}"
                SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA).let { sdf ->
                    if (textAdditionalInfo.isNotBlank()) textAdditionalInfo += "\n"
                    binding.info.text = "${textAdditionalInfo}일시 : ${sdf.format(data.dateTime)}"
                }

                Glide.with(this).load(data.imgSrc).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        createSnackBarMessage(
                            binding.root,
                            "이미지를 불러오는 중 문제가 발생햇습니다.\uD83D\uDE28\n잠시 후 다시 시도해주십시오.\n${e?.localizedMessage}"
                        )
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