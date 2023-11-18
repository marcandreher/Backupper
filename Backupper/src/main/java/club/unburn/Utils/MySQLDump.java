package club.unburn.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import club.unburn.Main;

public class MySQLDump {
    private String username;
    private String databaseName;
    private String passwordArg;

    public MySQLDump(String username, String password, String databaseName) {
        this.username = username;
        this.databaseName = databaseName;
        if (Main.cfg.mySQLPassword.equals("")) {
            passwordArg = null;
        } else {
            passwordArg = "-p" + password;
        }
    }

    public void dumpEntireDatabase(String outputFilePath) throws Exception {
    	Logger.add(Prefix.BUILD_BACKUP, "MySQL Database Dump of " + databaseName + " started");
        try {
            ProcessBuilder processBuilder;
            if (passwordArg == null) {
                processBuilder = new ProcessBuilder(
                        Main.cfg.mySQLDumpLoc,
                        "-u" + username,
                        databaseName
                );
            } else {
                processBuilder = new ProcessBuilder(
                        Main.cfg.mySQLDumpLoc,
                        "-u" + username,
                        passwordArg,
                        databaseName
                );
            }

            Path outputPath = Paths.get(outputFilePath);
            processBuilder.redirectOutput(outputPath.toFile());
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                Logger.add(Prefix.BUILD_BACKUP, "Database dumped successfully to " + outputFilePath);
            } else {
                Logger.add(Prefix.ERROR , "Error dumping the database. Exit code: " + exitCode);
                throw new Exception("Failed connecting to MySQL");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new Exception("Failed connecting to MySQL");
        }
    }

    public void dumpTable(String tableName, String outputFilePath) throws Exception {
    	Logger.add(Prefix.BUILD_BACKUP, "MySQL Table Dump of " + tableName + " started");
        try {
            ProcessBuilder processBuilder;
            if (passwordArg == null) {
                processBuilder = new ProcessBuilder(
                        Main.cfg.mySQLDumpLoc,
                        "-u" + username,
                        databaseName
                );
            } else {
                processBuilder = new ProcessBuilder(
                        Main.cfg.mySQLDumpLoc,
                        "-u" + username,
                        passwordArg,
                        databaseName
                );
            }

            Path outputPath = Paths.get(outputFilePath);
            processBuilder.redirectOutput(outputPath.toFile());
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println(Prefix.BUILD_BACKUP + "Table " + tableName + " dumped successfully to " + outputFilePath);
            } else {
                System.out.println(Prefix.ERROR + "Error dumping the table. Exit code: " + exitCode);
                throw new Exception("Failed connecting to MySQL");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}