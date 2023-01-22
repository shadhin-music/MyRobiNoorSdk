package com.gakk.noorlibrary.jobService

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.gakk.noorlibrary.data.rest.api.RestRepository
import com.gakk.noorlibrary.roza.IfterAndSehriTimePopulationControl
import com.gakk.noorlibrary.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RozaAlarmJobService : JobService() {
    private var context: Context? = null
    private lateinit var scope: CoroutineScope

    /**
     * starts job in background upon getting called
     * @param params(JobParameters instance)
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartJob(params: JobParameters?): Boolean {

        if (!this::scope.isInitialized) {
            scope = CoroutineScope(Job())
        }

        scope.launch {
            context = this@RozaAlarmJobService
            populateAndUpdateIfterAndSehriTimes()

            jobFinished(params, false)
        }


        return true
    }

    /**
     * stops the job upon getting called
     * @param params(JobParameters instance)
     * @return true(boolean)
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    suspend fun populateAndUpdateIfterAndSehriTimes() {
        IfterAndSehriTimePopulationControl.populateAndSaveUpdatedIfterSehriTimes()
    }


    override fun onDestroy() {
        if (this::scope.isInitialized) {
            scope.cancel()
        }

        super.onDestroy()
    }
}