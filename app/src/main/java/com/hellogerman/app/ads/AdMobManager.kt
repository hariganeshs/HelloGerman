package com.hellogerman.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobManager {
    companion object {
        private const val TAG = "AdMobManager"
        
        // Ad Unit IDs (Production)
        const val BANNER_AD_1 = "ca-app-pub-2722920301958819/2577129002"
        const val BANNER_AD_2 = "ca-app-pub-2722920301958819/6780081985"
        const val INTERSTITIAL_AD = "ca-app-pub-2722920301958819/8950965667"

        // Test ad unit IDs (commented out)
        // const val BANNER_AD_1 = "ca-app-pub-3940256099942544/6300978111" // Test ad
        // const val BANNER_AD_2 = "ca-app-pub-3940256099942544/6300978111" // Test ad
        // const val INTERSTITIAL_AD = "ca-app-pub-3940256099942544/1033173712" // Test ad
        
        private var interstitialAd: InterstitialAd? = null
        
        fun initialize(context: Context) {
            MobileAds.initialize(context) { initializationStatus ->
                Log.d(TAG, "AdMob initialization status: $initializationStatus")
            }
        }
        
        fun loadInterstitialAd(context: Context) {
            val adRequest = AdRequest.Builder().build()
            
            InterstitialAd.load(
                context,
                INTERSTITIAL_AD,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Interstitial ad loaded successfully")
                        interstitialAd = ad
                        
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Interstitial ad was dismissed")
                                interstitialAd = null
                                // Load the next interstitial ad
                                loadInterstitialAd(context)
                            }
                            
                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                                interstitialAd = null
                            }
                            
                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "Interstitial ad showed full screen content")
                            }
                        }
                    }
                    
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                        interstitialAd = null
                    }
                }
            )
        }
        
        fun showInterstitialAd(activity: Activity) {
            interstitialAd?.let { ad ->
                ad.show(activity)
            } ?: run {
                Log.d(TAG, "Interstitial ad not ready yet")
                // Load a new ad for next time
                loadInterstitialAd(activity)
            }
        }
        
        fun isInterstitialAdReady(): Boolean {
            return interstitialAd != null
        }
    }
}

@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var adLoaded by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
        }
    }
    
    DisposableEffect(adUnitId) {
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("AdMobManager", "Banner ad loaded successfully: $adUnitId")
                adLoaded = true
                adError = null
            }
            
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e("AdMobManager", "Banner ad failed to load: ${loadAdError.message}")
                adError = loadAdError.message
                adLoaded = false
            }
            
            override fun onAdOpened() {
                Log.d("AdMobManager", "Banner ad opened")
            }
            
            override fun onAdClicked() {
                Log.d("AdMobManager", "Banner ad clicked")
            }
        }

        adView.loadAd(AdRequest.Builder().build())
        Log.d("AdMobManager", "Loading banner ad with ID: $adUnitId")

        onDispose {
            adView.destroy()
        }
    }
    
    AndroidView(
        modifier = modifier,
        factory = { adView },
        update = { adView ->
            // Update if needed
        }
    )
    
    // Show loading or error state overlay
    if (!adLoaded && adError == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
        }
    } else if (adError != null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ad: $adError",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}

@Composable
fun BannerAd1(modifier: Modifier = Modifier) {
    BannerAd(AdMobManager.BANNER_AD_1, modifier)
}

@Composable
fun BannerAd2(modifier: Modifier = Modifier) {
    BannerAd(AdMobManager.BANNER_AD_2, modifier)
}
