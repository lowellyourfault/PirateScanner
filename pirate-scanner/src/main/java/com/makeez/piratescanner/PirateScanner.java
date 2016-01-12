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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PirateScanner {
    public interface Callback {
        void onCompleted(List<Pirate> pirateList);
    }

    private final List<Pirate> mCustomPirateList = new ArrayList<>();
    private final List<Integer> mFilters = new ArrayList<>();
    private FinderTask mTask;

    /**
     * Add custom (static) pirate that will be put to the final pirate list for scanning.
     *
     * Notes:
     * Added custom pirate will not overwrite the official list from the World Wide Web.
     * Each custom pirate must have its own unique ID to make sure it wouldn't get removed _
     * accidentally by removePirate().
     *
     * @param pirate Custom (static) pirate that will be scanned in start().
     */
    public void addPirate(Pirate pirate) {
        int pos = findPiratePos(pirate);
        if (pos < 0) {
            mCustomPirateList.add(pirate);
        }
    }

    /**
     * Remove custom (static) pirate.
     *
     * Notes:
     * Calling this method removes pirate added through addPirate() only.
     *
     * @param id Unique pirate ID of the corresponding pirate to be removed.
     */
    public void removePirate(int id) {
        int pos = findPiratePos(id);
        if (pos >= 0) {
            mCustomPirateList.remove(pos);
        }
    }

    /**
     * Remove all added custom (static) pirates from the list.
     */
    public void clearPirates() {
        mCustomPirateList.clear();
    }

    private int findPiratePos(Pirate pirate) {
        return findPiratePos(pirate.ID);
    }

    private int findPiratePos(int id) {
        for (int i = 0, j = mCustomPirateList.size(); i < j; i++) {
            if (mCustomPirateList.get(i).ID == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Specify the filter to be used while scanning for pirates.
     * Default: scan all
     *
     * @param filter Filter to be used while scanning for pirates.
     */
    public void addFilter(int filter) {
        if (!mFilters.contains(filter)) {
            mFilters.add(filter);
        }
    }

    /**
     * Remove added filter.
     *
     * @param filter Filter to be removed.
     */
    public void removeFilter(int filter) {
        mFilters.remove(Integer.valueOf(filter));
    }

    /**
     * Empty the filter list, calling this make sure scanner will scan through the whole pirate list.
     */
    public void clearFilters() {
        mFilters.clear();
    }

    /**
     * Start pirate scanner, if it is not already running.
     *
     * @param context   Context where the scanner should run.
     * @param callback  Callback when the scanner is done.
     */
    public void start(Context context, Callback callback) {
        if (context == null) {
            throw new NullPointerException("Context must not be null.");
        }
        if (callback == null) {
            throw new NullPointerException("Callback must not be null.");
        }
        if ((mTask == null) || (mTask.getStatus() == AsyncTask.Status.FINISHED)) {
            mTask = new FinderTask(context, callback, mFilters, mCustomPirateList);
            mTask.execute();
        }
    }

    /**
     * Cancel any running pirate scanner, this should be called upon user leaving (quit) the app.
     */
    public void cancel() {
        if ((!mTask.isCancelled()) && (mTask.getStatus() != AsyncTask.Status.FINISHED)) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    private static class FinderTask extends AsyncTask<Void, Void, List<Pirate>> {
        private WeakReference<Context> mContextRef;
        private Callback mCallback;

        private List<Pirate> mPirateList = new ArrayList<>();
        private int[] mFilters;

        public FinderTask(Context context, Callback callback,
                          List<Integer> filters, List<Pirate> customPirateList) {
            mContextRef = new WeakReference<>(context);
            mCallback = callback;
            if ((filters != null) && (!filters.isEmpty())) {
                mFilters = new int[filters.size()];
                for (int i = 0; i < mFilters.length; i++) {
                    mFilters[i] = filters.get(i);
                }
            } else {
                mFilters = Filter.LIST;
            }
            if ((customPirateList != null) && (!customPirateList.isEmpty())) {
                mPirateList.addAll(customPirateList);
            }
        }

        @Override
        protected List<Pirate> doInBackground(Void... params) {
            List<Pirate> found = new ArrayList<>();
            Context context = mContextRef.get();
            if (context != null) {
                List<Pirate> pirates = PirateList.get(context);
                if (pirates != null) {
                    mPirateList.addAll(pirates);
                }
                if (!mPirateList.isEmpty()) {
                    PackageManager manager = context.getPackageManager();
                    for (Pirate pirate : mPirateList) {
                        if (isCancelled()) {
                            break;
                        }
                        if (containsAny(pirate.FILTERS, mFilters)) {
                            for (String packageName : pirate.PACKAGES) {
                                if (hasPackage(manager, packageName)) {
                                    found.add(pirate);
                                }
                            }
                        }
                    }
                }
            }
            return found.isEmpty() ? null : Collections.unmodifiableList(found);
        }

        @Override
        protected void onPostExecute(List<Pirate> pirates) {
            if (isCancelled()) {
                return;
            }
            if (mCallback != null) {
                mCallback.onCompleted(pirates);
            }
        }

        private static boolean hasPackage(PackageManager manager, String packageName) {
            if ((manager == null) || (packageName == null)) {
                return false;
            }
            try {
                ApplicationInfo info = manager.getApplicationInfo(packageName, 0);
                return info != null;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean containsAny(int[] sources, int[] targets) {
            if ((sources == null) || (targets == null)) {
                return false;
            }
            if ((sources.length == 0) || (targets.length == 0)) {
                return false;
            }
            for (int source : sources) {
                for (int target : targets) {
                    if (source == target) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
