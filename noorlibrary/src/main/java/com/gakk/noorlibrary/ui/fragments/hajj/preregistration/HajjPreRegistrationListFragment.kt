package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentHajjPreRegistrationListBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.hajjpackage.HajjPreRegistrationListResponse
import com.gakk.noorlibrary.model.hajjpackage.PreRegisteredUserInfo
import com.gakk.noorlibrary.ui.adapter.PreregistrationListAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

class HajjPreRegistrationListFragment : Fragment(), PaymentControl {

    private var mCallback: DetailsCallBack? = null
    private lateinit var binding: FragmentHajjPreRegistrationListBinding
    private lateinit var repository: RestRepository
    private lateinit var viewModel: HajjViewModel
    private lateinit var viewModelPreRegistration: PreregistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HajjPreRegistrationListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_hajj_pre_registration_list,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateToolbarForThisFragment()

        viewModelPreRegistration =
            ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            viewModel = ViewModelProvider(
                this@HajjPreRegistrationListFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            subscribeObserver()

            viewModel.loadPreRegistrationList()
        }

    }

    fun updateToolbarForThisFragment() {
        mCallback?.setToolBarTitle("আপনার প্রাক-নিবন্ধন সমূহ")
    }

    private fun subscribeObserver() {
        viewModel.preRegistrationList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    val list = it.data?.data as MutableList<HajjPreRegistrationListResponse.Data>

                    if (list.size > 0) {
                        binding.rvPreregistration.adapter =
                            PreregistrationListAdapter(list, mCallback, this)
                    } else {
                        binding.noDataLayout.item = ImageFromOnline("bg_no_data.png")
                        binding.noDataLayout.root.visibility = View.VISIBLE
                        binding.noDataLayout.tvTitle.setText("প্রাক-নিবন্ধন নেই")
                        binding.noDataLayout.tvDes.visibility = View.VISIBLE
                        binding.noDataLayout.tvDes.setText("নতুন প্রাক-নিবন্ধিতদের এখানে যোগ করা হবে।")
                    }

                    binding.progressLayout.root.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }

    override fun gotoPaymentPage(trackingNo: String?, name: String?, email: String?) {
        viewModelPreRegistration.gotoNext(2)
        val preRegisteredUserInfo = PreRegisteredUserInfo(
            trackingNo,
            name,
            email
        )
        viewModelPreRegistration.selectedInfo(preRegisteredUserInfo)
        mCallback?.addFragmentToStackAndShow(
            HajjpreRegistrationDetailsFragment.newInstance()
        )
    }
}

interface PaymentControl {
    fun gotoPaymentPage(trackingNo: String?, name: String?, email: String?)
}