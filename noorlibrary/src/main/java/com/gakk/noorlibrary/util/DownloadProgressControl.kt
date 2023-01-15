package com.gakk.noorlibrary.util

import android.content.Context
import android.util.Log
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.gakk.noorlibrary.R
import com.gakk.noorlibrary.base.BaseApplication
import com.gakk.noorlibrary.data.prefs.AppPreference
import com.gakk.noorlibrary.databinding.FragmentLiteratureDetailsBinding
import com.gakk.noorlibrary.databinding.LayoutDownloadableBinding
import com.gakk.noorlibrary.views.PieProgressDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DownloadProgressControl {

    private var layoutDownloadableBindingMap: HashMap<String, ViewBinding>?
    private var fragmentLiteratureDetailsBindingMap: HashMap<String, ViewBinding>?


    init {
        layoutDownloadableBindingMap = HashMap()
        fragmentLiteratureDetailsBindingMap = HashMap()
    }

    fun removeLayoutFromMap(id: String,binding: ViewBinding) {
        when(binding){
            is LayoutDownloadableBinding->{layoutDownloadableBindingMap?.remove(id)}
            is FragmentLiteratureDetailsBinding->{fragmentLiteratureDetailsBindingMap?.remove(id)}
        }

    }

    fun addLayoutToMapAndUpdate(id: String, binding: ViewBinding) {
        addLayoutToMap(id, binding)
        updateDownloadLayoutById(id,binding)

    }

    private fun addLayoutToMap(id: String, binding: ViewBinding) {

        when(binding){
            is LayoutDownloadableBinding->{layoutDownloadableBindingMap?.let {
                it[id] = binding
            }}
            is FragmentLiteratureDetailsBinding->{fragmentLiteratureDetailsBindingMap?.let {
                it[id] = binding
            }}
        }


    }

    fun updateDownloadLayouts() {
        layoutDownloadableBindingMap?.let {
            for ((k, v) in it) {
                updateDownloadLayoutById(k,v)
            }
        }
        fragmentLiteratureDetailsBindingMap?.let {
            for ((k, v) in it) {
                updateDownloadLayoutById(k,v)
            }
        }

    }

    private fun updateDownloadLayoutById(id: String,binding:ViewBinding) {

        when(binding){
            is LayoutDownloadableBinding->{
                layoutDownloadableBindingMap?.let {
                    var binding = it.get(id) as LayoutDownloadableBinding
                    val progress = AppPreference.getDownloadProgress(id)
                    Log.i("Download progress", "$progress")
                    //not downloaded
                    when (progress) {
                        //not downloaded
                        -1 -> {
                            binding.btnDownload.isEnabled = true
                            binding.btnDownload.visibility = VISIBLE
                            binding.imgDownloadProgress.visibility = GONE
                            binding.imgDownloadProgressShadow.visibility = GONE
                        }
                        //downloaded
                        100 -> {
                            binding.btnDownload.isEnabled = false
                            binding.btnDownload.visibility = VISIBLE
                            binding.imgDownloadProgress.visibility = GONE
                            binding.imgDownloadProgressShadow.visibility = GONE

                        }
                        //downloading
                        else -> {
                            binding.btnDownload.isEnabled = false
                            binding.btnDownload.visibility = INVISIBLE
                            binding.imgDownloadProgress.visibility = VISIBLE
                            binding.imgDownloadProgressShadow.visibility = VISIBLE
                        }
                    }
                    setProgress(progress,binding.root.context!!,binding)
                }
            }
            is FragmentLiteratureDetailsBinding->{
                    fragmentLiteratureDetailsBindingMap?.let {
                        var binding = it.get(id) as FragmentLiteratureDetailsBinding
                        val progress = AppPreference.getDownloadProgress(id)
                        Log.i("Download progress", "$progress")
                        //not downloaded
                        when (progress) {
                            //not downloaded
                            -1 -> {
                                binding.btnDownload.isEnabled = true
                                binding.btnDownload.visibility = VISIBLE
                                binding.imgDownloadProgress.visibility = GONE
                                binding.imgDownloadProgressShadow.visibility = GONE
                            }
                            //downloaded
                            100 -> {

                                binding.btnDownload.isEnabled = false
                                binding.btnDownload.visibility = VISIBLE
                                binding.imgDownloadProgress.visibility = GONE
                                binding.imgDownloadProgressShadow.visibility = GONE

                            }
                            //downloading
                            else -> {
                                binding.btnDownload.isEnabled = false
                                binding.btnDownload.visibility = INVISIBLE
                                binding.imgDownloadProgress.visibility = VISIBLE
                                binding.imgDownloadProgressShadow.visibility = VISIBLE
                            }
                        }
                        setProgress(progress, binding.root.context!!, binding)
                    }
            }
        }




    }

    fun setProgress(progress: Int, context: Context,binding: ViewBinding): PieProgressDrawable {
        val pieProgressDrawable = PieProgressDrawable()
        pieProgressDrawable.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        when(binding){
            is LayoutDownloadableBinding->binding?.imgDownloadProgress?.setImageDrawable(pieProgressDrawable)
            is FragmentLiteratureDetailsBinding->binding?.imgDownloadProgress?.setImageDrawable(pieProgressDrawable)
        }
        pieProgressDrawable.setLevel(progress)
        return pieProgressDrawable
    }
}