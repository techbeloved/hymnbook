package com.techbeloved.hymnbook.sheetmusic

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.databinding.FragmentSheetMusicDetailBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class SheetMusicDetailFragment : Fragment() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private var hymnId: Int = 1
    private lateinit var binding: FragmentSheetMusicDetailBinding
    private lateinit var viewModel: SheetMusicDetailViewModel

    private val hymnDetailObserver: Observer<Lce<OnlineHymn>> = Observer { hymnLce ->
        when (hymnLce) {
            is Lce.Loading -> showContentLoading(true)
            is Lce.Error -> showError(hymnLce.error)
            is Lce.Content -> showContentDetail(hymnLce.content)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sheet_music_detail, container, false)

        binding.textviewErrorLoadingDetail.setOnClickListener { v ->
            loadHymnDetail(hymnId)
        }

        viewModel.hymnDetail.observe(this, hymnDetailObserver)


        if (arguments != null && arguments?.containsKey(ARG_HYMN_INDEX) != null) {
            val hymnIndexToBeLoaded = arguments!!.getInt(ARG_HYMN_INDEX)
            Timber.i("Received an initial id: $hymnIndexToBeLoaded")
            hymnId = hymnIndexToBeLoaded
            loadHymnDetail(hymnIndexToBeLoaded)
        } else {
            showError("No hymn index supplied")
        }
        return binding.root
    }

    private fun loadHymnDetail(indexToBeLoaded: Int) {
        viewModel.loadHymnDetail(indexToBeLoaded)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = SheetMusicDetailViewModel.Factory(Injection.provideOnlineRepo().value, Injection.provideAppContext())
        viewModel = ViewModelProviders.of(this, factory)[SheetMusicDetailViewModel::class.java]
    }

    private fun showContentDetail(content: OnlineHymn) {
        binding.pdfViewSheetMusicDetail.fromUri(Uri.parse(content.sheetMusicUrl))
                .onError { error ->
                    Timber.w(error, "Error loading document")
                    showError("Error loading document. Tap to retry. ${error.message}")
                }
                .onPageError { page, t ->
                    Timber.w(t, "Error loading page")
                    showError("Error loading page. Tap to retry. ${t.message}")
                }
                .pageSnap(true)
                .enableDoubletap(true)
                .load()
    }

    private fun showError(error: String, show: Boolean = true) {
        if (show) {
            binding.textviewErrorLoadingDetail.text = error
            binding.textviewErrorLoadingDetail.visibility = View.VISIBLE
        } else {
            binding.textviewErrorLoadingDetail.visibility = View.GONE
        }
    }

    private fun showContentLoading(loading: Boolean) {
        binding.isLoading = loading
    }

    fun init(hymnNo: Int) {
        val args = Bundle()
        args.putInt(ARG_HYMN_INDEX, hymnNo)
        this.arguments = args
    }
}

private const val ARG_HYMN_INDEX = "hymnIndex"