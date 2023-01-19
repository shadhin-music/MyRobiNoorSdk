package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentDonationBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.donation.CharityAdapter
import com.gakk.noorlibrary.ui.adapter.donation.DonationAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private const val ARG_CAT_NAME = "CatName"

internal class DonationFragment : Fragment() {

    private lateinit var binding: FragmentDonationBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var literatureList: MutableList<Literature> = mutableListOf()
    private var catName: String? = null
    private var catId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            catName = it.getString(ARG_CAT_NAME)
        }
        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance(catName: String) =
            DonationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CAT_NAME, catName)
                }
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_donation,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        when (catName) {
            "Donation" -> {
                mDetailsCallBack?.setToolBarTitle(getString(R.string.txt_donate))
                catId = R.string.donation_id.getLocalisedTextFromResId()
            }

            else -> {
                mDetailsCallBack?.setToolBarTitle(getString(R.string.txt_charity_organization))
                catId = R.string.charity_organisation_id.getLocalisedTextFromResId()
            }
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@DonationFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            loadData()

            model.literatureListData.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressLayout.root.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        literatureList = it.data?.data ?: mutableListOf()

                        when (catName) {
                            "Donation" -> {

                                val margins =
                                    (binding.rvDonation.layoutParams as ConstraintLayout.LayoutParams).apply {
                                        leftMargin = 16
                                        rightMargin = 16
                                    }
                                binding.rvDonation.layoutParams = margins
                                binding.rvDonation.layoutManager =
                                    GridLayoutManager(requireContext(), 2)
                                binding.rvDonation.adapter =
                                    DonationAdapter(literatureList, mDetailsCallBack!!)
                            }
                            else -> {
                                binding.rvDonation.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.rvDonation.adapter =
                                    CharityAdapter(literatureList, mDetailsCallBack!!)
                            }
                        }

                        binding.progressLayout.root.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun loadData() {
        model.loadTextBasedLiteratureListBySubCategory(
            catId!!,
            "undefined",
            "1"
        )
    }
}