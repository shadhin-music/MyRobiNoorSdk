package com.gakk.noorlibrary.ui.fragments.instructiveVideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.activity.QuranSchoolPlayerActivity
import com.gakk.noorlibrary.ui.adapter.InstructiveVideoAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.VideoViewModel
import kotlinx.coroutines.launch

private const val ARG_CATEGORY_TYPE = "categoryType"


internal class InstructiveVideoFragment : Fragment(), InstructiveVideoAdapter.OnItemClickListener {

    private lateinit var mCallback: DetailsCallBack
    private lateinit var repository: RestRepository
    private lateinit var videoModel: VideoViewModel
    private var mCatType: String? = null

    //view
    private lateinit var progressLayout : ConstraintLayout
    private lateinit var rvInstructiveVideo: RecyclerView


    companion object {
        @JvmStatic
        fun newInstance(
            categoryType: String
        ) =
            InstructiveVideoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_TYPE, categoryType)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCatType = it.getString(ARG_CATEGORY_TYPE)
        }
        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_instructive_video,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {
        progressLayout = view.findViewById(R.id.progressLayout)
        rvInstructiveVideo = view.findViewById(R.id.rvInstructiveVideo)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("cattype", "aa$mCatType")

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            videoModel = ViewModelProvider(
                this@InstructiveVideoFragment,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)

            var number = ""
            AppPreference.userNumber.let {
                number = it!!
            }

            val catId: String

            if (mCatType.equals("Instructive Video")) {
                mCallback.setToolBarTitle(getString(R.string.cat_instructive_video))
                catId = R.string.instructive_video_cat_id.getLocalisedTextFromResId()
            } else {
                mCallback.setToolBarTitle(getString(R.string.cat_live_qa))
                catId = R.string.live_qa_cat_id.getLocalisedTextFromResId()
            }


            videoModel.loadIslamicVideosByCatId(
                catId,
                "undefined",
                "1"
            )

            subscribeObserver()
        }

    }

    private fun subscribeObserver() {
        videoModel.videoList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressLayout.visibility = View.GONE
                    val classList = it.data?.data
                    setupRV(classList)

                    Log.e("videodata", "SUCCESS")
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

    private fun setupRV(list: MutableList<Data>?) {

        val adapter = InstructiveVideoAdapter(this, mCallback).apply {
            submitList(list)
        }
        rvInstructiveVideo.adapter = adapter

    }

    override fun onItemClickVideo(postion: Int, currentList: List<Data>) {
        startActivity(
            Intent(requireActivity(), QuranSchoolPlayerActivity::class.java)
                .putExtra(YOUTUBE_SELECTED_VIDEO_TAG, postion)
                .putExtra(YOUTUBE_VIDEO_TAG_SCHOOL, ArrayList(currentList))
                .putExtra(IS_INSTRUCTIVE_VIDEO, true)
                .putExtra("CatType", mCatType)
        )
    }

}