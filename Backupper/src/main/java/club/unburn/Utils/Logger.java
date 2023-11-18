package club.unburn.Utils;

import java.util.ArrayList;
import java.util.List;

import club.unburn.Models.Action;

public class Logger {
	private static List<String> logger = new ArrayList<String>();
	
	
	public static void reset() {
		logger.clear();
	}
	public static void add(Prefix error, String message) {
		System.out.println(error + message);
		logger.add("[LOG / " + error.name() + "]: " + message);
	
		
		
	}
	
	public static void exportLog(Action act) {
		FileUtils.createFolderIfNotExists("logs/");
		FileUtils.createFolderIfNotExists("logs/" + act.name);
		String location = "logs/" + act.name + "/" + act.exportname + ".log";
		String fileContent = "";
		for(int i = 0; i<logger.size(); i++) {
			String log = logger.get(i);
			fileContent += log + "\n";
		}
		FileUtils.writeStringToFile(location, fileContent);
	}

}
