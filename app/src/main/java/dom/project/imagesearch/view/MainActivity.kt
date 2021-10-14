package dom.project.imagesearch.view

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
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
import dom.project.imagesearch.view.adapter.ImageLoadStateAdapter
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
        binding.list.adapter =
            adapter.withLoadStateFooter(footer = ImageLoadStateAdapter(binding.root) { adapter.retry() })

        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            //todo emptyList show

            // Only show the list if refresh succeeds.
            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            if(loadState.source.refresh is LoadState.Loading) showProgressing() else hideProgressing()
            // Show the retry state if initial load or refresh fails.
            binding.reload.isVisible = loadState.source.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                //todo snackbar
//                Toast.makeText(
//                    this,
//                    "\uD83D\uDE28 Wooops ${it.error}",
//                    Toast.LENGTH_LONG
//                ).show()
            }
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
        binding.inputSearch.text?.let {
            binding.list.isVisible = it.isNotBlank()
            if (binding.list.isVisible) {
                search(it.toString())
            }
            //todo empty list show
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