package com.techbeloved.hymnbook.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)

        binding.textviewMoreSettings.setOnClickListener { _ ->
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToSettingsFragment())
        }
        binding.textviewMoreAbout.setOnClickListener { _ ->
            Snackbar.make(binding.coordinatorLayoutMore, "About hymnbook app", Snackbar.LENGTH_SHORT).show()
        }

        binding.textviewMoreHymnOfTheDay.setOnClickListener { _ ->
            Snackbar.make(binding.coordinatorLayoutMore, "Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.textviewMoreShare.setOnClickListener { _ ->
            Snackbar.make(binding.coordinatorLayoutMore, "Coming Soon", Snackbar.LENGTH_SHORT).show()

        }
        return binding.root
    }
}