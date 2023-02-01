package com.gakk.noorlibrary.ui.fragments.hajj

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.subcategory.Data
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.ui.adapter.HajjCategoryAdapter
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPreRegistrationFragment
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch


internal class HajjHomeFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null

    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel
    private lateinit var header_image: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var hajjPreRegistration: ConstraintLayout
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var sub_cat_rv: RecyclerView
    private lateinit var btnRetry: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_hajj_home,
            container, false
        )

        initUi(view)

        return view
    }

    private fun initUi(view: View) {
        header_image = view.findViewById(R.id.header_image)
        progressBar = view.findViewById(R.id.progressBar)
        hajjPreRegistration = view.findViewById(R.id.hajjPreRegistration)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        progressLayout = view.findViewById(R.id.progressLayout)
        sub_cat_rv = view.findViewById(R.id.sub_cat_rv)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarForThisFragment()


        val item = ImageFromOnline("hajj_page_top_image.png")

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
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                })
                .error(R.drawable.place_holder_16_9_ratio)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(header_image)
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HajjHomeFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
            loadData()

            btnRetry.handleClickEvent {
                loadData()
            }

        }

        hajjPreRegistration.handleClickEvent {
            mCallback?.addFragmentToStackAndShow(
                HajjPreRegistrationFragment.newInstance()
            )
        }

    }


    fun updateToolbarForThisFragment() {
        mCallback?.setToolBarTitle(resources.getString(R.string.cat_hajj))
    }

    private fun subscribeObserver() {
        model.subCategoryListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.GONE

                    val list = it.data?.data ?: mutableListOf()
                    val sortedListRobi =
                        list.sortedByDescending { list.indexOf(it) }
                            .toMutableList()

                    setUpRV(sortedListRobi)
                }
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                    noInternetLayout.visibility = View.GONE
                }
                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpRV(list: MutableList<Data>) {
        val currencyCat = requireContext().getString(R.string.hajj_currency_cateogry_id)
        val sortedList = list.filterNot { it.id.equals(currencyCat) }
        sub_cat_rv.apply {
            adapter = HajjCategoryAdapter().apply {
                submitList(sortedList)
                setOnItemClickListener {
                    Log.d("itemClicked", "setUpRV: " + it.id)

                    val mapCat = requireContext().getString(R.string.hajj_map_cateogry_id)

                    val hajjGuideCat =
                        requireContext().getString(R.string.hajj_guide_sub_category_id)
                    it.category?.let { catID ->
                        when (it.id) {
                            mapCat -> {

                                when (Util.checkSub()) {
                                    true -> {
                                        mCallback?.addFragmentToStackAndShow(
                                            HajjMapFragment.newInstance()
                                        )
                                    }
                                    else -> {
                                        requireContext().startActivity(
                                            Intent(requireContext(), DetailsActivity::class.java)
                                                .putExtra(PAGE_NAME, PAGE_SUBSCRIPTION_OPTION_LIST)
                                        )
                                    }
                                }

                            }

                            hajjGuideCat -> {
                                Log.e("hajjGuide", "Called")

                                if (Util.checkSub()) {
                                    if (isNetworkConnected(requireContext())) {
                                        val fragment = FragmentProvider.getFragmentByName(
                                            PAGE_HAJJ_GUIDE,
                                            detailsActivityCallBack = mCallback
                                        )
                                        mCallback?.addFragmentToStackAndShow(fragment!!)
                                    } else {
                                        mCallback?.showToastMessage(getString(R.string.txt_check_internet))
                                    }
                                } else {
                                    requireContext().startActivity(
                                        Intent(requireContext(), DetailsActivity::class.java)
                                            .putExtra(PAGE_NAME, PAGE_SUBSCRIPTION_OPTION_LIST)
                                    )
                                }
                            }

                            else -> {
                                val fragment = FragmentProvider.getFragmentByName(
                                    name = PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                                    detailsActivityCallBack = mCallback,
                                    catId = catID,
                                    subCatId = it.id,
                                    isFav = false
                                )
                                fragment?.let { it1 -> mCallback?.addFragmentToStackAndShow(it1) }
                            }
                        }
                    }

                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HajjHomeFragment()
    }

    fun loadData() {
        val mCatId = requireContext().resources.getString(R.string.hajj_cateogry_id)
        model.loadSubCategoriesByCatId(mCatId, "1")
    }
}