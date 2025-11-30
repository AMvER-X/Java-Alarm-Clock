package alarmclock;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// This class represents each alarm in the GUI
public class AlarmPanel extends JPanel {
    private JLabel timeLabel;
    private JCheckBox enableCheckbox;
    private JButton[] dayButtons = new JButton[7];
    private JButton editButton;

    private boolean[] daysSelected = new boolean[7];
    private DayEnum[] daysChoice = {DayEnum.MONDAY, DayEnum.TUESDAY, DayEnum.WEDNESDAY, DayEnum.THURSDAY, DayEnum.FRIDAY, DayEnum.SATURDAY, DayEnum.SUNDAY};
    private LocalTime alarmTime;

    private int alarmWidth = 450, alarmHeight = 100;

    private Alarm alarm;

    // Code for displaying the alarms
    public AlarmPanel(Alarm alarm){
        this.alarm = alarm;
        this.alarmTime = alarm.getAlarmTime();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        setMaximumSize(new Dimension(alarmWidth, alarmHeight)); // NOTE THAT THESE DIMS CAN CHANGE
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(alarmWidth, alarmHeight));

        // Code for Top Row panel, containing time + enable switch + edit button
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Time label
        timeLabel = new JLabel(alarm.getAlarmTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topRow.add(timeLabel);

        // Checkbox label
        enableCheckbox = new JCheckBox("Enabled");
        enableCheckbox.setSelected(alarm.getStatus());
        topRow.add(enableCheckbox);
        enableCheckbox.addActionListener(e -> alarm.setStatus(enableCheckbox.isSelected()));

        // EditButton
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editAlarmTime());
        topRow.add(editButton);

        add(topRow, BorderLayout.NORTH);

        // Code for Bottom Row, containing days of the week
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (int i = 0; i < days.length; i++){
            int dayIndex = i;
            JButton dayButton = new JButton(days[i]);
            dayButton.setBackground(Color.LIGHT_GRAY);
            dayButton.setFocusPainted(false);
            dayButton.addActionListener(e -> toggleDay(dayButton, dayIndex));
            dayButtons[i] = dayButton;
            daysPanel.add(dayButton);
        }

        add(daysPanel, BorderLayout.SOUTH);
    }

    private void toggleDay(JButton button, int index){
        daysSelected[index] = !daysSelected[index];
        button.setBackground(daysSelected[index] ? Color.GREEN : Color.LIGHT_GRAY);

        if (daysSelected[index] && !alarm.getDays().contains(daysChoice[index])){
            alarm.addDay(daysChoice[index]);
        }
        else
        if (!daysSelected[index] && alarm.getDays().contains(daysChoice[index])){
            alarm.removeDay(daysChoice[index]);
        }
    }

    private void editAlarmTime(){
        String newTime = JOptionPane.showInputDialog(this,
                "Enter new time (HH:mm:ss):",
                alarmTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );

        if (newTime != null && newTime.matches("\\d{2}:\\d{2}:\\d{2}")){
            try{
                alarmTime = LocalTime.parse(newTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
                alarm.setAlarmTime(alarmTime);
                timeLabel.setText(alarmTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid time format.");
            }
        }
    }

    public boolean isEnabled(){
        return enableCheckbox.isSelected();
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public boolean[] getSelectedDays() {
        return daysSelected;
    }


}
