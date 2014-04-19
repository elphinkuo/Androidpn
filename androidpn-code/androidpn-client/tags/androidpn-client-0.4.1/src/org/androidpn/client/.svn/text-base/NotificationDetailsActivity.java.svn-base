/*
 * Copyright (C) 2010 The Androidpn Team
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
package org.androidpn.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * An Activity that is to displays the notification details view.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class NotificationDetailsActivity extends Activity {

    private static final String LOGTAG = LogUtil
            .makeLogTag(NotificationDetailsActivity.class);

    public NotificationDetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String notificationId = intent
                .getStringExtra(Constants.NOTIFICATION_ID);
        String notificationApiKey = intent
                .getStringExtra(Constants.NOTIFICATION_API_KEY);
        String notificationTitle = intent
                .getStringExtra(Constants.NOTIFICATION_TITLE);
        String notificationMessage = intent
                .getStringExtra(Constants.NOTIFICATION_MESSAGE);
        String notificationTicker = intent
                .getStringExtra(Constants.NOTIFICATION_TICKER);
        String notificationUrl = intent
                .getStringExtra(Constants.NOTIFICATION_URL);

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

        Button okButton = new Button(this);
        okButton.setText("Ok");
        okButton.setWidth(100);
        //closeButton.setLayoutParams(layoutParams);
        //linearLayout.addView(closeButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NotificationDetailsActivity.this.finish();
            }
        });

        //        Button closeButton = new Button(this);
        //        closeButton.setText("Close");
        //        closeButton.setWidth(100);
        //        closeButton.setOnClickListener(new View.OnClickListener() {
        //            public void onClick(View view) {
        //                NotificationDetailsActivity.this.finish();
        //            }
        //        });
        //
        //        Button viewButton = new Button(this);
        //        viewButton.setText("View");
        //        viewButton.setWidth(100);
        //        viewButton.setOnClickListener(new View.OnClickListener() {
        //            public void onClick(View view) {
        //                NotificationDetailsActivity.this.finish();
        //                Intent intent = new Intent();
        //                //                intent.setClassName(DemoAppActivity.class.getPackage()
        //                //                        .getName(), DemoAppActivity.class.getName());
        //                intent.setClassName(callbackActivityPackageName,
        //                        callbackActivityClassName);
        //                context.startActivity(intent);
        //            }
        //        });

        LinearLayout innerLayout = new LinearLayout(context);
        innerLayout.setGravity(Gravity.CENTER);
        innerLayout.addView(okButton);
        //        innerLayout.addView(closeButton);
        //        innerLayout.addView(viewButton);

        linearLayout.addView(innerLayout);

        return linearLayout;
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

}
