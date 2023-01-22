package com.gakk.noorlibrary.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.*
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.databinding.FragmentLiteratureDetailsBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.DownloadProgressControl
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

private const val ARG_LITERATURE_LIST_CALL_BACK = "literatureListCallBack"
private const val ARG_SELECTED_INDEX = "selectedIndex"
private const val ARG_CURRENT_PAGE_NO = "currentPageNo"
private const val ARG_IS_FAV_LIST = "isFavList"


internal class LiteratureDetailsFragment : Fragment(), PrevNextPanelControlCallBack, ToolbarControlCallBack {

    private var mDetailsCallback: DetailsCallBack? = null
    private var literatureListWrapper: LiteratureListWrapper? = null
    private var mSelectedIndex: Int? = null
    private var mIsFavList: Boolean? = null
    private var mLiteratureList: MutableList<Literature>? = null
    private var mCurrentPageNo: Int? = null
    private var mHasMoreData = true
    private var mIsSelectedLiteratureFav: Boolean? = null
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private var mFavButtonClicked = false
    var literature: Literature? = null


    private var favOrUnFavAction: () -> Unit = {
        mFavButtonClicked = true
        model.favouriteOrUnFavouriteLiterature(
            literatureId = literature?.id!!,
            catId = literature?.category!!,
            subCatId = literature?.subcategory ?: "undefined",
            makeFavourite = !(mIsSelectedLiteratureFav ?: false)
        )
    }


    private lateinit var binding: FragmentLiteratureDetailsBinding

