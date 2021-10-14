package dom.project.imagesearch.view

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.coroutineScope
import androidx.paging.LoadState
import dom.project.imagesearch.base.BaseActivity
import dom.project.imagesearch.base.ItemClickListener
import dom.project.imagesearch.databinding.ActivityMainBinding
import dom.project.imagesearch.model.remote.dto.Document
import dom.project.imagesearch.utills.LAST_SEARCH_KEYWORD
import dom.project.imagesearch.view.adapter.ImageAdapter
import dom.project.imagesearch.view.adapter.ImageItemDecoration
import dom.project.imagesearch.viewmodel.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(), ItemClickListener {

    private val vm: MainViewModel by viewModel()
    private var searchJob: Job? = null
    private val adapter = ImageAdapter(this)
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        val lastKeyword = savedInstanceState?.getString(LAST_SEARCH_KEYWORD) ?: "BRANDI"
        search(lastKeyword)
        initSearch(lastKeyword)
    }

    private fun initViews() {
        binding.list.addItemDecoration(ImageItemDecoration())
        binding.list.adapter = adapter

        binding.buttonSearch.setOnClickListener {
            updateSearchFromInput()
        }


    }

    private fun initSearch(keyword: String) {
        binding.inputSearch.setText(keyword)

        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateSearchFromInput()
                true
            } else false
        }

        binding.inputSearch.addTextChangedListener {
            //todo timer 처리..
        }

        lifecycle.coroutineScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.list.scrollToPosition(0) }
        }
    }

    private fun updateSearchFromInput() {
        //todo timer 처리..
        timer?.cancel()
        binding.inputSearch.text.let {
            if (it.isNotBlank()) {
                search(it.toString())
            }
        }

    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = lifecycle.coroutineScope.launch {
            vm.getSearchImages(keyword).collect {
                adapter.submitData(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_KEYWORD, binding.inputSearch.text.toString())
    }

    override fun onDestroy() {
        finishAffinity()
        super.onDestroy()
    }

    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun <T> onClickItem(item: T) {
        if (item is Document) {
            // todo go to detail activity
        }
    }
}