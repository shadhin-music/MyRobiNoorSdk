package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentRefundRequestHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


internal class RefundRequestFragment : Fragment() {

    private lateinit var binding: FragmentRefundRequestHomeBinding
    private var mCallback: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()

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
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_refund_request_home,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle("রিফান্ড আবেদন")

        binding.item = ImageFromOnline("ic_refund_peocess.png")

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

        binding.btnNextStep.handleClickEvent {
            if (!binding.appCompatCheckBox.isChecked) {
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
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    literatureList = it.data?.data ?: mutableListOf()

                    binding.literature = literatureList.get(0)

                    binding.progressLayout.root.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }
}