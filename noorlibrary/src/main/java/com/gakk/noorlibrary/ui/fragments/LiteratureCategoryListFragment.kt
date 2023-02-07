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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.PagingViewCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.adapter.LiteratureCategoryAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


private const val ARG_LITERATURE_TYPE = "literatureType"
private const val ARG_LITERATURE_CAT_ID = "literatureCatId"


internal class LiteratureCategoryListFragment : Fragment(), PagingViewCallBack {

    private var mDetailsCallback: DetailsCallBack? = null
    private var mLiteratureType: LiteratureType? = null
    private var mCatId: String? = null

    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var rvLiteratureCategories: RecyclerView
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton

    private var adapter: LiteratureCategoryAdapter? = null

    private var pageNo: Int = 0
    private var hasMoreData = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCatId = it.getString(ARG_LITERATURE_CAT_ID)
            mLiteratureType = it.getSerializable(ARG_LITERATURE_TYPE) as LiteratureType
        }

        mDetailsCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_literature_category_list,
            container, false
        )
        progressLayout = view.findViewById(R.id.progressLayout)
        rvLiteratureCategories = view.findViewById(R.id.rvLiteratureCategories)
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
                this@LiteratureCategoryListFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            model.subCategoryListData.observe(viewLifecycleOwner, Observer {

                when (it.status) {
                    Status.SUCCESS -> {
                        progressLayout.visibility = GONE

                        when (pageNo) {
                            1 -> {
                                adapter = LiteratureCategoryAdapter(
                                    mDetailsCallback!!,
                                    it.data?.data ?: mutableListOf(),
                                    this@LiteratureCategoryListFragment,
                                    catId = mCatId!!
                                )
                                rvLiteratureCategories.adapter = adapter
                                rvLiteratureCategories.layoutManager =
                                    GridLayoutManager(
                                        context,
                                        2,
                                        RecyclerView.VERTICAL,
                                        false
                                    )

                            }

                            else -> {
                                val list = it.data?.data
                                when (list == null) {
                                    false -> {
                                        adapter?.addItemToList(list)
                                        Log.e("ISNT", "not null ${list.size}")
                                    }
                                    true -> {
                                        Log.e("ISNT", "null")
                                        hasMoreData = false
                                        adapter?.hideFooter()
                                    }
                                }
                            }

                        }
                        progressLayout.visibility = GONE
                        progressLayout.visibility = GONE


                    }
                    Status.LOADING -> {
                        noInternetLayout.visibility = GONE
                        if (pageNo == 1) {
                            progressLayout.visibility = VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                        noInternetLayout.visibility = VISIBLE
                    }
                }
            })

            initPagingProperties()
            loadData()

            btnRetry.handleClickEvent {
                loadData()
            }


        }


        updateToolbarForThisFragment()
    }


    fun updateToolbarForThisFragment() {
        when (mCatId) {
            R.string.namaz_rules_cat_id.getLocalisedTextFromResId() -> {
                mDetailsCallback?.setToolBarTitle(resources.getString(R.string.cat_namaz_sikhha))
            }
        }

        mDetailsCallback?.toggleToolBarActionIconsVisibility(false)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            literatureType: LiteratureType,
            catId: String
        ) =
            LiteratureCategoryListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_TYPE, literatureType)
                    putString(ARG_LITERATURE_CAT_ID, catId)
                }
            }
    }

    override fun initPagingProperties() {
        pageNo = 1
        hasMoreData = true
    }

    fun loadData() {
        model.loadSubCategoriesByCatId(mCatId!!, "$pageNo")
    }

    override fun loadNextPage() {
        pageNo++
        loadData()
    }

    override fun hasMoreData(): Boolean {
        return hasMoreData
    }
}