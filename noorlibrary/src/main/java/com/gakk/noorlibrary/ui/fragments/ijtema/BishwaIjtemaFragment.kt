package com.gakk.noorlibrary.ui.fragments.ijtema

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.callbacks.LiteratureListCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.video.category.Data
import com.gakk.noorlibrary.ui.adapter.ijtema.IjtemaAdapter
import com.gakk.noorlibrary.ui.fragments.LiteratureItemClickCallBack
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import com.gakk.noorlibrary.viewModel.VideoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


internal class BishwaIjtemaFragment : Fragment(), LiteratureItemClickCallBack, LiteratureListCallBack,
    IjtemaControl {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var repository: RestRepository
    private lateinit var model: LiteratureViewModel
    private lateinit var videoModel: VideoViewModel
    private var literatureList: MutableList<Literature> = mutableListOf()
    private var mLiteratureListCallBack: LiteratureListCallBack? = null
    private var adapter: IjtemaAdapter? = null
    var videoList: MutableList<Data> = mutableListOf()
    private var literatureListWrapper: LiteratureListWrapper? = null

    //view
    private lateinit var progressLayout : ConstraintLayout
    private lateinit var rvLearning: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BishwaIjtemaFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_bishwa_ijtema,
            container, false
        )

        initView(view)


        return view
    }

    private fun initView(view:View)
    {
        progressLayout = view.findViewById(R.id.progressLayout)
        rvLearning = view.findViewById(R.id.rvLearning)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLiteratureListCallBack = this

        updateToolbarForThisFragment()

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()


            videoModel = ViewModelProvider(
                this@BishwaIjtemaFragment,
                VideoViewModel.FACTORY(repository)
            ).get(VideoViewModel::class.java)


            model = ViewModelProvider(
                this@BishwaIjtemaFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)


            videoModel.loadIslamicVideosByCatId(
                R.string.ijtema_id.getLocalisedTextFromResId(),
                "undefined",
                "1"
            )


            videoModel.videoList.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        videoList = it.data?.data!!
                        model.loadTextBasedLiteratureListBySubCategory(
                            R.string.ijtema_id.getLocalisedTextFromResId(),
                            "undefined",
                            "1"
                        )
                    }
                    Status.LOADING -> {
                        Log.e("videodata", "Loading")
                    }
                    Status.ERROR -> {
                        Log.e("videodata", "Error")
                    }
                }
            }

            model.literatureListData.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        progressLayout.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        literatureList = it.data?.data ?: mutableListOf()

                        val liveList = literatureList.filter {
                            it.title?.trim().equals("লাইভ বিশ্ব ইজতেমা")
                        }

                        BaseApplication.IJTEMA_LIVE_VIDEO_ID = liveList.get(0).refUrl

                        literatureList.removeAll {
                            it.title?.trim().equals("লাইভ বিশ্ব ইজতেমা")
                        }
                        adapter = IjtemaAdapter(
                            literatureList, mDetailsCallBack!!, this@BishwaIjtemaFragment,
                            videoList,
                            R.string.ijtema_id.getLocalisedTextFromResId(),
                            "undefined",
                            this@BishwaIjtemaFragment
                        )

                        rvLearning.adapter = adapter

                        progressLayout.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_ijtema))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeOne)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false, ActionButtonType.TypeTwo)
    }

    override fun goToListeratureDetailsFragment(selectedIndex: Int, isFav: Boolean) {

        literatureListWrapper = LiteratureListWrapper(literatureList)
        val fragment = FragmentProvider.getFragmentByName(
            name = PAGE_LITERATURE_DETAILS,
            detailsActivityCallBack = mDetailsCallBack,
            literatureListWrapper = literatureListWrapper,
            selectedLiteratureIndex = selectedIndex,
            currentPageNo = 1,
            isFav = false
        )

        if (mDetailsCallBack != null) {
            mDetailsCallBack?.addFragmentToStackAndShow(fragment!!)
        }
    }

    override fun getLiteratureList(): MutableList<Literature>? {
        return adapter?.getIjtemaList()
    }

    override fun showDialog() {
        showNoLiveDialog()
    }

    fun showNoLiveDialog() {
        val customDialog =
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MaterialAlertDialog_rounded
            )

        val view = layoutInflater.inflate(
            R.layout.dialog_no_live,
            null,
            false
        )


        val dialogView: View = view
        customDialog.setView(dialogView)

        val alertDialog = customDialog.show()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.setCancelable(false)
        alertDialog.show()

        view.findViewById<AppCompatButton>(R.id.btnComplete).handleClickEvent {
            alertDialog.dismiss()
        }

    }
}

interface IjtemaControl {
    fun showDialog()
}

