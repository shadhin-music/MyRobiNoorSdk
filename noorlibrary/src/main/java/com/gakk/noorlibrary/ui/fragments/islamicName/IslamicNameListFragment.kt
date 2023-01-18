package com.gakk.noorlibrary.ui.fragments.islamicName

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.DialogIslamicNameBinding
import com.gakk.noorlibrary.databinding.FragmentIslamicNamesListBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.islamicName.IslamicName
import com.gakk.noorlibrary.ui.adapter.IslamicNameAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.IslamicNameViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

private const val ARG_PAGE_TYPE = "pageType"
private const val SHOW_FEMALE_NAMES_TAG = "nameType"
private const val TAG = "DebugForIslamicName"

internal class IslamicNameListFragment : Fragment() {

    private lateinit var gender: String
    private lateinit var binding: FragmentIslamicNamesListBinding
    private lateinit var nameAdapter: IslamicNameAdapter

    private var mPageType: String? = null
    private var mDetailsCallBack: DetailsCallBack? = null
    private var isFavPageIsShowing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPageType = it.getString(ARG_PAGE_TYPE)
            val showFemaleName = it.getBoolean(SHOW_FEMALE_NAMES_TAG)
            gender = if (showFemaleName) FEMALE else MALE
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    private lateinit var repository: RestRepository
    private lateinit var viewmodel: IslamicNameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        lifecycleScope.launch {
            binding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.fragment_islamic_names_list,
                    container,
                    false
                )

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            viewmodel = ViewModelProvider(
                this@IslamicNameListFragment,
                IslamicNameViewModel.FACTORY(repository)
            ).get(IslamicNameViewModel::class.java)

            initView()
            subscribeObserver()
            requestData()

            binding.noInternetLayout.btnRetry.handleClickEvent {
                requestData()
            }
        }
        return binding.root
    }

    private fun initView() {
        nameAdapter = IslamicNameAdapter()
    }

    private fun requestData() {
        if (mPageType == NAME_LIST) {
            isFavPageIsShowing = false
            //need name list, call names list api
            viewmodel.getIslamicNameByGender(gender)
        } else {
            isFavPageIsShowing = true
            //need fav name list, call fav name list api
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFavPageIsShowing && DataHolder.needToRefresh) {
            setupRV(DataHolder.getFavoraitedDataOnly())
            DataHolder.needToRefresh = false
        }
    }


    private fun subscribeObserver() {
        viewmodel.nameResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.noInternetLayout.root.hide()
                    binding.progressLayout.root.visibility = View.GONE
                    binding.headerLayout.show()
                    it.data?.data?.let { list ->
                        if (list.isNotEmpty()) {
                            DataHolder.contentList = list
                            setupRV(list)
                        }
                    }

                }
                Status.ERROR -> {
                    binding.headerLayout.hide()
                    binding.progressLayout.root.visibility = View.GONE
                    binding.noInternetLayout.root.show()
                    mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                }
                Status.LOADING -> {
                    binding.headerLayout.hide()
                    binding.noInternetLayout.root.hide()
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
            }
        }

        viewmodel.favouriteIslamicNameResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.ERROR -> Log.d(TAG, "favorite status error: ${it.status}")
                Status.LOADING -> Unit
                Status.SUCCESS -> {
                    Log.d(TAG, "favorite status: ${it.status}")

                }
            }
        })

        viewmodel.unFavouriteIslamicNameResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.ERROR -> Log.d(TAG, "unfavorite status error: ${it.status}")
                Status.LOADING -> Unit
                Status.SUCCESS -> {
                    Log.d(TAG, "unfavorite status: ${it.status}")
                }
            }
        })


        viewmodel.allFavNameResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.noInternetLayout.root.hide()
                    binding.progressLayout.root.visibility = View.GONE
                    it.data?.data?.let { list ->
                        Log.d(TAG, "get all fav favorite status: ${list.size}")
                        if (list.isNotEmpty()) {
                            setupRV(list)
                        }
                    }

                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                    binding.noInternetLayout.root.show()
                    mDetailsCallBack?.showToastMessage(resources.getString(R.string.error_message))
                }
                Status.LOADING -> {
                    binding.noInternetLayout.root.hide()
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun setupRV(list: MutableList<IslamicName>) {
        nameAdapter.apply {
            submitList(list)
            setOnItemClickListener {
                showSelectedNameInDialog(it)
            }
        }
        binding.nameListRv.apply {
            if (adapter == null) {
                adapter = nameAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }
    }

    private fun showSelectedNameInDialog(islamicName: IslamicName) {
        val customDialog =
            MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
        val binding: DialogIslamicNameBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()),
            R.layout.dialog_islamic_name,
            null,
            false
        )


        val dialogView: View = binding.root
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        binding.item = islamicName
        binding.itemBoy = ImageFromOnline("ic_boy_avatar.png")

        binding.btnFav.isSelected = islamicName.userFavouritedThis

        binding.favLayout.handleClickEvent {
            if (islamicName.userFavouritedThis) {
                islamicName.userFavouritedThis = false
                viewmodel.unFavouriteIslamicName(islamicName.id)

            } else {
                islamicName.userFavouritedThis = true
                viewmodel.favouriteIslamicName(islamicName.id)
            }
            binding.btnFav.isSelected = !binding.btnFav.isSelected


            if (isFavPageIsShowing) {
                //if user in favListPage and after update just refresh rv
                setupRV(DataHolder.getFavoraitedDataOnly())
                alertDialog.dismiss()
            } else {
                //fav change in name list page so need to update list in FavList page
                DataHolder.needToRefresh = true
            }
        }

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        binding.btnDismiss.handleClickEvent {
            alertDialog.dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            paramPageType: String,
            detailsCallBack: DetailsCallBack?,
            showFemaleName: Boolean
        ) =
            IslamicNameListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PAGE_TYPE, paramPageType)
                    putBoolean(SHOW_FEMALE_NAMES_TAG, showFemaleName)
                }
            }
    }

    object DataHolder {
        var contentList = mutableListOf<IslamicName>()
            set(value) {
                needToRefresh = true
                field = value
            }

        var needToRefresh = true

        fun getFavoraitedDataOnly(): MutableList<IslamicName> {
            val newlist = contentList.filter {
                it.userFavouritedThis
            }

            return newlist.toMutableList()
        }

    }


}