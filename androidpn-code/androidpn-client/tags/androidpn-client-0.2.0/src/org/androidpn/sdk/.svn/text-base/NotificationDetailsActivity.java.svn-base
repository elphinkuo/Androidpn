/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationDetailsActivity extends Activity {

    private static final String LOGTAG = NotificationDetailsActivity.class
            .getName();

    private String callbackActivityPackageName;

    private String callbackActivityClassName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sdkPreferences = this.getSharedPreferences(
                Constants.SDK_PREFERENCES, Context.MODE_PRIVATE);
        callbackActivityPackageName = sdkPreferences.getString(
                Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
        callbackActivityClassName = sdkPreferences.getString(
                Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");

        Intent intent = getIntent();
        String notificationId = intent.getStringExtra("NOTIFICATION_ID");
        String notificationApiKey = intent
                .getStringExtra("NOTIFICATION_API_KEY");
        String notificationTitle = intent.getStringExtra("NOTIFICATION_TITLE");
        String notificationMessage = intent
                .getStringExtra("NOTIFICATION_MESSAGE");
        String notificationTicker = intent
                .getStringExtra("NOTIFICATION_TICKER");
        String notificationUrl = intent.getStringExtra("NOTIFICATION_URL");

        Log.d(LOGTAG, "notificationId=" + notificationId);
        Log.d(LOGTAG, "notificationApiKey=" + notificationApiKey);
        Log.d(LOGTAG, "notificationTitle=" + notificationTitle);
        Log.d(LOGTAG, "notificationMessage=" + notificationMessage);
        Log.d(LOGTAG, "notificationTicker=" + notificationTicker);
        Log.d(LOGTAG, "notificationUrl=" + notificationUrl);

        //        Display display = getWindowManager().getDefaultDisplay();
        //        View rootView;
        //        if (display.getWidth() > display.getHeight()) {
        //            rootView = null;
        //        } else {
        //            rootView = null;
        //        }

        View rootView = createView(notificationTitle, notificationMessage);
        setContentView(rootView);
    }

    private View createView(String title, String message) {

        final Context context = NotificationDetailsActivity.this;

        LinearLayout linearLayout = new LinearLayout(this);
        // linearLayout.setBackgroundResource(0x106000b);
        linearLayout.setBackgroundColor(0xffeeeeee);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(5, 5, 5, 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        linearLayout.setLayoutParams(layoutParams);

        TextView textTitle = new TextView(this);
        textTitle.setText(title);
        textTitle.setTextSize(16);
        textTitle.setTextColor(0xff000000);
        textTitle.setGravity(Gravity.CENTER);

        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 30, 30, 0);
        textTitle.setLayoutParams(layoutParams);
        linearLayout.addView(textTitle);

        TextView textDetails = new TextView(this);
        textDetails.setText(message);
        textDetails.setTextSize(12);
        textDetails.setTextColor(0xff333333);
        textDetails.setGravity(Gravity.CENTER);

        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 10, 30, 20);
        textDetails.setLayoutParams(layoutParams);
        linearLayout.addView(textDetails);

        Button closeButton = new Button(this);
        closeButton.setText("Close");
        closeButton.setWidth(100);
        //closeButton.setLayoutParams(layoutParams);
        //linearLayout.addView(closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationDetailsActivity.this.finish();
            }
        });

        Button viewButton = new Button(this);
        viewButton.setText("View");
        viewButton.setWidth(100);
        //viewButton.setLayoutParams(layoutParams);
        //linearLayout.addView(viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationDetailsActivity.this.finish();
                Intent intent = new Intent();
                //                intent.setClassName(DemoAppActivity.class.getPackage()
                //                        .getName(), DemoAppActivity.class.getName());
                intent.setClassName(callbackActivityPackageName,
                        callbackActivityClassName);
                context.startActivity(intent);
            }
        });

        LinearLayout innerLayout = new LinearLayout(context);
        innerLayout.setGravity(Gravity.CENTER);
        innerLayout.addView(closeButton);
        innerLayout.addView(viewButton);

        linearLayout.addView(innerLayout);

        return linearLayout;
    }

}
