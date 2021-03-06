package com.techbeloved.hymnbook.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.BuildConfig
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)

        binding.textviewMoreSettings.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToSettingsActivity())
        }
        binding.textviewMoreAbout.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToAboutFragment())
        }

        binding.textviewMoreHymnOfTheDay.setOnClickListener {
            Snackbar.make(binding.coordinatorLayoutMore.rootView, "Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.textviewMoreShare.setOnClickListener {
            ShareCompat.IntentBuilder.from(requireActivity()).apply {
                setChooserTitle(R.string.share_app)
                setType("text/plain")
                setText(getString(R.string.play_store_web, BuildConfig.APPLICATION_ID))
            }
                    .startChooser()
        }

        NavigationUI.setupWithNavController(binding.toolbarFragmentMore, findNavController())
        return binding.root
    }
}