package com.gakk.noorlibrary.ui.adapter.hajjguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.ItemHajjGuideBinding
import com.gakk.noorlibrary.databinding.ItemHajjGuideHeaderBinding
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.hajj.hajjguide.ImageChangeListener
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.handleClickEvent

class HajjGuideAdapter(
    val stepList: List<Literature>,
    val imageChangeListener: ImageChangeListener
) :
    RecyclerView.Adapter<HajjGuideAdapter.ViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_GUIDE = 1

    inner class ViewHolder : RecyclerView.ViewHolder {

        var bindingGuideHeader: ItemHajjGuideHeaderBinding? = null

        constructor(itemView: ItemHajjGuideHeaderBinding) : super(itemView.root) {
            bindingGuideHeader = itemView
        }

        var hajjGuideBinding: ItemHajjGuideBinding? = null

        constructor(itemView: ItemHajjGuideBinding) : super(itemView.root) {
            hajjGuideBinding = itemView
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val binding: ItemHajjGuideHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_hajj_guide_header,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
            else -> {
                val binding: ItemHajjGuideBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_hajj_guide,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_GUIDE -> {
                val listItem = stepList.get(position - 1)
                holder.hajjGuideBinding?.item = listItem

                holder.hajjGuideBinding?.tvSerialStep?.setText(TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(position)!!))


                if (AppPreference.loadHajjGuideStep(position.toString() + AppPreference.userNumber)) {
                    holder.hajjGuideBinding?.ivStatus?.visibility = View.VISIBLE
                    holder.hajjGuideBinding?.rbDone?.isChecked = true
                } else {
                    holder.hajjGuideBinding?.ivStatus?.visibility = View.GONE
                    holder.hajjGuideBinding?.rbNotYet?.isChecked = true
                }

                holder.hajjGuideBinding?.rbDone?.handleClickEvent {
                    imageChangeListener.onRadioBtnClick(position,"Done")
                }

                holder.hajjGuideBinding?.rbNotYet?.handleClickEvent {
                    imageChangeListener.onRadioBtnClick(position,"NotYet")
                }

                holder.hajjGuideBinding?.clContainerParent?.handleClickEvent {
                    when (holder.hajjGuideBinding?.clToogle?.visibility) {
                        View.VISIBLE -> {
                            listItem.isExand = false
                            holder.hajjGuideBinding?.clToogle?.visibility = View.GONE
                            holder.hajjGuideBinding?.ivItemToogle?.setImageResource(R.drawable.ic_expand_more)
                        }
                        View.GONE -> {
                            listItem.isExand = true
                            holder.hajjGuideBinding?.clToogle?.visibility = View.VISIBLE
                            holder.hajjGuideBinding?.ivItemToogle?.setImageResource(R.drawable.ic_expand_less)
                        }
                    }
                }

                if (listItem.isExand) {
                    holder.hajjGuideBinding?.clToogle?.visibility = View.VISIBLE
                } else {
                    holder.hajjGuideBinding?.clToogle?.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return stepList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_HEADER
            }

            else -> {
                ITEM_GUIDE
            }
        }
    }
}