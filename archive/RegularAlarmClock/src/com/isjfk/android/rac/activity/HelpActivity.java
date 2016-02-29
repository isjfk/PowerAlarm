/**
 * (C) Copyright InfiniteSpace Studio, 2011-2011. All rights reserved.
 */
package com.isjfk.android.rac.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.isjfk.android.rac.R;
import com.isjfk.android.util.AndroidUtil;
import com.isjfk.android.util.JavaUtil;

/**
 * 帮助界面。
 *
 * @author Jimmy F. Klarke
 * @version 1.0, 2012-9-7
 */
public class HelpActivity extends Activity {

    public static final String HELP_ANCHOR_KEY = "helpAnchor";

    private String anchor;

    private TextView titleBarText;
    private WebView webContent;

    /**
     * {@inheritDoc}
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        Intent intent = getIntent();

        anchor = AndroidUtil.getExtra(intent, HELP_ANCHOR_KEY);
        String url = "file:///android_asset/help/index.html";

        Button backButton = (Button) findViewById(R.id.titleBarBackButton);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleBarText = (TextView) findViewById(R.id.titleBarText);
        webContent = (WebView) findViewById(R.id.webContent);

        webContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (JavaUtil.isNotEmpty(anchor)) {
                    String urlWithAnchor = url + "#" + anchor;
                    anchor = null;
                    webContent.loadUrl(urlWithAnchor);
                    return;
                }
                titleBarText.setText(view.getTitle());
            }
        });
        webContent.loadUrl(url);
    }

}
