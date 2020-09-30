package com.mi6.commands;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RunCommads {

	private static final Logger log = LoggerFactory.getLogger(RunCommads.class);

	/**
	 * Kill the EXE if they are open for some reason.
	 */
	protected static final String KILL = "taskkill /F /IM ";

	protected PropertyChangeSupport support;

	/**
	 * Command Line to pass to the EXE
	 */
	protected List<String> cmdLine;

	public RunCommads() {
		cmdLine = new ArrayList<>();
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	protected void publish(String evtName) {
		publish(evtName, null);
	}

	protected void publish(String evtName, Object value) {
		support.firePropertyChange(evtName, null, value);
	}

	public abstract void output(String value);
	public abstract void error(String error);


	public boolean runCommand() {

		ProcessBuilder builder = new ProcessBuilder(cmdLine);

		Process process;

		builder.redirectErrorStream(true);

		try {
			process = builder.start();
			setStream(process);

			publish("wait", true);
			process.waitFor();
			publish("wait", false);
			return true;

		} catch (IOException e1) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}

	}

	
	private void setStream(Process process) {
		setProcessErrorStream(process.getErrorStream());
		setProcessOutputStream(process.getInputStream());
	}

	public void setProcessErrorStream(InputStream inputStream){
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				error(line);
			}
		} catch (IOException e) {
			log.error("Error Output Stream ", e.getMessage());
			log.trace(" ", e);
		}

	}

	public void setProcessOutputStream(InputStream inputStream){
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				output(line);
			}
		} catch (IOException e) {
			log.error("Error Output Stream ", e.getMessage());
			log.trace(" ", e);
		}
	}

}

