package com.gakk.noorlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.databinding.LayoutTasbihCountItemBinding
import com.gakk.noorlibrary.ui.fragments.CountControl
import com.gakk.noorlibrary.util.handleClickEvent

class TasbihAdapter(
    val duas: Array<String>,
    countControl: CountControl,
    viewClickedIndex: Int,
    buttonClickedIndex: Int
) :
    RecyclerView.Adapter<TasbihAdapter.ViewHolder>() {
    private var viewClickedIndex: Int
    private var buttonClickedIndex: Int
    private val mcountControl: CountControl
    var arabicNames = arrayOf(
        "سُبْحَانَ ٱللَّٰهِ",
        "ٱلْحَمْدُ لِلَّٰه",
        "بسم الله",
        "الله أكبر",
        "أستغفر الله",
        "الله",
        "لا إله إلا الله",
        "لا حول ولا قوة إلا بالله"
    )

    init {
        mcountControl = countControl
        this.viewClickedIndex = viewClickedIndex
        this.buttonClickedIndex = buttonClickedIndex
    }


    inner class ViewHolder(binding: LayoutTasbihCountItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: LayoutTasbihCountItemBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutTasbihCountItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_tasbih_count_item,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val x = duas[position]

        holder.binding?.rlThirtyThree?.background = ContextCompat.getDrawable(
            holder.binding?.rlThirtyThree?.context!!,
            R.drawable.ic_shape_tahbih_disable
        )
        holder.binding?.rlThirtyFour?.background = ContextCompat.getDrawable(
            holder.binding?.rlThirtyThree?.context!!,
            R.drawable.ic_shape_tahbih_disable
        )
        holder.binding?.rlNinetyNine?.background = ContextCompat.getDrawable(
            holder.binding?.rlNinetyNine?.context!!,
            R.drawable.ic_shape_tahbih_disable
        )
        when (viewClickedIndex == position) {
            true -> {
                holder.binding?.layoutParent?.setBackgroundResource(R.drawable.border_rounded_green)
                when (buttonClickedIndex) {
                    0 -> {
                        holder.binding?.rlThirtyThree?.background = ContextCompat.getDrawable(
                            holder.binding?.rlThirtyFour?.context!!,
                            R.drawable.ic_shape_tahbih_enable
                        )
                    }
                    1 -> {
                        holder.binding?.rlThirtyFour?.background = ContextCompat.getDrawable(
                            holder.binding?.rlThirtyFour?.context!!,
                            R.drawable.ic_shape_tahbih_enable
                        )
                    }
                    2 -> {
                        holder.binding?.rlNinetyNine?.background = ContextCompat.getDrawable(
                            holder.binding?.rlThirtyFour?.context!!,
                            R.drawable.ic_shape_tahbih_enable
                        )
                    }
                }
            }
            else -> {
                holder.binding?.layoutParent?.setBackgroundResource(R.drawable.rounded_white)
            }
        }

        holder.binding?.tvTahbihItem?.setText(x)
        holder.binding?.tvArabic?.setText(arabicNames[position])


        holder.binding?.rlThirtyThree?.handleClickEvent {
            viewClickedIndex = holder.adapterPosition
            buttonClickedIndex = 0
            mcountControl.getUserCount(33)
            mcountControl.getSelectedItem(viewClickedIndex.toString())
            holder.binding?.rlThirtyThree?.background = ContextCompat.getDrawable(
                holder.binding?.rlThirtyFour?.context!!,
                R.drawable.ic_shape_tahbih_enable
            )
            holder.binding?.rlThirtyFour?.background = ContextCompat.getDrawable(
                holder.binding?.rlThirtyThree?.context!!,
                R.drawable.ic_shape_tahbih_disable
            )
            holder.binding?.rlNinetyNine?.background = ContextCompat.getDrawable(
                holder.binding?.rlNinetyNine?.context!!,
                R.drawable.ic_shape_tahbih_disable
            )

            notifyDataSetChanged()
        }

        holder.binding?.rlThirtyFour?.handleClickEvent {
            viewClickedIndex = holder.adapterPosition
            buttonClickedIndex = 1
            mcountControl.getUserCount(34)
            mcountControl.getSelectedItem(viewClickedIndex.toString())
            holder.binding?.rlThirtyFour?.background = ContextCompat.getDrawable(
                holder.binding?.rlThirtyFour?.context!!,
                R.drawable.ic_shape_tahbih_enable
            )
            holder.binding?.rlThirtyThree?.background = ContextCompat.getDrawable(
                holder.binding?.rlThirtyThree?.context!!,
                R.drawable.ic_shape_tahbih_disable
            )
            holder.binding?.rlNinetyNine?.background = ContextCompat.getDrawable(
                holder.binding?.rlNinetyNine?.context!!,
                R.drawable.ic_shape_tahbih_disable
            )
            notifyDataSetChanged()
        }

        holder.binding?.rlNinetyNine?.handleClickEvent {
            viewClickedIndex = holder.adapterPosition
            buttonClickedIndex = 2
            mcountControl.getUserCount(99)
            mcountControl.getSelectedItem(viewClickedIndex.toString())
            holder.binding?.rlNinetyNine?.background = ContextCompat.getDrawable(
                holder.binding?.rlThirtyFour?.context!!,
                R.drawable.ic_shape_tahbih_enable
            )
            holder.binding?.rlThirtyThree?.background = ContextCompat.getDrawable(
                holder.binding?.rlThirtyThree?.context!!,
                R.drawable.ic_shape_tahbih_disable
            )
            holder.binding?.rlThirtyFour?.background = ContextCompat.getDrawable(
                holder.binding?.rlNinetyNine?.context!!,
                R.drawable.ic_shape_tahbih_disable
            )

            notifyDataSetChanged()
        }
    }

    fun getViewClickedIndex() = viewClickedIndex
    fun getButtonClickedIndex() = buttonClickedIndex

    override fun getItemCount(): Int {
        return duas.count()
    }

}