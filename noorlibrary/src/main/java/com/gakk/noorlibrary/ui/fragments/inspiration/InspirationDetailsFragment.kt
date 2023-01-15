package com.gakk.noorlibrary.ui.fragments.inspiration

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
import com.gakk.noorlibrary.databinding.FragmentInspirationDetailsBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.InspirationAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch


private const val ARG_CAT_ID = "catId"

class InspirationDetailsFragment : Fragment() {

    private lateinit var binding: FragmentInspirationDetailsBinding
    private lateinit var mCallback: DetailsCallBack
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private var mCatId: String? = null


    companion object {
        @JvmStatic
        fun newInstance(
            catId: String,
        ) =
            InspirationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CAT_ID, catId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCatId = it.getString(ARG_CAT_ID)
        }
        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_inspiration_details,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@InspirationDetailsFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            mCallback.setToolBarTitle(getString(R.string.txt_title_inspiration))
            mCatId?.let {
                model.loadTextBasedLiteratureListBySubCategory(
                    it, "undefined", "1"
                )
            }
            subscribeObserver()
        }
    }

    private fun subscribeObserver() {

        model.literatureListData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> Unit

                Status.SUCCESS -> {
                    // binding.progressLayout.root.visibility = View.GONE
                    val item = it.data?.data?.get(0)
                    binding.literature = item
                    model.loadImageLiteratureListBySubCategory(
                        mCatId!!,
                        "undefined",
                        "1"
                    )
                }
                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                    mCallback.showToastMessage(it.message ?: "Error occured !")
                }


            }
        }


        model.literatureListImageData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> Unit

                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE

                    val imageList = it.data?.data
                    setupRV(imageList)
                }
                Status.ERROR -> {
                    mCallback.showToastMessage(it.message ?: "Error occured !")
                }

            }
        }
    }

    private fun setupRV(list: MutableList<Literature>?) {
        val adapter = list?.let { InspirationAdapter(it) }
        binding.rvMoreInspiration.adapter = adapter
    }
}