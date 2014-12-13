package in.sahildave.gazetti.news_activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import in.sahildave.gazetti.R;

/**
 * Created by sahil on 5/10/14.
 */
public class WebViewFragment extends Fragment {

    private WebView mWebView;
    private String mURL;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.article_webview_progress);
        mWebView = (WebView) view.findViewById(R.id.article_webview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);

        if(getArguments()!=null){
            mURL = getArguments().getString("URL");
        }

        if(mURL!=null){
                mWebView.loadUrl(mURL);
        }

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressBar();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgressBar();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                setProgressBar(newProgress);
            }
        });
    }

    private void setProgressBar(int newProgress) {
        mProgressBar.setProgress(newProgress);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        mWebView.getSettings().setBuiltInZoomControls(false);
        super.onDestroy();
        mWebView.setVisibility(View.GONE);
        mWebView.destroy();
    }
}
