package dom.project.imagesearch.etc

import okhttp3.Request
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

// retrofit을 통한 API 통신 결과를 sealed 클래스로 받을 수 있도록 처리해주는 클래스들

sealed class ApiResult<out T> {
    data class Success<T>(val data: T?) : ApiResult<T>()
    data class Failure(val statusCode: Int?) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}


abstract class CallDelegate<TIn, TOut>(
    protected val proxy: Call<TIn>
) : Call<TOut> {
    override fun execute(): Response<TOut> = throw NotImplementedError()
    final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    final override fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, ApiResult<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<ApiResult<T>>) = proxy.enqueue(object: Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()
                val successResult: ApiResult.Success<T> = ApiResult.Success(body)
                successResult
            } else {
                ApiResult.Failure(code)
            }

            callback.onResponse(this@ResultCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = if (t is IOException) {
                ApiResult.NetworkError
            } else {
                ApiResult.Failure(null)
            }

            callback.onResponse(this@ResultCall, Response.success(result))
        }
    })

    override fun cloneImpl() = ResultCall(proxy.clone())
}

class ResultAdapter(
    private val type: Type
): CallAdapter<Type, Call<ApiResult<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<ApiResult<Type>> = ResultCall(call)
}


class RetrofitApiCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ) = when (getRawType(returnType)) {
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                ApiResult::class.java -> {
                    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                    ResultAdapter(resultType)
                }
                else -> null
            }
        }
        else -> null
    }
}