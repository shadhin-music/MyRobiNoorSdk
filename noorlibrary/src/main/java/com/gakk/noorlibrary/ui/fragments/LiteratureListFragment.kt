package com.gakk.noorlibrary.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.*
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.adapter.LiteratureListAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.util.RepositoryProvider.Companion.getRepository
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private const val ARG_IS_FAV_LIST = "isFav"
private const val ARG_CAT_ID = "catId"
private const val ARG_SUB_CAT_ID = "subCatId"
private const val ARG_PAGE_TITLE = "pageTitle"
private const val ARG_SHOW_CALCULATE_BTN = "showCalculateBtn"
private const val ARG_SHOW_HEADER_IMG_Miladunnobi = "showHeaderImgMiladunnobi"
private var literatureListWrapper: LiteratureListWrapper? = null
private const val ARG_LITERATURE_LIST_CALL_BACK = "literatureListCallBack"


internal class LiteratureListFragment : Fragment(), PagingViewCallBack, FavUnFavCallBack,
    LiteratureItemClickCallBack {

    @Transient
    private var mDetailsCallBack: DetailsCallBack? = null

    @Transient
    private var mainCallback: MainCallback? = null

    @Transient
    private var mIsFavList: Boolean? = null

    @Transient
    private var mCatId: String? = null

    @Transient
    private var mSubCatId: String? = null

    @Transient
    private var mPageTitle: String? = null

    @Transient
    private var showCalculateBtn: Boolean? = false

    @Transient
    private var showHeaderImageMiladunnobi: Boolean? = false

    @Transient
    private var literatureListAdapter: LiteratureListAdapter? = null

    @Transient
    private lateinit var model: LiteratureViewModel

    @Transient
    private lateinit var repository: RestRepository

    @Transient
    private var pageNo: Int = 0

    @Transient
    private var hasMoreData = false

    @Transient
    private var clickedIndex = -1

    @Transient
    private lateinit var layoutNewCalculation: ConstraintLayout

    @Transient
    private lateinit var cardMiladunnanbi: CardView

    @Transient
    private lateinit var noInternetLayout: ConstraintLayout

    @Transient
    private lateinit var progressLayout: ConstraintLayout

    @Transient
    private lateinit var rvLiteratureList: RecyclerView

    @Transient
    private lateinit var btnRetry: AppCompatButton

    @Transient
    private lateinit var ivHeaderMiladunnanbi: AppCompatImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            mIsFavList = it.getBoolean(ARG_IS_FAV_LIST)
            mCatId = it.getString(ARG_CAT_ID)
            mSubCatId = it.getString(ARG_SUB_CAT_ID)
            mPageTitle = it.getString(ARG_PAGE_TITLE)
            showCalculateBtn = it.getBoolean(ARG_SHOW_CALCULATE_BTN)
            showHeaderImageMiladunnobi = it.getBoolean(ARG_SHOW_HEADER_IMG_Miladunnobi)
        }
        mainCallback = requireActivity() as? MainCallback
        mDetailsCallBack = requireActivity() as? DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_literature_list,
            container, false
        )

        layoutNewCalculation = view.findViewById(R.id.layoutNewCalculation)
        cardMiladunnanbi = view.findViewById(R.id.cardMiladunnanbi)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)
        progressLayout = view.findViewById(R.id.progressLayout)
        rvLiteratureList = view.findViewById(R.id.rvLiteratureList)
        ivHeaderMiladunnanbi = view.findViewById(R.id.ivHeaderMiladunnanbi)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        hideActionButtons()

        initPagingProperties()
        updateToolbarForThisFragment()

        lifecycleScope.launch {


            if (showCalculateBtn == true) {
                layoutNewCalculation.visibility = VISIBLE
            }

            if (showHeaderImageMiladunnobi == true) {

                val item = ImageFromOnline("miladunnobi_header.png")

                Noor.appContext?.let {
                    Glide.with(it)
                        .load(item.fullImageUrl)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                        })
                        .error(R.drawable.place_holder_16_9_ratio)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(ivHeaderMiladunnanbi)
                }
                cardMiladunnanbi.visibility = VISIBLE
                cardMiladunnanbi.handleClickEvent {
                    val fragment = FragmentProvider.getFragmentByName(
                        PAGE_BIOGRAPHY,
                        detailsActivityCallBack = mDetailsCallBack
                    )
                    mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
                }
            }

            layoutNewCalculation.handleClickEvent {
                Intent(context, DetailsActivity::class.java).apply {
                    this.putExtra(PAGE_NAME, PAGE_JAKAT_NEW_CALCULATION)
                    startActivity(this)
                }
            }
            var job = launch {
                repository = getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@LiteratureListFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            model.literatureListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        noInternetLayout.visibility = GONE
                        if (pageNo == 1) {
                            progressLayout.visibility = VISIBLE
                        }

                    }
                    Status.SUCCESS -> {
                        updateToolbarForThisFragment()
                        when (pageNo) {

                            1 -> {
                                literatureListAdapter = LiteratureListAdapter(
                                    it.data?.data,
                                    mIsFavList!!,
                                    mDetailsCallBack,
                                    this@LiteratureListFragment,
                                    this@LiteratureListFragment,
                                    this@LiteratureListFragment
                                )

                                rvLiteratureList.adapter = literatureListAdapter
                                when (mCatId) {
                                    R.string.animation_cat_id.getLocalisedTextFromResId(), R.string.wallpaper_cat_id.getLocalisedTextFromResId() -> {
                                        rvLiteratureList.layoutManager = GridLayoutManager(
                                            context,
                                            2,
                                            RecyclerView.VERTICAL,
                                            false
                                        )
                                    }
                                    else -> {
                                        rvLiteratureList.layoutManager =
                                            LinearLayoutManager(context)
                                    }
                                }


                            }
                            else -> {
                                when (it.data?.data == null) {
                                    false -> literatureListAdapter?.addLiteratureToList(it.data?.data)
                                    true -> {
                                        hasMoreData = false
                                        literatureListAdapter?.hideFooter()
                                    }
                                }

                            }

                        }

                        mIsFavList?.let {
                            if (it) {
                                FavouriteObservingService.initService(literatureListAdapter)
                            }
                        }

                        progressLayout.visibility = GONE
                        progressLayout.visibility = GONE
                    }
                    Status.ERROR -> {
                        noInternetLayout.visibility = VISIBLE
                        progressLayout.visibility = GONE
                    }
                }
            })

            model.favOrUnFavData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        progressLayout.visibility = GONE
                        when (it.data?.data) {
                            true -> {
                                literatureListAdapter?.removeItemAtPosition(clickedIndex)
                            }
                            else -> {
                            }
                        }
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = GONE
                    }
                    Status.LOADING -> {
                        progressLayout.visibility = VISIBLE
                    }
                }
            })

            btnRetry.handleClickEvent {
                loadData()
            }

            loadData()

        }
    }

    fun updateToolbarForThisFragment() {

        val txtMiladunnobi: String
        txtMiladunnobi = getString(R.string.title_eid_e_miladunnobi_robi)

        val title: String?
        title = when (mCatId) {
            R.string.namaz_rules_cat_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_namaz_sikhha)
            R.string.dua_cat_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_dua)
            R.string.hadis_cat_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_hadith)
            R.string.wallpaper_cat_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_wallpaper)
            R.string.animation_cat_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_animation)
            R.string.jakat_cat_id.getLocalisedTextFromResId() -> resources.getString(R.string.txt_jakat_calculator)
            R.string.hajj_cateogry_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_hajj)
            R.string.qurbani_cateogry_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_qurbani)
            R.string.miladunnobi_cateogry_id.getLocalisedTextFromResId() -> txtMiladunnobi
            R.string.donation_importance_id.getLocalisedTextFromResId() -> resources.getString(R.string.txt_charity_importance)
            R.string.event_cateogry_id.getLocalisedTextFromResId() -> resources.getString(R.string.cat_islamic_event)

            "625cf907bf0f1370b2ed535d" -> mPageTitle

            else -> resources.getString(R.string.cat_namaz_sikhha)

        }

        mDetailsCallBack?.setToolBarTitle(title)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }

    override fun onDestroy() {
        super.onDestroy()

        mIsFavList?.let {
            if (it) {
                FavouriteObservingService.clearServiceComponent()
            }
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(
            isFavList: Boolean,
            catId: String,
            subCatId: String,
            pageTitle: String? = null,
            showCalculateBtn: Boolean? = false,
            showHeaderImageMiladunnobi: Boolean? = false
        ) =
            LiteratureListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_FAV_LIST, isFavList)
                    putString(ARG_CAT_ID, catId)
                    putString(ARG_SUB_CAT_ID, subCatId)
                    putString(ARG_PAGE_TITLE, pageTitle)
                    putBoolean(ARG_SHOW_CALCULATE_BTN, showCalculateBtn!!)
                    putBoolean(ARG_SHOW_HEADER_IMG_Miladunnobi, showHeaderImageMiladunnobi!!)
                    putSerializable(ARG_LITERATURE_LIST_CALL_BACK, literatureListWrapper)
                }
            }
    }

    fun loadData() {

        when (mIsFavList) {
            true -> {
                Log.e("sss", "home${mIsFavList}")

                model.loadFavouriteLiteratureListBySubCategory(
                    mCatId!!,
                    "",
                    "$pageNo"
                )
            }
            else -> {
                Log.e("sss", "home${mIsFavList}")

                when (mCatId) {
                    R.string.wallpaper_cat_id.getLocalisedTextFromResId(), R.string.animation_cat_id.getLocalisedTextFromResId() -> {
                        model.loadImageBasedLiteratureListBySubCategory(
                            mCatId!!,
                            mSubCatId!!,
                            "$pageNo"
                        )
                    }
                    else -> {

                        model.loadTextBasedLiteratureListBySubCategory(
                            mCatId!!,
                            mSubCatId!!,
                            "$pageNo"
                        )
                    }
                }

            }
        }
    }

    override fun initPagingProperties() {
        pageNo = 1
        hasMoreData = true
    }

    override fun loadNextPage() {
        pageNo++
        loadData()
    }

    override fun hasMoreData(): Boolean {
        return hasMoreData
    }

    override fun performFavOrUnFavAction(
        catId: String,
        subCatId: String,
        literatureId: String,
        indexInAdapter: Int?,
        makeFav: Boolean
    ) {
        clickedIndex = indexInAdapter!!
        model.favouriteOrUnFavouriteLiterature(catId, subCatId, literatureId, makeFav)
    }


    override fun goToListeratureDetailsFragment(
        selectedIndex: Int,
        isFavList: Boolean
    ) {
        literatureListWrapper = LiteratureListWrapper(literatureListAdapter?.getLiteratureList())
        val fragment = FragmentProvider.getFragmentByName(
            name = PAGE_LITERATURE_DETAILS,
            detailsActivityCallBack = mDetailsCallBack,
            literatureListWrapper = literatureListWrapper,
            selectedLiteratureIndex = selectedIndex,
            currentPageNo = pageNo,
            isFav = isFavList,
        )

        if (mDetailsCallBack != null) {
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        } else {
            when (Util.checkSub()) {
                true -> {
                    Log.i(
                        "asdsdasddd",
                        "goToListeratureDetailsFragment: " + (mainCallback == null).toString()
                    )
                    mainCallback?.openDetailsActivityWithPageName(
                        pageName = PAGE_LITERATURE_DETAILS,
                        selectedIndex = selectedIndex,
                        currentPageNo = pageNo,
                        isFav = isFavList,
                        literatures = literatureListAdapter?.getLiteratureList(),
                    )
                }

                else -> {
                    mainCallback?.openDetailsActivityWithPageName(
                        pageName = PAGE_SUBSCRIPTION
                    )
                }
            }
        }

    }

    private fun hideActionButtons() {
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }


    object FavouriteObservingService {

        var adapter: LiteratureListAdapter? = null

        fun initService(literatureListAdapter: LiteratureListAdapter?) {
            adapter = literatureListAdapter

        }

        fun postFavouriteNotification(literature: Literature) {
            adapter?.addItemToList(literature)

        }

        fun postUnFavouriteNotification(literature: Literature) {
            adapter?.removeItemFromListIfExists(literature)
        }

        fun clearServiceComponent() {
            adapter = null
        }
    }
}

interface LiteratureItemClickCallBack {
    fun goToListeratureDetailsFragment(selectedIndex: Int, isFav: Boolean)
}




