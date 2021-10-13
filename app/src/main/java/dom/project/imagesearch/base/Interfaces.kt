package dom.project.imagesearch.base


interface DialogListener {
    fun <T> onClick(data: T)
}

interface ItemClickListener {
    fun <T> onClickItem(item: T)
}