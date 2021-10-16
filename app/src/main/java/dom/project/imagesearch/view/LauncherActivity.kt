package dom.project.imagesearch.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import dom.project.imagesearch.R
import dom.project.imagesearch.base.BaseActivity
import dom.project.imagesearch.databinding.ActivityLauncherBinding

class LauncherActivity : BaseActivity<ActivityLauncherBinding>() {

    private var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mp = MediaPlayer.create(this, R.raw.way_back_then_cut).apply {
            start()
            setOnCompletionListener {
                it.stop()
                startActivity(
                    Intent(this@LauncherActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@LauncherActivity)
                        .toBundle()
                )
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mp?.release()
        mp = null
    }

    override fun initViewBinding() {
        binding = ActivityLauncherBinding.inflate(layoutInflater)
    }
}