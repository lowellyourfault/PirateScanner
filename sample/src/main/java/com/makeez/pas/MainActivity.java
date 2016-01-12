/**
 * Copyright (c) 2016 Daniel Tan <tantzewee@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.makeez.pas;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.makeez.piratescanner.Pirate;
import com.makeez.piratescanner.PirateScanner;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView mTextField;
    private PirateScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextField = (TextView) findViewById(R.id.text_field);
        mTextField.setText("Searching for pirate apps..");
    }

    @Override
    protected void onStart() {
        super.onStart();

        /** PirateFinder **/
        scanner = new PirateScanner();
        /**
         * Add custom (static) pirate to the list. Pirate added this way will be put to the final
         * pirate list automatically when scanner is running.
         * Specify a unique ID for each pirate or they will all be treated the same.
         * (eg. PirateFinder.removePirate() removes the first pirate with the same ID it found)
         */
//        finder.addPirate(new Pirate(
//                1,
//                "Lucky Patcher",
//                new String[] {
//                        "com.dimonvideo.luckypatcher",
//                        "com.chelpus.lackypatch" },
//                new int[] {
//                        Filter.BYPASS_IAB,
//                        Filter.BYPASS_LICENSE_CHECK,
//                        Filter.REMOVE_PERMISSION }));
        /**
         * Add filter(s) to scan through only those that will impact on the app.
         * Default: scan all
         */
//        finder.addFilter(Filter.BYPASS_LICENSE_CHECK);
        /**
         * Start pirate scanner (in the background).
         * Upon completion, PirateScanner.Callback.onCompleted() will be called.
         * Note that the return pirate list may be null (indicating no pirate found or error might
         * occurred along the way).
         */
        scanner.start(this, new PirateScanner.Callback() {
            @Override
            public void onCompleted(@Nullable List<Pirate> pirates) {
                StringBuilder builder = new StringBuilder();
                if (pirates != null) {
                    builder.append(pirates.size());
                    builder.append(" pirates found.");
                    builder.append("\n\n");
                    for (Pirate pirate : pirates) {
                        builder.append("Name: ");
                        builder.append(pirate.NAME);
                        builder.append("\n");
                        builder.append("Packages: ");
                        for (String packageName : pirate.PACKAGES) {
                            builder.append(packageName);
                            builder.append(", ");
                        }
                        builder.append("\n");
                        builder.append("Filters: ");
                        for (int filter : pirate.FILTERS) {
                            builder.append(filter);
                            builder.append(", ");
                        }
                        builder.append("\n\n");
                    }
                } else {
                    builder.append("No pirate found.");
                }
                mTextField.setText(builder.toString());
            }
        });
    }

    @Override
    protected void onStop() {
        if (scanner != null) {
            scanner.cancel();
            scanner = null;
        }
        super.onStop();
    }
}
