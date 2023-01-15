package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentHajjpreRegistrationBinding
import com.gakk.noorlibrary.ui.adapter.HajjPreRegistrationAdapter
import com.gakk.noorlibrary.extralib.StepBarView.stepbarView
import com.gakk.noorlibrary.util.setApplicationLanguage
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel

class HajjpreRegistrationDetailsFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var viewModel: PreregistrationViewModel
    private lateinit var binding: FragmentHajjpreRegistrationBinding

    private val step_bar_view : stepbarView = stepbarView()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_hajjpre_registration,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle("হজ্জ প্রাক-নিবন্ধন")

        var number = ""
        AppPreference.userNumber.let {
            number = it!!
        }

        viewModel = ViewModelProvider(requireActivity())[PreregistrationViewModel::class.java]

        val adapter = HajjPreRegistrationAdapter(
            requireActivity().supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            mCallback
        )

        binding.viewPager.adapter = adapter


        context?.let { step_bar_view.setup_stepbar(it,binding.pageStepper,3,40F,3F,9F , false,binding.viewPager,true) }

        //binding.pageStepper.setupWithViewPager(binding.viewPager)

        viewModel.pagerSelectedPos.observe(viewLifecycleOwner) {
            binding.viewPager.setCurrentItem(it, true)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HajjpreRegistrationDetailsFragment()
    }
}



