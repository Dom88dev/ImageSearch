package dom.project.imagesearch.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import dom.project.imagesearch.R

abstract class BaseDialog<T : ViewBinding>(private val dialogListener: DialogListener) :
    DialogFragment() {

    @JvmField
    val TAG = this::class.java.simpleName

    protected lateinit var binding: T
        private set

    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?)

    fun setBinding(viewBinding: T) {
        binding = viewBinding
    }

    protected lateinit var listener: DialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AddPatientDialogListener so we can send events to the host
            listener = dialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement DialogAlertTaskListener")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        dialog?.window?.statusBarColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(R.color.half_transparent_black, null)
        } else {
            resources.getColor(R.color.half_transparent_black)
        }
    }

}