package com.techbeloved.hymnbook.home

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.palette.graphics.Palette

import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.SettingsActivity
import com.techbeloved.hymnbook.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.cardviewHymns.setOnClickListener {
            findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToHymnListingFragment())
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(activity, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
