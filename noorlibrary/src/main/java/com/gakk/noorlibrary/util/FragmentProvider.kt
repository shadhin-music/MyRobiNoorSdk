package com.gakk.noorlibrary.util

import androidx.fragment.app.Fragment
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.data.wrapper.LiteratureListWrapper
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.model.quran.surah.Data
import com.gakk.noorlibrary.ui.fragments.*
import com.gakk.noorlibrary.ui.fragments.eidjamat.EidJamatFragment
import com.gakk.noorlibrary.ui.fragments.hajj.HajjHomeFragment
import com.gakk.noorlibrary.ui.fragments.hajj.hajjguide.HajjGuideFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjPreRegistrationFragment
import com.gakk.noorlibrary.ui.fragments.hajj.preregistration.HajjpreRegistrationDetailsFragment
import com.gakk.noorlibrary.ui.fragments.hajj.umrah_hajj.UmrahHajjFragment
import com.gakk.noorlibrary.ui.fragments.islamicdiscuss.IslamicDiscussFragment
import com.gakk.noorlibrary.ui.fragments.subscription.SslSubscriptionFragment
import com.gakk.noorlibrary.ui.fragments.subscription.SubscriptionFragment
import com.gakk.noorlibrary.ui.fragments.subscription.SubscriptionOptionListFragment
import com.gakk.noorlibrary.ui.fragments.zakat.ZakatCalculatorFragment
import com.gakk.noorlibrary.ui.fragments.zakat.donation.*
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
        catName: String? = null,
        literature: Literature? = null
    ): Fragment? {

        return when (name) {
            PAGE_QURAN_HOME -> QuranHomeFragment.newInstance()
            PAGE_SURAH_DETAILS -> SurahDetailsFragment.newInstance(
                id!!, detailsActivityCallBack!!, surahList
            )

            PAGE_NEAREST_MOSQUE -> when (Util.checkSub()) {
                true -> {
                    NearestMosqueFragment.newInstance(
                        PAGE_NEAREST_MOSQUE
                    )
                }
                else -> {
                    SubscriptionOptionListFragment.newInstance()
                }
            }

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

            PAGE_HAJJ_PRE_HOME -> HajjPreRegistrationFragment.newInstance()


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


            PAGE_HAJJ_HOME ->
                HajjHomeFragment.newInstance()

            PAGE_SUBSCRIPTION -> SubscriptionFragment.newInstance()
            PAGE_SUBSCRIPTION_OPTION_LIST -> SubscriptionOptionListFragment.newInstance()
            PAGE_SUBSCRIPTION_SSL -> SslSubscriptionFragment.newInstance()
            PAGE_SUBSCRIPTION_GPAY -> SslSubscriptionFragment.newInstance(PAGE_SUBSCRIPTION_GPAY)

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
