/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.hardkernel.odroid.settings.util;

import android.os.SystemProperties;
import android.support.v7.preference.Preference;

/**
 * Utilities for working with Droid.
 */
public final class DroidUtils {

	/**
	 * Non instantiable.
	 */
	private DroidUtils() {
	}

	public static boolean hasTvUiMode() {
		return SystemProperties.getBoolean("ro.vendor.platform.has.tvuimode", false);
	}

	public static boolean hasMboxUiMode() {
		return SystemProperties.getBoolean("ro.vendor.platform.has.mboxuimode", false);
	}

	public static void invisiblePreference(Preference preference, boolean tvUiMode) {
		if (preference == null) {
			return;
		}
		if (tvUiMode) {
			preference.setVisible(false);
		} else {
			preference.setVisible(true);
		}
	}

	public static boolean is64Bit() {
		return Build.SUPPORTED_64_BIT_ABIS.length > 0;
	}

	public static boolean isOdroidN2() {
		return getBoard().equals("odroidn2");
	}

	public static boolean isOdroidC4() {
		return getBoard().equals("odroidc4");
	}

	private static String board = null;

	private static String getBoard() {
		if (board != null)
			return board;

		board = SystemProperties.get("ro.product.board", "odroidn2");

		return board;
	}
}
