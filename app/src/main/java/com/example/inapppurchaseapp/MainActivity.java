package com.example.inapppurchaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.inapppurchaseapp.util.Purchase;
import com.example.inapppurchaseapp.utility.AppUtility;
import com.example.inapppurchaseapp.utility.SubscriptionUtil;

public class MainActivity extends AppCompatActivity implements AppUtility.OnPurchaseFinished {
    Button buttonBuyNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonBuyNow=findViewById(R.id.btn_buy_now);
        buttonBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtility.dialogForIAP(MainActivity.this,"",MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!SubscriptionUtil.getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPurchaseFinished(Purchase purchase) {

    }
}
