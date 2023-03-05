package com.gakk.noorlibrary.ui.fragments.subscription

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.util.*

class SubscriptionOptionListFragment : Fragment() {
    private var mCallback: DetailsCallBack? = null
    private lateinit var cardRobi: CardView
    private lateinit var cardMfs: CardView
    private lateinit var CardGpay:CardView

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
    }
}