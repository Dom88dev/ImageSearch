package dom.project.imagesearch.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import dom.project.imagesearch.R
import dom.project.imagesearch.base.BaseDialog
import dom.project.imagesearch.base.DialogListener
import dom.project.imagesearch.databinding.DialogExitBinding

class DialogExit(dialogListener: DialogListener): BaseDialog<DialogExitBinding>(dialogListener) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.button_o).setOnClickListener {
            listener.onClick(true)
            dismiss()
        }
        view.findViewById<ImageButton>(R.id.button_x).setOnClickListener {
            listener.onClick(false)
            dismiss()
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        setBinding(DialogExitBinding.inflate(inflater, container, false))
    }
}