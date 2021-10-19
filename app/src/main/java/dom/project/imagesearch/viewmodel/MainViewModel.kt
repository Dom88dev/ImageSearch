package dom.project.imagesearch.viewmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import dom.project.imagesearch.base.BaseViewModel
import dom.project.imagesearch.model.local.entity.SearchedKeyword
import dom.project.imagesearch.model.remote.dto.Document
import dom.project.imagesearch.repository.Repository
import dom.project.imagesearch.view.adapter.KeywordHistoryAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class MainViewModel(private val repository: Repository) : BaseViewModel() {

    private var currentSearchKeyword: String? = null

    private var currentSearchResult: Flow<PagingData<Document>>? = null

    private lateinit var keywordHistory: Flow<PagingData<SearchedKeyword>>

    fun getSearchImages(keyword: String): Flow<PagingData<Document>> {
        val lastResult = currentSearchResult
        if (keyword == currentSearchKeyword && lastResult != null) {
            return lastResult
        }
        currentSearchKeyword = keyword
        val newResult = repository.searchImages(keyword).cachedIn(vScope)
        storeKeyword(keyword)
        currentSearchResult = newResult
        return newResult
    }

    fun initKeywordHistory(adapter: KeywordHistoryAdapter) {
        launchBackTaskWithoutProgressing {
            keywordHistory = repository.getKeywordHistory().cachedIn(vScope)
            keywordHistory.collect {
                adapter.submitData(it)
            }
        }
    }

    fun storeKeyword(keyword: String) {
        launchBackTask {
            repository.saveKeyword(keyword)
        }
    }

    fun deleteKeyword(keyword: SearchedKeyword) {
        launchBackTask {
            repository.deleteKeyword(keyword)
        }
    }

}