package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.donation.DonateZakatAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

private const val ARG_LITERATURE_DETAILS = "literatureDetails"

internal class DonateZakatFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiterature: Literature? = null
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var literatureList: MutableList<Literature> = mutableListOf()
    private lateinit var rvOrganization: RecyclerView
    private lateinit var progressLayout: ConstraintLayout

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

        val view = inflater.inflate(
            R.layout.fragment_donate_zakat,
            container, false
        )

        rvOrganization = view.findViewById(R.id.rvOrganization)
        progressLayout = view.findViewById(R.id.progressLayout)

        return view
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
                        progressLayout.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        literatureList = it.data?.data ?: mutableListOf()

                        rvOrganization.adapter =
                            DonateZakatAdapter(literatureList, mLiterature!!, mDetailsCallBack!!)
                       progressLayout.visibility = View.GONE
                    }
                }
            }
        }
    }
}