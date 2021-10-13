package dom.project.imagesearch.view

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityOptionsCompat
import dom.project.imagesearch.R
import dom.project.imagesearch.base.BaseActivity
import dom.project.imagesearch.databinding.ActivityLauncherBinding

class LauncherActivity : BaseActivity<ActivityLauncherBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.root.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.end) {
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }, ActivityOptionsCompat.makeSceneTransitionAnimation(this@LauncherActivity).toBundle())
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}

        })
    }

    override fun initViewBinding() {
        binding = ActivityLauncherBinding.inflate(layoutInflater)
    }
}