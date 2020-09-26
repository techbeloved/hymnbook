package com.techbeloved.hymnbook.home

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.palette.graphics.Palette
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentHomeBinding
import com.techbeloved.hymnbook.utils.AUTHORITY
import com.techbeloved.hymnbook.utils.CATEGORY_WCCRM
import com.techbeloved.hymnbook.utils.SCHEME_NORMAL

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.cardviewHymns.setOnClickListener {
            val navUri = Uri.Builder()
                    .authority(AUTHORITY)
                    .scheme(SCHEME_NORMAL)
                    .appendEncodedPath(CATEGORY_WCCRM)
                    .appendEncodedPath(0.toString())
                    .build()
            findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToHymnListingFragment(navUri = navUri.toString()))
        }
        binding.cardviewSheetMusic.setOnClickListener {
            findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToSheetMusicListing())
        }
        NavigationUI.setupWithNavController(binding.toolbarHome, findNavController())

        val wccrmPalette =
                Palette.from(BitmapFactory.decodeResource(resources,
                        R.drawable.wccrm_logo)).generate()
        val mutedColor = wccrmPalette.getLightMutedColor(
                resources.getColor(R.color.primary_white))

        binding.cardviewHymns.setCardBackgroundColor(mutedColor)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

}
