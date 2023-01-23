package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.extralib.StepBarView.stepbarView
import com.gakk.noorlibrary.ui.adapter.HajjPreRegistrationAdapter
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel

internal class HajjpreRegistrationDetailsFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var viewModel: PreregistrationViewModel

    private val step_bar_view: stepbarView = stepbarView()
    private lateinit var viewPager: ViewPager
    private lateinit var pageStepper: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_hajjpre_registration,
            container, false
        )
        viewPager = view.findViewById(R.id.viewPager)
        pageStepper = view.findViewById(R.id.page_stepper)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle("হজ্জ প্রাক-নিবন্ধন")


        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        val adapter = HajjPreRegistrationAdapter(
            requireActivity().supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            mCallback
        )

        viewPager.adapter = adapter


        context?.let {
            step_bar_view.setup_stepbar(
                it,
                pageStepper,
                3,
                40F,
                3F,
                9F,
                false,
                viewPager,
                true
            )
        }

        viewModel.pagerSelectedPos.observe(viewLifecycleOwner) {
            viewPager.setCurrentItem(it, true)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HajjpreRegistrationDetailsFragment()
    }
}



