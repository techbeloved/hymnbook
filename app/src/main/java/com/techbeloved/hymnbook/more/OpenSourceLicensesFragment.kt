package com.techbeloved.hymnbook.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentOpenSourceLicensesBinding

class OpenSourceLicensesFragment : Fragment() {
    private lateinit var binding: FragmentOpenSourceLicensesBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_open_source_licenses, container, false)

        NavigationUI.setupWithNavController(binding.toolbarFragmentOpenSourceLicenses, findNavController())

        binding.webviewOpenSourceLicenses.loadUrl("file:///android_asset/open_source_licenses.html")
        return binding.root
    }
}