package alarmclock;

import com.google.gson.reflect.TypeToken;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Clock extends FileWriteFunctions {
    private LocalTime currentTime;
    private ArrayList<Alarm> alarms;
    private String alarmFilePath;
    private final String idObjFilePath = "C:\\Users\\User\\OneDrive\\Coding projects\\Java tutorial\\Alarm_Clock\\data_files\\IDS.ser\\IDS.ser";
    private Clip alarmClip;
    private ClockListener listener;
    private Alarm lastTriggeredAlarm;

    // Constructor
    Clock(){
        currentTime = LocalTime.now();
        alarms = new ArrayList<>();

        Thread timeUpdater = new Thread(new TimeUpdater());
        timeUpdater.setDaemon(true);
        timeUpdater.start();
        Thread alarmChecker = new Thread(new AlarmChecker());
        alarmChecker.start();
    }


    // Bellow are getter/ setter methods
    public LocalTime getCurrTime(){
        return this.currentTime;
    }

    public ArrayList<Alarm> getAlarms(){
        return alarms;
    }

    // Returns single instance of alarm based on ID
    public Alarm getAlarmAtByID(int ID){
        Alarm alarm = findAlarmByID(ID);
        if (alarm != null){
            return alarm;
        }
        else
        {
            System.out.println("Unable to return alarm, invalid index");
            return null;
        }
    }

    // Function to add alarm objects to array list
    public void addAlarm(Alarm alarm){ alarms.add(alarm); }

    // Filter through alarms for alarms with ID to remove
    private Alarm findAlarmByID(int ID){
        for (Alarm alarm : alarms){
            if (alarm.getID() == ID){
                return alarm;
            }
        }
        return null;
    }

    // Filter through object file and remove IDs
    private void writeCurrentIDs(){
        try (FileOutputStream fileOut = new FileOutputStream(idObjFilePath);
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)){
            for(Alarm alarm : alarms){
                objOut.writeObject((Object) alarm.getID());
            }
        }
        catch (FileNotFoundException e){ // order of exceptions matters in this case
            System.out.println("Could not locate file location. WriteCurrentIDs()");
        }
        catch (IOException e){
            System.out.println("Could not write file. WriteCurrentIDs()");
        }
        catch (Exception e){
            System.out.println("Something went wrong in write object function. WriteCurrentIDs()");
        }

    }

    // Removing alarm
    private void removeAlarm(int ID){
        alarms.remove(findAlarmByID(ID));
        SerialiseObjectJson(this.alarmFilePath, alarms);
    }

    // Remove alarm from app
    public void removeAlarmByID(int ID){
        if (alarms.isEmpty()){
            readAlarms(this.alarmFilePath);
        }
        removeAlarm(ID);
        writeCurrentIDs();
    }

    // Write a single alarm to a json file for saving
    public void writeSingleAlarm(String filePathAlarm, String filePathID, int index){
        SerialiseObjectJson(filePathAlarm, alarms.get(index));
    }

    // Writes all alarms to a json file for saving
    public void writeAllAlarms(String filePath) { SerialiseObjectJson(filePath, alarms); }

    // Read alarms from json file
    public void readAlarms(String filePath){
        this.alarmFilePath = filePath;
        Type alarmListType = new TypeToken<ArrayList<Alarm>>(){}.getType();
        this.alarms = ReadObject(filePath, alarmListType);

        // Fix: truncate all times to seconds to avoid nanoseconds in JSON
        if (this.alarms != null) {
            for (Alarm alarm : this.alarms) {
                alarm.setAlarmTime(alarm.getAlarmTime().truncatedTo(ChronoUnit.SECONDS));
            }
        } else {
            this.alarms = new ArrayList<>(); // ensure it's never null
        }
    }

    // Clock listener to post clock events
    public void setClockListener(ClockListener listener){
        this.listener = listener;
    }

    // Used to keep an accurate recording of time
    private class TimeUpdater implements Runnable{
        // Updates time every 1 second
        @Override
        public void run(){
            while (true){
                currentTime = LocalTime.now();
                // Notify listener whenever the time updates
                if (listener != null){
                    listener.onTimeChange(currentTime);
                }
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted! TimeUpdater class");
                    break;
                }
            }
        }
    }

    // Converts to day ENUM, initially worked with enums, hassle to go back and refactor
    private DayEnum convertDayToDayEnum(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> DayEnum.MONDAY;
            case TUESDAY -> DayEnum.TUESDAY;
            case WEDNESDAY -> DayEnum.WEDNESDAY;
            case THURSDAY -> DayEnum.THURSDAY;
            case FRIDAY -> DayEnum.FRIDAY;
            case SATURDAY -> DayEnum.SATURDAY;
            case SUNDAY -> DayEnum.SUNDAY;
        };
    }

    private class AlarmChecker implements Runnable{
        // Checks whether alarm needs to go off
        @Override
        public void run(){
            while (true){
                LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                DayOfWeek today = LocalDate.now().getDayOfWeek();

                for (Alarm alarm: alarms){
                    if (alarm.getStatus()
                        && !alarm.isPlaying()
                        && alarm.getAlarmTime().truncatedTo(ChronoUnit.SECONDS).equals(now) // Change to seconds to avoid nanosecond errors
                        && alarm.getDays().contains(convertDayToDayEnum(today))){
                        // Now we start music after alarm is active, time = alarm time and day is active
                        startAlarmMusic(alarm);

                        if (listener != null){
                            listener.onAlarmTriggered(alarm);
                        }
                    }
                }
                try {
                    Thread.sleep(500); // Checks twice a second for us
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted! AlarmChecker class");
                    break;
                }
            }
        }
    }

    // Logic to start the alarm music
    private void startAlarmMusic(Alarm alarm) {
        lastTriggeredAlarm = alarm; // to keep the reference
        String filePath =
                "C:\\Users\\User\\OneDrive\\Coding projects\\Java tutorial\\Alarm_Clock\\Music\\tropical-alarm-clock-168821.wav";
        alarm.setPlaying(true);
        try {
            File file = new File(filePath);
            System.out.println("Attempting to play alarm file: " + file.getAbsolutePath() + " exists=" + file.exists());

            if (file.exists()){
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                alarmClip = AudioSystem.getClip();
                alarmClip.open(audioStream);
                alarmClip.start();
            }
            else {
                System.err.println("Alarm audio file does not exist: " + file.getAbsolutePath());
            }

        }
        catch (FileNotFoundException e){
            System.out.println("Could not locate file!");
        }
        catch (UnsupportedAudioFileException e){
            System.out.println("Audio file is not supported!");
        }
        catch (LineUnavailableException e){
            System.out.println("Unable to access audio resource!");
        }
        catch (IOException e){
            System.out.println("Something went wrong!");
        }
    }

    // Stops playing music
    public void stopAlarmMusic(){
        if (alarmClip != null && alarmClip.isRunning()){
            alarmClip.stop();
            alarmClip.close();
        }

        if (listener != null && lastTriggeredAlarm != null){
            listener.onAlarmStop(lastTriggeredAlarm);
            lastTriggeredAlarm.setPlaying(false);
            lastTriggeredAlarm = null; // Reset
        }
    }

}






