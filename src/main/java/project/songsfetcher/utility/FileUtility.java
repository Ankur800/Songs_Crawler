package project.songsfetcher.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class FileUtility {

    public static boolean createFile(String fileNameWithPath){

        File file = new File(fileNameWithPath);
        boolean isFileCreated = false;

        try {
            isFileCreated = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isFileCreated;
    }

    public static ArrayList<String> readFileAsList(String fileName) {

        Scanner scanner = null;

        ArrayList<String> strings = new ArrayList<String>();

        try{
            File file = new File(fileName);
            scanner = new Scanner(file);

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                strings.add(line);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(scanner != null){
                //this is very important ----- CAUTION MEMORY LEAK PROBLEM
                scanner.close();
            }
        }
        return strings;
    }

    public static boolean writeToFile(String fileNameWithPath, String content) {

        try {
            FileWriter fileWriter = new FileWriter(fileNameWithPath);
            fileWriter.append(content);
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean appendToFile(String fileName, String content){

        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.append("\n");
            fileWriter.append(content);
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean deleteFile(String fileNameWithPath){
        File file = new File(fileNameWithPath);
        boolean isDeleted = file.delete();
        return isDeleted;
    }

    public static void main(String[] args) {

        System.out.println("This side is ANKUR KUMAR RAI");
        System.out.println("Running FileUtility at " + new Date().toString());

        String nameOfNewFile = "D:\\Array\\data\\practice\\file\\" + "create-file.txt";
        boolean created = createFile(nameOfNewFile);
        System.out.println("File created : " + created);

        ArrayList<String> stringArrayList = readFileAsList(nameOfNewFile);
        for(String row : stringArrayList){
            System.out.println("Line : " + row);
        }
        System.out.println("Number of lines in file : " + stringArrayList.size());

        String nameOfWriteFile = "D:\\Array\\data\\practice\\file\\" + "write-file.txt";

        boolean wroteToFile = writeToFile(nameOfWriteFile, "This file is generated on Ankur's system by Java.");

        for(int i=1;i<=100;i++){
            String data = i + "";
            appendToFile(nameOfWriteFile, data);
        }

        System.out.println("Append file length " + readFileAsList(nameOfWriteFile).size());

        String nameOfFileToBeDeleted = "C:\\Users\\Friends\\Desktop\\dell.pdf";
        boolean isDeleted = deleteFile(nameOfFileToBeDeleted);

        System.out.println("File deleted: " + isDeleted);

    }
}
