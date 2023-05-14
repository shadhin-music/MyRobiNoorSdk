package com.gakk.noorlibrary.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.adapter.LiteratureListFragmentPagerAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.views.CustomTabLayout
import kotlinx.coroutines.launch


private const val ARG_LITERATURE_TYPE = "literatureType"

internal class LiteratureHomeFragment : Fragment() {
    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiteratureType: LiteratureType? = null
    private lateinit var pager: ViewPager
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var repository: RestRepository
    private lateinit var modelUserTracking: AddUserTrackigViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLiteratureType = it.getSerializable(ARG_LITERATURE_TYPE) as LiteratureType
        }
        mDetailsCallBack = requireActivity() as? DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_literature_home,
            container, false
        )

        pager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mPageTitles = when (mLiteratureType) {
            LiteratureType.Hadis -> {
                arrayOf(
                    requireContext().resources.getString(R.string.hadis_list),
                    requireContext().resources.getString(R.string.fav_list)
                )
            }

            LiteratureType.Jakat -> {
                arrayOf(
                    requireContext().resources.getString(R.string.txt_jakat_calculator),
                    requireContext().resources.getString(R.string.txt_save_calculation)
                )
            }
            else -> {
                arrayOf(
                    requireContext().resources.getString(R.string.category_list),
                    requireContext().resources.getString(R.string.fav_list)
                )
            }
        }

        val catId = when (mLiteratureType) {
            LiteratureType.Hadis -> R.string.hadis_cat_id.getLocalisedTextFromResId()
            LiteratureType.Dua -> R.string.dua_cat_id.getLocalisedTextFromResId()
            LiteratureType.Jakat -> R.string.jakat_cat_id.getLocalisedTextFromResId()
            else -> R.string.hadis_cat_id.getLocalisedTextFromResId()
        }


        pager.adapter = LiteratureListFragmentPagerAdapter(
            fragmentManager = childFragmentManager,
            pageTitles = mPageTitles,
            detailsCallBack = mDetailsCallBack,
            catId = catId,
            literatureType = mLiteratureType!!
        )
        tabLayout.setupWithViewPager(pager)

        updateToolbarForThisFragment()

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            modelUserTracking = ViewModelProvider(
                this@LiteratureHomeFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            val pageName = when (mLiteratureType) {
                LiteratureType.Hadis -> PAGE_HADIS
                LiteratureType.Dua -> PAGE_DUA
                LiteratureType.Jakat -> PAGE_JAKAT
                else -> PAGE_HADIS
            }

            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, pageName)
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


    fun updateToolbarForThisFragment() {
        val pageTitle = when (mLiteratureType) {
            LiteratureType.Hadis -> resources.getString(R.string.cat_hadith)
            LiteratureType.Dua -> resources.getString(R.string.cat_dua)
            LiteratureType.NamazRules -> resources.getString(R.string.cat_namaz_sikhha)
            LiteratureType.Jakat -> resources.getString(R.string.txt_jakat_calculator)
            else -> ""
        }

        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)

        pageTitle.let {
            mDetailsCallBack?.setToolBarTitle(pageTitle)
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(literatureType: LiteratureType) =
            LiteratureHomeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_TYPE, literatureType)
                }
            }
    }


}
