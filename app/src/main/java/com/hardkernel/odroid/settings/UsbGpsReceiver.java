/*
 * Copyright (c) 2019 Voodik, Inc. All rights reserved.
 *
 * This source code is subject to the terms and conditions defined in the
 * file 'LICENSE' which is part of this source code package.
 *
 */

package com.hardkernel.odroid.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.SystemProperties;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.IOException;


public class UsbGpsReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbDevReceiver";
    private static final String MODEM_TRIGGER_PROP = "sys.usb_modem_trigger";
    private static final String MODEM_CACHED_PROP = "sys.usb_modem_cahed";

    private static final int DETACHED = 0;
    private static final int ATTACHED = 1;    

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);



        if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

			int dev_vid = device.getVendorId();
			int dev_pid = device.getProductId();

			if (dev_vid > 0 && dev_pid > 0) {
			    handleGps(dev_vid, dev_pid, ATTACHED);                
			    handleModem(dev_vid, dev_pid, ATTACHED);                
            }


        }
        else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

			int dev_vid = device.getVendorId();
			int dev_pid = device.getProductId();

			if (dev_vid > 0 && dev_pid > 0) {
			    handleGps(dev_vid, dev_pid, DETACHED);                
            }

        }
    }

    private static void handleGps(int dev_vid, int dev_pid, int action) {

        int vid = Integer.parseInt(SystemProperties.get("ro.gps.id.vendor", "1546"), 16);
		int pid = Integer.parseInt(SystemProperties.get("ro.gps.id.product", "0"), 16);
		String gpscfgnode = SystemProperties.get("ro.kernel.android.gps", "");

        dev_pid = pid > 0 ? dev_pid : 0;

        if(dev_vid == vid && dev_pid == pid) {
            if (action == ATTACHED) {
                String ttynode = findtty(dev_vid, dev_pid);
                SystemProperties.set("sys.kernel.android.gps", ttynode);
                Log.i(TAG, "MNG attached: " + ttynode);
            } else if(action == DETACHED) {
				SystemProperties.set("sys.kernel.android.gps", "none");
				Log.i(TAG, "MNG detached: " + dev_vid + " " + dev_pid);
            }
        }
    }

    private static void handleModem(int dev_vid, int dev_pid, int action) {
        String[] modem_prop = SystemProperties.get("persist.usbmodem_vidpid", "0:0").split(":");
        boolean same_vidpid = SystemProperties.getBoolean("persist.usbmodem_vidpid_same", false);
        String[] cached_prop = SystemProperties.get(MODEM_CACHED_PROP, "0:0").split(":");

        int vid, pid, cached_vid, cached_pid;


        try {
            vid = Integer.parseInt(modem_prop[0].trim(), 16);
		    pid = Integer.parseInt(modem_prop[1].trim(), 16);
        } catch (NumberFormatException nfe) {
            Log.i(TAG, "MNG modem invalid VID/PID: " + nfe.getMessage());
            vid = pid = 0;
        }

        try {
            cached_vid = Integer.parseInt(cached_prop[0].trim(), 10);
		    cached_pid = Integer.parseInt(cached_prop[1].trim(), 10);
        } catch (NumberFormatException nfe) {
            Log.i(TAG, "MNG modem invalid cached VID/PID: " + nfe.getMessage());
            cached_vid = cached_pid = 0;
        }

        if (dev_vid == vid && dev_pid == pid) {
            if (action == ATTACHED) {
                if (dev_vid != cached_vid || dev_pid != cached_pid) {
                    if (same_vidpid) {
                        SystemProperties.set(MODEM_CACHED_PROP, dev_vid + ":" + dev_pid);
                        Log.i(TAG, "MNG modem same_vidpid is true, caching VID/PID: " + Integer.toHexString(dev_vid) + " " + Integer.toHexString(dev_pid));
                    }
                    SystemProperties.set(MODEM_TRIGGER_PROP, "true");
                    Log.i(TAG, "MNG modem attached: " + Integer.toHexString(dev_vid) + " " + Integer.toHexString(dev_pid));
                } else {
                    if (same_vidpid) {
                        Log.i(TAG, "MNG modem switch complete remove cached VID/PID: " + cached_vid + " " + cached_pid);
                        SystemProperties.set(MODEM_CACHED_PROP, "0:0");
                    }
                }
            }
        }
    }

    private static String findtty(int vid, int pid) {

		File f = new File("/sys/class/tty");
		BufferedReader reader;
		String path = "none";
		int ttyvid = 0;
		int ttypid = 0;

		Log.i(TAG, "MNG finding tty node with vid 0x" + Integer.toHexString(vid) + " pid 0x" + Integer.toHexString(pid));

		// Create a FilenameFilter
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File f, String name) {
				return name.startsWith("ttyUSB") || name.startsWith("ttyACM");
			}
		};

        File[] files = f.listFiles(filter);

		for (int i = 0; i < files.length; i++) {
			try {
				path = files[i].getName();
				reader = new BufferedReader(new FileReader("/sys/class/tty/" + path + "/device/uevent"));
				String line = reader.readLine();
					while (line != null) {
						if (line.startsWith("PRODUCT")){
							line = line.substring(8);
							String[] split = line.split("/");
							ttyvid = Integer.parseInt(split[0], 16);
							ttypid = pid > 0 ? Integer.parseInt(split[1], 16) : 0;
							break;
						}
						// read next line
						line = reader.readLine();
					}
				reader.close();
                } catch (IOException e) {
					e.printStackTrace();
                }
			if (ttyvid == vid && ttypid == pid) {
				return path;
			}
		}
		return "none";
    }

}
