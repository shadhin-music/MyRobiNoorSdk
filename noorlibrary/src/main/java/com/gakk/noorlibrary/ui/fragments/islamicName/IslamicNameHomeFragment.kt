package com.gakk.noorlibrary.ui.fragments.islamicName

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentIslamicNameHomeBinding
import com.gakk.noorlibrary.model.ImageFromOnline
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setApplicationLanguage

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/15/2021, Thu
 */

class IslamicNameHomeFragment : Fragment() {

    private lateinit var binding: FragmentIslamicNameHomeBinding
    private var mCallback: DetailsCallBack? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_islamic_name_home, container, false)


        initViews()

        binding.item = ImageFromOnline("ic_islamic_name_bg.png")
        binding.itemGirl = ImageFromOnline("ic_girl_avatar.webp")
        binding.itemBoy = ImageFromOnline("ic_boy_avatar.png")

        return binding.root
    }

    private fun initViews() {
        binding.boyNamesLayout.handleClickEvent {
            mCallback?.addFragmentToStackAndShow(IslamicNameTabFragment.newInstance(false))
        }

        binding.girlNamesLayout.handleClickEvent {
            mCallback?.addFragmentToStackAndShow(IslamicNameTabFragment.newInstance(true))
        }

        mCallback?.setToolBarTitle(resources.getString(R.string.islamic_name))
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            IslamicNameHomeFragment()
    }

}