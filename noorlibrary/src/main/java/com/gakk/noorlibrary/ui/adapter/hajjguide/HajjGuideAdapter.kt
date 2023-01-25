package com.gakk.noorlibrary.ui.adapter.hajjguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.model.literature.Literature
import com.gakk.noorlibrary.ui.fragments.hajj.hajjguide.ImageChangeListener
import com.gakk.noorlibrary.util.TimeFormtter
import com.gakk.noorlibrary.util.handleClickEvent

internal class HajjGuideAdapter(
    val stepList: List<Literature>,
    val imageChangeListener: ImageChangeListener
) :
    RecyclerView.Adapter<HajjGuideAdapter.ViewHolder>() {

    val ITEM_HEADER = 0
    val ITEM_GUIDE = 1

    inner class ViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
       var view: View = layoutView

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        when (viewType) {
            ITEM_HEADER -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.item_hajj_guide_header,parent,false)

                return ViewHolder(view)

            }
            else -> {
                val  view = LayoutInflater.from(parent.context).inflate(R.layout.item_hajj_guide,parent,false)

                return ViewHolder(view)

            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (holder.itemViewType) {
            ITEM_GUIDE -> {
                val listItem = stepList.get(position - 1)

              //  holder.hajjGuideBinding?.item = listItem
             val title:AppCompatTextView = holder.view.findViewById(R.id.appCompatTextView26)
                title.text = listItem.title
                val text:AppCompatTextView = holder.view.findViewById(R.id.appCompatTextView27)
                text.text = listItem.text
               val tvSerialStep:AppCompatTextView = holder.view.findViewById(R.id.tvSerialStep)
                tvSerialStep?.setText(TimeFormtter.getNumberByLocale(TimeFormtter.getNumber(position)!!))
//                val tvStep:AppCompatTextView = holder.view.findViewById(R.id.tvStep)
               val ivStatus:AppCompatImageView = holder.view.findViewById(R.id.ivStatus)
               val ivItemToogle:AppCompatImageView = holder.view.findViewById(R.id.ivItemToogle)
                val rbDone: AppCompatRadioButton = holder.view.findViewById(R.id.rbDone)
                val rbNotYet:AppCompatRadioButton = holder.view.findViewById(R.id.rbNotYet)
                val clContainerParent:ConstraintLayout = holder.view.findViewById(R.id.clContainerParent)
                val clToogle:ConstraintLayout = holder.view.findViewById(R.id.clToogle)
                if (AppPreference.loadHajjGuideStep(position.toString() + AppPreference.userNumber)) {
                    ivStatus?.visibility = View.VISIBLE
                      rbDone?.isChecked = true
                } else {
                   ivStatus?.visibility = View.GONE
                    rbNotYet?.isChecked = true
                }

             rbDone?.handleClickEvent {
                    imageChangeListener.onRadioBtnClick(position,"Done")
                }

               rbNotYet?.handleClickEvent {
                    imageChangeListener.onRadioBtnClick(position,"NotYet")
                }

                  clContainerParent?.handleClickEvent {
                    when (clToogle?.visibility) {
                        View.VISIBLE -> {
                            listItem.isExand = false
                          clToogle?.visibility = View.GONE
                      ivItemToogle?.setImageResource(R.drawable.ic_expand_more)
                        }
                        View.GONE -> {
                            listItem.isExand = true
                        clToogle?.visibility = View.VISIBLE
                        ivItemToogle?.setImageResource(R.drawable.ic_expand_less)
                        }
                    }
                }

                if (listItem.isExand) {
                 clToogle?.visibility = View.VISIBLE
                } else {
                    clToogle?.visibility = View.GONE
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