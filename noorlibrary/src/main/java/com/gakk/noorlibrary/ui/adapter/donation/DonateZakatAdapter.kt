package com.gakk.noorlibrary.ui.adapter.donation

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.callbacks.DetailsCallBack
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.activity.DetailsActivity
import com.gakk.noorlibrary.util.*

internal class DonateZakatAdapter(
    val organizationList: MutableList<Literature>,
    val literature: Literature,
    detailsCallBack: DetailsCallBack
) :
    RecyclerView.Adapter<DonateZakatAdapter.DonateZakatViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_ORGANIZATIONS = 1
    val mDetailsCallBack = detailsCallBack

    inner class DonateZakatViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var view: View = layoutView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateZakatViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_donate_zakat, parent, false)

                return DonateZakatViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_item_charity_organizations, parent, false)
                return DonateZakatViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: DonateZakatViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_HEADER -> {
                val details = holder.view.findViewById<AppCompatTextView>(R.id.tvDetails)
                details.text = literature.text
                val constraintLayout2 =
                    holder.view.findViewById<ConstraintLayout>(R.id.constraintLayout2)
                constraintLayout2?.handleClickEvent {
                    Intent(constraintLayout2?.context, DetailsActivity::class.java).apply {
                        this.putExtra(PAGE_NAME, PAGE_JAKAT_NEW_CALCULATION)
                        constraintLayout2?.context?.startActivity(this)
                    }
                }
            }

            ITEM_ORGANIZATIONS -> {
                val listItem = organizationList.get(position - 1)
                val progressBar = holder.view.findViewById<ProgressBar>(R.id.progressBar)
                progressBar.visibility = View.GONE
                val clItem = holder.view.findViewById<ConstraintLayout>(R.id.clItem)
                val textViewNormal9 =
                    holder.view.findViewById<AppCompatTextView>(R.id.textViewNormal9)
                val textViewNormal20 =
                    holder.view.findViewById<AppCompatTextView>(R.id.textViewNormal20)
                val appCompatImageView10 =
                    holder.view.findViewById<AppCompatImageView>(R.id.appCompatImageView10)

                Glide.with(appCompatImageView10.context)
                    .load(listItem.fullImageUrl?.replace("<size>", "1280"))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                    })
                    .error(R.drawable.place_holder_1_1_ratio)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(appCompatImageView10)


                textViewNormal9.text = listItem.subcategoryName
                textViewNormal20.text = listItem.textInArabic

                clItem?.handleClickEvent {
                    val fragment = FragmentProvider.getFragmentByName(
                        PAGE_DONATION_FORM,
                        detailsActivityCallBack = mDetailsCallBack,
                        literature = listItem
                    )
                    mDetailsCallBack.addFragmentToStackAndShow(fragment!!)
                }
            }

            else -> {

            }
        }

    }

    override fun getItemCount(): Int {
        return 1 + organizationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_HEADER
            }

            else -> {
                ITEM_ORGANIZATIONS
            }
        }
    }
}