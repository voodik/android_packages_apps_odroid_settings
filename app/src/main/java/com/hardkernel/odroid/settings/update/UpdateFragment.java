package com.hardkernel.odroid.settings.update;

import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.provider.DocumentsContract;
import androidx.leanback.preference.LeanbackPreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import com.hardkernel.odroid.settings.R;
import com.hardkernel.odroid.settings.LeanbackAddBackPreferenceFragment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static android.app.Activity.RESULT_OK;


public class UpdateFragment extends LeanbackAddBackPreferenceFragment {

    private static final String TAG = "OdroidUpdateFragment";
    public static final int FILE_SELECT_CODE = 101;

    private static final String KEY_FROM_ONLINE = "update_from_online";
    private static final String KEY_SELECT_SERVER = "selected_server";
    private static final String KEY_FROM_STORAGE = "update_from_storage";
    private static final String PROP_BUILD_CHARACTERISTICS = "ro.build.characteristics";
	private static final boolean IS_ATV = SystemProperties.get(PROP_BUILD_CHARACTERISTICS, "tablet").equalsIgnoreCase("tv");

    private static Preference update_server;
    private static SwitchPreference checkAtUpdate;

    public static UpdateFragment newInstance() { return new UpdateFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        refreshStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        setPreferencesFromResource(R.xml.update, null);
        update_server = findPreference(KEY_SELECT_SERVER);

        String server = null;
        switch (updateManager.getServer()) {
            case updateManager.KEY_OFFICIAL:
                server = getString(R.string.update_official_server);
                break;
            case updateManager.KEY_MIRROR:
                server = getString(R.string.update_mirror_server);
                break;
            case updateManager.KEY_CUSTOM:
                server = getString(R.string.update_custom_server);
                break;
        }
        update_server.setSummary(server);

        checkAtUpdate = (SwitchPreference) findPreference(updateManager.KEY_CHECK_UPDATE);
        checkAtUpdate.setChecked(updateManager.isCheckAtBoot());
        checkAtUpdate.setEnabled(false);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        Context context = getContext();

        switch (key) {
            case KEY_FROM_ONLINE:
//                UpdatePackage.checkLatestVersion(context);
                break;
            case KEY_FROM_STORAGE:
                updatePackageFromStorage();
                break;
            case updateManager.KEY_CHECK_UPDATE:
                updateManager.setCheckUpdate(checkAtUpdate.isChecked());
        }

        return super.onPreferenceTreeClick(preference);
    }

    private void updatePackageFromStorage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Update"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UpdateFragment.FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Context context = getContext();
                    Uri uri = data.getData();
                    String path = getPath(context, uri);
                    if (path == null)
                        return;
                    UpdatePackage.installPackage(context, new File(path));
                }
                break;
        }
    }

    private static String getPath(Context context, Uri uri) {
        final boolean isKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;



        // DocumentProvider
        if (isKitkat && DocumentsContract.isDocumentUri(context, uri)) {
        Log.d(TAG, "getPath 1 " + uri.toString() + " autority " + uri.getAuthority());
            if (isDownloadDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.
                        withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if(isStorageDocument(uri)){
                try {
                    String result = java.net.URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8.name());
                    result = result.substring(65);
//                    result = Environment.getExternalStorageDirectory().toString() + "/" + result;
                    result = "/data/media/0/" + result;
                    Log.d(TAG, "substring " + result);
                    return result;
                } catch (UnsupportedEncodingException e) {
                    // not going to happen - value came from JDK's own StandardCharsets
                }
            }
        } else if ( "file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return  null;
    }

    private static String getDataColumn (Context context, Uri uri,
                                         String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadProvider.
     */
    private static boolean isDownloadDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
}
