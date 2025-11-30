package alarmclock;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;

public class FileWriteFunctions {

    // Logic to serialise an object to a JSON file
    public static void SerialiseObjectJson(String filePath, Object data){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new JsonSerializer<LocalTime>() {
                    @Override
                    public JsonElement serialize(LocalTime time, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context){
                        return new JsonPrimitive(time.toString());
                    }
                })
                .setPrettyPrinting()
                .create();

        String jsonOutput = gson.toJson(data);

        // Writing to JSON file
        FileWriter fw = null;
        try {
            File file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
            }

            fw = new FileWriter(file);
            fw.write(jsonOutput);
            System.out.println("Successfully written alarm object to JSON file. SerialiseObjectJson()");
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Unable to open file! SerialiseObjectJson()");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Something went wrong in Json write object function! SerialiseObjectJson()" );
        }
        finally {
            try {
                if (fw!=null){
                    fw.close();
                }
            }
            catch (Exception e){
                System.out.println("Error closing filewriter, in write object json file function! SerialiseObjectJson()");
            }
        }
    }

    // Read objects from json file
    public static <T> T ReadObject(String filePath, Type typeOfT){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, context)
                        -> LocalTime.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            return gson.fromJson(reader, typeOfT);
        }
        catch(FileNotFoundException e){
            System.out.println("Could not locate file. ReadObject()");
        }
        catch(IOException e){
            System.out.println("Could not read file, something went wrong. ReadObject()");
        }
        catch (JsonParseException e){
            System.out.println("Error with deserializing json file contents. ReadObject()");
        }
        catch (Exception e){
            System.out.println("Something went wrong in read Json object function. ReadObject()");
        }

        System.out.println("Object never populates in read json function. ReadObject()");
        return null;
    }

    // Writes data to a file using FileWriter
    public static void WriteUsedID(String filePath, Object data) {
        File file = new File(filePath);

        boolean fileExists = file.exists();
        boolean fileIsEmpty = !fileExists || file.length() == 0;

        try{
            if (!fileExists || fileIsEmpty){
                try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
                    oos.writeObject(data);
                }
            }
            else{
                try (AppendObjectOutputStream aoos = new AppendObjectOutputStream(new FileOutputStream(file, true))){
                    aoos.writeObject(data);
                }
            }

        }
        catch (IOException e){
            System.out.println("Could not write file. WriteUsedID()");
            e.printStackTrace();
        }
    }


    // Reads objects from a file to a FileWriter
    public static ArrayList<Object> ReadUsedID(String filePath) {
        ArrayList<Object> data = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            System.out.println("File does not exist or is empty. ReadUsedID()");
            return data;
        }

        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream objectOut = new ObjectInputStream(fileIn)) {

            while (true) {
                try {
                    Object obj = objectOut.readObject();
                    data.add(obj);
                }
                catch (EOFException e) {
                    break; // end of stream/ file
                }
                catch(ClassNotFoundException e){
                    System.out.println("Class not found while reading object. ReadUsedID()");
                    e.printStackTrace();
                    break;
                }
            }

        }
        catch (FileNotFoundException e) {
            System.out.println("Could not locate file location. ReadUsedID()");
        }
        catch (StreamCorruptedException e) {
            System.out.println("Stream header corrupted. This may be due to an incorrect append. ReadUsedID()");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("I/O error while reading file. ReadUsedID()");
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("Something went wrong in read object function. ReadUsedID()");
            e.printStackTrace();
        }

        return data;
    }

}
