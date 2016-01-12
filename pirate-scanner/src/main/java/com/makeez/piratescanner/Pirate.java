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

public final class Pirate {
    public final int ID;
    public final String NAME;
    public final String[] PACKAGES;
    public final int[] FILTERS;

    public Pirate(String name, String[] packages, int[] filters) {
        this(-1, name, packages, filters);
    }

    public Pirate(int id, String name, String[] packages, int[] filters) {
        ID = id;
        NAME = name;
        PACKAGES = packages;
        FILTERS = filters;
    }
}
