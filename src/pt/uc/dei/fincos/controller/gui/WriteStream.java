package pt.uc.dei.fincos.controller.gui;

import javax.swing.JOptionPane;

import pt.uc.dei.fincos.basic.EventType;

//Create or Update Streams
public class WriteStream {

	public static void updateEventType(EventType oldType, EventType newType) {
		JOptionPane.showMessageDialog(null, "Stream correctly updated.", "Update", JOptionPane.INFORMATION_MESSAGE);		
	}

	public static void addEventType(EventType newType) {
		JOptionPane.showMessageDialog(null, "¡Stream created!", "Create", JOptionPane.INFORMATION_MESSAGE);
		System.out.println("WriteStreams:NewType: "+newType);
	}

}
