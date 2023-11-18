package club.unburn.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	
	public static void deleteFolderIfExists(String folderPath) {
        Path path = Paths.get(folderPath);

        if (Files.exists(path)) {
            try {
                // Walk through the directory and delete all files and subdirectories
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file); // Delete the file
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Handle the failure to visit a file (optional)
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir); // Delete the directory after all its contents are processed
                        return FileVisitResult.CONTINUE;
                    }
                });

                Logger.add(Prefix.BUILD_BACKUP,  "Folder '" + folderPath + "' successfully deleted.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	
	public static void zipFolder(String sourceFolderPath, String destinationZipFilePath) {
        try {
            Path sourcePath = Paths.get(sourceFolderPath);
            File destinationZipFile = new File(destinationZipFilePath);

            // Create a ZipOutputStream to write to the destination zip file
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destinationZipFile))) {
                // Traverse the source folder and add its contents to the zip file
                Files.walk(sourcePath)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> addToZipFile(sourcePath, path, zipOutputStream));
            }

            Logger.add(Prefix.BUILD_BACKUP,  "Folder '" + sourceFolderPath + "' successfully zipped to '" + destinationZipFilePath + "'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addToZipFile(Path sourcePath, Path filePath, ZipOutputStream zipOutputStream) {
        try {
            // Create a ZipEntry representing the file within the zip file
            String relativePath = sourcePath.relativize(filePath).toString().replace("\\", "/");
            ZipEntry zipEntry = new ZipEntry(relativePath);
            Logger.add(Prefix.BUILD_BACKUP,  "Adding " + filePath.getFileName() + " to archive");
            // Add the ZipEntry to the zip file
            zipOutputStream.putNextEntry(zipEntry);
            
            // Copy the file content to the zip file
            Files.copy(filePath, zipOutputStream);

            // Close the current entry in the zip file
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	
	public static Boolean checkIfDirectory(String pathString) {
        Path path = Paths.get(pathString);

        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                return true;
            } 
        } else {
           
        }
        return false;
    }
	
	public static void createFolderIfNotExists(String folderPath) {
        Path path = Paths.get(folderPath);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }
	
	 public static String readStringFromFile(String filePath) {
	        Path path = Path.of(filePath);

	        try {
	            return Files.readString(path);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
	 public static boolean doesFileExist(String filePath) {
	        Path path = Path.of(filePath);
	        return Files.exists(path);
	    }
	
	public static void writeStringToFile(String filePath, String content) {
        Path path = Path.of(filePath);
        try {
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
