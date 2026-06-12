import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Smart File Manager Pro
 * * Features:
 * 1. Read File
 * 2. Write File
 * 3. Append Content
 * 4. Modify Specific Line
 * 5. Replace Text
 * 6. Search Keyword
 * 7. File Analytics
 * 8. Automatic Backup
 * 9. Transaction Logging
 *
 * Author: Sneha (Refined)
 */
public class SmartFileManager {

    private static final Scanner sc = new Scanner(System.in);

    private static final String FILE_NAME = "sample.txt";
    private static final String BACKUP_FILE = "backup_sample.txt";
    private static final String LOG_FILE = "transaction_log.txt";

    public static void main(String[] args) {
        // Ensure the base file exists right at the start to prevent early crashes
        ensureFileExists();

        int choice = 0;

        try {
            do {
                System.out.println("\n=================================");
                System.out.println("      SMART FILE MANAGER PRO");
                System.out.println("=================================");
                System.out.println("1. Read File");
                System.out.println("2. Write File");
                System.out.println("3. Append Content");
                System.out.println("4. Modify Specific Line");
                System.out.println("5. Replace Text");
                System.out.println("6. Search Keyword");
                System.out.println("7. File Analytics");
                System.out.println("8. Exit");
                System.out.print("Enter Choice: ");
  
                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                    sc.nextLine(); // Consume newline
                } else {
                    System.out.println("Invalid Input! Please enter a number.");
                    sc.nextLine(); // Clear the invalid input buffer
                    continue;
                }

                switch (choice) {
                    case 1 -> readFile();
                    case 2 -> writeFile();
                    case 3 -> appendFile();
                    case 4 -> modifyLine();
                    case 5 -> replaceText();
                    case 6 -> searchKeyword();
                    case 7 -> fileAnalytics();
                    case 8 -> System.out.println("Exiting System...");
                    default -> System.out.println("Invalid Choice! Please select 1-8.");
                }
            } while (choice != 8);
        } catch (Exception e) {
            System.out.println("An unexpected system error occurred: " + e.getMessage());
        } finally {
            sc.close(); // Clean up system resources and avoid memory leak
        }
    }

    /**
     * Checks if target file exists, creates it if not.
     */
    private static void ensureFileExists() {
        try {
            Path path = Paths.get(FILE_NAME);
            if (!Files.exists(path)) {
                Files.createFile(path);
                logTransaction("INITIALIZED SYSTEM FILE");
            }
        } catch (IOException e) {
            System.out.println("Initialization Error: " + e.getMessage());
        }
    }

    /**
     * Records every operation performed.
     */
    private static void logTransaction(String operation) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fw.write(time + " -> " + operation + "\n");
        } catch (IOException e) {
            System.out.println("Log Error: " + e.getMessage());
        }
    }

    /**
     * Creates backup before modification.
     * @return true if successful, false otherwise.
     */
    private static boolean createBackup() {
        try {
            Path source = Paths.get(FILE_NAME);
            if (!Files.exists(source)) {
                System.out.println("Backup Error: Source file does not exist yet.");
                return false;
            }
            Files.copy(source, Paths.get(BACKUP_FILE), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup Created Successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Backup Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads file content.
     */
    private static void readFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            System.out.println("\n----- FILE CONTENT -----");
            if (lines.isEmpty()) {
                System.out.println("[File is empty]");
            } else {
                for (String line : lines) {
                    System.out.println(line);
                }
            }
            logTransaction("READ FILE");
        } catch (IOException e) {
            System.out.println("Read Error: " + e.getMessage());
        }
    }

    /**
     * Overwrites file content. Safeguards with a backup verification step first.
     */
    private static void writeFile() {
        try {
            // Guard clause added to verify backup safety before clearing target contents
            if (!createBackup()) {
                System.out.println("Write operation aborted due to failed backup safety protection.");
                return;
            }
            
            System.out.println("Enter Content:");
            String content = sc.nextLine();

            Files.write(Paths.get(FILE_NAME), content.getBytes());
            System.out.println("File Written Successfully.");
            logTransaction("WRITE FILE");
        } catch (IOException e) {
            System.out.println("Write Error: " + e.getMessage());
        }
    }

    /**
     * Appends content to file safely.
     */
    private static void appendFile() {
        try {
            System.out.println("Enter Content To Append:");
            String content = sc.nextLine();
            
            // Check if file has content already to avoid an ugly leading newline on empty files
            long fileSize = Files.size(Paths.get(FILE_NAME));
            String formattedContent = (fileSize > 0) ? "\n" + content : content;

            Files.write(
                    Paths.get(FILE_NAME),
                    formattedContent.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            System.out.println("Content Appended.");
            logTransaction("APPEND CONTENT");
        } catch (IOException e) {
            System.out.println("Append Error: " + e.getMessage());
        }
    }

    /**
     * Modify a specific line safely.
     */
    private static void modifyLine() {
        try {
            if (!createBackup()) return; // Abort if backup failed

            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            if (lines.isEmpty()) {
                System.out.println("File is empty. Nothing to modify.");
                return;
            }

            System.out.print("Enter Line Number (1 to " + lines.size() + "): ");
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Line number must be an integer.");
                sc.nextLine(); // Clear buffer
                return;
            }
            int lineNo = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (lineNo < 1 || lineNo > lines.size()) {
                System.out.println("Invalid Line Number.");
                return;
            }

            System.out.print("Enter New Content: ");
            String newContent = sc.nextLine();

            lines.set(lineNo - 1, newContent);
            Files.write(Paths.get(FILE_NAME), lines);

            System.out.println("Line Modified.");
            logTransaction("MODIFY LINE");
        } catch (IOException e) {
            System.out.println("Modify Error: " + e.getMessage());
        }
    }

    /**
     * Replace text throughout file.
     */
    private static void replaceText() {
        try {
            if (!createBackup()) return; // Abort if backup failed

            System.out.print("Old Text: ");
            String oldText = sc.nextLine();

            System.out.print("New Text: ");
            String newText = sc.nextLine();

            String content = Files.readString(Paths.get(FILE_NAME));
            
            if (!content.contains(oldText)) {
                System.out.println("Text sequence not found. No modifications made.");
                return;
            }

            content = content.replace(oldText, newText);
            Files.writeString(Paths.get(FILE_NAME), content);

            System.out.println("Replacement Completed.");
            logTransaction("REPLACE TEXT");
        } catch (IOException e) {
            System.out.println("Replace Error: " + e.getMessage());
        }
    }

    /**
     * Search keyword with line numbers.
     */
    private static void searchKeyword() {
        try {
            System.out.print("Enter Keyword: ");
            String keyword = sc.nextLine();

            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            int count = 0;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println("Found at Line " + (i + 1) + ": " + lines.get(i).trim());
                    count++;
                }
            }

            System.out.println("Total Occurrences: " + count);
            logTransaction("SEARCH KEYWORD");
        } catch (IOException e) {
            System.out.println("Search Error: " + e.getMessage());
        }
    }

    /**
     * Counts lines, words and characters cleanly.
     */
    private static void fileAnalytics() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));

            int lineCount = lines.size();
            int wordCount = 0;
            int charCount = 0;

            for (String line : lines) {
                charCount += line.length();
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String[] words = trimmed.split("\\s+");
                    wordCount += words.length;
                }
            }

            System.out.println("\n------ FILE ANALYTICS ------");
            System.out.println("Lines      : " + lineCount);
            System.out.println("Words      : " + wordCount);
            System.out.println("Characters : " + charCount);

            logTransaction("FILE ANALYTICS");
        } catch (IOException e) {
            System.out.println("Analytics Error: " + e.getMessage());
        }
    }
}