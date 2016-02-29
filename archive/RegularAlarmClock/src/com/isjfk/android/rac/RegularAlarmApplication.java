/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.isjfk.android.rac.common.RACContext;

/**
 * 规则闹钟主程序。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-11-7
 */
@ReportsCrashes(
        formKey = "",
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        reportType = org.acra.sender.HttpSender.Type.JSON,
        formUri = "http://acra.isjfk.com:5984/acra-poweralarm/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "poweralarm",
        formUriBasicAuthPassword = "pow3rAla$m")
public class RegularAlarmApplication extends Application {

    @Override
    public void onCreate() {
        RACContext.init(this);

        // initialize ACRA only in release mode
        if (RACContext.isReleaseMode()) {
            ACRA.init(this);
        }

        super.onCreate();
    }

}
