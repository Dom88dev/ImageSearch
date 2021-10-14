package dom.project.imagesearch.viewmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import dom.project.imagesearch.base.BaseViewModel
import dom.project.imagesearch.model.remote.dto.Document
import dom.project.imagesearch.repository.Repository
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val repository: Repository) : BaseViewModel() {

    private var currentSearchKeyword: String? = null

    private var currentSearchResult: Flow<PagingData<Document>>? = null

    fun getSearchImages(keyword: String): Flow<PagingData<Document>> {
        val lastResult = currentSearchResult
        if (keyword == currentSearchKeyword && lastResult != null) {
            return lastResult
        }
        currentSearchKeyword = keyword
        val newResult = repository.searchImages(keyword).cachedIn(vScope)
        currentSearchResult = newResult
        return newResult
    }

}