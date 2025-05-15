package com.example.webbrowser;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText addressBar;
    private TabLayout tabLayout;
    private Button goButton, newTabButton, closeTabButton;
    private HashMap<String, WebView> webViewMap = new HashMap<>();
    private WebView currentWebView;

    private static final String DEFAULT_URL = "https://www.google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressBar = findViewById(R.id.address_bar);
        tabLayout = findViewById(R.id.tab_layout);
        goButton = findViewById(R.id.go_button);
        newTabButton = findViewById(R.id.new_tab_button);
        closeTabButton = findViewById(R.id.close_tab_button);

        addNewTab("Tab 1");
        tabLayout.getTabAt(0).select();

        goButton.setOnClickListener(view -> {
            String url = addressBar.getText().toString();
            if (currentWebView != null) {
                loadWebPage(currentWebView, url);
            }
        });

        newTabButton.setOnClickListener(view -> addNewTab("New Tab"));

        closeTabButton.setOnClickListener(view -> {
            TabLayout.Tab selectedTab = tabLayout.getSelectedTabPosition() >= 0
                    ? tabLayout.getTabAt(tabLayout.getSelectedTabPosition())
                    : null;
            if (selectedTab != null) {
                closeTab(selectedTab);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabId = tab.getTag().toString();
                switchToTab(tabId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void addNewTab(String initialTabName) {
        TabLayout.Tab newTab = tabLayout.newTab().setText(initialTabName);
        String tabId = "Tab " + (tabLayout.getTabCount() + 1);
        newTab.setTag(tabId);
        tabLayout.addTab(newTab);

        WebView newWebView = new WebView(this);
        configureWebView(newWebView);
        newWebView.loadUrl(DEFAULT_URL);
        webViewMap.put(tabId, newWebView);

        ((LinearLayout) findViewById(R.id.web_view_container)).addView(newWebView);
        newWebView.setVisibility(View.GONE);

        tabLayout.selectTab(newTab);
        switchToTab(tabId);
    }

    private void switchToTab(String tabId) {
        if (currentWebView != null) {
            currentWebView.setVisibility(View.GONE);
        }

        currentWebView = webViewMap.get(tabId);
        if (currentWebView != null) {
            currentWebView.setVisibility(View.VISIBLE);
            addressBar.setText(currentWebView.getUrl());
        }
    }

    private void closeTab(TabLayout.Tab tab) {
        int position = tab.getPosition();
        String tabId = tab.getTag().toString();

        webViewMap.remove(tabId);
        ((LinearLayout) findViewById(R.id.web_view_container)).removeView(currentWebView);

        tabLayout.removeTab(tab);

        if (tabLayout.getTabCount() > 0) {
            tabLayout.selectTab(tabLayout.getTabAt(Math.max(0, position - 1)));
        }
    }

    private void configureWebView(WebView webView) {
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    String tabId = tab.getTag().toString();
                    if (webView.equals(webViewMap.get(tabId))) {
                        tab.setText(title);
                        break;
                    }
                }
            }
        });
    }

    private void loadWebPage(WebView webView, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        webView.loadUrl(url);
    }
}
