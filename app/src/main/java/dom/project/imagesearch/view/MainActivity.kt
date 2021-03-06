package dom.project.imagesearch.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.coroutineScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dom.project.imagesearch.base.BaseActivity
import dom.project.imagesearch.base.ItemClickListener
import dom.project.imagesearch.databinding.ActivityMainBinding
import dom.project.imagesearch.model.local.entity.SearchedKeyword
import dom.project.imagesearch.utills.LAST_SEARCH_KEYWORD
import dom.project.imagesearch.utills.createSnackBarMessage
import dom.project.imagesearch.view.adapter.ImageAdapter
import dom.project.imagesearch.view.adapter.ImageItemDecoration
import dom.project.imagesearch.view.adapter.ImageLoadStateAdapter
import dom.project.imagesearch.view.adapter.KeywordHistoryAdapter
import dom.project.imagesearch.view.dialog.DialogExit
import dom.project.imagesearch.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
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
    private val keywordAdapter = KeywordHistoryAdapter(this)
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews(savedInstanceState)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        initList()

        binding.buttonScrollTop.setOnClickListener {
            binding.list.smoothScrollToPosition(0)
        }

        savedInstanceState?.getString(LAST_SEARCH_KEYWORD)?.let { lastKeyword ->
            search(lastKeyword)
            binding.inputSearch.setText(lastKeyword)
        }

        initSearchInput()

        initObservers()
    }

    private fun initList() {
        binding.listKeyword.adapter = keywordAdapter
        keywordAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && keywordAdapter.itemCount == 0
            Log.d("MainActivity", "keyword History : isEmpty = $isListEmpty")
            binding.listKeyword.isVisible = !isListEmpty

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Log.e(TAG, "keyword History loading Error : ${it.error}")
            }
        }
        vm.initKeywordHistory(keywordAdapter)


        binding.list.addItemDecoration(ImageItemDecoration())
        binding.list.adapter =
            adapter.withLoadStateFooter(footer = ImageLoadStateAdapter(binding.root) { adapter.retry() })

        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noResult.isVisible = isListEmpty

            // Only show the list if refresh succeeds.
            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            if (loadState.source.refresh is LoadState.Loading) showProgressing() else hideProgressing()
            // Show the retry state if initial load or refresh fails.
            binding.reload.isVisible = loadState.source.refresh is LoadState.Error

            // PagingSource ????????? ????????? ????????? snackBar??? ????????????.
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                createSnackBarMessage(
                    binding.root,
                    "\uD83D\uDE28 ??????! ???????????????.\n????????? ??????????????????.\n?????? ?????? : ${it.error}"
                )
                Log.e(TAG, "Image Search Error : ${it.error}")
            }
        }

        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // ????????? ?????? ?????? ?????? ??????????????? ???????????? FAB??? ????????????.
                binding.buttonScrollTop.isVisible = dy < 0

                // ???????????? ???????????? ???????????? FAB??? ????????????.
                if ((recyclerView.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                    binding.buttonScrollTop.isVisible = false
                }
            }
        })
    }

    private fun initSearchInput() {

        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateSearchFromInput(true)
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    binding.inputSearch.windowToken,
                    0
                )
                true
            } else false
        }

        binding.inputSearch.addTextChangedListener {
            timer?.cancel()
            object : TimerTask() {
                override fun run() {
                    updateSearchFromInput()
                }
            }.also {
                Timer().apply {
                    timer = this
                    schedule(it, 1000)
                }
            }
        }

        lifecycle.coroutineScope.launch(exceptionHandler) {
            adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.list.scrollToPosition(0) }
        }
    }

    private fun updateSearchFromInput(pressSearchButton: Boolean = false) {
        if (pressSearchButton) timer?.cancel()
        binding.inputSearch.text?.let {
            lifecycle.coroutineScope.launch(Dispatchers.Main+exceptionHandler) {
                binding.list.isVisible = it.isNotBlank()
                binding.keywordBlank.isVisible = it.isBlank()
                if (binding.noResult.isVisible) binding.noResult.isVisible = false
                if (binding.list.isVisible) {
                    search(it.toString())
                }
            }
        }

    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = lifecycle.coroutineScope.launch(exceptionHandler) {
            vm.getSearchImages(keyword).collect {
                adapter.submitData(it)
            }
        }
    }

    private fun initObservers() {
        vm.snackbar.observe(this, {
            it?.let {
                createSnackBarMessage(binding.root, it)
                vm.onSnackbarShown()
            }
        })
        vm.spinner.observe(this, {
            if (it) showProgressing() else hideProgressing()
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_KEYWORD, binding.inputSearch.text.toString())
    }


    override fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        // ?????? ??????
        showDialog(DialogExit(this))
    }

    override fun <T> onClickItem(item: T) {
        when (item) {
            is ImageAdapter.ViewData -> {
                // go to viewer activity
                startActivity(
                    Intent(this, ViewerActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("data", item.data)
                    },
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        item.view,
                        item.view.transitionName
                    ).toBundle()
                )
            }
            is String -> {
                binding.inputSearch.setText(item)
            }
            is SearchedKeyword -> {
                vm.deleteKeyword(item)
            }
        }

    }

    override fun <T> onClick(data: T) {
        if (data is Boolean) {
            if (data) {
                finishAffinity()
            }
        }
    }
}