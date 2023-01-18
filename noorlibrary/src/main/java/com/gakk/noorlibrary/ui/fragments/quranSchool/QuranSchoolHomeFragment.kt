package com.gakk.noorlibrary.ui.fragments.quranSchool

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.FragmentQuranSchoolBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.model.quranSchool.QuranSchoolModel
import com.gakk.noorlibrary.model.quranSchool.Scholar
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.activity.QuranSchoolPlayerActivity
import com.gakk.noorlibrary.ui.adapter.QuranSchoolAdapter
import com.gakk.noorlibrary.ui.adapter.QuranSchoolChildAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.QuranSchoolViewModel
import com.gakk.noorlibrary.viewModel.VideoViewModel
import kotlinx.coroutines.launch


/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/1/2021, Thu
 */

private const val SCHOLAR_ITEM = "scholar_item"
private const val SCHOLAR_ID = "scholar_id"
private const val ARG_IS_CAT_QURAN_SCHOOL = "isCatQuranSchool"

internal class QuranSchoolHomeFragment : Fragment(), QuranSchoolChildAdapter.OnItemClickListener,
    QuranSchoolAdapter.OnItemClickListener {

    private lateinit var binding: FragmentQuranSchoolBinding
    private lateinit var mCallback: DetailsCallBack
    private var scholar: Scholar? = null
    private var scholarID: String? = null
    private var isCatQuranSchool: Boolean? = false
    private lateinit var viewModel: QuranSchoolViewModel
    private lateinit var model: LiteratureViewModel
    private lateinit var repository: RestRepository
    private lateinit var videoModel: VideoViewModel

    companion object {
        @JvmStatic
        fun newInstance(
            scholar: Scholar? = null,
            scholarId: String? = null,
            isCatQuranSchool: Boolean = false
        ) =
            QuranSchoolHomeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SCHOLAR_ITEM, scholar)
                    putString(SCHOLAR_ID, scholarId)
                    putBoolean(ARG_IS_CAT_QURAN_SCHOOL, isCatQuranSchool)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            scholar = it.getParcelable(SCHOLAR_ITEM)
            scholarID = it.getString(SCHOLAR_ID)
            isCatQuranSchool = it.getBoolean(ARG_IS_CAT_QURAN_SCHOOL)
        }

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_quran_school, container, false)


        return binding.root
    }

    private fun subscribeQuranObserver() {

        videoModel.videoList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
                    val classList = it.data?.data
                    val sortedList: MutableList<Data> =
                        classList?.sortedByDescending { classList.indexOf(it) }!!.toMutableList()
                    setupRVSchool(sortedList)
                }
                Status.LOADING -> {
                    Log.e("videodata", "Loading")
                }
                Status.ERROR -> {
                    mCallback.showToastMessage(it.message ?: "Error occured !")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.item = ImageFromOnline("ic_bg_quran_school.png")

        lifecycleScope.launch {

            if (isCatQuranSchool == true) {
                binding.headerLayoutQuran.visibility = View.VISIBLE
                binding.headerLayoutScholarVideo.visibility = View.GONE
                binding.title.setText(getString(R.string.quran_clasess))

            } else {
                binding.headerLayoutScholarVideo.visibility = View.VISIBLE
                binding.headerLayoutQuran.visibility = View.GONE
                binding.title.setText(getString(R.string.live_clasess))
            }

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@QuranSchoolHomeFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            videoModel = ViewModelProvider(
                this@QuranSchoolHomeFragment,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)

            initView()
            if (isCatQuranSchool == true) {
                subscribeQuranObserver()
            } else {
                subscribeObserver()
            }
        }

    }

    private fun subscribeObserver() {

        viewModel.singleScholarsLiveData.observe(viewLifecycleOwner
        ) {
            when (it.status) {
                Status.LOADING -> Unit

                Status.SUCCESS -> {
                    binding.progressLayout.root.visibility = View.GONE
                    val scholar = it.data?.scholar
                    scholar?.let { scholarItem ->
                        binding.data = scholar
                        scholarItem.id?.let { id -> viewModel.getQuranSchoolByScholars(id) }
                    }
                }

                Status.ERROR -> {
                    mCallback.showToastMessage(it.message ?: "Error occured !")
                }
            }
        }

        viewModel.quranSchoolLiveData.observe(viewLifecycleOwner
        ) {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressLayout.root.visibility = View.VISIBLE
                }

                Status.SUCCESS -> {

                    binding.progressLayout.root.visibility = View.GONE
                    val scholars = it.data?.items
                    setupRV(scholars)
                }

                Status.ERROR -> {
                    binding.progressLayout.root.visibility = View.GONE
                }
            }
        }
    }


    private fun setupRV(scholars: List<QuranSchoolModel>?) {
        scholars?.let {
            val adapter = QuranSchoolChildAdapter(this).apply {
                submitList(it)
            }
            binding.childRv.adapter = adapter
        }
    }

    private fun setupRVSchool(list: MutableList<Data>?) {

        val adapter = QuranSchoolAdapter(this).apply {
            submitList(list)
        }
        binding.childRv.adapter = adapter

    }

    private fun initView() {
        mCallback.setToolBarTitle(getString(R.string.cat_quran_school))

        model.loadTextBasedLiteratureListBySubCategory(
            R.string.quran_class_cat_id.getLocalisedTextFromResId(), "undefined",
            "1"
        )

        videoModel.loadIslamicVideosByCatId(R.string.quran_class_cat_id.getLocalisedTextFromResId(), "undefined", "1")


        scholar?.let {
            binding.data = it
            it.id?.let { it1 -> viewModel.getQuranSchoolByScholars(it1) }

        } ?: kotlin.run {
            scholarID?.let {
                viewModel.getScholarsById(it)
            }
        }
    }

    override fun onItemClick(postion: Int, currentList: List<QuranSchoolModel>) {
        startActivity(
            Intent(requireActivity(), QuranSchoolPlayerActivity::class.java)
                .putExtra(YOUTUBE_SELECTED_VIDEO_TAG, postion)
                .putParcelableArrayListExtra(YOUTUBE_VIDEO_TAG, ArrayList(currentList))
                .putExtra(IS_QURAN_SCHOOL, false)
        )
    }

    override fun onItemClickVideo(postion: Int, currentList: List<Data>) {
        startActivity(
            Intent(requireActivity(), QuranSchoolPlayerActivity::class.java)
                .putExtra(YOUTUBE_SELECTED_VIDEO_TAG, postion)
                .putExtra(YOUTUBE_VIDEO_TAG_SCHOOL, ArrayList(currentList))
                .putExtra(IS_QURAN_SCHOOL, true)
        )
    }
}