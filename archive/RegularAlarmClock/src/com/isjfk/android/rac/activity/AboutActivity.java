/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.rac.common.RACContext;

/**
 * “关于”界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2011-10-30
 */
public class AboutActivity extends Activity {

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        String version = RACContext.getVersion();
        if (RACContext.isAdVersion()) {
            version += " (AD)";
        }
        ((TextView) findViewById(R.id.version)).setText(version);
        ((TextView) findViewById(R.id.build)).setText(RACContext.getBuild());
        ((TextView) findViewById(R.id.author)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.iconLicense)).setMovementMethod(LinkMovementMethod.getInstance());
    }

}
