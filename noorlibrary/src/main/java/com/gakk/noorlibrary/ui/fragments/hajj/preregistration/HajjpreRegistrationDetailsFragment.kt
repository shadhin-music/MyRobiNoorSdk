package com.gakk.noorlibrary.ui.fragments.hajj.preregistration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.extralib.StepBarView.stepbarView
import com.gakk.noorlibrary.ui.adapter.HajjPreRegistrationAdapter
import com.gakk.noorlibrary.util.PAGE_DONATION_DETAILS
import com.gakk.noorlibrary.util.PAGE_HAJJ_PRE_REGISTRATION
import com.gakk.noorlibrary.util.RepositoryProvider
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import com.gakk.noorlibrary.viewModel.PreregistrationViewModel
import kotlinx.coroutines.launch

internal class HajjpreRegistrationDetailsFragment : Fragment() {

    private var mCallback: DetailsCallBack? = null
    private lateinit var viewModel: PreregistrationViewModel

    private val step_bar_view: stepbarView = stepbarView()
    private lateinit var viewPager: ViewPager
    private lateinit var pageStepper: RecyclerView
    private lateinit var repository: RestRepository
    private lateinit var modelUserTracking: AddUserTrackigViewModel

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

        mCallback?.setToolBarTitle("হজ প্রাক-নিবন্ধন")


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

        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            modelUserTracking = ViewModelProvider(
                this@HajjpreRegistrationDetailsFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, PAGE_HAJJ_PRE_REGISTRATION)
            }

            modelUserTracking.trackUser.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.e("trackUser", "LOADING")
                    }
                    Status.ERROR -> {
                        Log.e("trackUser", "ERROR")
                    }

                    Status.SUCCESS -> {
                        Log.e("trackUser", "SUCCESS")
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HajjpreRegistrationDetailsFragment()
    }
}



