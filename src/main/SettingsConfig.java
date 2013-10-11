package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import mapEditor.MapClass;

public class SettingsConfig {
	public static File configFile = new File("config");
	public static boolean AA = true;
	public static boolean vignetting = true;
	public static boolean flares = true;
	public static boolean fancyMenu = true;
	public static boolean sound = true;
	public static String presets = "Custom";
	public static boolean fullScreen = true;
	public static int CPUs;
	public static void readConfig() {
		Scanner in = null;
		String cur = null;
		try {
			try {
				in = new Scanner(new BufferedReader(new FileReader(configFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			in.useDelimiter(";\n*");
			while(in.hasNext()) {
				cur = in.next();
				String temp = "";
				if (cur.contains("Presets=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					presets = temp;
				} else if (cur.contains("AA=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					AA = Boolean.valueOf(temp);
				} else if (cur.contains("Vignetting=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					vignetting = Boolean.valueOf(temp);
				} else if (cur.contains("FancyMenu=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					fancyMenu = Boolean.valueOf(temp);
				} else if (cur.contains("Sound=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					sound = Boolean.valueOf(temp);
				} else if (cur.contains("FullScreen=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					fullScreen = Boolean.valueOf(temp);
				} else if (cur.contains("CPU=")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						temp += cur.charAt(i);
					}
					CPUs = Integer.valueOf(temp);
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	public static void writeConfig() {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileWriter(configFile));
			out.println("Presets=\"" + presets + "\";");
			out.println("AA=\"" + AA + "\";");
			out.println("Vignetting=\"" + vignetting + "\";");
			out.println("FancyMenu=\"" + fancyMenu + "\";");
			out.println("Sound=\"" + sound + "\";");
			out.println("FullScreen=\"" + fullScreen + "\";");
			out.println("CPU=\"" + CPUs + "\";");
			out.close();
		} catch (IOException e) {
			System.out.println("There was an error saving your settings.");
			e.printStackTrace();
		}

	}
}
