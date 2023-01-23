package com.gakk.noorlibrary.ui.fragments.zakat.donation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.databinding.FragmentOrganizationDetailsBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.FragmentProvider
import com.gakk.noorlibrary.util.PAGE_DONATION
import com.gakk.noorlibrary.util.handleClickEvent

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

private const val ARG_LITERATURE_DETAILS = "literatureDetails"

internal class OrganizationDetailsFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private var mLiterature: Literature? = null
    private lateinit var btnDonate: AppCompatButton
    private lateinit var layoutVisit: ConstraintLayout
    private lateinit var textViewNormal10: AppCompatTextView
    private lateinit var tvDetails: AppCompatTextView
    private lateinit var tvDesOrganisation: AppCompatTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var appCompatImageView10: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            mLiterature = it?.getSerializable(ARG_LITERATURE_DETAILS) as Literature?
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance(
            itemLiterature: Literature
        ) =
            OrganizationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LITERATURE_DETAILS, itemLiterature)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_organization_details,
            container, false
        )

        btnDonate = view.findViewById(R.id.btnDonate)
        layoutVisit = view.findViewById(R.id.layoutVisit)
        textViewNormal10 = view.findViewById(R.id.textViewNormal10)
        tvDetails = view.findViewById(R.id.tvDetails)
        tvDesOrganisation = view.findViewById(R.id.tvDesOrganisation)
        progressBar = view.findViewById(R.id.progressBar)
        appCompatImageView10 = view.findViewById(R.id.appCompatImageView10)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val  literature = mLiterature
        textViewNormal10.text = literature?.subcategoryName
        tvDetails.text = literature?.textInArabic
        tvDesOrganisation.text = literature?.text

        Noor.appContext?.let {
            Glide.with(it)
                .load(literature?.fullImageUrl)
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
                .error(R.drawable.place_holder_2_3_ratio)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(appCompatImageView10)
        }

        btnDonate.handleClickEvent {
            val fragment = FragmentProvider.getFragmentByName(
                PAGE_DONATION,
                detailsActivityCallBack = mDetailsCallBack,
                catName = "Donation"
            )
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }

       layoutVisit.handleClickEvent {
            mLiterature?.refUrl?.let { mDetailsCallBack?.openUrl(it) }

        }
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.txt_charity_organization))
    }
}