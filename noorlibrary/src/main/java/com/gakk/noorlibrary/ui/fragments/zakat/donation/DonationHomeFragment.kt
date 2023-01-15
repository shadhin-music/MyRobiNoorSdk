package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentDonationHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

class DonationHomeFragment : Fragment() {

    private lateinit var binding: FragmentDonationHomeBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var termUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DonationHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_donation_home,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarForThisFragment()

        binding.item = ImageFromOnline("ic_donation_header.png")

        binding.clOrganisations.visibility = View.VISIBLE
        binding.tvDonate.setText(getString(R.string.txt_donate))


        binding.ivDonate.handleClickEvent {

            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION,
                detailsActivityCallBack = mDetailsCallBack,
                catName = "Donation"
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

        binding.ivCharityOrganization.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION,
                detailsActivityCallBack = mDetailsCallBack,
                catName = "Charity"
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

        binding.ivDonationImportance.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION_IMPORTANCE,
                detailsActivityCallBack = mDetailsCallBack
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }


        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@DonationHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.literatureListData.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE
                    }

                    Status.SUCCESS -> {
                        termUrl = it.data?.data?.get(0)?.refUrl
                        binding.progressLayout.root.visibility = View.GONE
                    }

                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.txt_donate))
    }
}