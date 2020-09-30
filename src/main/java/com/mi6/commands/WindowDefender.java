package com.mi6.commands;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowDefender extends RunCommads {

	private static final Logger log = LoggerFactory.getLogger(WindowDefender.class);

	private static boolean running = false;

	public void startDefender() {
		log.info("Starting Window Defender using Command Line. ");
		cmdLine.add("sc");
		cmdLine.add("start");
		cmdLine.add("Windefend");
		runCommand();
	}

	public void stopDefender() {
		log.info("Stoping Window Defender using Command Line. ");
		cmdLine.add("sc");
		cmdLine.add("stop");
		cmdLine.add("Windefend");
		runCommand();
	}

	public void queryDefender() {
		log.info("Checking Status of Windows Defender. ");
		cmdLine.add("sc");
		cmdLine.add("query");
		cmdLine.add("Windefend");
		runCommand();
	}

	public boolean setDefenderEnable(boolean b) {
		log.info("Enabling Windows Defender using Registory");
		String key = "HKLM\\SOFTWARE\\Policies\\Microsoft\\Windows Defender";
		String value = "DisableAntiSpyware";
		try {
			cmdLine.add("REG");
			cmdLine.add("ADD");
			cmdLine.add(key);
			cmdLine.add("/v");
			cmdLine.add(value);
			cmdLine.add("/t");
			cmdLine.add("REG_DWORD");
			cmdLine.add("/d");
			cmdLine.add((b?"0":"1"));
			cmdLine.add("/f");
			cmdLine.add("/reg:64");
			runCommand();
		} catch (Exception e) {
			log.error("Cannot Disable Windows Defender ", e.getMessage());
			log.trace(", e");
			return false;
		}
		return true;
	}

	public static boolean isRunning() {
		WindowDefender defender = new WindowDefender();
		defender.queryDefender();
		return running;
	}

	@Override
	public void output(String value) {

		if (value.contains("STOPPED")) {
			running = false;
			publish("stopped");
		}

		if (value.contains("RUNNING")) {
			running = true;
			publish("running");
		}

	}

	@Override
	public void error(String error) {
		publish("error", error);
		log.error(error);

	}

	public static void main(String[] args) throws IOException {
		WindowDefender defender = new WindowDefender();
		defender.setDefenderEnable(true);
	}

}
