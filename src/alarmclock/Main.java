package alarmclock;

/*
 * Alarm clock app, using graphics 2D for GUI as well as Java Swing
 * Author: AMvER.x
 */

/*
* TO-DO
* 5) Make it so users can add alarms in the GUI
* 6) I need to add logic to determine if that alarm time has been hit
* 7) I need to play a sound effect when alarm hits, use multi-threading for points 6 and 7

*
* */

import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame implements ClockListener{
	private JLabel clockLabel;
	private JPanel alarmsPanel;
	private List<AlarmPanel> alarmPanels;
	private Clock clock;
	// Code for GUI in constructor
	public Main(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int screenWidth = screenSize.width, screenHeight = screenSize.height;
		int borderTop = 10, borderLeft = 10, borderBottom = 10, borderRight = 10;

		setTitle("Alarm Clock App");
		setSize(screenWidth, screenHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		// Top clock label
		clockLabel = new JLabel();
		clockLabel.setFont(new Font("Ariel", Font.BOLD, 24));
		clockLabel.setHorizontalAlignment(SwingConstants.CENTER);

		clock = new Clock();
		clock.setClockListener(this);
		String alarmFilePath = "C:\\Users\\User\\OneDrive\\Coding projects\\Java tutorial\\Alarm_Clock\\data_files\\alarms.json";

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(clockLabel);

		JButton addAlarmButton = new JButton("Add Alarm");
		addAlarmButton.addActionListener(e ->{
			String input = JOptionPane.showInputDialog(this, "Enter alarm time (HH:mm:ss):", "Add Alarm", JOptionPane.PLAIN_MESSAGE);
			if (input != null && !input.trim().isEmpty()){
				try{
					LocalTime alarmTime = LocalTime.parse(input.trim(), DateTimeFormatter.ofPattern("HH:mm:ss"));
					Alarm newAlarm = new Alarm(alarmTime);
					clock.addAlarm(newAlarm);
					addAlarmPanel(alarmTime, newAlarm);
					clock.writeAllAlarms(alarmFilePath);
				}
				catch (Exception ex){
					JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		topPanel.add(addAlarmButton);

		add(topPanel, BorderLayout.NORTH);



		// Center panel for scrollable alarms
		alarmsPanel = new JPanel();
		alarmsPanel.setLayout(new BoxLayout(alarmsPanel, BoxLayout.Y_AXIS));
		alarmsPanel.setBorder(new EmptyBorder(borderTop, borderLeft, borderBottom, borderRight));

		alarmPanels = new ArrayList<>();

		JScrollPane scrollPane = new JScrollPane(alarmsPanel);
		add(scrollPane, BorderLayout.CENTER);

		clock.readAlarms(alarmFilePath);
		if (clock.getAlarms() != null) {
			for (Alarm alarm : clock.getAlarms()) {
				addAlarmPanel(alarm.getAlarmTime(), alarm);
			}
		}
		updateClock();

		setVisible(true);

	}

	private void updateClock() {
		clockLabel.setText(clock.getCurrTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
	}

	private void addAlarmPanel(LocalTime time, Alarm alarm){
		AlarmPanel panel = new AlarmPanel(alarm);
		alarmsPanel.add(panel);
		alarmPanels.add(panel);
		alarmsPanel.revalidate();
		alarmsPanel.repaint();
	}

	@Override
	public void onTimeChange(LocalTime newTime){
		SwingUtilities.invokeLater(() -> {
						clockLabel.setText(newTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		});
	}

	@Override
	public void onAlarmTriggered(Alarm alarm){
		SwingUtilities.invokeLater(() ->{
			int dialogueWidth = 300, dialogueHeight = 100;
			JDialog dialog = new JDialog(this, "Alarm Ringing", true);
			dialog.setSize(dialogueWidth, dialogueHeight);
			dialog.setLocationRelativeTo(this);

			JPanel panel = new JPanel(new BorderLayout());
			JLabel message = new JLabel("Alarm is ringing!\n" + alarm.getAlarmTime().toString(),  SwingConstants.CENTER);
			JButton stopButton = new JButton("Stop Alarm");

			//lastTriggeredAlarm = alarm;

			stopButton.addActionListener(e -> {
				clock.stopAlarmMusic();
				dialog.dispose();
			});

			panel.add(message, BorderLayout.CENTER);
			panel.add(stopButton, BorderLayout.SOUTH);
			dialog.add(panel);

			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		});
	}

	@Override
	public void onAlarmStop(Alarm alarm){
		SwingUtilities.invokeLater(() ->{
			JOptionPane.showMessageDialog(this, "Alarm Stopped " + alarm.getAlarmTime());
		});
	}




	public static void main(String[] args){
		SwingUtilities.invokeLater(Main::new);
	}
}
