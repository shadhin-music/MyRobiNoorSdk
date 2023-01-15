package com.gakk.noorlibrary.ui.fragments.hajj

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gakk.noorlibrary.BuildConfig
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.rest.Status
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.databinding.DialogCountryListBinding
import com.gakk.noorlibrary.databinding.FragmentCurrencyconverterBinding
import com.gakk.noorlibrary.model.currency.CurrencyModel
import com.gakk.noorlibrary.model.currency.CurrentCurrencyModel
import com.gakk.noorlibrary.ui.adapter.CountryListAdapter
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.viewModel.HajjViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.properties.Delegates

class CurrencyConverterFragment : Fragment(), CountryListAdapter.OnItemClickListener {

    private var alertDialog: AlertDialog? = null
    private lateinit var binding: FragmentCurrencyconverterBinding
    private var mCallback: DetailsCallBack? = null

    private lateinit var repository: RestRepository
    private lateinit var model: HajjViewModel

    private var fromCountry: Int by Delegates.notNull()
    private var toCountry: Int = 176

    enum class SELECTION {
        FROM, TO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCallback = requireActivity() as DetailsCallBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppPreference.language?.let { context?.setApplicationLanguage(it) }
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_currencyconverter,
                container,
                false
            )

        return binding.root
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
            binding.imgCountryFrom.setImageResource(R.drawable.ic_bd)
            txtFromCountry = "Bangladesh - BDT"

        binding.spnrFrom.text = txtFromCountry
        binding.spnrTo.text = ("Soudi Arabia - SAR")

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
            val userInt = binding.edtInputValue.text.toString().trim().toDouble()

            val main_ratio = t / f
            val percent = main_ratio * userInt

            binding.convertedCurrecnyName.show()

            binding.updateTime.text =
                (resources.getString(R.string.currency_update) + it.date.getTime())


            binding.updateTime.show()

            var s = DecimalFormat("##.##").format(
                percent
            )
            if (AppPreference.language == LAN_BANGLA) {
                s = s.toString().getNumberInBangla()
            }
            binding.txtDisplay.text = s

        }
    }

    private fun loadCountryListInSpinner(data: List<CurrencyModel>?) {
        data?.let {
            Log.d("listOdCU", "loadCountryListInSpinner: ${it.size}")

            binding.spnrFrom.handleClickEvent {
                showSelectedNameInDialog(it, SELECTION.FROM)
            }

            binding.spnrTo.handleClickEvent {
                showSelectedNameInDialog(it, SELECTION.TO)
            }

            binding.llBtnCurrencyConvert.handleClickEvent {
                swapCurrency(it)
                binding.btnCurrencyConvert.performClick()
            }

            binding.btnCurrencyConvert.handleClickEvent {
                val input = binding.edtInputValue.text.toString()
                if (input.isNotBlank()) {
                    convertCurrency(it)
                }
            }

            binding.icRefresh.handleClickEvent {
                val txtDisplay =
                    if (AppPreference.language == LAN_BANGLA) {
                        "0.0".getNumberInBangla()
                    } else {
                        "0.0"
                    }

                binding.txtDisplay.text = txtDisplay
                binding.updateTime.invisible()
                binding.convertedCurrecnyName.invisible()
            }
        }
    }

    private fun convertCurrency(list: List<CurrencyModel>) {
        val from = list[fromCountry]
        val to = list[toCountry]

        binding.convertedCurrecnyName.text = ("${from.currency} - ${to.currency}")
        model.getCurrentRates(from.alphabeticCode, to.alphabeticCode)
    }

    private fun showSelectedNameInDialog(dataList: List<CurrencyModel>, selection: SELECTION) {
        val customDialog =
            MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
        val dialogBinding: DialogCountryListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()),
            R.layout.dialog_country_list,
            null,
            false
        )

        dialogBinding.countryList.apply {
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

        val dialogView: View = dialogBinding.root
        customDialog.setView(dialogView)

        alertDialog = customDialog.show()
        alertDialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        alertDialog?.window?.setGravity(Gravity.CENTER)
        alertDialog?.setCancelable(false)
        alertDialog?.show()

        dialogBinding.btnDismiss.handleClickEvent {
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
            binding.spnrFrom.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        } else {
            toCountry = postion
            binding.spnrTo.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        }
    }

    private fun updateView(currencyModel: CurrencyModel, selection: SELECTION) {
        val view: ImageView?
        if (selection == SELECTION.FROM) {
            view = binding.imgCountryFrom
            binding.spnrFrom.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
        } else {
            view = binding.imgCountryTo
            binding.spnrTo.text = ("${currencyModel.entity} - ${currencyModel.alphabeticCode}")
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