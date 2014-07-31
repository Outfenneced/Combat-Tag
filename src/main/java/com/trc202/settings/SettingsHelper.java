package com.trc202.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsHelper {

    private final Properties prop;
    private final File file;
    private final String pluginName;

    public SettingsHelper(File file, String pluginName) {
        prop = new Properties();
        this.file = file;
        this.pluginName = pluginName;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("[" + pluginName + "] has encountered an IOException while trying to create the properties file");
                e.printStackTrace();
            }
        }
    }

    public static boolean hasSettingsFile(File f) {
        return f.exists();
    }

    public static void deleteSettingsFile(File f) {
        if (f.exists()) {
            f.delete();
        }
    }

    public void loadConfig() {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("[" + pluginName + "] failed to load the properties file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[" + pluginName + "] encountered an IOException while loading the properties file");
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            prop.store(fos, "");
            fos.close();
        } catch (IOException e) {
            System.out.println("[" + pluginName + "] has encountered an IOException while saving the properties file");
        }
    }

    public void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
