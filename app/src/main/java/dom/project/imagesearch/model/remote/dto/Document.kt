package dom.project.imagesearch.model.remote.dto

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Document(
    @SerializedName("collection")       // 컬렉션(카테고리 느낌)
    val category: String,

    @SerializedName("thumbnail_url")    // 미리보기 이미지 URL
    val thumbnail: String,

    @SerializedName("image_url")        // 이미지 URL
    val imgSrc: String,

    @SerializedName("width")            // 이미지의 가로 길이
    val width: Int,

    @SerializedName("height")           // 이미지의 세로 길이
    val height: Int,

    @SerializedName("display_sitename") // 출처
    val provenance: String,

    @SerializedName("doc_url")          // 문서 URL
    val infoUrl: String,

    @SerializedName("datetime")         // 문서 작성시간
    val dateTime: Date
): Parcelable {}