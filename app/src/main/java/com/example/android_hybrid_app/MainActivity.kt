package com.example.android_hybrid_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

//    private var bridge: bridge? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var myWebView: WebView = findViewById<WebView>(R.id.mainWebView)
        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()
        myWebView.settings.javaScriptEnabled = true

        myWebView.addJavascriptInterface(Bridge(this), "Android" )

        myWebView.loadUrl("http://172.21.128.1:3000")
    }

    fun openUrl(url: String) {
        val webView = findViewById<WebView>(R.id.mainWebView)
        webView.loadUrl( url )
    }
}
//
class Bridge(
    private var main: MainActivity
) {
    fun notifyEvent( eventName: String, param: String? = null ) {
        val paramEsc = param?.replace( "\\", "\\\\" )

        val notifyUrl = if( param != null ) "javascript:smallbeeAndroidEvent('$eventName', '$paramEsc')"
        else "javascript:smallbeeAndroidEvent('$eventName')"

        CoroutineScope( Dispatchers.Main ).launch {
            main.openUrl(notifyUrl)
        }
    }

    fun notifyCallback( callback: String, isFinished: Boolean, param: String, error: String? = null ) {
        val paramEsc = param.replace( "\\", "\\\\" )
        val errorEsc = error?.replace( "\\", "\\\\" )

        val callbackUrl = if( error == null ) "javascript:$callback( $isFinished, '$paramEsc')"
        else "javascript:$callback( $isFinished, '$paramEsc', '$errorEsc')"

        CoroutineScope( Dispatchers.Main ).launch {
            main.openUrl(callbackUrl)
        }
    }
    @JavascriptInterface
    fun helloBridge(): String {
        return "i am helloBridge"
    }

    @JavascriptInterface
    fun callbackTest_async( msg: String, callback: String ) {
        notifyCallback( callback, false, "123123" )
        notifyCallback( callback, true, "123123456" )
    }
}