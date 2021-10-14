package dom.project.imagesearch.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*


abstract class BaseViewModel : ViewModel() {

    @JvmField
    val TAG = this::class.java.simpleName

    protected val _snackBar = MutableLiveData<String?>()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String?>
        get() = _snackBar

    protected val _spinner = MutableLiveData<Boolean>(false)

    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    protected val job: Job = SupervisorJob()

    // 에러 발생 시 처리 코드 구현..
    fun errorOccur(errorMsg: String) {
        vScope.launch {
            _snackBar.value = errorMsg
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        var errorMessage = ""
        if (throwable.localizedMessage != null) errorMessage = throwable.localizedMessage!!
        for (stackTrace in throwable.stackTrace) {
            errorMessage += "\nfile : ${stackTrace.fileName} / class : ${stackTrace.className} / line : ${stackTrace.lineNumber}"
        }
        Log.e("COROUTINES_ERROR", "Occur Error : $errorMessage")
        throwable.printStackTrace()
        errorOccur(errorMessage)
    }

    protected val vScope = viewModelScope + job + coroutineExceptionHandler

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackBar.value = null
    }

    protected fun launchBackTask(block: suspend () -> Unit): Unit {
        vScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Exception) {
                Log.e("ViewModel", "launchBackTask: Error = ${error.message}")
                _snackBar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }

    protected fun launchBackTaskWithoutProgressing(block: suspend () -> Unit): Unit {
        vScope.launch {
            try {
                block()
            } catch (error: Exception) {
                Log.e("ViewModel", "launchBackTask: Error = ${error.message}")
                _snackBar.value = error.message
            }
        }
    }

}