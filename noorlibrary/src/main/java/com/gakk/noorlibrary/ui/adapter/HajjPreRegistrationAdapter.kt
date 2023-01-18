package com.gakk.noorlibrary.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjAddressFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPaymentFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPersonalInfoFragment

internal class HajjPreRegistrationAdapter(
    fm: FragmentManager,
    behaviour: Int,
    val callback: DetailsCallBack? = null
) : FragmentStatePagerAdapter(fm, behaviour) {


    override fun getItem(p0: Int): Fragment {

        return when (p0) {
            0 -> {
                HajjPersonalInfoFragment.newInstance()

            }
            1 -> {
                HajjAddressFragment.newInstance()
            }
            2 -> {
                HajjPaymentFragment()
            }

            else -> getItem(p0)
        }
    }


    override fun getCount(): Int {

        return 3
    }

}
