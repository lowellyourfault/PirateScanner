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
package com.makeez.piratescanner;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public final class PirateList {
    /**
     * Latest pirate list are always available here.
     */
    private static final String WEB_JSON
            = "https://storage.googleapis.com/anti-piracy/android-pirate-app-list.json";

    /**
     * A local copy of the pirate list will be stored in this file (in internal storage)
     */
    private static final String LOCAL_JSON
            = "pirate-app-list.json";

    /**
     * This method will automatically grab the pirate list either from internet or locally.
     *
     * @return List of pirate apps that ruin the developers' life.
     */
    public static List<Pirate> get(Context context) {
        if (context == null) {
            throw new NullPointerException("Context must not be null.");
        }
        List<Pirate> list = getFromInternet(context);
        if (list == null) {
            list = getFromLocal(context);
        }
        return list;
    }

    /**
     * Get a (latest) copy of the pirate list from the World Wide Web.
     *
     * @return List of pirate apps that ruin the developers' life.
     */
    public static List<Pirate> getFromInternet(Context context) {
        if (context == null) {
            throw new NullPointerException("Context must not be null.");
        }
        String json;
        try {
            URL url = new URL(WEB_JSON);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            json = read(connection.getInputStream());
            connection.disconnect();
        } catch (IOException e) {
            json = null;
        }
        if (json != null) {
            save(context, json);
        }
        return createPirateList(json);
    }

    /**
     * Get a (maybe outdated) copy of the pirate list from internal storage.
     *
     * @return List of pirate apps that ruin the developers' life.
     */
    public static List<Pirate> getFromLocal(Context context) {
        if (context == null) {
            throw new NullPointerException("Context must not be null.");
        }
        String json;
        try {
            FileInputStream fis = context.openFileInput(LOCAL_JSON);
            json = read(fis);
        } catch (FileNotFoundException e) {
            json = null;
        }
        return createPirateList(json);
    }

    private static List<Pirate> createPirateList(String json) {
        List<Pirate> pirateList = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray blacklist = new JSONArray(json);
                for (int i = 0, j = blacklist.length(); i < j; i++) {
                    try {
                        JSONObject app = blacklist.getJSONObject(i);
                        String name = app.getString("name");
                        JSONArray packages = app.getJSONArray("packages");
                        JSONArray filters = app.getJSONArray("filters");
                        String[] p = new String[packages.length()];
                        for (int a = 0; a < p.length; a++) {
                            p[a] = packages.getString(a);
                        }
                        int[] f = new int[filters.length()];
                        for (int a = 0; a < f.length; a++) {
                            f[a] = filters.getInt(a);
                        }
                        pirateList.add(new Pirate(name, p, f));
                    } catch (JSONException e) {
                        // Corrupted pirate data, ignore this
                    }
                }
            } catch (JSONException e) {
                // Invalid pirate list, ignore this
            }
        }
        return pirateList.isEmpty() ? null : Collections.unmodifiableList(pirateList);
    }

    private static String read(InputStream stream) {
        try {
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedInputStream is = new BufferedInputStream(stream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            is.close();
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static void save(Context context, String json) {
        try {
            FileOutputStream fos = context.openFileOutput(LOCAL_JSON, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PirateList(){}
}
