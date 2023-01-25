package com.gakk.noorlibrary.ui.fragments.hajj

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.model.currency.CurrencyModel
import com.gakk.noorlibrary.model.currency.CurrentCurrencyModel
import com.gakk.noorlibrary.ui.adapter.CountryListAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.properties.Delegates

internal class CurrencyConverterFragment : Fragment(), CountryListAdapter.OnItemClickListener {

    private var alertDialog: AlertDialog? = null
    private var mCallback: DetailsCallBack? = null

    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel

    private var fromCountry: Int by Delegates.notNull()
    private var toCountry: Int = 176

    enum class SELECTION {
        FROM, TO
    }

    private lateinit var imgCountryFrom: ImageView
    private lateinit var spnrFrom: AppCompatTextView
    private lateinit var spnrTo: AppCompatTextView
    private lateinit var edtInputValue: EditText
    private lateinit var convertedCurrecnyName: AppCompatTextView
    private lateinit var updateTime: AppCompatTextView
    private lateinit var txtDisplay: AppCompatTextView
    private lateinit var llBtnCurrencyConvert: LinearLayout
    private lateinit var btnCurrencyConvert: AppCompatTextView
    private lateinit var icRefresh: ImageView
    private lateinit var imgCountryTo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_currencyconverter,
            container, false
        )
        imgCountryFrom = view.findViewById(R.id.imgCountryFrom)
        spnrFrom = view.findViewById(R.id.spnrFrom)
        spnrTo = view.findViewById(R.id.spnrTo)
        edtInputValue = view.findViewById(R.id.edtInputValue)
        convertedCurrecnyName = view.findViewById(R.id.converted_currecny_name)
        updateTime = view.findViewById(R.id.update_time)
        txtDisplay = view.findViewById(R.id.txtDisplay)
        llBtnCurrencyConvert = view.findViewById(R.id.llBtnCurrencyConvert)
        btnCurrencyConvert = view.findViewById(R.id.btnCurrencyConvert)
        icRefresh = view.findViewById(R.id.ic_refresh)
        imgCountryTo = view.findViewById(R.id.imgCountryTo)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            val job = launch {
                repository = RepositoryProvider.getRepository()
            }
            job.join()
            model = ViewModelProvider(
                this@CurrencyConverterFragment,
                HajjViewModel.FACTORY(repository)
            ).get(HajjViewModel::class.java)

            initToolbar()
            subscribeObserver()
            model.parseCountryList(requireContext())
        }
    }

    private fun initToolbar() {
        mCallback?.setToolBarTitle(requireContext().getString(R.string.currency_converter))

        val txtFromCountry: String

        fromCountry = 16
        imgCountryFrom.setImageResource(R.drawable.ic_bd)
        txtFromCountry = "Bangladesh - BDT"

        spnrFrom.text = txtFromCountry
        spnrTo.text = ("Soudi Arabia - SAR")

    }

    private fun swapCurrency(list: List<CurrencyModel>) {
        val from = list[toCountry]
        val to = list[fromCountry]

        updateView(from, SELECTION.FROM)
        updateView(to, SELECTION.TO)

        val t = fromCountry
        fromCountry = toCountry
        toCountry = t

    }

    private fun subscribeObserver() {
        model.countryListLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadCountryListInSpinner(it.data)
                }
                Status.LOADING -> Unit
                Status.ERROR -> Unit
            }
        }

        model.currentRateLivaData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    calculateCurrency(it.data)
                }
                Status.LOADING -> Unit
                Status.ERROR -> {
                    mCallback?.showToastMessage(it.message ?: "No currency data found")
                }
            }
        }
    }

    private fun calculateCurrency(data: CurrentCurrencyModel?) {
        data?.let {
            val f: Double = it.from.toDouble()
            val t: Double = it.to.toDouble()
            val userInt = edtInputValue.text.toString().trim().toDouble()

            val main_ratio = t / f
            val percent = main_ratio * userInt

            convertedCurrecnyName.show()

            updateTime.text =
                (resources.getString(R.string.currency_update) + it.date.getTime())


            updateTime.show()

            var s = DecimalFormat("##.##").format(
                percent
            )
            if (AppPreference.language == LAN_BANGLA) {
                s = s.toString().getNumberInBangla()
            }
            txtDisplay.text = s

        }
    }

    private fun loadCountryListInSpinner(data: List<CurrencyModel>?) {
        data?.let {
            Log.d("listOdCU", "loadCountryListInSpinner: ${it.size}")

            spnrFrom.handleClickEvent {
                showSelectedNameInDialog(it, SELECTION.FROM)
            }

            spnrTo.handleClickEvent {
                showSelectedNameInDialog(it, SELECTION.TO)
            }

            llBtnCurrencyConvert.handleClickEvent {
                swapCurrency(it)
                btnCurrencyConvert.performClick()
            }

            btnCurrencyConvert.handleClickEvent {
                val input = edtInputValue.text.toString()
                if (input.isNotBlank()) {
                    convertCurrency(it)
                }
            }

            icRefresh.handleClickEvent {
                val txtDisplays =
                    if (AppPreference.language == LAN_BANGLA) {
                        "0.0".getNumberInBangla()
                    } else {
                        "0.0"
                    }

                txtDisplay.text = txtDisplays
                updateTime.invisible()
                convertedCurrecnyName.invisible()
            }
        }
    }

    private fun convertCurrency(list: List<CurrencyModel>) {
        val from = list[fromCountry]
        val to = list[toCountry]

        convertedCurrecnyName.text = ("${from.currency} - ${to.currency}")
        model.getCurrentRates(from.alphabeticCode, to.alphabeticCode)
    }

    private fun showSelectedNameInDialog(dataList: List<CurrencyModel>, selection: SELECTION) {
        val customDialog =
            MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)

        val dialogView: View = layoutInflater.inflate(R.layout.dialog_country_list, null)
        val btnDismiss = dialogView.findViewById<ImageButton>(R.id.btnDismiss)
        val countryList = dialogView.findViewById<RecyclerView>(R.id.countryList)

        countryList.apply {
            adapter = CountryListAdapter(
                if (selection == SELECTION.FROM) {
                    fromCountry
                } else {
                    toCountry
                },
                this@CurrencyConverterFragment,
                selection
            ).apply { submitList(dataList) }
        }


        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog?.window?.setGravity(Gravity.CENTER)
        alertDialog?.setCancelable(false)
        alertDialog?.show()

        btnDismiss.handleClickEvent {
            alertDialog?.dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CurrencyConverterFragment()
    }


    override fun onItemClick(postion: Int, currencyModel: CurrencyModel, selection: SELECTION) {
        alertDialog?.dismiss()
        updateView(currencyModel, selection)

        if (selection == SELECTION.FROM) {
            fromCountry = postion
            spnrFrom.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        } else {
            toCountry = postion
            spnrTo.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        }
    }

    private fun updateView(currencyModel: CurrencyModel, selection: SELECTION) {
        val view: ImageView?
        if (selection == SELECTION.FROM) {
            view = imgCountryFrom
            spnrFrom.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        } else {
            view = imgCountryTo
            spnrTo.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        }
        Glide.with(requireContext())
            .load(currencyModel.fullImageUrl)
            .into(view)
    }

    override fun onDestroyView() {
        mCallback?.setToolBarTitle(resources.getString(R.string.cat_hajj))
        super.onDestroyView()
    }
}