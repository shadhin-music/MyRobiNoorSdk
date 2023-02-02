package com.gakk.noorlibrary.ui.fragments.zakat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.zakat.ZakatModel
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.ZakatViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*


/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

internal class ZakatCalculatorFragment : Fragment() {

    private lateinit var mCallback: DetailsCallBack
    private lateinit var viewModel: ZakatViewModel
    private lateinit var repository: RestRepository
    private lateinit var layoutNagadTakaHeader: ConstraintLayout
    private lateinit var tvTitleHeaderNagadTaka: AppCompatTextView
    private lateinit var ivInfoHeaderNagadTaka: AppCompatImageView
    private lateinit var layoutNagadTakacontent: ConstraintLayout
    private lateinit var contentTitleNagadTaka: AppCompatTextView
    private lateinit var layoutBankNagadTakacontent: ConstraintLayout
    private lateinit var contentTitleBankNagadTaka: AppCompatTextView
    private lateinit var layoutOrnamentAmtHeader: ConstraintLayout
    private lateinit var ivInfoHeaderOrnamentAmt: AppCompatImageView
    private lateinit var tvTitleHeaderOrnamentAmt: AppCompatTextView
    private lateinit var layoutGoldAmtcontent: ConstraintLayout
    private lateinit var contentTitleGoldAmt: AppCompatTextView
    private lateinit var layoutSilverAmtcontent: ConstraintLayout
    private lateinit var contentTitleSilverAmt: AppCompatTextView
    private lateinit var layoutInvestmentAmtHeader: ConstraintLayout
    private lateinit var ivInfoHeaderInvestmentAmt: AppCompatImageView
    private lateinit var tvTitleHeaderInvestmentAmt: AppCompatTextView
    private lateinit var layoutShareMarketcontent: ConstraintLayout
    private lateinit var contentTitleShareMarket: AppCompatTextView
    private lateinit var layoutOtherInvestcontent: ConstraintLayout
    private lateinit var contentTitleOtherInvest: AppCompatTextView
    private lateinit var layoutAssetHeader: ConstraintLayout
    private lateinit var ivInfoHeaderAsset: AppCompatImageView
    private lateinit var tvTitleHeaderAsset: AppCompatTextView
    private lateinit var layoutHouseRentcontent: ConstraintLayout
    private lateinit var contentTitleHouseRent: AppCompatTextView
    private lateinit var layoutAssetcontent: ConstraintLayout
    private lateinit var contentTitleAsset: AppCompatTextView
    private lateinit var layoutBusinessHeader: ConstraintLayout
    private lateinit var ivInfoHeaderBusiness: AppCompatImageView
    private lateinit var tvTitleHeaderBusiness: AppCompatTextView
    private lateinit var layoutNogodBusinescontent: ConstraintLayout
    private lateinit var contentTitleNogodBusines: AppCompatTextView
    private lateinit var layoutProductcontent: ConstraintLayout
    private lateinit var contentTitleProduct: AppCompatTextView
    private lateinit var layoutOtherHeader: ConstraintLayout
    private lateinit var ivInfoHeaderOther: AppCompatImageView
    private lateinit var tvTitleHeaderOther: AppCompatTextView
    private lateinit var layoutPensioncontent: ConstraintLayout
    private lateinit var contentTitlePension: AppCompatTextView
    private lateinit var layoutLoancontent: ConstraintLayout
    private lateinit var contentTitleLoan: AppCompatTextView
    private lateinit var layoutCapitalcontent: ConstraintLayout
    private lateinit var contentTitleCapital: AppCompatTextView
    private lateinit var layoutFarmingHeader: ConstraintLayout
    private lateinit var ivInfoHeaderFarming: AppCompatImageView
    private lateinit var tvTitleHeaderFarming: AppCompatTextView
    private lateinit var layoutFarmingcontent: ConstraintLayout
    private lateinit var contentTitleFarming: AppCompatTextView
    private lateinit var layoutLiabilityHeader: ConstraintLayout
    private lateinit var ivInfoHeaderLiability: AppCompatImageView

