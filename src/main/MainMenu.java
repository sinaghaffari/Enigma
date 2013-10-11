package main;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.ListCellRenderer;

import tools.StackBlurFilter;


public class MainMenu extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
	private float featherModifier = 0.9f;
	private float logoAlphaModifier = 1;
	private int MouseX, MouseY;
	private int fadeAmount = 255;
	private int windowWidth = GameFrame.f.getContentPane().getWidth(), windowHeight = GameFrame.f.getContentPane().getHeight();
	StackBlurFilter filter = new StackBlurFilter(6, 1);
	private SButton startButton = new SButton(10, 300, 245, 100, "Start", 40);
	private SButton changeMapButton = new SButton (255, 300, 245, 100, "Change Map", 40);
	private SButton mapEditorButton = new SButton (500, 300, 245, 100, "Map Editor", 40);
	private SButton settingsButton = new SButton (745, 300, 245, 100, "Settings", 40);
	private SButton exitButton = new SButton(790, 565, 200, 25, "Exit Game", 20);
	private SButton returnButton = new SButton (790, 565, 200, 25, "Return", 20);
	private SButton backButton = new SButton (10, 565, 200, 25, "Back", 20);
	private SButton saveButton = new SButton (210, 565, 200, 25, "Save", 20);
	private Vector<SRadioButton> mapButtons = new Vector<SRadioButton>();
	private Vector<SButton> buttonList = new Vector<SButton>();
	private Vector<SRadioButton> radioButtonList = new Vector<SRadioButton>();
	private Vector<SRadioButton[]> settingsButtonList = new Vector<SRadioButton[]>();
	private SRadioButton[] presetButtons = new SRadioButton[3];
	private SRadioButton[] AAButtons = new SRadioButton[2];
	private SRadioButton[] vignettingButtons = new SRadioButton[2];
	private SRadioButton[] fancyMenuButtons = new SRadioButton[2];
	private SRadioButton[] flareButtons = new SRadioButton[2];
	private SRadioButton[] soundButtons = new SRadioButton[2];
	private SRadioButton[] fullScreenButtons = new SRadioButton[2];
	private boolean intro = true;
	private boolean fadeOut = false;
	private BufferedImage img;
	private BufferedImage blurLogo;
	private Clip bgSound = null;
	private Clip thumpSound = null;
	private AudioInputStream ais = null;
	private AudioInputStream thumpais = null;
	private boolean clickableButton = true;
	private boolean settings = false;
	private Font font;
	String presets = SettingsConfig.presets;
	boolean AA = SettingsConfig.AA;
	boolean vignetting = SettingsConfig.vignetting;
	boolean flares = SettingsConfig.flares;
	boolean fancyMenu = SettingsConfig.fancyMenu;
	boolean sound = SettingsConfig.sound;
	boolean fullScreen = SettingsConfig.fullScreen;
	int CPUs = SettingsConfig.CPUs;
	private boolean tempBool = false;
	Composite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
	public MainMenu( boolean intro ) {
		clickableButton = true;
		if (SettingsConfig.presets.equals("High")) {
			if (SettingsConfig.CPUs >= 4) {
				presetButtons[0] = new SRadioButton(windowWidth - 610, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "High Settings (R)", 20f, true);
				presetButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "Low Settings", 20f, false);
			} else {
				presetButtons[0] = new SRadioButton(windowWidth - 610, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "High Settings", 20f, true);
				presetButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "Low Settings (R)", 20f, false);
			}
		} else {
			if (SettingsConfig.CPUs < 4) {
				presetButtons[0] = new SRadioButton(windowWidth - 610, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "High Settings", 20f, false);
				presetButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "Low Settings (R)", 20f, true);
			} else {
				presetButtons[0] = new SRadioButton(windowWidth - 610, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "High Settings (R)", 20f, false);
				presetButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "Low Settings", 20f, true);
			}
		}
		presetButtons[2] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50, 200, 50, "Custom", 20f, false);
		settingsButtonList.add(presetButtons);

		AAButtons[0] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f + 50, 200, 50, "Off", 20f, true);
		AAButtons[1] = new SRadioButton(windowWidth - 210 - AAButtons[0].getBounds2D().getWidth(), (windowHeight - 50) / 7.0f + 50, 200, 50, "On", 20f, false);
		settingsButtonList.add(AAButtons);

		vignettingButtons[0] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f + 50, 200, 50, "Off", 20f, true);
		vignettingButtons[1] = new SRadioButton(windowWidth - vignettingButtons[0].getBounds2D().getWidth() - 210, (windowHeight - 50) / 7.0f + 50, 200, 50, "On", 20f, false);
		settingsButtonList.add(vignettingButtons);

		fancyMenuButtons[0] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f) + 50, 200, 50, "Off", 20f, true);
		fancyMenuButtons[1] = new SRadioButton(windowWidth - vignettingButtons[0].getBounds2D().getWidth() - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f) + 50, 200, 50, "On", 20f, false);
		settingsButtonList.add(fancyMenuButtons);

		flareButtons[0] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 2) + 50, 200, 50, "Off", 20f, true);
		flareButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 2) + 50, 200, 50, "On", 20f, false);
		settingsButtonList.add(flareButtons);

		soundButtons[0] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 3) + 50, 200, 50, "Off", 20f, true);
		soundButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 3) + 50, 200, 50, "On", 20f, false);
		settingsButtonList.add(soundButtons);

		fullScreenButtons[0] = new SRadioButton(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 4) + 50, 200, 50, "Off", 20f, true);
		fullScreenButtons[1] = new SRadioButton(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 4) + 50, 200, 50, "On", 20f, false);
		settingsButtonList.add(fullScreenButtons);
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("font/Erbar LT Bold Condensed.ttf"));
			font = font.deriveFont(30f);
		} catch (FontFormatException e) {} catch (IOException e) {}
		try {
			img = ImageIO.read(new File("img/SplashScreen/EnigmaLogo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		initFancyComponents();
		this.intro = intro;
		setSize(windowWidth, windowHeight);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		initBackGround(SettingsConfig.fancyMenu);
		repainter();
		if (this.intro) {
			intro();
			if (SettingsConfig.sound)
				MusicStart();
		} else {
			if (SettingsConfig.sound)
				MusicStart();
			initButtons();
			fadeIn(0.5f, 0);
		}
	}
	public void initFancyComponents() {
		blurLogo = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) blurLogo.getGraphics();
		g2.drawImage(img, filter, 0, 0);
		initBackGround( true );
	}
	public void disableFancyComponents() {
		blurLogo = null;
	}
	public void intro() {
		Thread introThread = new Thread(new Runnable() {
			public void run() {
				fadeIn(0.5f, 1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				fadeOut(0.5f, 1);
				Desktop d = Desktop.getDesktop();
				try {
					d.open(new File("Instructions.txt"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				intro = false;
				initButtons();
				fadeIn(0.5f, 0);
			}
		});
		introThread.start();		
	}
	public void initBackGround( boolean b ) {
		if (b) {
			featherAnim();
			logoAlphaAnim();
		}
	}
	public void initButtons() {
		add(startButton);
		add(changeMapButton);
		add(mapEditorButton);
		add(settingsButton);
		add(exitButton);
	}
	public void MusicStart() {
		Thread menuMusicThread = new Thread(new Runnable() {
			public void run() {
				try {
					bgSound = AudioSystem.getClip();
				} catch (LineUnavailableException e1) {}
				try {
					ais = AudioSystem.getAudioInputStream( new File("audio/Menu Ambience.wav") );
				} catch (UnsupportedAudioFileException e1) {} catch (IOException e1) {}
				try {
					bgSound.open(ais);
				} catch (LineUnavailableException e1) {} catch (IOException e1) {}
				bgSound.loop(-1);
			}
		});
		menuMusicThread.start();
	}
	public void MusicStop() {
		bgSound.close();
		try {
			ais.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void startGame() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				thumpSound();
				if (bgSound != null) {
					bgSound.close();
				}
				if (ais != null) {
					try {
						ais.close();
					} catch (IOException e) {}
				}
				fadeOut(0.5f, 1);
				try {
					GameFrame.gframe.startGame();
				} catch (IOException e) {}
			}
		});
		thread.start();
	}
	public void startMapEditor() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int password = 0;
				JPasswordField pwd = new JPasswordField(10);
				password = JOptionPane.showConfirmDialog(GameFrame.f, pwd, "Enter the developer password.", JOptionPane.OK_CANCEL_OPTION);
				while (!new String(pwd.getPassword()).equals("PorkChops") && password == 0 ){
					password = JOptionPane.showConfirmDialog(GameFrame.f, pwd, "Incorrect password.", JOptionPane.OK_CANCEL_OPTION);
				}
				if (password == 0) {
					thumpSound();
					if (bgSound != null)
						bgSound.close();
					if (ais != null) {
						try {
							ais.close();
						} catch (IOException e) {}
					}
					fadeOut(0.5f, 1);
					GameFrame.startMapEditor();
				} else {
					clickableButton = true;
				}

			}
		});
		thread.start();
	}
	public void backToMainMenu() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				thumpSound();
				fadeOut(0.5f, 1);
				removeAll();
				initButtons();
				clickableButton = true;
				settings = false;
				fadeIn(0.5f, 1);
			}
		});
		thread.start();
	}
	public void saveSettings() {
		byte invokeFancyMenu = 0;
		byte invokeSound = 0;
		byte invokeFullScreen = 0;
		if (fancyMenu) {
			if (SettingsConfig.fancyMenu != fancyMenu) {
				invokeFancyMenu = 1;
			}
		} else {
			if (SettingsConfig.fancyMenu != fancyMenu)
				invokeFancyMenu = 2;
		}
		if (sound) {
			if (SettingsConfig.sound != sound)
				invokeSound = 1;
		} else {
			if (SettingsConfig.sound != sound)
				invokeSound = 2;
		}
		if (sound) {
			if (SettingsConfig.sound != sound)
				invokeSound = 1;
		} else {
			if (SettingsConfig.sound != sound)
				invokeSound = 2;
		}
		if (fullScreen) {
			if (SettingsConfig.fullScreen != fullScreen)
				invokeFullScreen = 1;
		} else {
			if (SettingsConfig.fullScreen != fullScreen)
				invokeFullScreen = 2;
		}
		SettingsConfig.presets = presets;
		SettingsConfig.AA = AA;
		SettingsConfig.vignetting = vignetting;
		SettingsConfig.flares = flares;
		SettingsConfig.fancyMenu = fancyMenu;
		SettingsConfig.sound = sound;
		SettingsConfig.fullScreen = fullScreen;
		SettingsConfig.writeConfig();
		if (invokeFancyMenu == 1) {
			initFancyComponents();
		} else if (invokeFancyMenu == 2) {
			disableFancyComponents();
		}
		if (invokeSound == 1) {
			MusicStart();
		} else if (invokeSound == 2) {
			MusicStop();
		}
		if (invokeFullScreen == 1) {
			GameFrame.gframe.initAFrame(true);
		} else if (invokeFullScreen == 2) {
			GameFrame.gframe.initAFrame(false);
		}
		backToMainMenu();
	}
	public void settings() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				checkButtons();

				thumpSound();
				fadeOut(0.5f, 1);
				remove(startButton);
				remove(changeMapButton);
				remove(mapEditorButton);
				remove(settingsButton);
				remove(exitButton);
				add(saveButton);
				add(backButton);

				clickableButton = true;
				settings = true;
				fadeIn(0.5f, 1);
			}
		});
		t.start();
	}

	public void checkButtons() {
		if (presets.equals("Custom")) {
			presetButtons[0].setSelected(false);
			presetButtons[1].setSelected(false);
			presetButtons[2].setSelected(true);
		} else if (presets.equals("High")) {
			presetButtons[0].setSelected(true);
			presetButtons[1].setSelected(false);
			presetButtons[2].setSelected(false);
		} else if (presets.equals("L+ow")) {
			presetButtons[0].setSelected(false);
			presetButtons[1].setSelected(true);
			presetButtons[2].setSelected(false);
		}
		if (AA) {
			AAButtons[0].setSelected(false);
			AAButtons[1].setSelected(true);
		} else {
			AAButtons[0].setSelected(true);
			AAButtons[1].setSelected(false);
		}

		if (vignetting) {
			vignettingButtons[0].setSelected(false);
			vignettingButtons[1].setSelected(true);
		} else {
			vignettingButtons[0].setSelected(true);
			vignettingButtons[1].setSelected(false);
		}

		if (flares) {
			flareButtons[0].setSelected(false);
			flareButtons[1].setSelected(true);
		} else {
			flareButtons[0].setSelected(true);
			flareButtons[1].setSelected(false);
		}

		if (fancyMenu) {
			fancyMenuButtons[0].setSelected(false);
			fancyMenuButtons[1].setSelected(true);
		} else {
			fancyMenuButtons[0].setSelected(true);
			fancyMenuButtons[1].setSelected(false);
		}

		if (sound) {
			soundButtons[0].setSelected(false);
			soundButtons[1].setSelected(true);
		} else {
			soundButtons[0].setSelected(true);
			soundButtons[1].setSelected(false);
		}
		if (fullScreen) {
			fullScreenButtons[0].setSelected(false);
			fullScreenButtons[1].setSelected(true);
		} else {
			fullScreenButtons[0].setSelected(true);
			fullScreenButtons[1].setSelected(false);
		}

	}

	public void changeMap() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int password = 0;
				JPasswordField pwd = new JPasswordField(10);
				password = JOptionPane.showConfirmDialog(GameFrame.f, pwd, "Enter the developer password.", JOptionPane.OK_CANCEL_OPTION);
				while (!new String(pwd.getPassword()).equals("PorkChops") && password == 0 ){
					password = JOptionPane.showConfirmDialog(GameFrame.f, pwd, "Incorrect password.", JOptionPane.OK_CANCEL_OPTION);
				}
				if (password == 0) {
					thumpSound();
					fadeOut(0.5f, 1);
					remove(startButton);
					remove(changeMapButton);
					remove(mapEditorButton);
					remove(settingsButton);
					remove(exitButton);
					File folder = new File("levels/");
					File[] listOfFiles = folder.listFiles();
					String files;
					String MapName = "";
					String MapLength = "";
					String MapHeight = "";
					int counter = 0;
					for (int a = 0; a < listOfFiles.length; a++) {
						MapName = "";
						MapLength = "";
						MapHeight = "";
						if (listOfFiles[a].isFile()) {
							files = listOfFiles[a].getName();
							if (files.endsWith(".map") || files.endsWith(".MAP")){
								Scanner in = null;
								String cur = null;
								try {
									in = new Scanner(new BufferedReader(new FileReader(listOfFiles[a])));
									in.useDelimiter(";\n*");
									while(in.hasNext()) {
										cur = in.next();
										// (in.next());
										if (cur.contains("MapName:")) {
											for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
												MapName += cur.charAt(i);
											}
										} else if (cur.contains("MapLength:")) {
											for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
												MapLength += cur.charAt(i);
											}
										} else if (cur.contains("MapHeight:")) {
											for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
												MapHeight += cur.charAt(i);
											}
										}
									}
								} catch (FileNotFoundException e) {
								} finally {
									if (in != null) {
										in.close();
									}
								}
								if (GameFrame.getMap().getName().equals(listOfFiles[a].getName())) {
									mapButtons.add(new SRadioButton(10, 10 + (25 * counter), 200, 25, MapName, 20, listOfFiles[a], true));
								} else {
									mapButtons.add(new SRadioButton(10, 10 + (25 * counter), 200, 25, MapName, 20, listOfFiles[a], false));
								}
								counter++;
							}
						}
					}
					clickableButton = true;
					for (int a = 0; a < mapButtons.size(); a++) {
						add(mapButtons.get(a));
					}
					mapButtons.clear();
					add(returnButton);
					fadeIn(0.5f, 0);
				}
			}
		});
		thread.start();
	}
	public void paint( Graphics g ) {
		windowWidth = GameFrame.f.getContentPane().getWidth();
		windowHeight = GameFrame.f.getContentPane().getHeight();
		startButton.setSize((windowWidth - 20) / 4.0, 100);
		startButton.setLocation(10, windowHeight / 2.0);

		changeMapButton.setSize((windowWidth - 20) / 4.0, 100);
		changeMapButton.setLocation(10 + startButton.getBounds2D().getWidth(), windowHeight / 2.0);

		mapEditorButton.setSize((windowWidth - 20) / 4.0, 100);
		mapEditorButton.setLocation(10 + (2 * startButton.getBounds2D().getWidth()), windowHeight / 2.0);

		settingsButton.setSize((windowWidth - 20) / 4.0, 100);
		settingsButton.setLocation(10 + (3 * startButton.getBounds2D().getWidth()), windowHeight / 2.0);

		exitButton.setLocation(windowWidth - exitButton.getBounds2D().getWidth() - 10, windowHeight - exitButton.getBounds2D().getHeight() - 10);

		returnButton.setLocation(windowWidth - returnButton.getBounds2D().getWidth() - 10, windowHeight - returnButton.getBounds2D().getHeight() - 10);
		saveButton.setLocation(10, windowHeight - 35);
		backButton.setLocation(210, windowHeight - 35);


		AAButtons[0].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		AAButtons[1].setLocation(windowWidth - AAButtons[0].getBounds2D().getWidth() - 210, (windowHeight - 50) / 7.0f + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		vignettingButtons[0].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 3) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		vignettingButtons[1].setLocation(windowWidth - AAButtons[0].getBounds2D().getWidth() - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 3) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		fancyMenuButtons[0].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		fancyMenuButtons[1].setLocation(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		flareButtons[0].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 2) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		flareButtons[1].setLocation(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 2) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		soundButtons[0].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 4) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		soundButtons[1].setLocation(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 4) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		presetButtons[0].setLocation(windowWidth - 610, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		presetButtons[1].setLocation(windowWidth - 410, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		presetButtons[2].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f - (windowHeight - 50) / 7.0f + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		fullScreenButtons[0].setLocation(windowWidth - 210, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 5) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));
		fullScreenButtons[1].setLocation(windowWidth - 410, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 5) + 50 - (AAButtons[0].getBounds2D().getHeight() / 2.0 + 10));

		Graphics2D g2 = (Graphics2D)g;
		if (SettingsConfig.AA)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setClip(0, 0, windowWidth, windowHeight);
		g2.setPaint(new Color(58, 58, 58));
		g2.fillRect(0, 0, windowWidth, windowHeight);
		if (!intro && !settings) {
			if (SettingsConfig.fancyMenu) {
				//System.out.println("draw blurry thing");
				ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoAlphaModifier );
				Graphics2D g3 = (Graphics2D) g2.create();
				g3.setComposite(ac);
				g3.drawImage(blurLogo, (int)(50 + (windowWidth - 1000) / 2.0), (int)(150 + (windowHeight - 600) / 2.0), null);
			} else {
				g2.drawImage(img, (int)(50 + (windowWidth - 1000) / 2.0), (int)(150 + (windowHeight - 600) / 2.0), null);
			}
		}
		if (intro) {
			g2.drawImage(img, (int)((windowWidth / 2.0) - (img.getWidth() / 2.0)), (int)((windowHeight / 2.0) - (img.getHeight() / 2.0)), null);
		}
		g2.setPaint(new RadialGradientPaint(new Rectangle(-150, -150, this.getWidth() + 300, this.getHeight() + 300), new float[] {0.2f, featherModifier}, new Color[] {new Color(0, 0, 0, 0), Color.black}, CycleMethod.NO_CYCLE));
		g2.fillRect(0, 0, windowWidth, windowHeight);
		g2.setPaint(new Color(255, 255, 255, 200));
		g2.setFont(font);
		if (settings) {
			g2.drawString("Presets:", 40, (windowHeight - 50) / 7.0f - ((windowHeight - 50) / 7.0f) + 50);
			g2.drawString("Anti-Aliasing:", 40, (windowHeight - 50) / 7.0f + 50);
			g2.drawString("Fancy Menu:", 40, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f) + 50);
			g2.drawString("Muzzle Flash:", 40, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 2) + 50);
			g2.drawString("Dynamic Vegnetting:", 40, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 3) + 50);
			g2.drawString("Sound:", 40, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 4) + 50);
			g2.drawString("Full Screen:", 40, (windowHeight - 50) / 7.0f + ((windowHeight - 50) / 7.0f * 5) + 50);
			for (int i = 0; i < settingsButtonList.size(); i++) {
				for (int p = 0; p < settingsButtonList.get(i).length; p++) {
					settingsButtonList.get(i)[p].drawComponent(g, g2);
				}
			}
		}
		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).drawComponent(g, g2);
		}
		for (int i = 0; i < radioButtonList.size(); i++) {
			radioButtonList.get(i).drawComponent(g, g2);
		}
		g2.setPaint(new Color(0, 0, 0, fadeAmount));
		g2.fillRect(0, 0, windowWidth, windowHeight);
		if (!intro)
			drawCursor(g, g2);
	}
	public void drawCursor( Graphics g, Graphics2D g2 ) {
		g2.setPaint(new RadialGradientPaint(MouseX, MouseY, 3.5f, new float[] {0.2f, 1}, new Color[] {new Color(198, 219, 216), new Color(198, 219, 216, 0)}));
		g2.fill(new Ellipse2D.Double(MouseX - 3.5, MouseY - 3.5, 7, 7));
		if (SettingsConfig.fancyMenu) {
			g2.setPaint(new RadialGradientPaint(new Point2D.Double(MouseX, MouseY), (float)(Math.sqrt(Math.pow(windowWidth / 2.0, 2) + Math.pow(windowHeight / 2.0, 2))), new float[] {0.3f, 1}, new Color[] {new Color(198, 219, 216, 0), new Color(198, 219, 216)}, CycleMethod.NO_CYCLE));
			g2.drawLine(0, MouseY, windowWidth, MouseY);
			g2.drawLine(MouseX, 0, MouseX, windowHeight);
		}
	}
	public void repainter() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (!main.GameFrame.isGaming) {	
					repaint();
					try {
						Thread.sleep(33);
					} catch (InterruptedException e) {}
				}
			}
		});
		thread.start();
	}
	public void fadeOut( final float amount, int type) {
		fadeOut = true;
		if (type == 0) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					double tempFade = fadeAmount;
					while(tempFade + amount < 255) {
						tempFade += amount;
						fadeAmount = (int) Math.round(tempFade);
						if (!fadeOut) {
							break;
						}
						try {
							Thread.sleep(7);
						} catch (InterruptedException e) {}
					}
					if (fadeOut)
						fadeAmount = 255;
				}
			});
			thread.start();
		} else if (type == 1) {
			double tempFade = fadeAmount;
			while(tempFade + amount < 255) {
				tempFade += amount;
				fadeAmount = (int) Math.round(tempFade);
				if (!fadeOut) {
					break;
				}
				try {
					Thread.sleep(7);
				} catch (InterruptedException e) {}
			}
			if (fadeOut)
				fadeAmount = 255;
		}
	}
	public void fadeIn( final float amount, int type) {
		fadeOut = false;
		if (type == 0) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					double tempFade = fadeAmount;
					while(tempFade - amount > 0) {
						tempFade -= amount;
						fadeAmount = (int) Math.round(tempFade);
						try {
							Thread.sleep(7);
						} catch (InterruptedException e) {}
						if (fadeOut) {
							break;
						}
					}
					if (!fadeOut)
						fadeAmount = 0;
				}
			});
			thread.start();
		} else if (type == 1) {
			double tempFade = fadeAmount;
			while(tempFade - amount > 0) {
				tempFade -= amount;
				fadeAmount = (int) Math.round(tempFade);
				if (fadeOut) {
					break;
				}
				try {
					Thread.sleep(7);
				} catch (InterruptedException e) {}
			}
			if (!fadeOut)
				fadeAmount = 0;
		}
	}
	public void featherAnim() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (!main.GameFrame.isGaming && SettingsConfig.fancyMenu) {	
					if (featherModifier < 0.95 && featherModifier > 0.8) {
						if (Math.random() >= 0.5) {
							featherModifier += 0.02;
						} else {
							featherModifier -= 0.02;
						}
					} else if (featherModifier > 0.95) {
						featherModifier -= 0.03;
					} else if (featherModifier < 0.8) {
						featherModifier += 0.03;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			}
		});
		thread.start();
	}
	public void logoAlphaAnim() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (!main.GameFrame.isGaming && SettingsConfig.fancyMenu) {	
					if (logoAlphaModifier < 0.95 && logoAlphaModifier > 0.8) {
						if (Math.random() >= 0.5) {
							logoAlphaModifier += 0.05;
						} else {
							logoAlphaModifier -= 0.05;
						}
					} else if (logoAlphaModifier > 0.3) {
						logoAlphaModifier -= 0.07;
					} else if (logoAlphaModifier < 0.9) {
						logoAlphaModifier += 0.07;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			}
		});
		thread.start();
	}
	public void add ( SButton b ) {
		buttonList.add(b);
	}
	public void add( SRadioButton b ) {
		radioButtonList.add(b);
	}
	public void remove ( SButton b ) {
		buttonList.remove(b);
	}
	public void remove( SRadioButton b ) {
		radioButtonList.remove(b);
	}
	public void removeAll() {
		buttonList.clear();
		mapButtons.clear();
		radioButtonList.clear();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		MouseX = e.getX();
		MouseY = e.getY();
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		for (int i = 0; i < radioButtonList.size(); i++) {
			if (radioButtonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				radioButtonList.get(i).setContains(true);
			} else {
				radioButtonList.get(i).setContains(false);
			}
		}
		if (settings) {
			for (int i = 0; i < settingsButtonList.size(); i++) {
				for (int p = 0; p < settingsButtonList.get(i).length; p++) {
					if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
						settingsButtonList.get(i)[p].setContains(true);
					} else {
						settingsButtonList.get(i)[p].setContains(false);
					}
				}
			}
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		MouseX = e.getX();
		MouseY = e.getY();
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		for (int i = 0; i < radioButtonList.size(); i++) {
			if (radioButtonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				radioButtonList.get(i).setContains(true);
			} else {
				radioButtonList.get(i).setContains(false);
			}
		}
		if (settings) {
			for (int i = 0; i < settingsButtonList.size(); i++) {
				for (int p = 0; p < settingsButtonList.get(i).length; p++) {
					if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
						settingsButtonList.get(i)[p].setContains(true);
					} else {
						settingsButtonList.get(i)[p].setContains(false);
					}
				}
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		MouseX = e.getX();
		MouseY = e.getY();
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);
			} else {
				buttonList.get(i).setContains(false);
			}
		}
		for (int i = 0; i < radioButtonList.size(); i++) {
			if (radioButtonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				radioButtonList.get(i).setContains(true);
			} else {
				radioButtonList.get(i).setContains(false);
			}
		}
		if (settings) {
			for (int i = 0; i < settingsButtonList.size(); i++) {
				for (int p = 0; p < settingsButtonList.get(i).length; p++) {
					if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
						settingsButtonList.get(i)[p].setContains(true);
					} else {
						settingsButtonList.get(i)[p].setContains(false);
					}
				}
			}
		}

	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseReleased(MouseEvent e) {
		MouseX = e.getX();
		MouseY = e.getY();
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY()) && clickableButton) {
				clickableButton = false;
				if (buttonList.get(i).getThis() == startButton) {
					startGame();
				} else if (buttonList.get(i).getThis() == changeMapButton) {
					changeMap();
				} else if (buttonList.get(i).getThis() == mapEditorButton) {

					startMapEditor();
				} else if (buttonList.get(i).getThis() == returnButton) {
					backToMainMenu();
				} else if (buttonList.get(i).getThis() == settingsButton) {
					settings();
				} else if (buttonList.get(i).getThis() == exitButton) {
					System.exit(0);
				} else if (buttonList.get(i).getThis() == saveButton) {
					saveSettings();
				} else if (buttonList.get(i).getThis() == backButton) {
					backToMainMenu();
				}
			}
		}
		if (!settings) {
			for (int i = 0; i < radioButtonList.size(); i++) {
				if (radioButtonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
					for (int a = 0; a < radioButtonList.size(); a++) {
						if (radioButtonList.get(a).getSelected()) {
							radioButtonList.get(a).setSelected(false);
						}
					}
					radioButtonList.get(i).setSelected(true);
					GameFrame.setMap(radioButtonList.get(i).getFile());
				}
			}
		} else {
			for (int i = 0; i < settingsButtonList.size(); i++) {
				for (int p = 0; p < settingsButtonList.get(i).length; p++) {
					if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
						settingsButtonList.get(i)[p].setContains(true);
						for (int a = 0; a < settingsButtonList.get(i).length; a++) {
							if (settingsButtonList.get(i)[a].getSelected()) {
								settingsButtonList.get(i)[a].setSelected(false);
							}
						}
						settingsButtonList.get(i)[p].setSelected(true);
						if (settingsButtonList.get(i)[p].getThis() == presetButtons[0]) {
							presets = "High";
							AA = true;
							vignetting = true;
							fancyMenu = true;
							flares = true;
						} else if (settingsButtonList.get(i)[p].getThis() == presetButtons[1]) {
							presets = "Low";
							AA = true;
							vignetting = false;
							fancyMenu = false;
							flares = false;
						} else if (settingsButtonList.get(i)[p].getThis() == presetButtons[2]) {
							presets = "Custom";
						} else {
							if (settingsButtonList.get(i)[p].getThis() == soundButtons[0]) {
								sound = false;
							} else if (settingsButtonList.get(i)[p].getThis() == soundButtons[1]) {
								sound = true;
							} else if (settingsButtonList.get(i)[p].getThis() == fullScreenButtons[0]) {
								fullScreen = false;
							} else if (settingsButtonList.get(i)[p].getThis() == fullScreenButtons[1]) {
								fullScreen = true;
							} else {
								presets = "Custom";
								if (settingsButtonList.get(i)[p].getThis() == AAButtons[0]) {
									AA = false;
								} else if (settingsButtonList.get(i)[p].getThis() == AAButtons[1]) {
									AA = true;
								}
								if (settingsButtonList.get(i)[p].getThis() == vignettingButtons[0]) {
									vignetting = false;
								} else if (settingsButtonList.get(i)[p].getThis() == vignettingButtons[1]) {
									vignetting = true;
								}
								if (settingsButtonList.get(i)[p].getThis() == fancyMenuButtons[0]) {
									fancyMenu = false;
								} else if (settingsButtonList.get(i)[p].getThis() == fancyMenuButtons[1]) {
									fancyMenu = true;
								}
								if (settingsButtonList.get(i)[p].getThis() == flareButtons[0]) {
									flares = false;
								} else if (settingsButtonList.get(i)[p].getThis() == flareButtons[1]) {
									flares = true;
								}
							}
						}
						checkButtons();
					} else {
						settingsButtonList.get(i)[p].setContains(false);
					}
				}
			} 
		}
	}
	public void thumpSound() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				if (SettingsConfig.sound) {
					try {
						thumpSound = AudioSystem.getClip();
					} catch (LineUnavailableException e1) {}
					try {
						thumpais = AudioSystem.getAudioInputStream( new File("audio/War Stomp.wav") );
					} catch (UnsupportedAudioFileException e1) {} catch (IOException e1) {}
					try {
						thumpSound.open(thumpais);
					} catch (LineUnavailableException e1) {} catch (IOException e1) {}
					thumpSound.start();
				}
			}
		});
		t.start();
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}


