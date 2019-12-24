package com.example.inapppurchaseapp.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import com.example.inapppurchaseapp.util.IabHelper;
import com.example.inapppurchaseapp.util.IabResult;
import com.example.inapppurchaseapp.util.Inventory;
import com.example.inapppurchaseapp.util.Purchase;
import com.example.inapppurchaseapp.util.SkuDetails;

import java.util.ArrayList;

public class SubscriptionUtil {
    public static final int REQUEST_CODE=1001;
    public static String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmuPW3r9qAFzlqmCTR8WMUcTJCm/erAr+xK2WBi7yPhAusehZwpKgVdUdhjzast5rtoTciijSW3aBGKKdFGN+WTWqeJM5KOZouUszPP2hnL1W2ETahXceNEmaSePM66MlX48FOxGYOCfCm7n2b34ART+GTpGrIedHG2/Rg/06F3SGPDkxyvzg3mbAhzPpoeffF+t8gdFGzBVLTzCHJnqwh0zeIBNoOnuO6dbCG0013ZbuvdVEZ/p9Q8PV+FYS+YnLK/2EC8e0iyFP7+KBD0NQcL+eXS/7E0cTlrQDXImwcuOjojIdBUMJz/RV4NxkasZHnbuQO9uwuIMqHp9qFV4GxwIDAQAB";
    private static IabHelper iabHelper;
    private static Context context;
    //IInAppBillingService inAppBillingService;
    private SubscriptionUtil(){
        //No Instance
    }
    public SubscriptionUtil(Context context){
        this.context = context;
        iabHelper = new IabHelper(context, base64EncodedPublicKey);
        iabHelper.enableDebugLogging(true, "TEST");
        setup();
    }
    private void setup() {
        if (iabHelper != null) {
            iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (result.isFailure()) {
                        Log.d("TEST", "Problem setting up In-app Billing: " + result);
                        dispose();
                    }
                }
            });
        }
    }

    public void initSubscription(final String subscriptionType,
                                 SubscriptionFinishedListener subscriptionFinishedListener) {
        initSubscriptionWithExtras(subscriptionType, subscriptionFinishedListener, "");
    }

    public void initSubscriptionWithExtras(final String subscriptionType,
                                           final SubscriptionFinishedListener subscriptionFinishedListener,
                                           String payload) {

        if (iabHelper != null) {
            try {
                iabHelper.flagEndAsync();           //ending previous purchase process.
                iabHelper.launchSubscriptionPurchaseFlow((Activity) context,
                        subscriptionType,
                        REQUEST_CODE,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (result.isFailure()) {
                                    Log.e("TEST", "Error purchasing: " + result);
                                    return;
                                }
                                if (info.getSku().equals(subscriptionType)) {
                                    //try {
                                        //final JSONObject json = new JSONObject(info.getOriginalJson());
                                       // boolean autoRenew=json.getBoolean("autoRenewing");

                                    //}catch (JSONException e){
                                   //    e.printStackTrace();
                                   // }
                                    //info.getOriginalJson()
                                    if(subscriptionFinishedListener != null){
                                      // inAppBillingService.getBuyIntent()
                                        info.getAutoRenewing();
                                        info.setAutoRenewing(false);
                                        subscriptionFinishedListener.onSuccess(info);
                                    }
                                    Log.e("TEST", "Thank you for upgrading to premium!");
                                }
                            }
                        },
                        payload
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            //In case you get below error:
            //`Can't start async operation (refresh inventory) because another async operation (launchPurchaseFlow) is in progress.`
            //Include this line of code to end proccess after purchase
            //iabHelper.flagEndAsync();
        }
    }

    public void getSkuDetailsList(
            final ArrayList<String> skuIdsList,
            final SubscriptionInventoryListener subscriptionInventoryListener
    ) {
        if(iabHelper!=null){
            try{
                iabHelper.queryInventoryAsync(true, skuIdsList, new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        if (result.isFailure()) {
                            Log.d("TEST", "Problem querying inventory: " + result);
                            dispose();
                            return;
                        }
                        ArrayList<SkuDetails> skuDetailsList = new ArrayList<>();
                        for (String skuId : skuIdsList) {
                            SkuDetails sku = inv.getSkuDetails(skuId);
                            if (sku.getSku().equals(skuId)) {
                                skuDetailsList.add(sku);
                                sku.getPrice();
                            }
                        }
                        if (subscriptionInventoryListener != null) {
                            subscriptionInventoryListener.onQueryInventoryFinished(skuDetailsList);
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void dispose() {

        if (iabHelper != null) {
            try{
                iabHelper.dispose();
            }catch (Exception e){
                e.printStackTrace();
            }
            iabHelper = null;
        }
    }

    public static IabHelper getIabHelper() {
        if (iabHelper == null) {
            iabHelper = new IabHelper(context, base64EncodedPublicKey);
        }
        return iabHelper;
    }
    public interface SubscriptionInventoryListener {
        void onQueryInventoryFinished(ArrayList<SkuDetails> skuList);
    }

    public interface SubscriptionFinishedListener{
        void onSuccess(Purchase purchase);
    }
}