    private lateinit var tvTitleHeaderLiability: AppCompatTextView
    private lateinit var layoutCreditCardcontent: ConstraintLayout
    private lateinit var contentTitleCreditCard: AppCompatTextView
    private lateinit var layoutCarcontent: ConstraintLayout
    private lateinit var contentTitleCar: AppCompatTextView
    private lateinit var layoutBusinessPaymentcontent: ConstraintLayout
    private lateinit var contentTitleBusinessPayment: AppCompatTextView
    private lateinit var layoutFamilyLoancontent: ConstraintLayout
    private lateinit var contentTitleFamilyLoan: AppCompatTextView
    private lateinit var layoutOtherLoancontent: ConstraintLayout
    private lateinit var contentTitleOtherLoan: AppCompatTextView
    private lateinit var btnSave: AppCompatButton
    private lateinit var textTotalAsset: AppCompatTextView
    private lateinit var textTotalJakat: AppCompatTextView
    private lateinit var progressLayout: ConstraintLayout
    private lateinit var rootView : ConstraintLayout

    companion object {
        @JvmStatic
        fun newInstance() = ZakatCalculatorFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_jakat_calculator,
            container, false
        )

        initUi(view)

        lifecycleScope.launch {
            repository =  RepositoryProvider.getRepository()

            viewModel = ViewModelProvider(requireActivity(),ZakatViewModel.FACTORY(repository))[ZakatViewModel::class.java]

            initObserver()

        }

