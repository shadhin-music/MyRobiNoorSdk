package com.gakk.noorlibrary.ui.fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.Noor
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.StepBarView.stepbarView
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch

@Transient
private const val ARG_CAT_NAME = "catName"

internal class NamazVisualFragment : Fragment(), PreviousNextPanelControlCallBack {

    @Transient
    private var mDetailsCallBack: DetailsCallBack? = null

    @Transient
    private var mCatName: String? = null

    @Transient
    private lateinit var model: LiteratureViewModel

    @Transient
    private lateinit var repository: RestRepository

    @Transient
    var index = 0

    @Transient
    lateinit var sortedList: MutableList<Literature>

    @Transient
    lateinit var mCatId: String

    @Transient
    lateinit var stepBarView: RecyclerView

    @Transient
    lateinit var progressLayout: ConstraintLayout

    @Transient
    lateinit var tvTitle: AppCompatTextView

    @Transient
    lateinit var tvDesVisual: AppCompatTextView

    @Transient
    lateinit var imgVisual: AppCompatImageView

    @Transient
    lateinit var prevNextPanel: ConstraintLayout

    @Transient
    lateinit var btnPrevContent: ImageButton

    @Transient
    lateinit var btnNextContent: ImageButton

    @Transient
    lateinit var tvPrevContent: AppCompatTextView

    @Transient
    lateinit var tvNextContent: AppCompatTextView

    @Transient
    lateinit var layoutPrevActionContent: ConstraintLayout

    @Transient
    lateinit var nextActionContent: ConstraintLayout

    @Transient
    private val step_bar_view: stepbarView = stepbarView()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCatName = it.getString(ARG_CAT_NAME)
        }

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_namaz_visual,
            container, false
        )

        stepBarView = view.findViewById(R.id.stepBarView)
        progressLayout = view.findViewById(R.id.progressLayout)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDesVisual = view.findViewById(R.id.tvDesVisual)
        imgVisual = view.findViewById(R.id.imgVisual)
        prevNextPanel = view.findViewById(R.id.prevNextPanel)
        btnPrevContent = prevNextPanel.findViewById(R.id.btnPrevContent)
        tvPrevContent = prevNextPanel.findViewById(R.id.tvPrevContent)
        btnNextContent = prevNextPanel.findViewById(R.id.btnNextContent)
        tvNextContent = prevNextPanel.findViewById(R.id.tvNextContent)
        layoutPrevActionContent = prevNextPanel.findViewById(R.id.layoutPrevActionContent)
        nextActionContent = prevNextPanel.findViewById(R.id.nextActionContent)

        when (mCatName) {
            CAT_MEN -> {
                mCatId = R.string.namaz_visual_men_id.getLocalisedTextFromResId()
                context?.let {
                    step_bar_view.setup_stepbar(
                        it,
                        stepBarView,
                        11,
                        36F,
                        3F,
                        7F,
                        true,
                        null,
                        false
                    )
                }

            }

            CAT_WOMEN -> {
                mCatId = R.string.namaz_visual_women_id.getLocalisedTextFromResId()
                context?.let {
                    step_bar_view.setup_stepbar(
                        it,
                        stepBarView,
                        10,
                        36F,
                        3F,
                        7F,
                        true,
                        null,
                        false
                    )
                }

            }
        }

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@NamazVisualFragment,
                LiteratureViewModel.FACTORY(repository)
            ).get(LiteratureViewModel::class.java)

            loadData()

            model.literatureListData.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        progressLayout.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        progressLayout.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        progressLayout.visibility = View.GONE
                        val list = it.data?.data ?: mutableListOf()
                        sortedList =
                            list.sortedByDescending { list.indexOf(it) }.toMutableList()
                        setData(sortedList, 0)
                        stepBarView.visibility = View.VISIBLE
                        setUpPrevNextControlState()
                    }
                }
            })

            step_bar_view.getActiveStep(object : stepbarView.stepListner {
                override fun setOnActiveStep(step: Int) {

                    index = step
                    setData(sortedList, index)
                    setUpPrevNextControlState()

                }
            })
        }

        updateToolbarForThisFragment()
        return view
    }

    fun setData(list: MutableList<Literature>, index: Int) {

        tvTitle.setText(TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(index + 1)!!) + ". " + list[index].title)
        tvDesVisual.setText(list[index].text)
        Noor.appContext?.let {
            Glide.with(it)
                .load(list[index].fullImageUrl?.replace("<size>", "1280"))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        step_bar_view.setActiveStep(index)
                        return false
                    }

                })
                .error(R.drawable.place_holder_4_3_ratio)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imgVisual)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(catName: String?) =
            NamazVisualFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CAT_NAME, catName)
                }
            }

    }

    fun loadData() {
        model.loadTextBasedLiteratureListBySubCategory(
            mCatId,
            "undefined",
            "1"
        )
    }

    fun updateToolbarForThisFragment() {
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_namaz_sikhha))
    }

    override fun setUpPrevNextControlState() {
        setPrevControlState()
        setNextControlState()
        setPrevControlClickEvent()
        setNextControlClickEvent()
    }

    override fun setPrevControlState() {
        index.let {

            when (it > 0) {
                true -> {
                    Log.e("index", "greater" + index)
                    btnPrevContent.isEnabled = true
                    tvPrevContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.colorPrimary
                        )
                    )
                }
                false -> {
                    Log.e("index", "smaller" + index)
                    btnPrevContent.isEnabled = false
                    tvPrevContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.disabled_color
                        )
                    )
                }
            }
        }
    }

    override fun setNextControlState() {
        index.let {

            when (it >= sortedList.size - 1) {
                true -> {
                    btnNextContent.isEnabled = false
                    tvNextContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.disabled_color
                        )
                    )
                }
                false -> {
                    btnNextContent.isEnabled = true
                    tvNextContent.setTextColor(
                        requireContext().resources.getColor(
                            R.color.colorPrimary
                        )
                    )
                }
            }
        }
    }

    override fun setPrevControlClickEvent() {

        layoutPrevActionContent.handleClickEvent {
            if (index > 0) {
                btnPrevContent.isEnabled = true
                tvPrevContent.setTextColor(
                    requireContext().resources.getColor(
                        R.color.colorPrimary
                    )
                )
                index--
                if (index >= 0) {
                    setData(sortedList, index)
                    setUpPrevNextControlState()
                } else index++

            }
        }
    }

    fun Context.dipToPixels(dipValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics)

    override fun setNextControlClickEvent() {

        stepBarView.layoutManager?.scrollToPosition(index)


        /*binding.stepBarView.scrollTo(
            requireContext().dipToPixels(82f * index).toInt(),
            0
        )*/

        nextActionContent.handleClickEvent {
            if (index < sortedList.size - 1) {
                btnNextContent.isEnabled = true
                tvNextContent.setTextColor(
                    requireContext().resources.getColor(
                        R.color.colorPrimary
                    )
                )
                index++
                setData(sortedList, index)
                setUpPrevNextControlState()

            }
        }

        if (index >= 0)
            step_bar_view.setActiveStep(index)

    }
}

private interface PreviousNextPanelControlCallBack {
    fun setUpPrevNextControlState()
    fun setPrevControlState()
    fun setNextControlState()
    fun setPrevControlClickEvent()
    fun setNextControlClickEvent()

}