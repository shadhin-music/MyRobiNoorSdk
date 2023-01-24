package com.gakk.noorlibrary.ui.fragments.onlinehut

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.adapter.hut.OnlineHutHomeAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

internal class OnlintHutHomeFragment : Fragment() {

    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var mCallback: DetailsCallBack? = null

    //view
    private lateinit var progressLayout : ConstraintLayout
    private lateinit var rvHut:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OnlintHutHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_online_hut_home,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {
        progressLayout = view.findViewById(R.id.progressLayout)
        rvHut = view.findViewById(R.id.rvHut)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(getString(R.string.title_online_hut))

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@OnlintHutHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            model.loadTextBasedLiteratureListBySubCategory(
                getString(R.string.online_hut_category_id),
                getString(R.string.online_hut_subcategory_id),
                "1"
            )

            subscribeObserver()
        }
    }

    private fun subscribeObserver() {
        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    progressLayout.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    val literatureList = it.data?.data ?: mutableListOf()
                    val adapter = OnlineHutHomeAdapter(literatureList, mCallback!!)
                    rvHut.adapter = adapter
                    progressLayout.visibility = View.GONE
                }
                Status.ERROR -> {
                    progressLayout.visibility = View.GONE
                }
            }
        }
    }
}