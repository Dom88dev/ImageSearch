package dom.project.imagesearch.view

import android.os.Bundle
import dom.project.imagesearch.base.BaseActivity
import dom.project.imagesearch.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroy() {
        finishAffinity()
        super.onDestroy()
    }

    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}