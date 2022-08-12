package com.techbeloved.hymnbook.hymndetail


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentDetailBinding
import com.techbeloved.hymnbook.usecases.Lce
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 *
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private  var _binding: FragmentDetailBinding? = null
    private  val binding: FragmentDetailBinding get() = _binding!!
    private val viewModel: HymnDetailViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this
        configureSettings()

        viewModel.hymnDetailLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Lce.Loading -> showProgressLoading(it.loading)
                is Lce.Content -> showContentDetail(it.content)
                is Lce.Error -> showContentError(it.error)
            }
        }

        if (arguments != null && arguments?.containsKey(ARG_HYMN_INDEX) != null) {
            val hymnIndexToBeLoaded = requireArguments().getInt(ARG_HYMN_INDEX)
            Timber.i("Received an initial id: $hymnIndexToBeLoaded")
            loadHymnDetail(hymnIndexToBeLoaded)
        } else {
            showContentError("No hymn index supplied")
        }
        return binding.root
    }

    private val disposables = CompositeDisposable()
    private var currentTextSize: Float = 1.0f

    private fun configureSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)

        val defaultTextSize = resources.getInteger(R.integer.normal_detail_text_size).toFloat()
        val fontSizePreference: Preference<Float> = rxPreferences.getFloat(
                getString(R.string.pref_key_detail_font_size), defaultTextSize)

        fontSizePreference.asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ currentSize ->
                    updateDetailTextSize(currentSize)
                    currentTextSize = currentSize
                }, { throwable ->
                    Timber.w(throwable, "Some error occurred")
                })
                .run { disposables.add(this) }
    }

    private fun updateDetailTextSize(currentSize: Float) {
        val calculatedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, currentSize, resources.displayMetrics)
        binding.textviewDetail.apply {
            textSize = calculatedSize
        }
    }

    private fun showContentError(error: String) {
        showProgressLoading(false)
        Timber.e(error)
    }

    private fun showContentDetail(content: HymnDetailItem) {
        Timber.i("About to display details: ${content.title}")
        showProgressLoading(false)
        binding.textviewDetail.text = content.content
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

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_HYMN_INDEX = "hymnIndex"
    }

}
