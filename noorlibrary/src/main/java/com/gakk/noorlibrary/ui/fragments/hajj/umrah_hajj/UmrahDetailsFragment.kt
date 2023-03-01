package com.gakk.noorlibrary.ui.fragments.hajj.umrah_hajj

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.ActionButtonType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.umrah_hajj.UmrahHajjData
import com.gakk.noorlibrary.util.PLACE_HOLDER_16_9
import com.gakk.noorlibrary.util.getNumberInBangla
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.util.setImageFromUrl

private const val ARG_PACKAGE_DATA = "umrahHajjData"

class UmrahDetailsFragment : Fragment()  {

    private var mDetailsCallBack: DetailsCallBack? = null
    private lateinit var packageData: UmrahHajjData

    //view

    private lateinit var contactInfo: TextView
    private lateinit var btnNext: AppCompatButton
    private lateinit var pack_img: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var titile: TextView
    private lateinit var packPrice:TextView
    private lateinit var packDes:TextView
    private lateinit var description:TextView
    private lateinit var includeFeature:TextView
    private lateinit var total_cost:TextView

    companion object {

        @JvmStatic
        fun newInstance(packageData : UmrahHajjData) =
            UmrahDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PACKAGE_DATA,packageData)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetailsCallBack = requireActivity() as DetailsCallBack

        arguments.let {
            if (it != null) {
                packageData = if (Build.VERSION.SDK_INT >= 33)
                    it.getParcelable(ARG_PACKAGE_DATA,UmrahHajjData::class.java)!!
                else
                    it.getParcelable(ARG_PACKAGE_DATA)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_umrah_details,
            container, false
        )

        initView(view)

        return view
    }

    private fun initView(view:View)
    {
        btnNext = view.findViewById(R.id.btnNext)
        pack_img = view.findViewById(R.id.pack_img)
        progressBar = view.findViewById(R.id.progressBar)
        titile = view.findViewById(R.id.titile)
        packPrice = view.findViewById(R.id.packPrice)
        packDes = view.findViewById(R.id.packDes)
        description = view.findViewById(R.id.description)
        includeFeature = view.findViewById(R.id.includeFeature)
        total_cost = view.findViewById(R.id.total_cost)
        contactInfo = view.findViewById(R.id.contactInfo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDetailsCallBack?.setToolBarTitle(resources.getString(R.string.cat_umrah_details))
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)

        setImageFromUrl(pack_img,packageData.image,progressBar,PLACE_HOLDER_16_9)
        titile.text = packageData.packageName
        packPrice.text = packageData.packagePrice
        packDes.text = packageData.packageDescription?.subHeading
        description.text = packageData.packageDescription?.description
        includeFeature.text = packageData.packageDescription?.includedFeatures
        contactInfo.text = packageData.packageDescription?.contactDetails

        total_cost.text = context?.getString(R.string.hajj_total_booking_format)?.format(packageData.bookingMoney?.getNumberInBangla())


        contactInfo.handleClickEvent {

            val phoneNumber = contactInfo.text.filter { it.isDigit() }

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }


        btnNext.handleClickEvent {

            mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
            mDetailsCallBack?.addFragmentToStackAndShow(UmrahPersonalInfoFragment.newInstance(packageData.umrahPackageId.toString(),packageData.bookingMoney.toString()))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(false)
        mDetailsCallBack?.toggleToolBarActionIconsVisibility(true, ActionButtonType.TypeThree,R.drawable.ic_payment_history)
   }



}

