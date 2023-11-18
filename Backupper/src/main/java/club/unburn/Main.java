package club.unburn;

import java.util.ArrayList;
import java.util.List;

import club.unburn.Models.Action;
import club.unburn.Models.Config;
import club.unburn.Models.Modules;
import club.unburn.Utils.FileUtils;
import club.unburn.Utils.Logger;
import club.unburn.Utils.Prefix;

public class Main {
	public static List<Action> actionList = new ArrayList<Action>();
	public static Config cfg = new Config();
	public static String configPath = "config.json";
	
	public static void main(String[] args) {
		FileUtils.createFolderIfNotExists("actions/");
		
		try {
			Modules.loadConfig();
		} catch (Exception e) {
			Logger.add(Prefix.ERROR,  "Error loading config.json");
			Logger.add(Prefix.ERROR,  e.getMessage());
		}
		
		try {
			Modules.loadActions();
		} catch (Exception e) {
			Logger.add(Prefix.ERROR,  "Error loading config.json");
			Logger.add(Prefix.ERROR,  e.getMessage());
		}
			
	    if(cfg.cronEnabled) {
           	// TODO: Handle Cron
        }else {
            Logger.add(Prefix.WARNING,  "Cron disabled you need to start the actions yourself");
        }
	        
	    if(args.length > 0) {
	        if(args[0].equals("-a")) {
		        String action = args[1];
		        if(action == null) {
		        	Logger.add(Prefix.ERROR,  "Error specifying Action");
		        }else {
		        	try {
		        		Modules.executeAction(action);
		    		} catch (Exception e) {
		    			Logger.add(Prefix.ERROR,  "Error loading config.json");
		    			Logger.add(Prefix.ERROR,  e.getMessage());
		    		}
		        }
		    }else if(args[0].equals("-all")) {
		    	for(int i = 0; i < actionList.size(); i++) {
		    		Action a = actionList.get(i);
		    		try {
		        		Modules.executeAction(a.name);
		    		} catch (Exception e) {
		    			Logger.add(Prefix.ERROR,  "Error loading config.json");
		    			Logger.add(Prefix.ERROR,  e.getMessage());
		    		}
		    	}
		    	Logger.add(Prefix.BUILD_BACKUP,  "Finished executing all actions");
		    }else if(args[0].equals("-gen")) {
		    	try {
		    		Modules.generateTest();
	    		} catch (Exception e) {
	    			Logger.add(Prefix.ERROR,  "Failed generating actions/gen.json");
	    			Logger.add(Prefix.ERROR,  e.getMessage());
	    		}
		    	Logger.add(Prefix.BUILD_BACKUP,  "Finished building Test File");
		    }
	    }
	   System.exit(0);
		
		
	}
	
    
}
