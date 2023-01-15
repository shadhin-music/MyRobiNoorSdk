package com.gakk.noorlibrary.job

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.gakk.noorlibrary.jobService.RozaAlarmJobService
import com.gakk.noorlibrary.util.JOB_ROZA_ALARM_CONTROL

class RozaAlarmControlJob(context: Context) {

    private var context: Context = context

    fun startJob(){

        val componentName = ComponentName(context, RozaAlarmJobService::class.java)
        val jobInfo = JobInfo.Builder(JOB_ROZA_ALARM_CONTROL, componentName)
            .setPersisted(true)
            .setPeriodic((1 * 15 * 60 * 1000).toLong())//every 15 mins
            .build()

        val jobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        cancelJob(jobScheduler)
        jobScheduler.schedule(jobInfo)
    }
    private fun cancelJob(jobScheduler: JobScheduler) {
        try {
            jobScheduler.cancel(JOB_ROZA_ALARM_CONTROL)
        } catch (e: Exception) {
        }
    }
}