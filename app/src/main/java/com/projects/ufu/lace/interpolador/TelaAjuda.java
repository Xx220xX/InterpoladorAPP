package com.projects.ufu.lace.interpolador;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.SystemClock.uptimeMillis;

public class TelaAjuda extends Activity {
    Button back;
    WebView tela;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_ajuda);
        back = findViewById(R.id.help_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tela = findViewById(R.id.help_webView);
        configurarWebView();

    }

    private void configurarWebView() {
        final String url = "https://google.com.br";
        tela.getSettings().setJavaScriptEnabled(true);
        tela.setVerticalScrollBarEnabled(true);
        tela.setHorizontalScrollBarEnabled(true);
        tela.getSettings().setBuiltInZoomControls(true);
        tela.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(url);
                return false;
            }
        });
        String fileName = "example.html";
        tela.loadUrl("file:///android_asset/" + fileName);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
