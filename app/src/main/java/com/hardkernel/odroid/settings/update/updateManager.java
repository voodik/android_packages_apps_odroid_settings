package com.hardkernel.odroid.settings.update;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemProperties;

public class updateManager {

    private static final boolean GAPPS = SystemProperties.getBoolean("ro.opengapps_installed", false);
    private static final boolean IS64 = Build.SUPPORTED_64_BIT_ABIS.length > 0;

    private static final String GSTR =  GAPPS ? "" : "-ng";
    private static final String ABISTR =  IS64 ? "-64" : "";

    public final static String OFFICIAL_URL =
            String.format("https://oph.mdrjr.net/voodik/S922X/ODROID-N2/Android/lineage-16.0%s%s/", ABISTR, GSTR);


    public final static String MIRROR_URL =
            "https://www.odroid.in/mirror/dn.odroid.com/S922X/ODROID-N2/Android/";

    public static final long PACKAGE_MAXSIZE = 800 * 1024 * 1024;   /* 800MB */
    public static final String LATEST_VERSION = IS64 ? "latestupdate_pie_64" : "latestupdate_pie";

    public static final String KEY_OFFICIAL = "server_official";
    public static final String KEY_MIRROR = "server_mirror";
    public static final String KEY_CUSTOM = "server_custom";
    public static final String KEY_CHECK_UPDATE = "check_update";

    private static String server = KEY_OFFICIAL;
    private static String url = OFFICIAL_URL;

    public static String getRemoteURL() {
        return url;
    }

    public static void setRemoteURL(String newURL) {
        setPreference(SH_KEY_URL, newURL);
        url = newURL;
    }

    public static final String SHPREF_UPDATE_SERVER = "update_server";
    private static final String SH_KEY_SERVER = "server";
    private static final String SH_KEY_URL = "url";

    private static SharedPreferences pref = null;

    public static void setPreference(SharedPreferences sharedPreferences) {
        if (pref == null)
            pref = sharedPreferences;
    }

    public static void initServer() {
        server = pref.getString(SH_KEY_SERVER, KEY_OFFICIAL);
    }

    public static void initURL() {
        setRemoteURL(pref.getString(SH_KEY_URL, OFFICIAL_URL));
    }

    public static String getServer() {
        return server;
    }

    public static Boolean isCheckAtBoot() {
        return pref.getBoolean(KEY_CHECK_UPDATE, false);
    }

    public static void setCheckUpdate(boolean check) {
        setPreference(KEY_CHECK_UPDATE, check);
    }

    public static void setServer(String serverName) {
        setPreference(SH_KEY_SERVER, serverName);
        server = serverName;
    }

    private static void setPreference(String target, Object value) {
        final SharedPreferences.Editor editor = pref.edit();

        if (value instanceof String)
            editor.putString(target, (String)value);
        else if (value instanceof Boolean)
            editor.putBoolean(target, (boolean)value);
        editor.commit();
    }
}
