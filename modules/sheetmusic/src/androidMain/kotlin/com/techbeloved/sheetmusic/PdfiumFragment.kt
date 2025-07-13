package com.techbeloved.sheetmusic

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.techbeloved.sheetmusic.databinding.PdfiumFragmentBinding
import java.io.File

internal class PdfiumFragment : Fragment(R.layout.pdfium_fragment) {
    private val documentPath: String get() = checkNotNull(requireArguments().getString("documentPath"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = PdfiumFragmentBinding.bind(view)

        val isDarkMode = isDarkMode()
        binding.pdfview.isZooming

        binding.pdfview.fromFile(File(documentPath))
            .onError { error ->
                println(error)
            }
            .onPageError { _, t ->
                t.printStackTrace()
            }
            .pageSnap(true)
            .pageFitPolicy(FitPolicy.WIDTH)
            .autoSpacing(false)
            .pageSnap(true)
            .enableAntialiasing(true)
            .nightMode(isDarkMode)
            .enableDoubletap(true)
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .load()
    }

    private fun isDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
