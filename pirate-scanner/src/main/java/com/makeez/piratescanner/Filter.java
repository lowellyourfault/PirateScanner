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

public final class Filter {
    /**
     * For pirates that block ad serving within app.
     */
    public static final int BLOCK_ADS = 1;
    /**
     * For pirates that bypass in-app billing (of sort).
     */
    public static final int BYPASS_IAB = 2;
    /**
     * For pirates that bypass license verification (of sort).
     */
    public static final int BYPASS_LICENSE_CHECK = 3;
    /**
     * For pirates that remove permission requested by the app.
     */
    public static final int REMOVE_PERMISSION = 4;

    /**
     * Full list of the filters.
     */
    public static final int[] LIST = {
            BLOCK_ADS,
            BYPASS_IAB,
            BYPASS_LICENSE_CHECK,
            REMOVE_PERMISSION
    };

    private Filter(){}
}
