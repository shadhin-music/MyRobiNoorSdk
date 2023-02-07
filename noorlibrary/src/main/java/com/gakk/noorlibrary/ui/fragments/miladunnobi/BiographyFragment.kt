package com.gakk.noorlibrary.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.StepBarView.stepbarView
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.adapter.SliderAdapter
import com.gakk.noorlibrary.ui.fragments.miladunnobi.BiographyImageFragment
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.util.getLocalisedTextFromResId
import com.gakk.noorlibrary.viewModel.LiteratureViewModel
import kotlinx.coroutines.launch
import java.io.Serializable
import kotlin.properties.Delegates

internal class BiographyFragment : Fragment(), SliderControl {

    @Transient
    private var literatureList: MutableList<Literature> = mutableListOf()

    @Transient
    private var mDetailsCallBack: DetailsCallBack? = null

    @Transient
    private lateinit var model: LiteratureViewModel

    @Transient
    private lateinit var repository: RestRepository

    private val step_bar_view : stepbarView = stepbarView()

    // view

    private lateinit var progressLayout : ConstraintLayout
    private lateinit var page_stepper: RecyclerView
    private lateinit var vpBiography: ViewPager2
    private lateinit var tvTitle : AppCompatTextView
    private lateinit var tvDesVisual: AppCompatTextView




    var currentIndex: Int by Delegates.observable(0) { property, old, new ->
        if (new != old) {

            step_bar_view.setActiveStep(new)
            //binding.stepBarView.reachedStep = new + 1
            vpBiography.currentItem = new
        }
    }

    private val fragmentList = ArrayList<Fragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsCallBack = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_biography,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {
        progressLayout = view.findViewById(R.id.progressLayout)
        page_stepper = view.findViewById(R.id.page_stepper)
        vpBiography = view.findViewById(R.id.vpBiography)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvDesVisual = view.findViewById(R.id.tvDesVisual)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            model = ViewModelProvider(
                this@BiographyFragment,
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
                        literatureList = it.data?.data ?: mutableListOf()

                        context?.let { step_bar_view.setup_stepbar(it,page_stepper,literatureList.size,36F,3F,7F , true,null,false) }

                        step_bar_view.getActiveStep(object: stepbarView.stepListner
                        {
                            override fun setOnActiveStep(step: Int) {

                                currentIndex = step

                            }

                        })

                        //binding.stepBarView.maxCount = literatureList.size
                        //binding.stepBarView.requestLayout()
                        val adapter =
                            SliderAdapter(requireActivity())
                        vpBiography.adapter = adapter


                        for (i in 0..literatureList.size - 1) {

                            fragmentList.add(
                                BiographyImageFragment.newInstance(
                                    getitem(i)
                                )
                            )
                        }

                        adapter.setFragmentList(fragmentList)

                    }
                }
            })

            vpBiography.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    setData(literatureList, position)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentIndex = position
                    page_stepper.layoutManager?.scrollToPosition(position)

                    /*binding.distanceSeekBar.scrollTo(
                        context!!.dipToPixels(85F * position).toInt(),
                        0
                    )*/
                }
            })

            /*binding.stepBarView.onStepChangeListener = object : StepBarView.OnStepChangeListener {
                override fun onStepChanged(currentStep: Int) {
                    currentIndex = currentStep - 1
                }
            }*/

        }
        updateToolbarForThisFragment()
    }


    fun Context.dipToPixels(dipValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics)

    fun setData(list: MutableList<Literature>, index: Int) {
        tvTitle.setText(list[index].title)
        tvDesVisual.setText(list[index].text)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BiographyFragment()
    }

    fun loadData() {
        model.loadTextBasedLiteratureListBySubCategory(
            R.string.biography_cateogry_id.getLocalisedTextFromResId(),
            "undefined",
            "1"
        )
    }

    fun updateToolbarForThisFragment() {

        val txtMiladunnobi: String

    }

    override fun getitem(position: Int) = literatureList.get(position)

}

interface SliderControl : Serializable {
    fun getitem(position: Int): Literature
}
