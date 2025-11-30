package alarmclock;

import java.time.LocalTime;

// Implements event based programming for notifying when events
public interface ClockListener {
    // Called when the clocks time changes (every second)
    void onTimeChange(LocalTime newTime);

    // Called when an alarm's time matches the current time and day
    void onAlarmTriggered(Alarm alarm);

    //  Called when an alarm is stopped
    void onAlarmStop(Alarm alarm);
}


