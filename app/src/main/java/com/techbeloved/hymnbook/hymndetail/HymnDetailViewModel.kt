package com.techbeloved.hymnbook.hymndetail

import android.content.Context
import android.graphics.Typeface
import android.text.style.AbsoluteSizeSpan
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fueled.snippety.core.Snippety
import com.fueled.snippety.core.Truss
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HymnDetailViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        private val hymnRepository: HymnsRepository) : ViewModel() {

    private val hymnDetailData: MutableLiveData<Lce<HymnDetailItem>> = MutableLiveData()

    /**
     * This will be monitored by the UI fragment
     */
    val hymnDetailLiveData: LiveData<Lce<HymnDetailItem>>
        get() = hymnDetailData

    private val hymnDetailStateConsumer: Consumer<Lce<HymnDetailItem>> = Consumer {
        hymnDetailData.value = it
    }

    private val errorConsumer: Consumer<Throwable> = Consumer {
        Timber.e(it, "Error loading item!")
        hymnDetailData.value = Lce.Error("Error loading item!\n${it.message}")
    }

    private val disposables = CompositeDisposable()
    fun loadHymnDetail(hymnNo: Int) {
        val disposable =
                hymnRepository.getHymnDetailByNumber(hymnNo)
                        .compose(getDetailUiModel())
                        .compose(getDetailUiState())
                        .startWith(Lce.Loading(true))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(hymnDetailStateConsumer, errorConsumer)

        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    /*
     * Use cases - Converting the hymn detail coming from the database to UI model that is suitable for display
     */

    @VisibleForTesting
    fun getDetailUiModel(): FlowableTransformer<HymnDetail, HymnDetailItem> = FlowableTransformer { upstream ->
        upstream.map { detail ->
            HymnDetailItem(
                    detail.num,
                    detail.title,
                    detail.topic,
                    detail.richContent
            )
        }
    }

    @VisibleForTesting
    fun getDetailUiState(): FlowableTransformer<HymnDetailItem, Lce<HymnDetailItem>> = FlowableTransformer { upstream ->
        upstream.map { Lce.Content(it) }
    }

    /**
     * Use to construct rich text content that can be displayed in our hymn detail textview
     * Makes use of [Truss] by Jake Wharton [https://gist.github.com/JakeWharton/11274467]
     * and [Snippety] to create beautiful spannable
     */
    private val HymnDetail.richContent: CharSequence
        get() {
            val leadWidth = 0/*getResources().getDimensionPixelOffset(R.dimen.space_medium)*/
            val gapWidth = context.resources.getDimensionPixelOffset(R.dimen.space_xlarge)
            val largeTextSize = context.resources.getDimensionPixelSize(R.dimen.text_hymnbook_large)

            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)


            val truss = Truss()

            // Add title and hymn number
            truss.pushSpan(AbsoluteSizeSpan(largeTextSize))
                    .pushSpan(LeadingMarginSpan.Standard(0, gapWidth * 2))
                    .append("$num.   $title")
                    .popSpan()
                    .popSpan()
            truss.newParagraph()

            // Add verses and chorus
            this.verses.forEachIndexed { i, verse ->
                truss.pushSpan(LeadingMarginSpan.Standard(0, gapWidth))
                        .pushSpan(Snippety().number(leadWidth, gapWidth, i + 1))
                        .append(verse.firstLine)
                        .popSpan()
                        .popSpan()
                        .pushSpan(LeadingMarginSpan.Standard(leadWidth + gapWidth, leadWidth + gapWidth + gapWidth))
                        .appendln(verse.otherLines)
                        .appendln()

                if (chorus != null) {
                    truss.pushSpan(Snippety().fontStyle(Snippety.FontStyle.ITALIC))
                            //.pushSpan(Snippety().textColor(context.composeResources.getColor(R.color.colorFadedText)))
                            .appendln(chorus)
                            .appendln()
                            //.popSpan()
                            .popSpan()
                }
                truss.popSpan()
            }

            // Add attribution
            if (this.attribution != null) {
                if (this.attribution!!.lyricsBy != null) {

                    truss.pushSpan(RelativeSizeSpan(0.7f))
                            .append("Lyrics by:  ", StyleSpan(Typeface.BOLD_ITALIC))
                            .appendln(attribution?.lyricsBy)
                            .newLine()
                            .append("Music by:  ", StyleSpan(Typeface.BOLD_ITALIC))
                            .appendln(attribution?.musicBy)
                            .popSpan()
                } else if (this.attribution!!.credits != null) {
                    truss.pushSpan(RelativeSizeSpan(0.7f))
                            .append("Credits:  ", StyleSpan(Typeface.BOLD_ITALIC))
                            .appendln(attribution?.credits)
                            .popSpan()
                }
            }

            return truss.build()
        }

    /**
     * Only used to get the first line of a verse or stanza of a hymn
     */
    private val String.firstLine: String
        get() {
            if (this.indexOf("\n") == -1) {
                Timber.i(this)
                return this
            }
            return this.substring(0, this.indexOf("\n"))
        }

    /**
     * Only used to get other lines in a hymn verse apart from the first
     */
    private val String.otherLines: String
        get() {
            if (this.indexOf("\n") == -1) {
                return this
            }
            return this.substring(this.indexOf("\n"))
        }

}