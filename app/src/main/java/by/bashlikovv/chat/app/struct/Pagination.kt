package by.bashlikovv.chat.app.struct

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val PAGE_HEIGHT = 30

@Parcelize
class Pagination(
    private var bottom: Int = 0,
    private var top: Int = 30
) : Parcelable {

    fun addTop(offset: Int) {
        bottom += offset
        top += offset
    }

    fun getRange(): IntRange {
        val value = top - bottom

        return if (value == PAGE_HEIGHT) {
            bottom..top
        } else if (value > PAGE_HEIGHT) {
            (top - 30)..top
        } else {
            bottom..top
        }
    }
}