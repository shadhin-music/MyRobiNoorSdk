package com.gakk.noorlibrary.ui.fragments.zakat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.zakat.ZakatModel
import com.gakk.noorlibrary.ui.adapter.ZakatListAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.ZakatViewModel
import kotlinx.coroutines.launch

internal class ZakatListFragment : Fragment(), ZakatListAdapter.OnItemClickListener {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var viewModel: ZakatViewModel
    private lateinit var repository: RestRepository
    private var adapter: ZakatListAdapter? = null
    private lateinit var listZakat: RecyclerView
    private lateinit var progressLayout : ConstraintLayout
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var noDataLayout: ConstraintLayout
    private lateinit var btnRetry: AppCompatButton
    private lateinit var imgNoInternet:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ZakatListFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_zakat_list,
            container, false
        )


        listZakat = view.findViewById(R.id.listZakat)
        progressLayout = view.findViewById(R.id.progressLayout)
        noInternetLayout = view.findViewById(R.id.noInternetLayout)
        noDataLayout = view.findViewById(R.id.noDataLayout)
        imgNoInternet = view.findViewById(R.id.imgNoInternet)
        btnRetry = noInternetLayout.findViewById(R.id.btnRetry)

        lifecycleScope.launch {

            repository = RepositoryProvider.getRepository()

            viewModel = ViewModelProvider(requireActivity(),ZakatViewModel.FACTORY(repository))[ZakatViewModel::class.java]

            initObserver()
            viewModel.getZakatList()

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRetry.handleClickEvent {

            lifecycleScope.launch {
                viewModel.getZakatList()
            }
        }
    }


    private fun initObserver()
    {

        viewModel.zakatCallback.observe(viewLifecycleOwner)
        {
            when(it)
            {
                1 -> lifecycleScope.launch {
                    viewModel.getZakatList()
                    viewModel.callback(0)
                }

                0-> Unit
            }
        }
        viewModel.zakat_list.observe(viewLifecycleOwner)
        {
            progressLayout.hide()
            noDataLayout.hide()
            noInternetLayout.hide()

            when(it)
            {
                is ZakatResource.Error -> noInternetLayout.show()
                ZakatResource.Loading -> progressLayout.show()
                is ZakatResource.zakatList ->
                {
                    when(it.data.data?.status)
                    {
                        200->
                        {
                                    it.data.data.data?.let {

                                            it1 -> adapter = ZakatListAdapter(it1, this@ZakatListFragment)
                                    }

                                listZakat.adapter = adapter
                        }
                        204 ->
                        {
                            noDataLayout.show()
                            NoDataLayout(noDataLayout)
                        }

                        else ->noInternetLayout.show()
                    }
                }
                is ZakatResource.zakatDelete ->
                {
                    when(it.data.data?.status)
                    {
                        200 ->
                        {
                            lifecycleScope.launch {
                                viewModel.getZakatList()
                            }
                        }

                        else -> mDetailsCallBack?.showToastMessage("Something went wrong! try again")
                    }
                }
                else -> Unit
            }
        }

    }

    override fun onItemClick(postion: Int, zakat: List<ZakatModel>) {

        lifecycleScope.launch {
            zakat.get(postion).id?.let { viewModel.delZakat(it) }
        }
    }

}

