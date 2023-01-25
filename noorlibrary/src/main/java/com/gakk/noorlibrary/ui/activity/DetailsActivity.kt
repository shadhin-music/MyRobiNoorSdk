package com.gakk.noorlibrary.ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.gakk.noorlibrary.BuildConfig
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseActivity
import com.gakk.noorlibrary.base.DialogType
import com.gakk.noorlibrary.callbacks.*
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.ui.adapter.DivisionCallbackFunc
import com.gakk.noorlibrary.ui.adapter.SurahListAdapter
import com.gakk.noorlibrary.ui.fragments.*
import com.gakk.noorlibrary.ui.fragments.hajj.HajjHomeFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPreRegistrationFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPreRegistrationListFragment
import com.gakk.noorlibrary.ui.fragments.ijtema.BishwaIjtemaFragment
import com.gakk.noorlibrary.ui.fragments.qurbani.QurbaniHomeFragment
import com.gakk.noorlibrary.ui.fragments.zakat.donation.DonationHomeFragment
import com.gakk.noorlibrary.ui.fragments.zakat.donation.OrganizationDetailsFragment
import com.gakk.noorlibrary.util.*
import com.gakk.noorlibrary.ui.fragments.BiographyFragment
import com.mcc.noor.ui.fragments.hajj.umrah_hajj.UmrahHajjFragment
import java.io.File
import java.util.*


internal class DetailsActivity : BaseActivity(), DetailsCallBack {

    //private var binding: ActivityDetailsBinding? = null
    private lateinit var mPage: String
    private var mFrament: Fragment? = null
    private lateinit var mFragmentStack: Stack<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setStatusColor(R.color.bg)
        setStatusbarTextDark()

        mFragmentStack = Stack()

        mPage = intent.getStringExtra(PAGE_NAME)!!


        mFrament = when (mPage) {
            PAGE_DUA -> FragmentProvider.getFragmentByName(
                name = mPage,
                detailsActivityCallBack = this,
                catId = "0"
            )

            PAGE_LITERATURE_DETAILS -> {
                findViewById<TextView>(R.id.title)?.setText(getString(R.string.cat_hadith))
                val index = intent.getIntExtra(SELECTED_INDEX, 0)
                val literatureListWrapper =
                    intent.getSerializableExtra(LITERATURE_LIST_CALL_BACK) as LiteratureListWrapper
                val currentPage = intent.getIntExtra(CURRENT_PAGE, 0)
                val isFav = intent.getBooleanExtra(IS_FAV, false)

                FragmentProvider.getFragmentByName(
                    name = PAGE_LITERATURE_DETAILS,
                    detailsActivityCallBack = this,
                    literatureListWrapper = literatureListWrapper,
                    selectedLiteratureIndex = index,
                    currentPageNo = currentPage,
                    isFav = isFav
                )


//                putExtra(SELECTED_INDEX,selectedIndex)
//                putExtra(LITERATURE_LIST_CALL_BACK,literatureListCallBack)
//                putExtra(CURRENT_PAGE,currentPageNo)
//                putExtra(IS_FAV,isFav)


            }

            PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY -> {
                FragmentProvider.getFragmentByName(
                    PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                    this,
                    catId = intent.getStringExtra(CAT_ID),
                    subCatId = intent.getStringExtra(SUB_CAT_ID),
                    isFav = intent.getBooleanExtra(IS_FAV, false)
                )
            }
            PAGE_ISLAMIC_EVENT -> {
                val isFav = intent.getBooleanExtra("FromHome", false)
                if (isFav){
                    Log.e("sss","home${isFav}")

                    FragmentProvider.getFragmentByName(
                        PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY,
                        this,
                        catId = intent.getStringExtra(CAT_ID),
                        subCatId = intent.getStringExtra(SUB_CAT_ID),
                        isFromHomeEvent = isFav
                    )
                }else {
                    Log.e("sss","home${isFav}")

                    FragmentProvider.getFragmentByName(name = mPage, detailsActivityCallBack = this)
                }

            }
            PAGE_QURAN_SCHOOL -> FragmentProvider.getFragmentByName(
                name = mPage,
                detailsActivityCallBack = this,
                scholar = intent.getParcelableExtra(SCHOLAR)
            )

            PAGE_SURAH_DETAILS -> {
                val id = intent.getStringExtra(SURAH_ID)
                val list: MutableList<Data> =
                    intent.getSerializableExtra(SURAH_LIST) as MutableList<Data>

                FragmentProvider.getFragmentByName(
                    name = mPage,
                    detailsActivityCallBack = this,
                    id = id,
                    surahList = list
                )
            }
            PAGE_CAT_INSLAMIC_INSPIRATION -> {
                val id = intent.getStringExtra(SURAH_ID)

                FragmentProvider.getFragmentByName(
                    name = mPage,
                    detailsActivityCallBack = this,
                    id = id
                )
            }

            PAGE_TASBIH -> {
                val index = intent.getIntExtra(SELECTED_INDEX, 0)
                val currentPage = intent.getIntExtra(CURRENT_PAGE, 0)
                val count = intent.getIntExtra(ITEM_COUNT, 0)
                val times = intent.getIntExtra(ITEM_TIMES, 33)
                FragmentProvider.getFragmentByName(
                    name = PAGE_TASBIH,
                    detailsActivityCallBack = this,
                    selectedLiteratureIndex = index,
                    currentPageNo = currentPage,
                    itemCount = count,
                    times = times
                )
            }
            PAGE_SUBSCRIPTION_NAGAD -> {
                val isFav = intent.getBooleanExtra(IS_FAV, false)
                FragmentProvider.getFragmentByName(
                    name = PAGE_SUBSCRIPTION_NAGAD,
                    detailsActivityCallBack = this,
                    isFav = isFav
                )
            }

            PAGE_UMRAH_HAJJ -> {

                FragmentProvider.getFragmentByName(
                    name = mPage
                )
            }

            else -> FragmentProvider.getFragmentByName(name = mPage, detailsActivityCallBack = this)
        }

