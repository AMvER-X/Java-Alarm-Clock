package alarmclock;
/*
 * Used as an enum to determine which day it is in the clock app + which days the alarms apply to
 * Author: AMvER.x
 */
public enum DayEnum {
    MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);
    private final int dayNumber;

    DayEnum(int dayNumber){
        this.dayNumber = dayNumber;
    }
    
    public int GetDayNumber(){
        return this.dayNumber;
    }
}
