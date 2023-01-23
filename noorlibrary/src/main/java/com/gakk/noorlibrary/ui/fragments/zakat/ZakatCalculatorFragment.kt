package com.gakk.noorlibrary.ui.fragments.zakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.roomdb.RoomRepository
import com.gakk.noorlibrary.data.roomdb.ZakatRoomDatabase
import com.gakk.noorlibrary.databinding.FragmentJakatCalculatorBinding
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import com.gakk.noorlibrary.ui.fragments.ZakatCalculationObserver
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.handleClickEvent
import com.gakk.noorlibrary.viewModel.ZakatViewModel
import java.text.DecimalFormat
import java.util.*

/**
 * @AUTHOR: Taslima Sumi
 * @DATE: 4/1/2021, Thu
 */

internal class ZakatCalculatorFragment : Fragment() {

    private lateinit var mCallback: DetailsCallBack
    private lateinit var binding: FragmentJakatCalculatorBinding
    private lateinit var viewModel: ZakatViewModel
    private lateinit var repository: RoomRepository
    private lateinit var layoutNagadTakaHeader: ConstraintLayout
    private lateinit var tvTitleHeaderNagadTaka: AppCompatTextView
    private lateinit var layoutNagadTakacontent: ConstraintLayout
    private lateinit var contentTitleNagadTaka: AppCompatTextView
    private lateinit var layoutBankNagadTakacontent: ConstraintLayout
    private lateinit var contentTitleBankNagadTaka: AppCompatTextView
    private lateinit var layoutOrnamentAmtHeader: ConstraintLayout
    private lateinit var tvTitleHeaderOrnamentAmt: AppCompatTextView
    private lateinit var layoutGoldAmtcontent: ConstraintLayout
    private lateinit var contentTitleGoldAmt: AppCompatTextView
    private lateinit var layoutSilverAmtcontent: ConstraintLayout
    private lateinit var contentTitleSilverAmt: AppCompatTextView
    private lateinit var layoutInvestmentAmtHeader: ConstraintLayout
    private lateinit var tvTitleHeaderInvestmentAmt: AppCompatTextView
    private lateinit var layoutShareMarketcontent: ConstraintLayout
    private lateinit var contentTitleShareMarket: AppCompatTextView
    private lateinit var layoutOtherInvestcontent: ConstraintLayout
    private lateinit var contentTitleOtherInvest: AppCompatTextView
    private lateinit var layoutAssetHeader: ConstraintLayout
    private lateinit var tvTitleHeaderAsset: AppCompatTextView
    private lateinit var layoutHouseRentcontent: ConstraintLayout
    private lateinit var contentTitleHouseRent: AppCompatTextView
    private lateinit var layoutAssetcontent: ConstraintLayout
    private lateinit var contentTitleAsset: AppCompatTextView