        return view
    }

    private fun initUi(view: View) {

        rootView = view.findViewById(R.id.rootview)
        progressLayout = view.findViewById(R.id.progressLayout)
        layoutNagadTakaHeader = view.findViewById(R.id.layoutNagadTakaHeader)
        ivInfoHeaderNagadTaka = layoutNagadTakaHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderNagadTaka = layoutNagadTakaHeader.findViewById(R.id.tvTitleHeader)
        layoutNagadTakacontent = view.findViewById(R.id.layoutNagadTakacontent)
        contentTitleNagadTaka = layoutNagadTakacontent.findViewById(R.id.contentTitle)
        layoutBankNagadTakacontent = view.findViewById(R.id.layoutBankNagadTakacontent)
        contentTitleBankNagadTaka = layoutBankNagadTakacontent.findViewById(R.id.contentTitle)
        layoutOrnamentAmtHeader = view.findViewById(R.id.layoutOrnamentAmtHeader)
        ivInfoHeaderOrnamentAmt = layoutOrnamentAmtHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderOrnamentAmt = layoutOrnamentAmtHeader.findViewById(R.id.tvTitleHeader)
        layoutGoldAmtcontent = view.findViewById(R.id.layoutGoldAmtcontent)
        contentTitleGoldAmt = layoutGoldAmtcontent.findViewById(R.id.contentTitle)
        layoutSilverAmtcontent = view.findViewById(R.id.layoutSilverAmtcontent)
        contentTitleSilverAmt = layoutSilverAmtcontent.findViewById(R.id.contentTitle)
        layoutInvestmentAmtHeader = view.findViewById(R.id.layoutInvestmentAmtHeader)
        ivInfoHeaderInvestmentAmt = layoutInvestmentAmtHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderInvestmentAmt = layoutInvestmentAmtHeader.findViewById(R.id.tvTitleHeader)
        layoutShareMarketcontent = view.findViewById(R.id.layoutShareMarketcontent)
        contentTitleShareMarket = layoutShareMarketcontent.findViewById(R.id.contentTitle)
        layoutOtherInvestcontent = view.findViewById(R.id.layoutOtherInvestcontent)
        contentTitleOtherInvest = layoutOtherInvestcontent.findViewById(R.id.contentTitle)
        layoutAssetHeader = view.findViewById(R.id.layoutAssetHeader)
        ivInfoHeaderAsset = layoutAssetHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderAsset = layoutAssetHeader.findViewById(R.id.tvTitleHeader)
        layoutHouseRentcontent = view.findViewById(R.id.layoutHouseRentcontent)
        contentTitleHouseRent = layoutHouseRentcontent.findViewById(R.id.contentTitle)
        layoutAssetcontent = view.findViewById(R.id.layoutAssetcontent)
        contentTitleAsset = layoutAssetcontent.findViewById(R.id.contentTitle)
        layoutBusinessHeader = view.findViewById(R.id.layoutBusinessHeader)
        ivInfoHeaderBusiness = layoutBusinessHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderBusiness = layoutBusinessHeader.findViewById(R.id.tvTitleHeader)
        layoutNogodBusinescontent = view.findViewById(R.id.layoutNogodBusinescontent)
        contentTitleNogodBusines = layoutNogodBusinescontent.findViewById(R.id.contentTitle)
        layoutProductcontent = view.findViewById(R.id.layoutProductcontent)
        contentTitleProduct = layoutProductcontent.findViewById(R.id.contentTitle)
        layoutOtherHeader = view.findViewById(R.id.layoutOtherHeader)
        ivInfoHeaderOther = layoutOtherHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderOther = layoutOtherHeader.findViewById(R.id.tvTitleHeader)
        layoutPensioncontent = view.findViewById(R.id.layoutPensioncontent)
        contentTitlePension = layoutPensioncontent.findViewById(R.id.contentTitle)
        layoutLoancontent = view.findViewById(R.id.layoutLoancontent)
        contentTitleLoan = layoutLoancontent.findViewById(R.id.contentTitle)
        layoutCapitalcontent = view.findViewById(R.id.layoutCapitalcontent)
        contentTitleCapital = layoutCapitalcontent.findViewById(R.id.contentTitle)
        layoutFarmingHeader = view.findViewById(R.id.layoutFarmingHeader)
        ivInfoHeaderFarming = layoutFarmingHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderFarming = layoutFarmingHeader.findViewById(R.id.tvTitleHeader)
        layoutFarmingcontent = view.findViewById(R.id.layoutFarmingcontent)
        contentTitleFarming = layoutFarmingcontent.findViewById(R.id.contentTitle)
        layoutLiabilityHeader = view.findViewById(R.id.layoutLiabilityHeader)
        ivInfoHeaderLiability = layoutLiabilityHeader.findViewById(R.id.ivInfoHeader)
        tvTitleHeaderLiability = layoutLiabilityHeader.findViewById(R.id.tvTitleHeader)
        layoutCreditCardcontent = view.findViewById(R.id.layoutCreditCardcontent)
        contentTitleCreditCard = layoutCreditCardcontent.findViewById(R.id.contentTitle)
        layoutCarcontent = view.findViewById(R.id.layoutCarcontent)
        contentTitleCar = layoutCarcontent.findViewById(R.id.contentTitle)
        layoutBusinessPaymentcontent = view.findViewById(R.id.layoutBusinessPaymentcontent)
        contentTitleBusinessPayment = layoutBusinessPaymentcontent.findViewById(R.id.contentTitle)
        layoutFamilyLoancontent = view.findViewById(R.id.layoutFamilyLoancontent)
        contentTitleFamilyLoan = layoutFamilyLoancontent.findViewById(R.id.contentTitle)
        layoutOtherLoancontent = view.findViewById(R.id.layoutOtherLoancontent)
        contentTitleOtherLoan = layoutOtherLoancontent.findViewById(R.id.contentTitle)
        btnSave = view.findViewById(R.id.btnSave)
        textTotalAsset = view.findViewById(R.id.textTotalAsset)
        textTotalJakat = view.findViewById(R.id.textTotalJakat)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCallback.setToolBarTitle(getString(R.string.txt_new_calculation))


        tvTitleHeaderNagadTaka.text = getText(R.string.title_nogod_taka)
        contentTitleNagadTaka.text = getText(R.string.title_nogod_taka)

        contentTitleBankNagadTaka.text =
            getText(R.string.title_bank_nogod_taka)

        tvTitleHeaderOrnamentAmt.text = getText(R.string.title_ornament_header)
        contentTitleGoldAmt.text = getText(R.string.text_gold_amount)
        contentTitleSilverAmt.text = getText(R.string.text_silver_amount)

        tvTitleHeaderInvestmentAmt.text = getText(R.string.title_investment)
        contentTitleShareMarket.text = getText(R.string.text_share_market)

        contentTitleOtherInvest.text = getText(R.string.text_other_investment)

        tvTitleHeaderAsset.text = getText(R.string.title_asset)
        contentTitleHouseRent.text = getText(R.string.text_house_rent)

        contentTitleAsset.text = getText(R.string.title_asset)

        tvTitleHeaderBusiness.text = getText(R.string.title_business)
        contentTitleNogodBusines.text = getText(R.string.text_nogod_business)

        contentTitleProduct.text = getText(R.string.text_product)

        tvTitleHeaderOther.text = getText(R.string.title_other)
        contentTitlePension.text = getText(R.string.text_pension)

        contentTitleLoan.text = getText(R.string.text_family_loan)

        contentTitleCapital.text = getText(R.string.text_other_capital)

        tvTitleHeaderFarming.text = getText(R.string.title_farming)
        contentTitleFarming.text = getText(R.string.text_taka_amount)

        tvTitleHeaderLiability.text = getText(R.string.title_lialibility)
        tvTitleHeaderLiability.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.deep_red
            )
        )

        contentTitleCreditCard.text = getText(R.string.text_credit_card)
        contentTitleCar.text = getText(R.string.text_car_payment)

        contentTitleBusinessPayment.text =
            getText(R.string.text_business_payment)

        contentTitleFamilyLoan.text =
            getText(R.string.text_family_loan_liability)

        contentTitleOtherLoan.text =
            requireContext().getText(R.string.text_other_loan)

        ivInfoHeaderNagadTaka.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_nogod_taka).toString(),
                description = getText(R.string.description_nogod_taka).toString()
            )
        }

        ivInfoHeaderOrnamentAmt.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_ornament_header).toString(),
                description = getText(R.string.description_gold_amount).toString()
            )
        }

        ivInfoHeaderInvestmentAmt.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_investment).toString(),
                description = getText(R.string.description_investment).toString()
            )
        }
        ivInfoHeaderAsset.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_asset).toString(),
                description = getText(R.string.description_asset).toString()
            )
        }

        ivInfoHeaderBusiness.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_business).toString(),
                description = getText(R.string.description_business).toString()
            )
        }

        ivInfoHeaderOther.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_other).toString(),
                description = getText(R.string.description_other).toString()
            )
        }

        ivInfoHeaderFarming.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_farming).toString(),
                description = getText(R.string.description_farming).toString()
            )
        }

        ivInfoHeaderLiability.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_lialibility).toString(),
                description = getText(R.string.description_liability).toString()
            )
        }


        btnSave.handleClickEvent {

            val etAmountNagadTaka: AppCompatEditText =
                layoutNagadTakacontent.findViewById(R.id.etAmount)
            val etAmountBankNagadTaka: AppCompatEditText =
                layoutBankNagadTakacontent.findViewById(R.id.etAmount)
            val etAmountGoldAmt: AppCompatEditText =
                layoutGoldAmtcontent.findViewById(R.id.etAmount)
            val etAmountSilverAmt: AppCompatEditText =
                layoutSilverAmtcontent.findViewById(R.id.etAmount)
            val etAmountShareMarket: AppCompatEditText =
                layoutShareMarketcontent.findViewById(R.id.etAmount)
            val etAmountOtherInvest: AppCompatEditText =
                layoutOtherInvestcontent.findViewById(R.id.etAmount)
            val etAmountHouseRent: AppCompatEditText =
                layoutHouseRentcontent.findViewById(R.id.etAmount)
            val etAmountAsset: AppCompatEditText = layoutAssetcontent.findViewById(R.id.etAmount)
            val etAmountNogodBusines: AppCompatEditText =
                layoutNogodBusinescontent.findViewById(R.id.etAmount)
            val etAmountProduct: AppCompatEditText =
                layoutProductcontent.findViewById(R.id.etAmount)
            val etAmountPension: AppCompatEditText =
                layoutPensioncontent.findViewById(R.id.etAmount)
            val etAmountLoan: AppCompatEditText = layoutLoancontent.findViewById(R.id.etAmount)
            val etAmountCapital: AppCompatEditText =
                layoutCapitalcontent.findViewById(R.id.etAmount)
            val etAmountFarming: AppCompatEditText =
                layoutFarmingcontent.findViewById(R.id.etAmount)
            val etAmountCreditCard: AppCompatEditText =
                layoutCreditCardcontent.findViewById(R.id.etAmount)
            val etAmountCar: AppCompatEditText = layoutCarcontent.findViewById(R.id.etAmount)
            val etAmountBusinessPayment: AppCompatEditText =
                layoutBusinessPaymentcontent.findViewById(R.id.etAmount)
            val etAmountFamilyLoan: AppCompatEditText =
                layoutFamilyLoancontent.findViewById(R.id.etAmount)
            val etAmountOtherLoan: AppCompatEditText =
                layoutOtherLoancontent.findViewById(R.id.etAmount)

            val nogodtakaText = etAmountNagadTaka.text
            val nogodtakaBankText = etAmountBankNagadTaka.text
            val goldText = etAmountGoldAmt.text
            val silverText = etAmountSilverAmt.text
            val shareMarketText = etAmountShareMarket.text
            val otherInvestmentText = etAmountOtherInvest.text
            val houseRentText = etAmountHouseRent.text
            val assetText = etAmountAsset.text
            val nogodbusinessText = etAmountNogodBusines.text
            val productText = etAmountProduct.text
            val pensionText = etAmountPension.text
            val loanText = etAmountLoan.text
            val otherCapitalText = etAmountCapital.text
            val farmingText = etAmountFarming.text
            val creditCardText = etAmountCreditCard.text
            val carPaymentText = etAmountCar.text
            val businessPaymentText = etAmountBusinessPayment.text
            val familyLoanText = etAmountFamilyLoan.text
            val otherLoanText = etAmountOtherLoan.text

            var nogodtakaAmount = 0.0
            var nogodtakaBankAmount = 0.0
            var goldAmount = 0.0
            var silverAmount = 0.0
            var shareMarketAmount = 0.0
            var otherInvestmentAmount = 0.0
            var houseRentAmount = 0.0
            var assetAmount = 0.0
            var nogodbusinessAmount = 0.0
            var productAmount = 0.0
            var pensionAmount = 0.0
            var loanAmount = 0.0
            var otherCapitalAmount = 0.0
            var farmingAmount = 0.0
            var creditCardAmount = 0.0
            var carPaymentAmount = 0.0
            var businessPaymentAmount = 0.0
            var familyLoanAmount = 0.0
            var otherLoanAmount = 0.0


            if (nogodtakaText.isNullOrEmpty()) {
            } else {
                nogodtakaAmount = nogodtakaText.toString().toDouble()
            }
            if (nogodtakaBankText.isNullOrEmpty()) {

            } else {
                nogodtakaBankAmount = nogodtakaBankText.toString().toDouble()
            }

            if (!goldText.isNullOrEmpty()) {
                goldAmount = goldText.toString().toDouble()
            }

            if (!silverText.isNullOrEmpty()) {
                silverAmount = silverText.toString().toDouble()
            }

            if (!shareMarketText.isNullOrEmpty()) {
                shareMarketAmount = shareMarketText.toString().toDouble()
            }

            if (!otherInvestmentText.isNullOrEmpty()) {
                otherInvestmentAmount = otherInvestmentText.toString().toDouble()
            }

            if (!houseRentText.isNullOrEmpty()) {
                houseRentAmount = houseRentText.toString().toDouble()
            }

            if (!assetText.isNullOrEmpty()) {
                assetAmount = assetText.toString().toDouble()
            }

            if (!nogodbusinessText.isNullOrEmpty()) {
                nogodbusinessAmount = nogodbusinessText.toString().toDouble()
            }

            if (!productText.isNullOrEmpty()) {
                productAmount = productText.toString().toDouble()
            }

            if (!pensionText.isNullOrEmpty()) {
                pensionAmount = pensionText.toString().toDouble()
            }

            if (!loanText.isNullOrEmpty()) {
                loanAmount = loanText.toString().toDouble()
            }

            if (!otherCapitalText.isNullOrEmpty()) {
                otherCapitalAmount = otherCapitalText.toString().toDouble()
            }

            if (!farmingText.isNullOrEmpty()) {
                farmingAmount = farmingText.toString().toDouble()
            }

            if (!creditCardText.isNullOrEmpty()) {
                creditCardAmount = creditCardText.toString().toDouble()
            }

            if (!carPaymentText.isNullOrEmpty()) {
                carPaymentAmount = carPaymentText.toString().toDouble()
            }

            if (!businessPaymentText.isNullOrEmpty()) {
                businessPaymentAmount = businessPaymentText.toString().toDouble()
            }

            if (!familyLoanText.isNullOrEmpty()) {
                familyLoanAmount = familyLoanText.toString().toDouble()
            }

            if (!otherLoanText.isNullOrEmpty()) {
                otherLoanAmount = otherLoanText.toString().toDouble()
            }



            val totalAsset =
                nogodtakaAmount + nogodtakaBankAmount + goldAmount + silverAmount + shareMarketAmount + otherInvestmentAmount + houseRentAmount + assetAmount + nogodbusinessAmount + productAmount + pensionAmount + loanAmount + otherCapitalAmount + farmingAmount

            val totalLiabilities =
                creditCardAmount + carPaymentAmount + businessPaymentAmount + familyLoanAmount + otherLoanAmount

            val totalNetAsset = totalAsset - totalLiabilities

            val totalZakat = ((totalNetAsset / 100) * 2.5)

            val cal: Calendar
            var year = 0
            var month = 0
            var day = 0

            try {
                cal = Calendar.getInstance()
                cal.setTime(Date())
                year = cal.get(Calendar.YEAR)
                month = cal.get(Calendar.MONTH)
                day = cal.get(Calendar.DATE)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val dateTxt: String =
                (TimeFormtter.getNumberByLocale(day.toString()) + " " + context?.let {
                    TimeFormtter.getBanglaMonthName(month, it)
                } + " " + TimeFormtter.getNumberByLocale(year.toString()))
            val minute = TimeFormtter.getCurrentTime() + " "

            textTotalAsset.text = DecimalFormat("##.##").format(totalNetAsset)
            textTotalJakat.text = DecimalFormat("##.##").format(totalZakat)


            val zakatModel = ZakatModel(
                farmingAmount,
                businessPaymentAmount,
                carPaymentAmount,
                nogodtakaAmount,
                nogodtakaBankAmount,
                nogodbusinessAmount,
                creditCardAmount,
                familyLoanAmount,
                loanAmount,
                houseRentAmount,
                true,
                "bn",
                otherCapitalAmount,
                otherInvestmentAmount,
                otherLoanAmount,
                pensionAmount,
                productAmount,
                assetAmount,
                shareMarketAmount,
                goldAmount,
                silverAmount,
                year)


            viewModel.saveZakatData(zakatModel)

        }
    }

    private fun initObserver()
    {
        viewModel.zakat_calculator?.observe(viewLifecycleOwner)
        {
            hideLoading()
            when(it)
            {
                is ZakatResource.Error -> mCallback.showToastMessage(it.errorMsg)
                ZakatResource.Loading -> showLoading()
                is ZakatResource.zakatSave ->
                {
                    when(it.data?.data?.status)
                    {
                        200 ->
                        {
                            mCallback.showToastMessage(getText(R.string.save_message).toString())
                            lifecycleScope.launch {

                                viewModel.callback(1)
                                viewModel.clearLiveData()
                            }
                        }
                        null -> Unit
                        else ->
                        {
                            mCallback.showToastMessage("Something went wrong! try again")
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    private fun showLoading()
    {
        for (i in 0 until rootView.childCount) {
            val view = rootView.getChildAt(i)
            if (view.id == R.id.progressLayout) {

                view.show()
            } else {

                view.hide()
            }
        }
    }

    private fun hideLoading()
    {
        for (i in 0 until rootView.childCount) {
            val view = rootView.getChildAt(i)
            if (view.id == R.id.progressLayout) {

                view.hide()
            } else {

                view.show()
            }
        }
    }

}