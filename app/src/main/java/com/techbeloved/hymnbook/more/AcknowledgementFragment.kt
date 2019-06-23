package com.techbeloved.hymnbook.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.databinding.FragmentAcknowledgementBinding

class AcknowledgementFragment : Fragment() {

    private lateinit var binding: FragmentAcknowledgementBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_acknowledgement, container, false)

        NavigationUI.setupWithNavController(binding.toolbarFragmentAck, findNavController())

        binding.constraintlayoutHymnary.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_hymnary))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }

        binding.imageViewCyberhymnal.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_cyberhymnal))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }
        binding.textViewCyberhymnalTagline.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_cyberhymnal))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }

        binding.textviewTimelessTruths.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_timelesstruths))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }

        return binding.root
    }
}