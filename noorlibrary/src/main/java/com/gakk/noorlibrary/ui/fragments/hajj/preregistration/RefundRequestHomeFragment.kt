package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
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
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


internal class RefundRequestFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()
    private lateinit var btnNextStep: AppCompatButton
    private lateinit var appCompatCheckBox: AppCompatCheckBox
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var appCompatImageView19: AppCompatImageView
    private lateinit var appCompatTextView22: AppCompatTextView
    private lateinit var tvPriceReg: AppCompatTextView
    private lateinit var tvProcess: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RefundRequestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_refund_request_home,
            container, false
        )
        btnNextStep = view.findViewById(R.id.btnNextStep)
        appCompatCheckBox = view.findViewById(R.id.appCompatCheckBox)
        progressLayout = view.findViewById(R.id.progressLayout)
        appCompatImageView19 = view.findViewById(R.id.appCompatImageView19)
        appCompatTextView22 = view.findViewById(R.id.appCompatTextView22)
        tvPriceReg = view.findViewById(R.id.tvPriceReg)
        tvProcess = view.findViewById(R.id.tvProcess)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle("রিফান্ড আবেদন")

        val item = ImageFromOnline("ic_refund_peocess.png")

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
                .into(appCompatImageView19)
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@RefundRequestFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            model.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.hajj_refund_category_id),
                "undefined",
                "1"
            )

            subscribeObserver()
        }

        btnNextStep.handleClickEvent {
            if (!appCompatCheckBox.isChecked) {
                Toast.makeText(
                    requireContext(),
                    "শর্তাবলীতে সম্মতি প্রদান করুন ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                mCallback?.addFragmentToStackAndShow(
                    RefundRequestSubmitFragment.newInstance(
                        literatureList.get(0).textInArabic,
                        literatureList.get(0).pronunciation
                    )
                )
            }
        }
    }

    private fun subscribeObserver() {
        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    literatureList = it.data?.data ?: mutableListOf()

                    val literature = literatureList.get(0)
                    appCompatTextView22.text = literature.textInArabic
                    tvPriceReg.text = literature.pronunciation
                    tvProcess.text = literature.text

                    progressLayout.visibility = View.GONE
                }

                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }
}