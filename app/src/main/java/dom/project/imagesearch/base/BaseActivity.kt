package dom.project.imagesearch.base

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import dom.project.imagesearch.R


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity(), DialogListener {

    @JvmField
    val TAG = this::class.java.simpleName

    protected lateinit var binding: T

    // 뷰바인딩 객체를 생성해 binding객체를 초기화해준다.
    abstract fun initViewBinding()


    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setContentView(binding.root)

        initAndAddProgressBar()
    }


    /*  fagmentDialog 로 만든 로딩 다이얼로그는 프로그레스 다이얼로그 추가 transaction이 미처 execute 되기전에
        비동기로 연속적인 프로그레스 다이얼로그 추가 처리를 하게 되면 fragment is already added 라는 exception 발생.
        해당 에러를 방지하는 것이 멀테 쓰레드중 완벽하게는 힘들다고 판단하여 프로그레스 다이얼로그를 없애고
        프로그레스바 객체를 생성해 액티비티의 레이아웃에 add 시켜 visibility를 조정해 로딩상태를 표시하도록 수정.*/
    private val progressBar: ProgressBar by lazy {
        ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleLarge
        )
    }

    // BaseActivity를 상속한 Activity에서 보다 간편하게 프로그레스바를 추가하기 위해 만들어진 함수
    // 필수 구현인 initViewBinding() 함수 내에서 binding 구현 후 호출해주면 된다.
    protected fun initAndAddProgressBar() {
        progressBar.id = android.R.id.progress
        progressBar.isIndeterminate = true
        progressBar.visibility = View.GONE
        progressBar.indeterminateDrawable.setTint(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                resources.getColor(R.color.pink, null)
            else resources.getColor(R.color.pink)
        )
        if (binding.root is ConstraintLayout) {
            val layoutParam = ConstraintLayout.LayoutParams(0, 0)
            layoutParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParam.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            (binding.root as ConstraintLayout).addView(progressBar, layoutParam)
        } else if (binding.root is ViewGroup) {
            val children = (binding.root as ViewGroup).children
            children.find { it is ConstraintLayout }?.let {
                if (it is ConstraintLayout) {
                    val layoutParam = ConstraintLayout.LayoutParams(it.layoutParams)
                    layoutParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParam.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    it.addView(progressBar, layoutParam)
                }
            }
        }
    }

    // 프로그레스바를 보이게 하고 로딩 중 사용자의 interaction을 막기 위해 window에 NOt_TOUCHABLE 플래그를 세팅.
    fun showProgressing() {
        if (progressBar.visibility == View.GONE) {
            progressBar.visibility = View.VISIBLE
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    // 프로그레스바를 감추며 사용자가 다시 interaction을 할 수 있도록 showProgressing()에서 설정한 플래그를 제거.
    fun hideProgressing() {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun hideProgressingByTrouble(error: String = "") {
        hideProgressing()
//        Utils.createSnackBarMessage(this.binding.root, if (error.isBlank()) "문제가 발생했습니다.\n잠시 후 다시 시도해주십시오." else error)
    }

    fun showDialog(dialog: DialogFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            transaction.remove(prev)
        }
        transaction.addToBackStack(null)
        dialog.show(transaction, "dialog")
    }


    override fun <T> onClick(data: T) {
        // implement in sub classes
    }
}