        mFrament?.let {
            addFragmentToStackAndShow(it)
        }

        findViewById<View>(R.id.btnBack)?.handleClickEvent {
            handleNavigationUpAction()
        }

        findViewById<View>(R.id.btnCustomActionOne)?.handleClickEvent {

        }

        findViewById<View>(R.id.btnCustomActionTwo)?.handleClickEvent {
            when (findViewById<View>(R.id.btnCustomActionTwo)?.tag) {
                MORE -> showDialogWithActionAndParam(dialogType = DialogType.SurahActionListDialog)
                NOTFICATION -> showDialogWithActionAndParam(dialogType = DialogType.RozaNotificationSettingDialog)
            }
        }

    }

    override fun setToolBarTitle(title: String?) {
        findViewById<TextView>(R.id.title)?.setText(title)
    }


    override fun showDialogWithActionAndParam(
        dialogType: DialogType,
        surahListAdapter: SurahListAdapter?,
        binding: View?,
        actionWithSingleParam: ((String) -> Unit)?,
        actionOneWithNoParameter: (() -> Unit)?,
        actionTwoWithNoParameter: (() -> Unit)?,
        pageReloadCallBack: PageReloadCallBack?,
        title: String?,
        description: String?,
        numberAyah: String?,
        textAyah: String?,
        textName: String?,
        divisionCallbackFunc: DivisionCallbackFunc?
    ) {
        showDialogWithParamByType(
            dilogType = dialogType,
            surahListAdapter = surahListAdapter,
            actionWithSingleParam = actionWithSingleParam,
            actionOneWithNoParameter = actionOneWithNoParameter,
            actionTwoWithNoParameter = actionTwoWithNoParameter,
            pageReloadCallBack = pageReloadCallBack,
            title = title,
            description = description,
            numberAyah = numberAyah,
            textAyah = textAyah,
            divisionCallbackFunc = divisionCallbackFunc
        )
    }

    override fun showAyaListBottomSheetDialog(id: String) {
        val bottomSheetFragment = AyatListDialogFragment.newInstance(id, this)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    override fun getWindowHeight(): Int {
        return super.getWindowHeight(this)
    }

    override fun setOrUpdateActionButtonTag(tag: String, buttonType: ActionButtonType) {
        when (buttonType) {
            ActionButtonType.TypeOne -> {
                findViewById<View>(R.id.btnCustomActionOne).tag = tag
                findViewById<ImageButton>(R.id.btnCustomActionOne)?.let { updateButtonIconBasedOnTag(it) }
            }
            ActionButtonType.TypeTwo -> {
                findViewById<View>(R.id.btnCustomActionTwo)?.tag = tag
                findViewById<ImageButton>(R.id.btnCustomActionTwo)?.let { updateButtonIconBasedOnTag(it) }
            }

            ActionButtonType.TypeThree -> {
                findViewById<View>(R.id.btnCustomActionThree)?.tag = tag
                findViewById<ImageButton>(R.id.btnCustomActionThree)?.let { updateButtonIconBasedOnTag(it) }
            }

        }
    }

    override fun setActionOfActionButton(action: () -> Unit, actionButtonType: ActionButtonType) {
        when (actionButtonType) {

            ActionButtonType.TypeOne -> findViewById<View>(R.id.btnCustomActionOne)?.handleClickEvent { action() }
            ActionButtonType.TypeTwo -> findViewById<View>(R.id.btnCustomActionTwo)?.handleClickEvent { action() }
            ActionButtonType.TypeThree -> findViewById<View>(R.id.btnCustomActionThree)?.handleClickEvent { action() }

        }

    }

    /**
     * Update toolbar icon based on tag
     */
    private fun updateButtonIconBasedOnTag(imageButton: ImageButton) {
        when (imageButton.tag) {
            MORE -> imageButton.setImageResource(R.drawable.ic_more)
            FAV -> imageButton.setImageResource(R.drawable.ic_favorite)
            FAV_FILLED -> imageButton.setImageResource(R.drawable.ic_favorite_filled)
            NOTFICATION -> imageButton.setImageResource(R.drawable.ic_notification)
            SHARE -> imageButton.setImageResource(R.drawable.ic_share)
        }
    }


    override fun addFragmentToStackAndShow(fragment: Fragment) {
        addFragmentToStack(fragment)
        showSelectedFragment(fragment)
    }


    fun showSelectedFragment(fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            //ft.replace(R.id.mainContainer, fragment)
            ft.add(R.id.mainContainer, fragment)
        }
        ft.commit()
    }

    fun addFragmentToStack(fragment: Fragment) {
        mFragmentStack.push(fragment)
    }

    private fun removeFragmentFromStack(fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            ft.remove(fragment)
        }
        ft.commit()
    }

    private fun updateToolbarForVisibleFragment(fragment: Fragment) {
        when (fragment::class.java) {
            LiteratureHomeFragment::class.java -> (fragment as LiteratureHomeFragment).updateToolbarForThisFragment()
            LiteratureCategoryListFragment::class.java -> (fragment as LiteratureCategoryListFragment).updateToolbarForThisFragment()
            LiteratureListFragment::class.java -> (fragment as LiteratureListFragment).updateToolbarForThisFragment()
            QuranHomeFragment::class.java -> (fragment as QuranHomeFragment).updateToolbarForThisFragment()
            SurahDetailsFragment::class.java -> {
                (fragment as SurahDetailsFragment).hideExistingUI()
                (fragment as SurahDetailsFragment).reloadPage()
            }
            BiographyFragment::class.java -> (fragment as BiographyFragment).updateToolbarForThisFragment()
            DonationHomeFragment::class.java -> (fragment as DonationHomeFragment).updateToolbarForThisFragment()
            OrganizationDetailsFragment::class.java -> (fragment as OrganizationDetailsFragment).updateToolbarForThisFragment()

            HajjHomeFragment::class.java -> (fragment as HajjHomeFragment).updateToolbarForThisFragment()
            HajjPreRegistrationFragment::class.java -> (fragment as HajjPreRegistrationFragment).updateToolbarForThisFragment()
            HajjPreRegistrationListFragment::class.java -> (fragment as HajjPreRegistrationListFragment).updateToolbarForThisFragment()
            QurbaniHomeFragment::class.java -> (fragment as QurbaniHomeFragment).updateToolbarForThisFragment()
            BishwaIjtemaFragment::class.java -> (fragment as BishwaIjtemaFragment).updateToolbarForThisFragment()
            UmrahHajjFragment::class.java -> (fragment as UmrahHajjFragment).setupToolbarCallback()

        }
    }

    override fun handleNavigationUpAction() {

        when (mFragmentStack.size > 1) {
            false -> {
                Log.e("NavUp", "Finished()")
                finish()
            }
            true -> {
                Log.e("NavUp", "pop()")
                var fragment = mFragmentStack.peek()//select the top fragment
                mFragmentStack.pop()//remove top fragment from stack
                removeFragmentFromStack(fragment)//now remove selected fragment from backstack
                fragment = mFragmentStack.peek()
                updateToolbarForVisibleFragment(fragment)
            }

        }
    }


    override fun onBackPressed() {
        handleNavigationUpAction()
    }

    override fun getScreenWith(): Int {
        return super.getScreenWidth()
    }

    override fun showToastMessage(message: String) {
        super.showToast(message)
    }


    override fun startDownloadIfPermissionGiven(action: () -> Unit) {

        performAction(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            showDialogWithActionAndParam(
                DialogType.DownloadConfirmDialog,
                actionOneWithNoParameter = action
            )
        }
    }

    override fun startDownloadCertificateIfPermissionGiven(action: () -> Unit) {
        performAction(Manifest.permission.WRITE_EXTERNAL_STORAGE, action)
    }

    override fun openFileInGalary(path: String) {

        val photoURI = FileProvider.getUriForFile(
            this, BuildConfig.LIBRARY_PACKAGE_NAME + ".provider",
            File(path)
        )
        Log.i("PhotoURI", "$photoURI")

        // val filePth=path.replace("file://", "content://")
        try {
            val intent = Intent().also {
                it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                it.action = Intent.ACTION_VIEW
                it.setDataAndType(photoURI, "image/*")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.i("EXCEPTION", e.message!!)
        }


    }

    override fun performAction(nameOfPermision: String, action: () -> Unit) {
        performActionifGivenPermision(action)
    }

    override fun openUrl(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }

    override fun showDialer(number: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    override fun toggleToolBarActionIconsVisibility(
        isVisible: Boolean,
        buttonType: ActionButtonType?,
        @DrawableRes
        buttonRes: Int?
    ) {
        when (buttonType) {
            ActionButtonType.TypeOne -> {
                when (isVisible) {
                    true -> {
                        findViewById<View>(R.id.btnCustomActionOne)?.visibility = VISIBLE
                    }
                    false -> {
                        findViewById<View>(R.id.btnCustomActionOne)?.visibility = GONE
                    }
                }
            }
            ActionButtonType.TypeTwo -> {
                when (isVisible) {
                    true -> {
                        findViewById<View>(R.id.btnCustomActionTwo)?.visibility = VISIBLE
                    }
                    false -> {
                        findViewById<View>(R.id.btnCustomActionTwo)?.visibility = GONE
                    }
                }
            }

            ActionButtonType.TypeThree -> {
                when (isVisible) {
                    true -> {
                        findViewById<View>(R.id.btnCustomActionThree)?.visibility = VISIBLE
                    }
                    false -> {
                        findViewById<View>(R.id.btnCustomActionThree)?.visibility = GONE
                    }
                }

                if (buttonRes != null) {
                    findViewById<ImageView>(R.id.btnCustomActionThree)?.setImageResource(buttonRes)
                }

            }

            null -> {
                when (isVisible) {
                    true -> {
                        findViewById<View>(R.id.btnCustomActionOne)?.visibility = VISIBLE
                        findViewById<View>(R.id.btnCustomActionTwo)?.visibility = VISIBLE
                        findViewById<View>(R.id.btnCustomActionThree)?.visibility = VISIBLE
                    }
                    false -> {
                        findViewById<View>(R.id.btnCustomActionOne)?.visibility = GONE
                        findViewById<View>(R.id.btnCustomActionTwo)?.visibility = GONE
                        findViewById<View>(R.id.btnCustomActionThree)?.visibility = GONE
                    }
                }
            }
        }
    }
}


