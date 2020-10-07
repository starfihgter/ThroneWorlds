package net.stardevelopments.throneworlds;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileLoader {

    private String recordFileName;

    public FileLoader(String recordName){
        recordFileName = recordName;
    }

    private File userRecordFile;
    private FileConfiguration userRecord;

    public void reloadUserRecord() {
        if (userRecordFile == null) {
            userRecordFile = new File(Main.plugin.getDataFolder(), recordFileName);
            userRecord = YamlConfiguration.loadConfiguration(userRecordFile);
        }else{
        userRecord = YamlConfiguration.loadConfiguration(userRecordFile);}
    }

    public FileConfiguration getUserRecord() {
        if (userRecord == null) {
            reloadUserRecord();
        }
        return userRecord;
    }

    public void saveCustomConfig() {
        try {
            getUserRecord().save(userRecordFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

