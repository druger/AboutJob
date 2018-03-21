package com.druger.aboutwork.fragments


import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.druger.aboutwork.Const.Bundles.SITE_URL

import com.druger.aboutwork.R


/**
 * A simple [Fragment] subclass.
 */
class WebviewFragment : BaseFragment() {

    private lateinit var webView: WebView

    companion object {
        fun getInstance(url: String): WebviewFragment {
            val webviewFragment = WebviewFragment()
            val bundle = Bundle()
            bundle.putString(SITE_URL, url);
            webviewFragment.arguments = bundle
            return webviewFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_webview, container, false)
        setupUI()
        setupWebView()
        return rootView
    }

    private fun setupUI() {
        webView = bindView(R.id.webview)
    }

    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                onReceivedError(view, error.errorCode, error.description.toString(), request.url.toString())
            }
        }
        webView.loadUrl(arguments.getString(SITE_URL))
    }
}
