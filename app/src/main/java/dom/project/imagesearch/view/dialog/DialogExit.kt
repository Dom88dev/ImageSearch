package dom.project.imagesearch.view.dialog

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import dom.project.imagesearch.R
import dom.project.imagesearch.base.BaseDialog
import dom.project.imagesearch.base.DialogListener
import dom.project.imagesearch.databinding.DialogExitBinding

class DialogExit(dialogListener: DialogListener) : BaseDialog<DialogExitBinding>(dialogListener) {

    private var mp: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            mp = MediaPlayer.create(it, R.raw.pink_soldiers).apply {
                isLooping = true
                start()
            }
        }

        view.findViewById<ImageButton>(R.id.button_o).setOnClickListener {
            mp?.stop()
            listener.onClick(true)
            dismiss()
        }
        view.findViewById<ImageButton>(R.id.button_x).setOnClickListener {
            mp?.stop()
            listener.onClick(false)
            dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        mp?.release()
        mp = null
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        setBinding(DialogExitBinding.inflate(inflater, container, false))
    }
}