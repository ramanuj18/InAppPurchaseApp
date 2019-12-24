package com.example.inapppurchaseapp.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.inapppurchaseapp.util.Purchase;

/**
 * created by Ramanuj Kesharawani on 24/12/19
 */
public class AppUtility {

    public static void dialogForIAP(final Context context, final String productId, final OnPurchaseFinished purchaseFinished) {
        final SubscriptionUtil subscriptionUtil = new SubscriptionUtil(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you want to purchase this item");
        builder.setTitle("buy");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                subscriptionUtil.initSubscription(productId, new SubscriptionUtil.SubscriptionFinishedListener() {
                    @Override
                    public void onSuccess(Purchase info) {
                        dialogInterface.dismiss();
                        purchaseFinished.onPurchaseFinished(info);
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface OnPurchaseFinished {
        void onPurchaseFinished(Purchase purchase);
    }
}
