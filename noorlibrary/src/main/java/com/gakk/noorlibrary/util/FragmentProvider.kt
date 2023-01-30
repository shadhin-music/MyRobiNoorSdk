package com.gakk.noorlibrary.util

import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.model.quranSchool.Scholar
import com.gakk.noorlibrary.ui.fragments.*
import com.gakk.noorlibrary.ui.fragments.eidjamat.EidJamatFragment
import com.gakk.noorlibrary.ui.fragments.hajj.HajjHomeFragment
import com.gakk.noorlibrary.ui.fragments.hajj.hajjguide.HajjGuideFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjpreRegistrationDetailsFragment
import com.gakk.noorlibrary.ui.fragments.ijtema.BishwaIjtemaFragment
import com.gakk.noorlibrary.ui.fragments.inspiration.InspirationDetailsFragment
import com.gakk.noorlibrary.ui.fragments.instructiveVideo.InstructiveVideoFragment
import com.gakk.noorlibrary.ui.fragments.islamicdiscuss.IslamicDiscussFragment
import com.gakk.noorlibrary.ui.fragments.onlinehut.OnlintHutHomeFragment
import com.gakk.noorlibrary.ui.fragments.quranSchool.QuranSchoolHomeFragment
import com.gakk.noorlibrary.ui.fragments.qurbani.QurbaniHomeFragment
import com.gakk.noorlibrary.ui.fragments.subscription.NagadSubscriptionFragment
import com.gakk.noorlibrary.ui.fragments.subscription.SubscriptionFragment
import com.gakk.noorlibrary.ui.fragments.subscription.SubscriptionOptionListFragment
import com.gakk.noorlibrary.ui.fragments.tracker.TrackerTabFragment
import com.gakk.noorlibrary.ui.fragments.zakat.ZakatCalculatorFragment
import com.gakk.noorlibrary.ui.fragments.zakat.donation.*
import com.mcc.noor.ui.fragments.hajj.umrah_hajj.UmrahHajjFragment
import java.io.Serializable


object FragmentProvider {

