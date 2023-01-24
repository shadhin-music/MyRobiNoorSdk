package com.gakk.noorlibrary.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.ui.adapter.SurahBasicInfoAdapter
import com.gakk.noorlibrary.util.ALL_SURAH
import com.gakk.noorlibrary.util.FAVOURITE_SURAH
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.QuranViewModel
import kotlinx.coroutines.launch
import java.io.Serializable


internal class SurahListFragment : Fragment(), PagingViewCallBack, FavUnFavActionCallBack {

    private val ARG_PAGE_TYPE = "pageType"
    private val ARG_ACTION_CREATE_SURAH_DETAIL_FRAGMENT = "createSurahDetailFragment"

    private var mPageType: String? = null
    private lateinit var mDetailsCallBack: DetailsCallBack
    private var getSurahDetailFragment: ((String, DetailsCallBack, MutableList<com.gakk.noorlibrary.model.quran.surah.Data>) -> Fragment?)? =
        null

    private lateinit var model: QuranViewModel
    private lateinit var repository: RestRepository
    private var surahList: MutableList<Data>? = null
    private lateinit var adapter: SurahBasicInfoAdapter

    private var mHasMoreData: Boolean = true
    private var mCurrentPage: Int = 1
    private var mFavUnFavIndex: Int = -1
    private var mFavUnFavSurah: Data? = null

    private lateinit var progressLayout: ConstraintLayout
    private lateinit var surahListRv: RecyclerView
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageType = it.getString(ARG_PAGE_TYPE)
            getSurahDetailFragment =
                it.getSerializable(ARG_ACTION_CREATE_SURAH_DETAIL_FRAGMENT) as ((String, DetailsCallBack, MutableList<com.gakk.noorlibrary.model.quran.surah.Data>) -> Fragment?)?
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_surah_list,
            container, false
        )

        progressLayout = view.findViewById(R.id.progressLayout)
        surahListRv = view.findViewById(R.id.surahList)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@SurahListFragment,
                QuranViewModel.FACTORY(repository)
            ).get(QuranViewModel::class.java)

            model.surahListResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        surahList = it.data?.data
                        if (mCurrentPage == 1) {
                            progressLayout.visibility = GONE
                            adapter = SurahBasicInfoAdapter(
                                surahList,
                                mPageType!!,
                                getSurahDetailFragment,
                                mDetailsCallBack,
                                this@SurahListFragment,
                                this@SurahListFragment
                            )
                            if (mPageType == FAVOURITE_SURAH) {
                                FavouriteSurahObserver.initService(adapter)
                                Log.i("OSASASASASS", "Observer added......")
                            }

                            surahListRv.adapter = adapter
                            surahListRv.layoutManager = LinearLayoutManager(context)
                        } else {
                            if (surahList == null) {
                                mHasMoreData = false
                                adapter.hideFooter()
                            } else {
                                adapter.addItemToList(surahList!!)
                            }
                        }

                    }
                    Status.LOADING -> {
                        if (mCurrentPage == 1) {
                            progressLayout.visibility = VISIBLE
                        }
                        noInternetLayout.visibility = GONE

                    }
                    Status.ERROR -> {
                        noInternetLayout.visibility = VISIBLE
                        progressLayout.visibility = GONE
                    }
                }
            })

            model.unFavouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressLayout.visibility = GONE
                        adapter.removeItemFromList(mFavUnFavIndex)
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                    Status.LOADING -> {
                        progressLayout.visibility = VISIBLE
                    }
                }
            })

            model.favouriteSurahResponse.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressLayout.visibility = GONE
                        adapter.insertItemToList(mFavUnFavSurah!!)
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                    }
                    Status.LOADING -> {
                        progressLayout.visibility = VISIBLE
                    }
                }
            })

            loadData()
            btnRetry.handleClickEvent {
                loadData()
            }

        }
    }

    override fun onDestroy() {
        FavouriteSurahObserver.clearServiceComponent()
        super.onDestroy()
    }


    companion object {

        @JvmStatic
        fun newInstance(
            paramPageType: String,
            detailsCallBack: DetailsCallBack,
            action: (String, DetailsCallBack, MutableList<com.gakk.noorlibrary.model.quran.surah.Data>) -> Fragment?
        ) =
            SurahListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_TYPE, paramPageType)
                    putSerializable(ARG_ACTION_CREATE_SURAH_DETAIL_FRAGMENT, action as Serializable)
                }
            }
    }

    fun loadData() {
        when (mPageType) {
            ALL_SURAH -> {
                model.getAllSurah("$mCurrentPage")
            }
            FAVOURITE_SURAH -> {
                model.getAllFavouriteSurah("$mCurrentPage")
            }

        }

    }

    override fun initPagingProperties() {
        mHasMoreData = true
        mCurrentPage = 1
    }

    override fun loadNextPage() {
        mCurrentPage++
        loadData()
    }

    override fun hasMoreData() = mHasMoreData

    override fun favSurah(id: String, listPosition: Int, data: Data) {
        if (this::model.isInitialized) {
            if (mPageType == FAVOURITE_SURAH) {
                model.favouriteSurah(id)
                mFavUnFavIndex = listPosition
            }

        }
    }


    override fun unFavSurah(id: String, listPosition: Int) {
        if (this::model.isInitialized) {
            if (mPageType == FAVOURITE_SURAH) {
                model.unFavouriteSurah(id)
                mFavUnFavIndex = listPosition
            }

        }
    }
}

interface FavUnFavActionCallBack {
    fun favSurah(id: String, listPosition: Int = -1, data: Data)
    fun unFavSurah(id: String, listPosition: Int = -1)
}

object FavouriteSurahObserver {

    var adapter: SurahBasicInfoAdapter? = null

    fun initService(surahBasicInfoAdapter: SurahBasicInfoAdapter?) {
        adapter = surahBasicInfoAdapter
    }

    fun postFavouriteNotification(surah: Data) {
        adapter?.insertItemToList(surah)

    }

    fun postUnFavouriteNotification(surah: Data) {
        adapter?.removeItemFromListIfExists(surah)

    }

    fun clearServiceComponent() {
        adapter = null

    }
}