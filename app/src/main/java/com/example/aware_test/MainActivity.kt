package com.example.aware_test

//import android.support.v7.app.AppCompatActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import com.awareframework.android.core.db.Engine
import com.awareframework.android.sensor.aware_appusage.AppusageSensor
import com.awareframework.android.sensor.aware_appusage.model.AppusageData
import com.project.core.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!checkReadStatsPermission()){
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }


        // To start the service.
        AppusageSensor.start(applicationContext, AppusageSensor.Config().apply {

            interval = 1000 //1min
            usageAppDisplaynames = mutableListOf("android")
            usageAppEventTypes = mutableListOf(UsageEvents.Event.SCREEN_NON_INTERACTIVE)

            awareUsageAppNotificationTitle = "studying now"
            awareUsageAppNotificationDescription = "App usage history is being retrieved."
            awareUsageAppNoticationId = "appusage_notification"

            dbType = Engine.DatabaseType.ROOM

            sensorObserver = object : AppusageSensor.Observer {
                override fun onDataChanged(datas: MutableList<AppusageData>?) {
                    println("ondatachanged in mainActivity $datas")
                    //ここをいじる
                }
            }
        })
    }

    private fun checkReadStatsPermission():Boolean{
        var aom: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        var mode:Int = aom.checkOp(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        if(mode == AppOpsManager.MODE_DEFAULT){
            return checkPermission("android.permission.PACKAGE_USAGE_STATS", android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}