package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

internal class DonationHomeFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var termUrl: String? = null
    private lateinit var clOrganisations: ConstraintLayout
    private lateinit var ivDonate: AppCompatImageView
    private lateinit var ivCharityOrganization: AppCompatImageView
    private lateinit var ivDonationImportance: AppCompatImageView
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var appCompatImageView3: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DonationHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_donation_home,
            container, false
        )

        clOrganisations = view.findViewById(R.id.clOrganisations)
        ivDonate = view.findViewById(R.id.ivDonate)
        ivCharityOrganization = view.findViewById(R.id.ivCharityOrganization)
        ivDonationImportance = view.findViewById(R.id.ivDonationImportance)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        appCompatImageView3 = view.findViewById(R.id.appCompatImageView3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarForThisFragment()

        val item = ImageFromOnline("ic_donation_header.png")
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
                .into(appCompatImageView3)
        }
        clOrganisations.visibility = View.VISIBLE

        ivDonate.handleClickEvent {

            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION,
                detailsActivityCallBack = mDetailsCallBack,
                catName = "Donation"
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

        ivCharityOrganization.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION,
                detailsActivityCallBack = mDetailsCallBack,
                catName = "Charity"
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

        ivDonationImportance.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION_IMPORTANCE,
                detailsActivityCallBack = mDetailsCallBack
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }


        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@DonationHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.literatureListData.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        progressLayout.visibility = View.VISIBLE
                    }

                    Status.SUCCESS -> {
                        termUrl = it.data?.data?.get(0)?.refUrl
                        progressLayout.visibility = View.GONE
                    }

                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.txt_donate))
    }
}