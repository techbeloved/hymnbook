package com.techbeloved.hymnbook.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.palette.graphics.Palette
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        NavigationUI.setupWithNavController(binding.toolbarHome, findNavController())

        val wccrmPalette =
            Palette.from(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.wccrm_logo
                )
            ).generate()
        val mutedColor = wccrmPalette.getLightMutedColor(
            ContextCompat.getColor(requireContext(), R.color.primary_white)
        )

        binding.cardviewHymns.setCardBackgroundColor(mutedColor)

        return binding.root
    }
}
