package com.mcc.noor.ui.fragments.hajj.umrah_hajj

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.UmrahHajjViewModel
import com.mcc.noor.model.umrah_hajj.UmrahHajjData
import com.gakk.noorlibrary.ui.adapter.umrahhajj.UmrahPackAdapter
import kotlinx.coroutines.launch


class UmrahHajjFragment : Fragment(), UmrahPackAdapter.OnItemClickListener, UmraHajjToolbarControl {

    private var mDetailsCallBack: DetailsCallBack? = null
    private  var umrah_pack_adapter : UmrahPackAdapter? = null
    private lateinit var viewModel : UmrahHajjViewModel
    private lateinit var repository: RestRepository

    //view

    private lateinit var umrah_pack:RecyclerView
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton
    private lateinit var progressLayout : ConstraintLayout


    private var historyIconClick: () -> Unit = {
        mDetailsCallBack?.addFragmentToStackAndShow(PaymentHistoryFragment.newInstance())
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            UmrahHajjFragment()
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

            viewModel = ViewModelProvider(
                this@UmrahHajjFragment,
                UmrahHajjViewModel.FACTORY(repository)
            ).get(UmrahHajjViewModel::class.java)

            viewModel.getUmrahHajjPackageList()

            initObserver()

        }

        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        val view = inflater.inflate(
            R.layout.fragment_umrah_hajj,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {
        umrah_pack = view.findViewById(R.id.umrah_pack)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        btnRetry = view.findViewById(R.id.btnRetry)
        progressLayout = view.findViewById(R.id.progressLayout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_umrah_details))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true, ActionButtonType.TypeThree,R.drawable.ic_payment_history)

        setupToolbarCallback()

        btnRetry.handleClickEvent {

            lifecycleScope.launch {
                viewModel.getUmrahHajjPackageList()
            }
        }


    }

    override fun onItemClick(postion: Int, umrah_pack_list: UmrahHajjData) {
        mDetailsCallBack?.addFragmentToStackAndShow(
            UmrahDetailsFragment.newInstance(umrah_pack_list)
        )
    }

    override fun setupToolbarCallback() {

        mDetailsCallBack?.setActionOfActionButton(historyIconClick, ActionButtonType.TypeThree)

    }

    private fun initObserver()
    {
        viewModel.umrah_hajj_package_list.observe(viewLifecycleOwner)
        {
            when(it)
            {
                is UmrahHajjResource.Error ->
                {

                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.VISIBLE
                }
                UmrahHajjResource.Loading ->
                {
                    progressLayout.visibility = View.VISIBLE
                    noInternetLayout.visibility = View.GONE
                }
                is UmrahHajjResource.UmrahHajjPackListresponse ->
                {

                    progressLayout.visibility = View.GONE
                    noInternetLayout.visibility = View.GONE

                    it.data.data?.data?.let {
                        umrah_pack_adapter =  UmrahPackAdapter(it,this@UmrahHajjFragment)

                        umrah_pack.adapter = umrah_pack_adapter

                    }?: run {
                        noInternetLayout.visibility = View.VISIBLE
                    }

                }
                else -> Unit
            }
        }
    }

}

private interface UmraHajjToolbarControl
{
    fun setupToolbarCallback()
}