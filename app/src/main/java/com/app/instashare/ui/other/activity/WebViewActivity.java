package com.app.instashare.ui.other.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.instashare.R;
import com.app.instashare.custom.AudioBar;

/**
 * Created by Pitisflow on 16/5/18.
 */

public class WebViewActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "URL";


    public static Intent newInstance(Context context, String url)
    {
        if (!url.contains("http")) url = "https://" + url;

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }


    private String url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_URL))
        {
            url = getIntent().getStringExtra(EXTRA_URL);
        }

        bindWebView();
        bindToolbarView();


    }



    private void bindWebView()
    {
        ProgressDialog progDailog = ProgressDialog.show(this,
                getString(R.string.webview_loading),
                getString(R.string.webview_wait), true);
        progDailog.setCancelable(false);


        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });

        webView.loadUrl(url);
    }


    private void bindToolbarView()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(url);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
