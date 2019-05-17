package com.techbeloved.hymnbook.sheetmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentSheetMusicDetailBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import timber.log.Timber
import java.io.File

class SheetMusicDetailFragment : Fragment() {

    private var hymnId: Int = 1
    private lateinit var binding: FragmentSheetMusicDetailBinding
    private lateinit var viewModel: SheetMusicDetailViewModel

    private val hymnDetailObserver: Observer<Lce<SheetMusicState>> = Observer { hymnLce ->
        when (hymnLce) {
            is Lce.Loading -> showContentLoading(true)
            is Lce.Error -> showError(hymnLce.error, true)
            is Lce.Content -> showContentDetail(hymnLce.content)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sheet_music_detail, container, false)

        binding.textviewErrorLoadingDetail.setOnClickListener {
            viewModel.download(hymnId)
        }

        viewModel.hymnDetail.observe(viewLifecycleOwner, hymnDetailObserver)


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

        val factory = SheetMusicDetailViewModel.Factory(Injection.provideHymnUsesCases)
        viewModel = ViewModelProviders.of(this, factory)[SheetMusicDetailViewModel::class.java]
    }

    private fun showContentDetail(content: SheetMusicState) {
        Timber.i("$content")
        showContentLoading(false)
        showError(show = false) // Hide any error that might be there before
        when (content) {
            is SheetMusicState.NotDownloaded -> viewModel.download(content.id)
            is SheetMusicState.DownloadFailed -> showError("Download failed. Tap to retry", true)
            is SheetMusicState.Downloading -> {
                showProgress(content.downloadProgress)
            }
            is SheetMusicState.Ready -> {
                binding.pdfViewSheetMusicDetail.fromFile(File(content.localUri))
                        .onError { error ->
                            Timber.w(error, "Error loading document")
                            showError("Error loading document. Tap to retry. ${error.message}", true)
                        }
                        .onPageError { page, t ->
                            Timber.w(t, "Error loading page")
                            showError("Error loading page. Tap to retry. ${t.message}", true)
                        }
                        .pageSnap(true)
                        .pageFitPolicy(FitPolicy.WIDTH)
                        .autoSpacing(false)
                        .pageSnap(true)
                        .enableAntialiasing(true)
                        .nightMode(content.darkMode)
                        .enableDoubletap(true)
                        .load()
                showProgress(show = false)
            }
        }

    }

    private fun showProgress(progress: Int = 0, show: Boolean = true) {
        if (show) {
            if (!binding.progressBarSheetMusicDownloading.isVisible) binding.progressBarSheetMusicDownloading.visibility = View.VISIBLE
            binding.progressBarSheetMusicDownloading.progress = progress
        } else {
            binding.progressBarSheetMusicDownloading.visibility = View.GONE
        }
    }

    private fun showError(error: String = "", show: Boolean = false) {
        Timber.w(error)
        showProgress(show = false)
        showContentLoading(false)
        if (show) {
            binding.textviewErrorLoadingDetail.text = error
            binding.textviewErrorLoadingDetail.visibility = View.VISIBLE
        } else if (binding.textviewErrorLoadingDetail.isVisible) {
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