    private var downloadStatusContol = DownloadStatusControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            literatureListWrapper =
                it.getSerializable(ARG_LITERATURE_LIST_CALL_BACK) as LiteratureListWrapper
            mSelectedIndex = it.getInt(ARG_SELECTED_INDEX)
            mCurrentPageNo = it.getInt(ARG_CURRENT_PAGE_NO)
            mIsFavList = it.getBoolean(ARG_IS_FAV_LIST)
        }

        mDetailsCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_literature_details,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {

            mDetailsCallback?.setOrUpdateActionButtonTag(SHARE, ActionButtonType.TypeTwo)

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@LiteratureDetailsFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            model.literatureListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE

                    }
                    Status.SUCCESS -> {

                        binding.progressLayout.root.visibility = View.GONE
                        when (it.data?.data) {
                            null -> {
                                mHasMoreData = false
                            }
                            else -> {
                                mLiteratureList?.addAll(it.data.data)
                                mCurrentPageNo = mCurrentPageNo!! + 1
                                mSelectedIndex = mSelectedIndex!! + 1
                            }
                        }

                        loadUIWithSelectedDataAndPrevNextControlState()

                    }
                    Status.ERROR -> {
                        mDetailsCallback?.showToastMessage(resources.getString(R.string.error_message))
                        binding.progressLayout.root.visibility = View.GONE
                    }
                }
            })
            model.favOrUnFavData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE
                        Log.e("favOrUnFavData", "LOADING")
                    }
                    Status.SUCCESS -> {
                        when (it.data?.status) {
                            200 -> {
                                loadFavStatus()
                                Log.e("favOrUnFavData", "SUCCESS")
                            }
                            else -> {
                                binding.progressLayout.root.visibility = View.GONE
                                mDetailsCallback?.showToastMessage(getString(R.string.api_error_msg))
                            }
                        }

                    }
                    Status.ERROR -> {
                        mDetailsCallback?.showToastMessage(resources.getString(R.string.error_message))
                        binding.progressLayout.root.visibility = View.GONE
                        Log.e("favOrUnFavData", "ERROR")
                    }
                }
            })
            model.isFavData.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressLayout.root.visibility = View.VISIBLE

                    }
                    Status.SUCCESS -> {
                        binding.progressLayout.root.visibility = View.GONE
                        mIsSelectedLiteratureFav = it.data?.data

                        if (mFavButtonClicked) {
                            mFavButtonClicked = false
                            when (mIsSelectedLiteratureFav) {
                                true -> {
                                    LiteratureListFragment.FavouriteObservingService.postFavouriteNotification(
                                        literature!!
                                    )
                                }
                                else -> {
                                    LiteratureListFragment.FavouriteObservingService.postUnFavouriteNotification(
                                        literature!!
                                    )
                                }
                            }

                        }

                        Log.i("SELECTED_LIT", "stat ${mIsSelectedLiteratureFav!!}")
                        setFavIcon()
                        setFavAction()

                    }
                    Status.ERROR -> {
                        mDetailsCallback?.showToastMessage(resources.getString(R.string.error_message))
                        binding.progressLayout.root.visibility = View.GONE
                    }
                }
            }
            loadInitialLiteratureList()
            loadUIWithSelectedDataAndPrevNextControlState()
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun loadInitialLiteratureList() {

        mLiteratureList =
            literatureListWrapper?.literatures
    }

    fun loadUIWithSelectedDataAndPrevNextControlState() {
        populateUIWithSelectedData()
        setUpPrevNextControlState()
        loadFavStatus()
        updateToolbarForThisFragment()
        hideAllContentContainers()
        setVisibilityOfContainers()
        updateDownloadStatusForDownloadables()
    }

    override fun loadFavStatus() {
        Log.e("contentId", "catid ${literature?.category}litId${literature?.id}")
        literature?.category?.let {
            model.loadIsLiteratureFav(literature?.category!!, "", literature?.id!!)
        }
    }

    override fun setFavIcon() {
        when (mIsSelectedLiteratureFav ?: false) {
            false -> {
                mDetailsCallback?.setOrUpdateActionButtonTag(FAV, ActionButtonType.TypeOne)
            }
            true -> {
                mDetailsCallback?.setOrUpdateActionButtonTag(FAV_FILLED, ActionButtonType.TypeOne)
            }
        }

    }

    override fun setFavAction() {
        mDetailsCallback?.setActionOfActionButton(favOrUnFavAction, ActionButtonType.TypeOne)
    }

    fun hideAllContentContainers() {
        binding.layoutTextContainer.visibility = GONE
        binding.layoutDownloadableContainer.visibility = GONE
    }

    fun setVisibilityOfContainers() {
        literature?.let {
            when (it.category) {
                R.string.wallpaper_cat_id.getLocalisedTextFromResId(), R.string.animation_cat_id.getLocalisedTextFromResId() -> {
                    binding.layoutDownloadableContainer.visibility = VISIBLE
                }
                else -> binding.layoutTextContainer.visibility = VISIBLE
            }
        }
    }

    fun updateDownloadStatusForDownloadables() {
        literature?.let {
            when (it.category) {
                R.string.wallpaper_cat_id.getLocalisedTextFromResId(), R.string.animation_cat_id.getLocalisedTextFromResId() -> {
                    downloadStatusContol.setTag(it.id!!, binding)
                }
                else -> {
                }
            }
        }
    }

    fun populateUIWithSelectedData() {
        literature = mLiteratureList?.get(mSelectedIndex!!)
        binding.tvDesArabic.visibility = GONE
        literature?.textInArabic?.let {
            if (it.isNotEmpty()) {
                Log.i("ACCESSED...", "$it.length")
                binding.tvDesArabic.visibility = VISIBLE
            }
        }

        binding.tvMeaning.visibility = GONE
        literature?.pronunciation?.let {
            if (it.isNotEmpty()) {
                Log.i("ACCESSED...", "$it.length")
                binding.tvMeaning.visibility = VISIBLE
            }
        }
        binding.literature = literature
    }

    override fun setUpPrevNextControlState() {
        setPrevControlState()
        setNextControlState()
        setPrevControlClickEvent()
        setNextControlClickEvent()
    }

    override fun setPrevControlState() {
        mSelectedIndex?.let {
            when (it > 0) {
                true -> {
                    binding.prevNextPanel.btnPrevContent.isEnabled = true
                    binding.prevNextPanel.tvPrevContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.colorPrimary
                        )
                    )
                }
                false -> {

                    binding.prevNextPanel.btnPrevContent.isEnabled = false
                    binding.prevNextPanel.tvPrevContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.disabled_color
                        )
                    )
                }
            }
        }

    }

    override fun setNextControlState() {
        mSelectedIndex?.let {
            Log.e("indexselected", "${it}")
            when (it >= mLiteratureList!!.size - 1 && !mHasMoreData) {
                true -> {
                    binding.prevNextPanel.btnNextContent.isEnabled = false
                    binding.prevNextPanel.tvNextContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.disabled_color
                        )
                    )
                }
                false -> {
                    binding.prevNextPanel.btnNextContent.isEnabled = true
                    binding.prevNextPanel.tvNextContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.colorPrimary
                        )
                    )
                }
            }
        }
    }

    override fun setPrevControlClickEvent() {
        binding.prevNextPanel.layoutPrevActionContent.handleClickEvent {
            mSelectedIndex?.let {
                when (it > 0) {
                    true -> {
                        mSelectedIndex = mSelectedIndex!! - 1
                        loadUIWithSelectedDataAndPrevNextControlState()
                    }
                    false -> {/*Do nothing*/
                    }
                }
            }
        }
    }

    override fun setNextControlClickEvent() {
        binding.prevNextPanel.nextActionContent.handleClickEvent {
            mSelectedIndex?.let {
                when (it > mLiteratureList!!.size && !mHasMoreData) {
                    true -> {/*Do nothing*/
                    }
                    false -> {
                        if (it < mLiteratureList!!.size - 1) {
                            mSelectedIndex = mSelectedIndex!! + 1
                            loadUIWithSelectedDataAndPrevNextControlState()
                        } else {
                            if (mHasMoreData) {
                                mCurrentPageNo = mCurrentPageNo!! + 1
                                //var literature=mLiteratureList!!.get(0)
                                when (mIsFavList) {
                                    true -> model.loadFavouriteLiteratureListBySubCategory(
                                        literature?.category!!,
                                        literature?.subcategory ?: "undefined",
                                        mCurrentPageNo.toString()
                                    )
                                    else -> model.loadTextBasedLiteratureListBySubCategory(
                                        literature?.category!!,
                                        literature?.subcategory ?: "undefined",
                                        mCurrentPageNo.toString()
                                    )
                                }

                            }
                        }

                    }
                }
            }
        }


    }

    fun updateToolbarForThisFragment() {
        mDetailsCallback?.toggleToolBarActionIconsVisibility(true, ActionButtonType.TypeOne)
        mDetailsCallback?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
        when (literature?.category) {
            R.string.namaz_rules_cat_id.getLocalisedTextFromResId(),
            R.string.animation_cat_id.getLocalisedTextFromResId(),
            R.string.wallpaper_cat_id.getLocalisedTextFromResId() -> mDetailsCallback?.toggleToolBarActionIconsVisibility(
                false,
                ActionButtonType.TypeOne
            )

            R.string.hajj_cateogry_id.getLocalisedTextFromResId() -> {
                mDetailsCallback?.toggleToolBarActionIconsVisibility(
                    false,
                    ActionButtonType.TypeOne
                )
            }

        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            literatureListWrapper: LiteratureListWrapper?,
            selectedIndex: Int,
            currentPageNo: Int,
            isFavList: Boolean
        ) =
            LiteratureDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_LIST_CALL_BACK, literatureListWrapper)
                    putInt(ARG_SELECTED_INDEX, selectedIndex)
                    putInt(ARG_CURRENT_PAGE_NO, currentPageNo)
                    putBoolean(ARG_IS_FAV_LIST, isFavList)
                }
            }
    }

    inner class DownloadStatusControl {
        var downloadTag: String? = null
        fun setTag(tag: String, binding: FragmentLiteratureDetailsBinding) {
            if (downloadTag != null) {
                DownloadProgressControl.removeLayoutFromMap(downloadTag!!, binding)
            }
            downloadTag = tag
            DownloadProgressControl.addLayoutToMapAndUpdate(downloadTag!!, binding)

        }
    }
}

private interface PrevNextPanelControlCallBack {
    fun setUpPrevNextControlState()
    fun setPrevControlState()
    fun setNextControlState()
    fun setPrevControlClickEvent()
    fun setNextControlClickEvent()

}

private interface ToolbarControlCallBack {
    fun loadFavStatus()
    fun setFavIcon()
    fun setFavAction()
}
