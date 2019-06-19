package com.techbeloved.hymnbook.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.techbeloved.hymnbook.BuildConfig
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        NavigationUI.setupWithNavController(binding.toolbarFragmentAbout, findNavController())

        binding.textviewAboutVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        binding.textviewAboutOpenSourceLicenses.setOnClickListener {
            findNavController().navigate(AboutFragmentDirections.actionAboutFragmentToOpenSourceLicensesFragment())
        }
        return binding.root
    }
}