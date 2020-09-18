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

        binding.textviewAboutSocialFacebook.setOnClickListener {
            val facebookIntent = try {
                requireContext().packageManager.getPackageInfo(getString(R.string.facebook_package_name), 0)
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fb_page_template, getString(R.string.wccrm_vowtv_fb_id))))
            } catch (e: Exception) {
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.wccrm_facebook_link)))
            }
            startActivity(facebookIntent)
        }

        binding.textviewAboutSocialTwitter.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.wccrm_twitter_link))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }

        binding.textviewAboutSocialYoutube.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.wccrm_youtube_link))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }

        binding.textviewAboutPrivacyPolicy.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_privacy_policy))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }

        binding.textviewAboutTermsAndConditions.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_terms_and_conditions))).apply {
                if (resolveActivity(requireContext().packageManager) != null) {
                    startActivity(this)
                }
            }
        }
        binding.textviewAboutAknowledgement.setOnClickListener {
            findNavController().navigate(AboutFragmentDirections.actionAboutFragmentToAcknowledgementFragment())
        }

        return binding.root
    }
}