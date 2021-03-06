package com.hardkernel.odroid.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class bootini {
    private final static String path = "/odm/boot.ini";

    public static String getBigCoreClock() {
        return getValue("max_freq_a73") + "000";
    }

    public static String getLittleCoreClock() {
        return getValue("max_freq_a53") + "000";
    }

    public static String getBigCoreGovernor() {
        return getValue("governor_a73");
    }

    public static String getLittleCoreGovernor() {
        return getValue("governor_a53");
    }

    /** Not used,
     *  It works on systemcontrol. read from /proc/cmdline
     */
    public static String getHdmiMode() {
        return getValue("hdmimode");
    }

    public static String getVoutMode() {
        return getValue("voutmode");
    }

    public static int getDisplayZoomrate() {
        return Integer.parseInt(getValue("zoom_rate"));
    }

    public static int getWakeOnLan() {
        return Integer.parseInt(getValue("enable_wol"));
    }

    public static String getColorAttribute() {
        return getValue("colorattribute");
    }

    private static String getValue(String keyWord) {
        return _getValue("setenv " + keyWord + " ");
    }

    private static String _getValue(String startTerm) {
        File boot_ini = new File(path);
        if (boot_ini.exists()) {
            try {
                String line;
                FileReader fileReader = new FileReader(boot_ini);
                BufferedReader reader = new BufferedReader(fileReader);
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(startTerm)) {
                        return line.substring(line.indexOf("\"") +1,
                                line.lastIndexOf("\""));
                    }
                }
                reader.close();
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void setBigCoreFreq(String freq) {
        setValue("max_freq_a73", freq.substring(0, freq.length()-3));
    }

    public static void setLittleCoreFreq(String freq) {
        setValue("max_freq_a53", freq.substring(0, freq.length()-3));
    }

    public static void setBigCoreGovernor(String governor) {
        setValue("governor_a73", governor);
    }

    public static void setLittleCoreGovernor(String governor) {
        setValue("governor_a53", governor);
    }

    public static void setHdmiMode(String mode) {
        mode = convertVUResolution(mode);
        setValue("hdmimode", mode);
        if (mode.equals("autodetect"))
            setDisplayAutodetect("true");
        else
            setDisplayAutodetect("false");
    }

    private static void setDisplayAutodetect(String mode) {
        setValue("display_autodetect", mode);
    }

    private static String convertVUResolution(String mode) {
        if (mode.equals("ODROID-VU5/7"))
            return "800x480p60hz";
        else if (mode.equals("ODROID-VU7 Plus"))
            return  "1024x600p60hz";
        else if (mode.equals("ODROID-VU8"))
            return "1024x768p60hz";
        return mode;
    }

    public static void setVoutMode(String mode) {
        setValue("voutmode", mode);
    }

    public static void setDisplayZoom(int rate) {
        setValue("zoom_rate", String.valueOf(rate));
    }

    public static void setWakeOnLan(int on) {
        setValue("enable_wol", String.valueOf(on));
    }

    public static void setColorAttribute(String color) {
        setValue("colorattribute", color);
    }

    private static void setValue (String keyWord, String val) {
        _setValue("setenv " + keyWord + " ", val);
    }

    private static void _setValue (String startTerm,String val) {
        try {
            File boot_ini = new File(path);
            FileReader fileReader = new FileReader(boot_ini);
            BufferedReader reader = new BufferedReader(fileReader);

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(startTerm))
                    line = startTerm + "\"" + val + "\"";
                lines.add(line + "\n");
            }

            fileReader.close();
            reader.close();

            FileWriter fileWriter = new FileWriter(boot_ini);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (String newline : lines)
                writer.write(newline);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
