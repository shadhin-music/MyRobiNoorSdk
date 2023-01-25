package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.gakk.noorlibrary.ui.adapter.HajjCategoryAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

internal class HajjPreRegistrationFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel
    private lateinit var viewModel: PreregistrationViewModel
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton
    private lateinit var btnHajjPreReg: AppCompatButton
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var rvOthersCat: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var hajjImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_hajj_pre_registration_home,
            container, false
        )
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)
        btnHajjPreReg = view.findViewById(R.id.btnHajjPreReg)
        progressLayout = view.findViewById(R.id.progressLayout)
        rvOthersCat = view.findViewById(R.id.rv_others_cat)
        progressBar = view.findViewById(R.id.progressBar)
        hajjImage = view.findViewById(R.id.hajjImage)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        val item = ImageFromOnline("ic_hajj_header_image.png")

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
                .into(hajjImage)
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@HajjPreRegistrationFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()
            loadData()

            btnRetry.handleClickEvent {
                loadData()
            }

        }
        btnHajjPreReg.handleClickEvent {
            viewModel.gotoNext(0)
            mCallback?.addFragmentToStackAndShow(
                HajjpreRegistrationDetailsFragment.newInstance()
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

                    val filteredList =
                        list.filter { it.id == "625d20b93e65c410063b7360" || it.id == "625d20db3e65c410063b7361" || it.id == "625d21403e65c410063b7364" || it.id == "625d20ec3e65c410063b7362" } as MutableList<Data>

                    setUpRV(filteredList)
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
        rvOthersCat.apply {
            adapter = HajjCategoryAdapter().apply {
                submitList(list)
                setOnItemClickListener {

                    val preRegistrationsId = "625d20b93e65c410063b7360"

                    it.category?.let { catID ->
                        when (it.id) {
                            preRegistrationsId -> {
                                mCallback?.addFragmentToStackAndShow(
                                    HajjPreRegistrationListFragment.newInstance()
                                )
                            }

                            else -> {
                                val fragment = FragmentProvider.getFragmentByName(
                                    name = PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                                    detailsActivityCallBack = mCallback,
                                    catId = catID,
                                    subCatId = it.id,
                                    isFav = false,
                                    pageTitle = it.name
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
            HajjPreRegistrationFragment()
    }

    fun loadData() {
        val mCatId =
            requireContext().resources.getString(R.string.hajj_pre_registration_category_id)
        model.loadSubCategoriesByCatId(mCatId, "1")
    }
}