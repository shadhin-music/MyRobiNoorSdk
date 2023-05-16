package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.AddUserTrackigViewModel
import kotlinx.coroutines.launch

class SubscriptionOptionListFragment : Fragment() {
    private var mCallback: DetailsCallBack? = null
    private lateinit var cardRobi: CardView
    private lateinit var cardMfs: CardView
    private lateinit var CardGpay:CardView
    private lateinit var repository: RestRepository
    private lateinit var modelUserTracking: AddUserTrackigViewModel

    companion object {

        fun newInstance() =
            SubscriptionOptionListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_subscription_option_list,
            container, false
        )
        cardRobi = view.findViewById(R.id.cardRobi)
        cardMfs = view.findViewById(R.id.cardMfs)
        CardGpay = view.findViewById(R.id.CardGpay)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback?.setToolBarTitle(getString(R.string.page_title_subscription))
        cardRobi.handleClickEvent {
            Intent(context, DetailsActivity::class.java).apply {
                this.putExtra(PAGE_NAME, PAGE_SUBSCRIPTION)
                startActivity(this)
            }
        }

        cardMfs.handleClickEvent {

            val fragment = FragmentProvider.getFragmentByName(
                PAGE_SUBSCRIPTION_SSL, detailsActivityCallBack = mCallback,
                isFav = false
            )
            mCallback?.addFragmentToStackAndShow(fragment!!)
        }

        CardGpay.handleClickEvent {

            val fragment = FragmentProvider.getFragmentByName(
                PAGE_SUBSCRIPTION_GPAY, detailsActivityCallBack = mCallback,
                isFav = false
            )
            mCallback?.addFragmentToStackAndShow(fragment!!)

        }


        lifecycleScope.launch {
            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()

            modelUserTracking = ViewModelProvider(
                this@SubscriptionOptionListFragment,
                AddUserTrackigViewModel.FACTORY(repository)
            ).get(AddUserTrackigViewModel::class.java)

            AppPreference.userNumber?.let { userNumber ->
                modelUserTracking.addTrackDataUser(userNumber, PAGE_PAYMENT_HOME)
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
}