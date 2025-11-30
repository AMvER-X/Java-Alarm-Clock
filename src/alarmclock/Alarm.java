package alarmclock;



import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
/*
 * Alarm data structure, used for storing information on the alarms 
 * Author: AMvER.x
 */

public class Alarm extends FileWriteFunctions {
    private int ID = 0;
    private boolean status; // If alarm is on or off
    private Set<DayEnum> days; //List of days alarm applies to
    private LocalTime alarmTime;
    private boolean playing;
    private final String filePath = "C:\\Users\\User\\OneDrive\\Coding projects\\Java tutorial\\Alarm_Clock\\data_files\\IDS.ser"; // Yes hardcoded, bite me!
    // Constructor for alarm
    Alarm(LocalTime alarmTime){
        this.ID = assignID();
        this.days = new HashSet<>();
        this.alarmTime = alarmTime;
        this.status = true;
        this.playing = false;
    }

    // Bellow are getter/ setter methods
    public Set<DayEnum> getDays(){ return this.days; }
    public void setDays(Set<DayEnum> days) { this.days = days; }

    public int getID(){ return this.ID; }
    public void setID(int ID){ this.ID = ID; }

    public boolean getStatus(){ return this.status; }
    public void setStatus(boolean status){ this.status = status; }

    // Remove days which alarm will apply to
    public void removeDay(DayEnum day){
        this.days.remove(day);
    }

    public LocalTime getAlarmTime(){
        return this.alarmTime;
    }

    public void setAlarmTime(LocalTime time){ this.alarmTime = time; }

    // Add days which alarm will apply to
    public void addDay(DayEnum day){
        if (this.days.size() < 8){
            try {
                this.days.add(day);
            }
            catch (IllegalArgumentException e){
                System.out.println("You can't specify duplicate days!");
            }
        }
        else {
            System.out.println("Already 7 days selected");
        }
    }
    // Checkers for music playing
    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    // Function to check and asign ID
    // Implemented random integer IDs as a cheap imitation of non-indexable IDs in DBs for security purposes
    public int assignID(){

        File file = new File(this.filePath);
        int id = 0;

        if (file.exists()) {
            ArrayList<Integer> intIds = new ArrayList<>();
            ArrayList<Object> objIds;
            objIds = ReadUsedID(this.filePath);

            for (Object obj : objIds) {
                if (obj instanceof Integer) {
                    intIds.add((Integer) obj);
                }
                else {
                    System.err.println("Unexpected object type: " + obj.getClass());
                }
            }

            Random random = new Random();
            int num = random.nextInt();
            while (intIds.contains(num)) {
                num = random.nextInt();
            }
            id = num;
        }

        WriteUsedID(filePath, id);

        return id;
    }
}
