package com.mcc.noor.ui.fragments.hajj.umrah_hajj

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.util.NoDataLayout
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.UmrahHajjViewModel
import com.google.gson.Gson
import com.gakk.noorlibrary.ui.adapter.umrahhajj.UmrahPaymentHistoryAdapter
import kotlinx.coroutines.launch


class PaymentHistoryFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var viewmodel: UmrahHajjViewModel
    private lateinit var repository: RestRepository
    private  var umrah_pay_history_adapter : UmrahPaymentHistoryAdapter? = null


    //view
    private lateinit var noDataLayout : ConstraintLayout
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton
    private lateinit var progressLayout:ConstraintLayout
    private lateinit var payHistory:RecyclerView
    private lateinit var imgNoInternet:ImageView


    companion object {

        @JvmStatic
        fun newInstance() =
            PaymentHistoryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        lifecycleScope.launch {

            repository = RepositoryProvider.getRepository()

            viewmodel = ViewModelProvider(
                this@PaymentHistoryFragment,
                UmrahHajjViewModel.FACTORY(repository)
            ).get(UmrahHajjViewModel::class.java)

            viewmodel.let {
               initObserver()
                viewmodel.getAllPaymentHistory(AppPreference.userNumber!!)
            }

        }

        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_umrah_pay_history))

        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        val view = inflater.inflate(
            R.layout.fragment_payment_history,
            container, false
        )

        initView(view)


        return view
    }

    private fun initView(view:View)
    {

        noDataLayout = view.findViewById(R.id.noDataLayout)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        btnRetry = view.findViewById(R.id.btnRetry)
        progressLayout = view.findViewById(R.id.progressLayout)
        payHistory = view.findViewById(R.id.payHistory)
        imgNoInternet = view.findViewById(R.id.imgNoInternet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true, ActionButtonType.TypeThree,R.drawable.ic_payment_history)
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_umrah_details))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NoDataLayout(view)

        btnRetry.handleClickEvent {

            lifecycleScope.launch {
                viewmodel.getAllPaymentHistory(AppPreference.userNumber!!)
            }
        }
    }


    private fun initObserver(){

        viewmodel.umrah_hajj_payment_history.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is UmrahHajjResource.Error ->
                {
                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.VISIBLE
                }
                is UmrahHajjResource.GetAllPaymentHistory ->
                {

                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.GONE
                    noDataLayout.visibility = View.GONE

                    when(it.data.data?.status)
                    {
                        200 ->
                        {
                            Log.e("Umrah Pay List",Gson().toJson(it.data.data))
                            umrah_pay_history_adapter = it.data.data?.data?.let { it1 ->
                                UmrahPaymentHistoryAdapter(
                                    it1
                                )
                            }

                            payHistory.adapter = umrah_pay_history_adapter

                        }

                        204 -> noDataLayout.visibility = View.VISIBLE
                    }

                }
                UmrahHajjResource.Loading ->
                {
                    progressLayout.visibility = View.VISIBLE
                    noInternetLayout.visibility = View.GONE
                }
                else -> Unit
            }
        }
    }



}