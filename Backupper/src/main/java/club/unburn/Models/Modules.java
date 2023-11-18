package club.unburn.Models;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import club.unburn.Main;
import club.unburn.Utils.Color;
import club.unburn.Utils.FileUtils;
import club.unburn.Utils.Logger;
import club.unburn.Utils.MySQLDump;
import club.unburn.Utils.Prefix;
import club.unburn.Utils.Stopwatch;

public class Modules {
	
	public static void loadConfig() throws Exception {
		ObjectMapper objectMapper = null;
		if(FileUtils.doesFileExist(Main.configPath)) {
			
			objectMapper = new ObjectMapper();
			Main.cfg = objectMapper.readValue(FileUtils.readStringFromFile(Main.configPath), Config.class);
			Logger.add(Prefix.INFO,  "Backupper Config loaded");
		}else {
			Logger.add(Prefix.INFO,  "Config does not exist, creating new one...");
			objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			String newFileContent = objectMapper.writeValueAsString(Main.cfg);
			FileUtils.writeStringToFile(Main.configPath, newFileContent);
		}
	}
	
	public static void generateTest() throws Exception {
		Action testAction = new Action();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		String newFileContent = objectMapper.writeValueAsString(testAction);
		FileUtils.writeStringToFile("actions/gen.json", newFileContent);
		
	}
	
	public static void loadActions() throws Exception {
		ObjectMapper objectMapper = null;
		File directory = new File("actions/");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
        	objectMapper = new ObjectMapper();
            for (File file : files) {
                Logger.add(Prefix.INFO,  "Found Action " + file.getName());
                Action act = objectMapper.readValue(FileUtils.readStringFromFile(file.getPath()), Action.class);
                Main.actionList.add(act);
                Logger.add(Prefix.INFO,  "Registered Action " + act.name);
            }
        }
	}
	
	public static void printStatus(Boolean failed, long sec, String savedPath) {
		if(failed) {
			System.out.println(
					Prefix.ERROR+  "##############################################\n" +
					Prefix.ERROR+ "#          " + Color.RED + "BUILD FAILED" + Color.RESET + " \n" +
					Prefix.ERROR+ "#                           \n" +
					Prefix.ERROR+ "#  NO FILE HAS BEEN SAVED  \n" +
					Prefix.ERROR+ "#  ELAPSED SECONDS: " + sec + "  \n" +
					Prefix.ERROR+ "#                                             \n" +
					Prefix.ERROR+ "###############################################");
		}else {
			System.out.println(
					Prefix.BUILD_BACKUP+  "##############################################\n" +
					Prefix.BUILD_BACKUP+ "#          " + Color.GREEN + "BUILD COMPLETED" + Color.RESET + "  \n" +
					Prefix.BUILD_BACKUP+ "#                                 \n" +
					Prefix.BUILD_BACKUP+ "#  SAVED ZIP: " + savedPath + "        \n" +
					Prefix.BUILD_BACKUP+ "#  ELAPSED SECONDS: " + sec + "     \n" +
					Prefix.BUILD_BACKUP+ "#                               \n" +
					Prefix.BUILD_BACKUP+ "###############################################");
		}
	}
	
	public static void executeAction(String name) throws Exception {
		Logger.reset();
		Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        String outputZipped = "";
		for(int i = 0; i < Main.actionList.size(); i++) {
			
			FileUtils.deleteFolderIfExists("temp/");
			
			Action act = Main.actionList.get(i);
			
			if(act.name.contains(name)) {
				act.exportname = act.exportname.replaceAll("%name%", act.name);
    			Date currentDate = new Date();
    	        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HH_mm");
    	        String formattedDate = dateFormat.format(currentDate);
    			act.exportname = act.exportname.replaceAll("%date%", formattedDate);
				Logger.add(Prefix.INFO,  "Executing Action " + act.name);
				
				FileUtils.createFolderIfNotExists("temp/");
				
				for(int i2 = 0; i2 < act.filepaths.size(); i2++){
					String filepath = act.filepaths.get(i2);
					if(FileUtils.checkIfDirectory(filepath)) {
						File f = new File(filepath);
						FileUtils.createFolderIfNotExists("temp/" + f.getName());
						Logger.add(Prefix.BUILD_BACKUP,  "Copying Directory " + f.getName());
						org.apache.commons.io.FileUtils.copyDirectory(new File(filepath), new File("temp/" + f.getName()));
						Logger.add(Prefix.BUILD_BACKUP,  "Finished copying Directory " + f.getName() + " to " + "temp/" + f.getName());

					}else {
						
						File f = new File(filepath);
						Logger.add(Prefix.BUILD_BACKUP,  "Copying File " + f.getName());
						org.apache.commons.io.FileUtils.copyFile(f, new File("temp/" + f.getName()));
						Logger.add(Prefix.BUILD_BACKUP,  "Finished copying File " + f.getName() + " to " + "temp/" + f.getName());
					}
				}
				
				Logger.add(Prefix.INFO,  "Files sucessfully copied");
				
				for(int i2 = 0; i2 < act.toexportdb.size(); i2++) {
					String database = act.toexportdb.get(i2);
					FileUtils.createFolderIfNotExists("temp/databases");
					File f = new File(database);
					if(f.exists()) {
						Logger.add(Prefix.BUILD_BACKUP,  "Copying Database " + f.getName());
						org.apache.commons.io.FileUtils.copyFile(f, new File("temp/databases/" + f.getName()));
						Logger.add(Prefix.BUILD_BACKUP,  "Finsihed copying Database " + f.getName() + " to " + "temp/databases/" + f.getName()); 
					}else {
						if(Main.cfg.mySQLEnabled) {
							MySQLDump mySQLDump = new MySQLDump(Main.cfg.mySQLUserName, Main.cfg.mySQLPassword, act.database);
							try {
								if(database.equals("*")) {
									mySQLDump.dumpEntireDatabase("temp/databases/" + act.database + ".sql");
								}else {
							        mySQLDump.dumpTable(database, "temp/databases/" + database + ".sql");
								}
							}catch(Exception e) {
								stopwatch.stop();
								Logger.add(Prefix.ERROR,  e.getMessage());
								Logger.exportLog(act);
								Modules.printStatus(true, stopwatch.getElapsedSeconds(), null);
								return;
							}
						}
					}
				}
				Logger.add(Prefix.INFO,  "Databases sucessfully copied");
				Logger.add(Prefix.BUILD_BACKUP,  "Completed Build Temp Files");
    			Logger.add(Prefix.INFO,  "Choosing compression: " + act.compressing);
    			
    			
    			switch(act.compressing) {
    				case "zip":
    					FileUtils.createFolderIfNotExists("output");
    					FileUtils.createFolderIfNotExists("output/" + act.name);
    					Logger.add(Prefix.BUILD_BACKUP,  "Building " + "output/" + act.name + "/" + act.exportname + ".zip");
    					FileUtils.zipFolder("temp/", "output/" + act.name + "/" + act.exportname + ".zip");
    					outputZipped = "output/" + act.name + "/" + act.exportname + ".zip";
    					break;
    				default:
    					Logger.add(Prefix.ERROR,  "This compression isn't build-in");
    					break;
    			}
    			Logger.exportLog(act);
    			FileUtils.deleteFolderIfExists("temp/");
			}
			
		}
		
		stopwatch.stop();
		Modules.printStatus(false, stopwatch.getElapsedSeconds(), outputZipped);
	}

}