    val database by lazy { ZakatRoomDatabase.getDatabase(requireContext()) }
    val repositoryRoom by lazy { RoomRepository(database.zakatDao()) }

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
        return view
    }

    private fun initUi(view: View) {
        layoutNagadTakaHeader = view.findViewById(R.id.layoutNagadTakaHeader)
        tvTitleHeaderNagadTaka = layoutNagadTakaHeader.findViewById(R.id.tvTitleHeader)
        layoutNagadTakacontent = view.findViewById(R.id.layoutNagadTakacontent)
        contentTitleNagadTaka = layoutNagadTakacontent.findViewById(R.id.contentTitle)
        layoutBankNagadTakacontent = view.findViewById(R.id.layoutBankNagadTakacontent)
        contentTitleBankNagadTaka = layoutBankNagadTakacontent.findViewById(R.id.contentTitle)
        layoutOrnamentAmtHeader = view.findViewById(R.id.layoutOrnamentAmtHeader)
        tvTitleHeaderOrnamentAmt = layoutOrnamentAmtHeader.findViewById(R.id.tvTitleHeader)
        layoutGoldAmtcontent = view.findViewById(R.id.layoutGoldAmtcontent)
        contentTitleGoldAmt = layoutGoldAmtcontent.findViewById(R.id.contentTitle)
        layoutSilverAmtcontent = view.findViewById(R.id.layoutSilverAmtcontent)
        contentTitleSilverAmt = layoutSilverAmtcontent.findViewById(R.id.contentTitle)
        layoutInvestmentAmtHeader = view.findViewById(R.id.layoutInvestmentAmtHeader)
        tvTitleHeaderInvestmentAmt = layoutInvestmentAmtHeader.findViewById(R.id.tvTitleHeader)
        layoutShareMarketcontent = view.findViewById(R.id.layoutShareMarketcontent)
        contentTitleShareMarket = layoutShareMarketcontent.findViewById(R.id.contentTitle)
        layoutOtherInvestcontent = view.findViewById(R.id.layoutOtherInvestcontent)
        contentTitleOtherInvest = layoutOtherInvestcontent.findViewById(R.id.contentTitle)
        layoutAssetHeader = view.findViewById(R.id.layoutAssetHeader)
        tvTitleHeaderAsset = layoutAssetHeader.findViewById(R.id.tvTitleHeader)
        layoutHouseRentcontent = view.findViewById(R.id.layoutHouseRentcontent)
        contentTitleHouseRent = layoutHouseRentcontent.findViewById(R.id.tvTitleHeader)
        layoutAssetcontent = view.findViewById(R.id.layoutAssetcontent)
        contentTitleAsset = layoutAssetcontent.findViewById(R.id.contentTitle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = repositoryRoom

        viewModel = ViewModelProvider(
            this@ZakatCalculatorFragment, ZakatViewModel.FACTORY(repository)
        ).get(ZakatViewModel::class.java)


        mCallback.setToolBarTitle(getString(R.string.txt_new_calculation))

        val symbol: String

        symbol = getString(R.string.text_symbol_tk)

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

        binding.layoutBusinessHeader.tvTitleHeader.text = getText(R.string.title_business)
        binding.layoutNogodBusinescontent.contentTitle.text = getText(R.string.text_nogod_business)
        binding.layoutNogodBusinescontent.tvSymbol.text = symbol

        binding.layoutProductcontent.contentTitle.text = getText(R.string.text_product)
        binding.layoutProductcontent.tvSymbol.text = symbol

        binding.layoutOtherHeader.tvTitleHeader.text = getText(R.string.title_other)
        binding.layoutPensioncontent.contentTitle.text = getText(R.string.text_pension)
        binding.layoutPensioncontent.tvSymbol.text = symbol

        binding.layoutLoancontent.contentTitle.text = getText(R.string.text_family_loan)
        binding.layoutLoancontent.tvSymbol.text = symbol

        binding.layoutCapitalcontent.contentTitle.text = getText(R.string.text_other_capital)
        binding.layoutCapitalcontent.tvSymbol.text = symbol

        binding.layoutFarmingHeader.tvTitleHeader.text = getText(R.string.title_farming)
        binding.layoutFarmingcontent.contentTitle.text = getText(R.string.text_taka_amount)
        binding.layoutFarmingcontent.tvSymbol.text = symbol

        binding.layoutLiabilityHeader.tvTitleHeader.text = getText(R.string.title_lialibility)
        binding.layoutLiabilityHeader.tvTitleHeader.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.deep_red
            )
        )

        binding.layoutCreditCardcontent.contentTitle.text = getText(R.string.text_credit_card)
        binding.layoutCreditCardcontent.tvSymbol.text = symbol

        binding.layoutCarcontent.contentTitle.text = getText(R.string.text_car_payment)
        binding.layoutCarcontent.tvSymbol.text = symbol

        binding.layoutBusinessPaymentcontent.contentTitle.text =
            getText(R.string.text_business_payment)
        binding.layoutBusinessPaymentcontent.tvSymbol.text = symbol

        binding.layoutFamilyLoancontent.contentTitle.text =
            getText(R.string.text_family_loan_liability)
        binding.layoutFamilyLoancontent.tvSymbol.text = symbol

        binding.layoutOtherLoancontent.contentTitle.text =
            requireContext().getText(R.string.text_other_loan)
        binding.layoutOtherLoancontent.tvSymbol.text = symbol

        binding.layoutNagadTakaHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_nogod_taka).toString(),
                description = getText(R.string.description_nogod_taka).toString()
            )
        }

        binding.layoutOrnamentAmtHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_ornament_header).toString(),
                description = getText(R.string.description_gold_amount).toString()
            )
        }

        binding.layoutInvestmentAmtHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_investment).toString(),
                description = getText(R.string.description_investment).toString()
            )
        }
        binding.layoutAssetHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_asset).toString(),
                description = getText(R.string.description_asset).toString()
            )
        }

        binding.layoutBusinessHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_business).toString(),
                description = getText(R.string.description_business).toString()
            )
        }

        binding.layoutOtherHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_other).toString(),
                description = getText(R.string.description_other).toString()
            )
        }

        binding.layoutFarmingHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_farming).toString(),
                description = getText(R.string.description_farming).toString()
            )
        }

        binding.layoutLiabilityHeader.ivInfoHeader.handleClickEvent {
            mCallback.showDialogWithActionAndParam(
                DialogType.ZakatInfoShow,
                title = getText(R.string.title_lialibility).toString(),
                description = getText(R.string.description_liability).toString()
            )
        }

        binding.btnSave.handleClickEvent {

            val nogodtakaText = binding.layoutNagadTakacontent.etAmount.text
            val nogodtakaBankText = binding.layoutBankNagadTakacontent.etAmount.text
            val goldText = binding.layoutGoldAmtcontent.etAmount.text
            val silverText = binding.layoutSilverAmtcontent.etAmount.text
            val shareMarketText = binding.layoutShareMarketcontent.etAmount.text
            val otherInvestmentText = binding.layoutOtherInvestcontent.etAmount.text
            val houseRentText = binding.layoutHouseRentcontent.etAmount.text
            val assetText = binding.layoutAssetcontent.etAmount.text
            val nogodbusinessText = binding.layoutNogodBusinescontent.etAmount.text
            val productText = binding.layoutProductcontent.etAmount.text
            val pensionText = binding.layoutPensioncontent.etAmount.text
            val loanText = binding.layoutLoancontent.etAmount.text
            val otherCapitalText = binding.layoutOtherLoancontent.etAmount.text
            val farmingText = binding.layoutFarmingcontent.etAmount.text
            val creditCardText = binding.layoutCreditCardcontent.etAmount.text
            val carPaymentText = binding.layoutCarcontent.etAmount.text
            val businessPaymentText = binding.layoutBusinessPaymentcontent.etAmount.text
            val familyLoanText = binding.layoutFamilyLoancontent.etAmount.text
            val otherLoanText = binding.layoutOtherLoancontent.etAmount.text


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
                //  mCallback.showToastMessage("Please enter amount")
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
            val fullDate = minute + "• " + dateTxt
            val data = ZakatDataModel(
                fullDate,
                DecimalFormat("##.##").format(totalNetAsset),
                DecimalFormat("##.##").format(totalZakat)
            )
            viewModel.insert(data)
            binding.textTotalAsset.text = DecimalFormat("##.##").format(totalNetAsset)
            binding.textTotalJakat.text = DecimalFormat("##.##").format(totalZakat)

            ZakatCalculationObserver.switchTabAtIndex(1)

            viewModel.allData.observe(viewLifecycleOwner) {
                ZakatCalculationObserver.updateZakatList(it)
                mCallback.showToastMessage(getText(R.string.save_message).toString())
            }
        }
    }
}