    fun getFragmentByName(
        name: String,
        detailsActivityCallBack: DetailsCallBack? = null,
        mosqueCallBack: DistanceControl? = null,
        id: String? = null,
        catId: String? = null, // Literature( categoryId of dua or hadis)
        subCatId: String? = null, // Literature->( sub-categoryId of dua or hadis)
        literatureListWrapper: LiteratureListWrapper? = null,
        selectedLiteratureIndex: Int? = null,
        currentPageNo: Int? = null,
        isFav: Boolean = false,
        surahList: MutableList<Data>? = null,
        pageTitle: String? = null,
        scholar: Scholar? = null,
        catName: String? = null,
        itemCount: Int? = null,
        times: Int? = null,
        userInfo: com.gakk.noorlibrary.model.profile.Data? = null,
        literature: Literature? = null,
        listNamesOfAllah: List<com.gakk.noorlibrary.model.names.Data>? = null,
        isFromHomeEvent: Boolean = false
    ): Fragment? {

        return when (name) {
            PAGE_QURAN_HOME -> QuranHomeFragment.newInstance()
            PAGE_SURAH_DETAILS -> SurahDetailsFragment.newInstance(
                id!!, detailsActivityCallBack!!, surahList
            )
            PAGE_CAT_INSLAMIC_INSPIRATION -> InspirationDetailsFragment.newInstance(
                id!!
            )

            PAGE_NEAREST_MOSQUE -> NearestMosqueFragment.newInstance(
                PAGE_NEAREST_MOSQUE
            )

            PAGE_FULL_PLAYER -> SurahFullPlayerFragment.newInstance()
            PAGE_ROZA -> com.gakk.noorlibrary.ui.fragments.roja.RozaInformationFragment.newInstance()

            PAGE_NEAREST_MOSQUE_MAP -> MapFragment.newInstance(
                mosqueCallBack!!
            )

            PAGE_DUA -> when (Util.checkSub()) {
                true -> {
                    LiteratureHomeFragment.newInstance(
                        LiteratureType.Dua
                    )
                }
                else -> {
                    SubscriptionOptionListFragment.newInstance()
                }
            }


            PAGE_LITERATURE_LILIST_BY_SUB_CATEGORY -> LiteratureListFragment.newInstance(
                isFav, catId = catId!!, subCatId = subCatId!!, pageTitle = pageTitle
            )
            PAGE_HADIS -> when (Util.checkSub()) {
                true -> {
                    LiteratureHomeFragment.newInstance(
                        LiteratureType.Hadis
                    )
                }

                else -> {
                    SubscriptionOptionListFragment.newInstance()

                }
            }
            PAGE_ISLAMIC_EVENT -> LiteratureCategoryListFragment.newInstance(
                LiteratureType.NamazRules, R.string.event_cateogry_id.getLocalisedTextFromResId()
            )

            PAGE_EID_JAMAT -> EidJamatFragment.newInstance()
            PAGE_ISLAMIC_PODCAST -> IslamicDiscussFragment.newInstance()

            PAGE_NAMAZ_RULES -> {
                when (Util.checkSub()) {
                    true -> {
                        LiteratureCategoryListFragment.newInstance(
                            LiteratureType.NamazRules,
                            R.string.namaz_rules_cat_id.getLocalisedTextFromResId()
                        )

                    }

                    else -> {
                        SubscriptionOptionListFragment.newInstance()

                    }
                }


            }
            PAGE_LITERATURE_DETAILS ->

                LiteratureDetailsFragment.newInstance(
                    literatureListWrapper,
                    selectedLiteratureIndex!!,
                    currentPageNo!!,
                    isFavList = isFav
                )

            PAGE_NAMAZ_VISUAL -> NamazVisualFragment.newInstance(
                catName
            )
            PAGE_BIOGRAPHY -> BiographyFragment.newInstance()


            PAGE_JAKAT -> when (Util.checkSub()) {
                true -> {
                    LiteratureHomeFragment.newInstance(
                        LiteratureType.Jakat
                    )
                }
                else -> {
                    SubscriptionOptionListFragment.newInstance()
                }
            }

            PAGE_JAKAT_NEW_CALCULATION -> ZakatCalculatorFragment.newInstance()

            PAGE_QURAN_SCHOOL -> QuranSchoolHomeFragment.newInstance(
                scholar = scholar
            )
            PAGE_CAT_QURAN_SCHOOL -> when (Util.checkSub()) {
                true -> {
                    QuranSchoolHomeFragment.newInstance(
                        isCatQuranSchool = true
                    )
                }
                else -> {
                    SubscriptionOptionListFragment.newInstance()
                }
            }


            PAGE_CAT_LIVE_QA ->
                InstructiveVideoFragment.newInstance(
                    "Live Qa"
                )


            PAGE_HAJJ_HOME ->
                HajjHomeFragment.newInstance()

           // PAGE_SUBSCRIPTION -> SubscriptionFragment.newInstance()
            PAGE_SUBSCRIPTION_OPTION_LIST -> SubscriptionOptionListFragment.newInstance()
            PAGE_SUBSCRIPTION_NAGAD -> NagadSubscriptionFragment.newInstance(
                isFav
            )

            PAGE_TRACKER -> when (Util.checkSub()) {
                true -> {
                    TrackerTabFragment.newInstance()
                }

                else -> {
                    SubscriptionOptionListFragment.newInstance()
                }
            }

            PAGE_QURBANI_HOME ->
                QurbaniHomeFragment.newInstance()


            PAGE_EID_E_MILADUNNOBI -> when (Util.checkSub()) {
                true -> {
                    LiteratureListFragment.newInstance(
                        false,
                        catId = R.string.miladunnobi_cateogry_id.getLocalisedTextFromResId(),
                        subCatId = "undefined",
                        pageTitle = "",
                        showHeaderImageMiladunnobi = true
                    )
                }

                else -> {
                    SubscriptionOptionListFragment.newInstance()
                }
            }


            PAGE_DONATION_HOME -> DonationHomeFragment.newInstance()

            PAGE_DONATION -> DonationFragment.newInstance(
                catName!!
            )


            PAGE_DONATE_ZAKAT -> DonateZakatFragment.newInstance(
                itemLiterature = literature!!
            )

            ORGANIZATION_DETAILS -> OrganizationDetailsFragment.newInstance(
                itemLiterature = literature!!
            )

            PAGE_DONATION_FORM -> DonationFormFragment.newInstance(
                itemLiterature = literature!!
            )

            PAGE_DONATION_IMPORTANCE -> LiteratureListFragment.newInstance(
                false,
                catId = R.string.donation_importance_id.getLocalisedTextFromResId(),
                subCatId = "undefined",
                pageTitle = ""
            )

            PAGE_IJTEMA -> BishwaIjtemaFragment.newInstance()

            ONLINE_HUT_HOME -> OnlintHutHomeFragment.newInstance()

            PAGE_HAJJ_GUIDE -> HajjGuideFragment.newInstance()

            PAGE_HAJJ_PREREGISTRATION -> HajjpreRegistrationDetailsFragment.newInstance()

            PAGE_UMRAH_HAJJ -> UmrahHajjFragment.newInstance()


            else -> null
        }
    }

}

sealed class LiteratureType : Serializable {
    object Dua : LiteratureType()
    object Hadis : LiteratureType()
    object NamazRules : LiteratureType()
    object Jakat : LiteratureType()
}
