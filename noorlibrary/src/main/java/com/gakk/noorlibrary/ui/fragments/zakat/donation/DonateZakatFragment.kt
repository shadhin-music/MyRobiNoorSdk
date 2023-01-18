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
import com.gakk.noorlibrary.databinding.FragmentDonateZakatBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.donation.DonateZakatAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

private const val ARG_LITERATURE_DETAILS = "literatureDetails"

internal class DonateZakatFragment : Fragment() {

    private lateinit var binding: FragmentDonateZakatBinding
    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiterature: Literature? = null
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var literatureList: MutableList<Literature> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLiterature = it.getSerializable(ARG_LITERATURE_DETAILS) as Literature?
        }
        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance(itemLiterature: Literature) =
            DonateZakatFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_DETAILS, itemLiterature)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_donate_zakat,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDetailsCallBack?.setToolBarTitle(getString(R.string.txt_donate_zakat))

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@DonateZakatFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                R.string.charity_organisation_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )

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

                        binding.rvOrganization.adapter =
                            DonateZakatAdapter(literatureList, mLiterature!!, mDetailsCallBack!!)
                        binding.progressLayout.root.visibility = View.GONE
                    }
                }
            }
        }
    }
}