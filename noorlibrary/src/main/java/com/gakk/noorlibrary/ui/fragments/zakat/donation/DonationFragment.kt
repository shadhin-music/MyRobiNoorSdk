package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.donation.CharityAdapter
import com.gakk.noorlibrary.ui.adapter.donation.DonationAdapter
import com.gakk.noorlibrary.util.PAGE_DONATION_DETAILS
import com.gakk.noorlibrary.util.PAGE_DONATION_HOME
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private const val ARG_CAT_NAME = "CatName"

internal class DonationFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var literatureList: MutableList<Literature> = mutableListOf()
    private var catName: String? = null
    private var catId: String? = null
    private lateinit var rvDonation: RecyclerView
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var modelUserTracking: AddUserTrackigViewModel

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

        val view = inflater.inflate(
            R.layout.fragment_donation,
            container, false
        )

        rvDonation = view.findViewById(R.id.rvDonation)
        progressLayout = view.findViewById(R.id.progressLayout)

        return view
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

            modelUserTracking = ViewModelProvider(
                this@DonationFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            if (catName.equals("Donation")) {
                AppPreference.userNumber?.let { userNumber ->
                    modelUserTracking.addTrackDataUser(userNumber, PAGE_DONATION_DETAILS)
                }
            }
            loadData()

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

                        when (catName) {
                            "Donation" -> {

                                val margins =
                                    (rvDonation.layoutParams as ConstraintLayout.LayoutParams).apply {
                                        leftMargin = 16
                                        rightMargin = 16
                                    }
                               rvDonation.layoutParams = margins
                               rvDonation.layoutManager =
                                    GridLayoutManager(requireContext(), 2)
                                rvDonation.adapter =
                                    DonationAdapter(literatureList, mDetailsCallBack!!)
                            }
                            else -> {
                                rvDonation.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                rvDonation.adapter =
                                    CharityAdapter(literatureList, mDetailsCallBack!!)
                            }
                        }

                        progressLayout.visibility = View.GONE
                    }
                }
            }

            modelUserTracking.trackUser.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("trackUser", "LOADING")
                    }
                    Status.ERROR -> {
                        Log.e("trackUser", "ERROR")
                    }

                    Status.SUCCESS -> {
                        Log.e("trackUser", "SUCCESS")
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