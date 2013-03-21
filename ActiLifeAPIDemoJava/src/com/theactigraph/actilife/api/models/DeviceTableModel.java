package com.theactigraph.actilife.api.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.theactigraph.actilife.ISO8601DateParser;
import com.theactigraph.actilife.Utils;

public class DeviceTableModel extends AbstractTableModel {

	private int rows = 0;
	final List<Object[]> data = new ArrayList<Object[]>();
	final String[] columnNames = new String[] { "Serial #", "ANT ID",
			"Subject Name", "Status", "Started", "Stopped", "Firmware",
			"Sample Rate", "Battery Voltage", "Battery Percentage" };

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row < 0 || row >= getRowCount()) return "";//why does this happen????
		return data.get(row)[col];
	}

	public void setValueAt(int row, int col, String value) {
		data.get(row)[col] = value;
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public Object[] getColumnNames() {
		return columnNames;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public void addRow(Device d) {
		data.add(new Object[] { d.getSerial(), d.getAntId(), d.getSubject(),
				d.getStatus(), d.getStartTime(), d.getStopTime(),
				d.getFirmware(), String.valueOf(d.getSampleRate()),
				d.getBatteryVoltage(), d.getBatteryPercentage() });

		this.fireTableDataChanged();
	}

	public void updateRow(int rowIndex, Device d) {
		setValueAt(rowIndex, 0, d.getSerial());
		setValueAt(rowIndex, 1, d.getAntId());
		setValueAt(rowIndex, 2, d.getSubject());
		setValueAt(rowIndex, 3, d.getStatus());
		setValueAt(rowIndex, 4, d.getStartTime());
		setValueAt(rowIndex, 5, d.getStopTime());
		setValueAt(rowIndex, 6, d.getFirmware());
		setValueAt(rowIndex, 7, d.getSampleRate());
		setValueAt(rowIndex, 8, d.getBatteryVoltage());
		setValueAt(rowIndex, 9, d.getBatteryPercentage());

		this.fireTableDataChanged();
	}

	public void addOrUpdateRow(Device d) {
		boolean updated = false;
		String newSerial = d.getSerial().replace(".0", "");
		String newAntId = d.getAntId().replace(".0", "");
		
		for (int i = 0; i < data.size(); i++) {
			String thisSerial = data.get(i)[0].toString().replace(".0", "");
			String thisAntId = data.get(i)[1].toString().replace(".0", "");
			
			if ( (!newSerial.equals("") && newSerial.equals(thisSerial)) || (!newAntId.equals("") && newAntId.equals(thisAntId)) ) {
				updateRow(i, d);
				updated = true;
			}
		}
		if (!updated) {
			addRow(d);
		}
	}

	public void clear() {
		while (data.size() > 0)
			data.remove(0);

		this.fireTableDataChanged();
	}
}
