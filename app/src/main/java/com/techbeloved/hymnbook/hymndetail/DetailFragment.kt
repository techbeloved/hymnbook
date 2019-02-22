package com.techbeloved.hymnbook.hymndetail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentDetailBinding
import com.techbeloved.hymnbook.di.Injection
import com.techbeloved.hymnbook.usecases.Lce
import timber.log.Timber
import xdroid.toaster.Toaster.toast

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewModel: HymnDetailViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        viewModel.hymnDetailLiveData.observe(this, Observer {
            when(it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> showContentDetail(it.content)
                is Lce.Error -> showContentError(it.error)
            }
        })

        if (arguments != null && arguments?.containsKey(ARG_HYMN_INDEX) != null) {
            val hymnIndexToBeLoaded = arguments!!.getInt(ARG_HYMN_INDEX)
            Timber.i("Received an initial id: $hymnIndexToBeLoaded")
            loadHymnDetail(hymnIndexToBeLoaded)
        } else {
            showContentError("No hymn index supplied")
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = HymnDetailViewModel.Factory(Injection.provideAppContext(),
                Injection.provideRepository())
        viewModel = ViewModelProviders.of(this, factory).get(HymnDetailViewModel::class.java)
    }

    private fun showContentError(error: String) {
        showProgressLoading(false)
        Timber.e(error)
        toast(error)
    }

    private fun showContentDetail(content: HymnDetailItem) {
        Timber.i("About to display details: ${content.title}")
        showProgressLoading(false)
        binding.detailWebview.loadDataWithBaseURL(activity?.getString(R.string.assest_base_url),
                content.content, "text/html", "UTF-8", null);
    }

    private fun showProgressLoading(loading: Boolean) {
        if (loading) binding.progressBarHymnDetailLoading.visibility = View.VISIBLE
        else binding.progressBarHymnDetailLoading.visibility = View.GONE
    }

    private fun loadHymnDetail(hymnNo: Int) {
        viewModel.loadHymnDetail(hymnNo)
    }

    fun init(hymnNo: Int) {
        val args = Bundle()
        args.putInt(ARG_HYMN_INDEX, hymnNo)
        this.arguments = args
    }

    companion object {
        private const val ARG_HYMN_INDEX = "hymnIndex"
    }

}
