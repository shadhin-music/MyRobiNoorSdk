package com.gakk.noorlibrary.ui.fragments.allahNames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentNinetyNineNamesOfAllahBinding
import com.gakk.noorlibrary.model.names.Data
import com.gakk.noorlibrary.ui.adapter.AllahNamesAdapter
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.NinetyNineNamesOfAllahViewModel
import kotlinx.coroutines.launch


class NinetyNineNamesOfAllahFragment : Fragment() {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var binding: FragmentNinetyNineNamesOfAllahBinding
    private lateinit var model: NinetyNineNamesOfAllahViewModel
    private lateinit var repository: RestRepository
    private lateinit var mList: List<Data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_ninety_nine_names_of_allah,
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
                this@NinetyNineNamesOfAllahFragment,
                NinetyNineNamesOfAllahViewModel.FACTORY(repository)
            ).get(NinetyNineNamesOfAllahViewModel::class.java)
            model.loadNamesOfAllah()

            model.nineNamesOfAllahData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.progressLayout.root.visibility = GONE
                        mList = it.data?.data!!
                        binding.listAllahNames.adapter = AllahNamesAdapter(
                            mList,
                            mDetailsCallBack!!
                        )

                    }
                    Status.LOADING -> {
                        binding.noInternetLayout.root.visibility = GONE
                        binding.progressLayout.root.visibility = VISIBLE
                    }
                    Status.ERROR -> {
                        binding.noInternetLayout.root.visibility = VISIBLE
                        binding.progressLayout.root.visibility = GONE
                    }
                }
            })
            binding.noInternetLayout.btnRetry.handleClickEvent {
                model.loadNamesOfAllah()
            }
        }

        updateToolbarForThisFragment()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NinetyNineNamesOfAllahFragment()
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.title_ninety_nine_names_allah))
    }
}