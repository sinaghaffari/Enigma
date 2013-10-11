package main;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import map.Map;
import tools.StackBlurFilter;
import enemies.EnemySpawner;
import enemies.EnemyTrack;
import explosions.Explosion;
import guns.Barrett;
import guns.Guns;
import guns.M16;
import guns.MP5;
import guns.Pistol;
import guns.ShotGun;



public class Game extends JComponent implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {
	Font fontExtraLarge = new Font("Trebuchet MS", 0, 75 );
	Font fontLarge = new Font("Trebuchet MS", 0, 20 );
	private long lastHit = -1;
	private boolean debug = true;
	public double score = 0;
	public double money = 0;
	private boolean smooth = true;
	private int windowWidth = this.getWidth();
	private int windowHeight = this.getHeight();
	private int isknifing=0;
	private int mx=1000;
	private boolean dead = false;
	private double maxSpeed = 2;
	private int MouseX, MouseY;
	private double pX=400;
	private double pY=500;
	private double spX, spY;
	private double ppX, ppY;
	private double shootAngle;
	public double shootX, shootY;
	private double temporaryX, temporaryY;
	private double mouseAngle;
	private boolean wallCollision, enemyWallCollision;
	private double ActualWalkAngle = 0;
	private double walkAngle, nwalkAngle;
	private double Velocity, nVelocity, relativeVelocity;
	private double Distance;
	private boolean Movement;
	private long lastShot = 0;
	private boolean shooting;
	private boolean allowedShoot;
	private boolean consol;
	private String deathMessage;
	private boolean movementType = false;
	private double mouseRotation = 0;
	private boolean mouseRotationUpOrDown;
	private boolean sprint = false;
	private boolean pressReload;
	private int crosshairType = 1;
	private int count;
	private float featherModifier = 1;
	public EnemySpawner spawnEnemies;
	public Guns gunManager = new Guns();
	public int pHealth = 100;
	public int tlcx=0;
	public int tlcy=0;
	public double tShootxShake = 0, tShootyShake = 0, vShootxShake = 0, vShootyShake = 0;
	public double tMovexShake = 0, tMoveyShake = 0, vMovexShake = 0, vMoveyShake = 0;
	private double aimAngle = 0;
	double smoothMouseX, smoothMouseY;
	double smoothMouseX2, smoothMouseY2;
	String direction = "UP";
	Font font = new Font("Trebuchet MS", 0, 20 );
	FontMetrics fontMetrics = getFontMetrics(font);
	private final Set<Integer> pressed = new HashSet<Integer>();
	BufferedImage HUDline, HUDPistol, HUDShotgun, HUDM16, HUDMP5, HUDSniper, HUDRocketLauncher;
	public BufferedImage playerPistolImg, playerShotgunImg, playerM16Img, playerEmptyImg;
	private Random rand = new Random();
	private Image[] explosionAnim = new Image[23];
	private int fadeAmount = 255;
	private boolean fadeOut = false;
	private Line2D lazerSightLine = new Line2D.Double(0, 0, 0, 0);
	public boolean pause = false;
	public boolean menu = false;
	public boolean shop = false;
	public Map map;
	private boolean initialized = false;
	private boolean gunVoided = false;
	private long timeSinceLastStep = System.currentTimeMillis();
	private boolean pD = false;
	Line2D[] tempLine;
	Line2D[] tempLine2;
	Line2D[] tempLine3;
	Line2D[] tempLine4;
	Line2D[] enemyTempLine;
	Line2D[] enemyTempLine2;
	Line2D[] enemyTempLine3;
	Line2D[] enemyTempLine4;
	double tempAngle, tempAngle2, tempAngle3;
	double enemyTempAngle, enemyTempAngle2, enemyTempAngle3;
	public boolean showDebugLines[] = {false, false, false, false};
	public GeneralPath emptyArea = new GeneralPath();
	public Clip[] Ricochet = new Clip[8];
	private AudioInputStream[] RicochetStream = new AudioInputStream[8];
	private boolean focusedOnEnemy = false;

	public BufferedImage[] imgSmall = new BufferedImage[5];
	public BufferedImage[] imgSmall2 = new BufferedImage[5];
	public BufferedImage[] imgShotgun = new BufferedImage[5];
	public BufferedImage[] imgShotgun2 = new BufferedImage[5];
	public StackBlurFilter filter = new StackBlurFilter(2, 3);

	private SButton resumeButton = new SButton(this.getWidth() / 2.0 - 100, this.getHeight() / 2.0 - 290, 200, 75, "Resume Over", 20);
	private SButton settingsButton = new SButton(this.getWidth() / 2.0 - 100, this.getHeight() / 2.0 - 130, 200, 75, "Settings", 20);
	private SButton exitButton = new SButton(this.getWidth() / 2.0 - 100, this.getHeight() / 2.0 - 130, 200, 75, "Exit", 20);
	private boolean settings = false;
	Ellipse2D EnemyCollision = new Ellipse2D.Double(700 - 30, 500 - 30, 60, 60);
	private Vector<SRadioButton[]> settingsButtonList = new Vector<SRadioButton[]>();

	private SRadioButton[] presetButtons = new SRadioButton[3];
	private SRadioButton[] AAButtons = new SRadioButton[2];
	private SRadioButton[] vignettingButtons = new SRadioButton[2];
	private SRadioButton[] fancyMenuButtons = new SRadioButton[2];
	private SRadioButton[] flareButtons = new SRadioButton[2];
	private SRadioButton[] soundButtons = new SRadioButton[2];
	private SRadioButton[] fullScreenButtons = new SRadioButton[2];

	private SButton backButton = new SButton (10, 565, 200, 25, "Back", 20);
	private SButton saveButton = new SButton (210, 565, 200, 25, "Save", 20);

	private Clip thumpSound = null;
	private AudioInputStream thumpais = null;

	String presets = SettingsConfig.presets;
	boolean AA = SettingsConfig.AA;
	boolean vignetting = SettingsConfig.vignetting;
	boolean flares = SettingsConfig.flares;
	boolean fancyMenu = SettingsConfig.fancyMenu;
	boolean sound = SettingsConfig.sound;
	boolean fullScreen = SettingsConfig.fullScreen;
	int CPUs = SettingsConfig.CPUs;

	public BufferedImage[] enemyImg = new BufferedImage[17]; 

	public Explosion expl = new Explosion();

	private Shop shopFrame;

	public HighScores hs=new HighScores();

	public Game( File gameMap) throws IOException {

		//hs.printScores();
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		smoothMouseX = smoothMouseX2 = MouseX;
		smoothMouseY = smoothMouseX2 = MouseY;
		initGame(gameMap);
	}
	public void initGame( final File gameMap) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
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
					map = new Map( gameMap );
				} catch (FileNotFoundException e2) {
					System.out.println("woah");
				}
				for (int a = 0; a < 17; a++) {
					try {
						enemyImg[a] = ImageIO.read(new File("img/Enemies/" + (a + 1) + ".png"));
					} catch (IOException e) {
						System.out.println("Bad enemy Image - " + a);
						e.printStackTrace();
					}
				}
				spawnEnemies = new EnemySpawner();
				initFlares();
				try {
					HUDline = ImageIO.read(new File("img/HUD/HUD Line.png"));
					HUDPistol = ImageIO.read(new File("img/HUD/Gun Icons/HUD Pistol.png"));
					HUDShotgun = ImageIO.read(new File("img/HUD/Gun Icons/HUD Shotgun.png"));
					HUDM16 = ImageIO.read(new File("img/HUD/Gun Icons/HUD M16.png"));
					HUDMP5 = ImageIO.read(new File("img/HUD/Gun Icons/HUD MP5.png"));
					HUDSniper = ImageIO.read(new File("img/HUD/Gun Icons/HUD Sniper.png"));
					HUDRocketLauncher = ImageIO.read(new File("img/HUD/Gun Icons/HUD RocketLauncher.png"));
					playerPistolImg = ImageIO.read(new File("img/Player/Pistol.png"));
					playerShotgunImg = ImageIO.read(new File("img/Player/Shotgun.png"));
					playerM16Img = ImageIO.read(new File("img/Player/M16.png"));
					playerEmptyImg = ImageIO.read(new File("img/Player/Empty.png"));
				} catch (IOException e2) {}
				tempLine =  new Line2D[map.getWallSize()];
				tempLine2 =  new Line2D[map.getWallSize()];
				tempLine3 =  new Line2D[map.getWallSize()];
				tempLine4 =  new Line2D[map.getWallSize()];
				for (int i = 0; i < map.getWallSize(); i++) {
					tempLine[i] =  new Line2D.Double(0, 0, 0, 0);
					tempLine2[i] =  new Line2D.Double(0, 0, 0, 0);
					tempLine3[i] =  new Line2D.Double(0, 0, 0, 0);
					tempLine4[i] =  new Line2D.Double(0, 0, 0, 0);
				}
				spawnEnemies.initializeSpawner();
				DataManager();
				for (int i = 0; i <= 22; i++) {
					explosionAnim[i] = Toolkit.getDefaultToolkit().getImage("img/EXPLOSION/Explosion" + i + ".png");
				}
				Music();
				if (SettingsConfig.vignetting) {
					featherAnim();
				}


				repainter();
				initialized = true;
				fadeIn(0.000001f,0f, 0.00001f, 0);
				//fadeIn(0.5f,0,0,0);
			}
		});
		thread.start();
	}
	public void Music() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				InputStream is = null;
				Player clip = null;
				while (GameFrame.isGaming) {
					while(SettingsConfig.sound && GameFrame.isGaming) {
						try {
							is = new FileInputStream("audio/Eerie Ambience.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
						while(!clip.isComplete()) {
							if (!SettingsConfig.sound || !GameFrame.isGaming) {
								try {
									if (is != null)
										is.close();
									if (clip != null)
										clip.close();
								} catch (IOException e) {}
								break;
							}
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {}
						}

					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
				}
			}
		});
		t.start();
	}
	public void initFlares() {
		double x, y;
		double tempAngle = 180;
		for (int b = 0; b < 5; b++) {
			imgSmall[b] = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
			imgSmall2[b] = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
			x = 500 - 10 * Math.cos(Math.toRadians(tempAngle));
			y = 300 - 10 * Math.sin(Math.toRadians(tempAngle));
			Graphics2D g2 = (Graphics2D)imgSmall[b].getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			GeneralPath tri = new GeneralPath();
			double temp1;
			double tempX, tempY, tempX2, tempY2;
			double temp2;
			double temp3;
			double temp4;
			tri.moveTo(x, y);
			temp1 = 10 + (7 * (Math.random() - 0.5));
			temp2 = 55 + (30 * (Math.random() - 0.5));
			temp3 = 10 + (7 * (Math.random() - 0.5));
			temp4 = 30 + (15 * (Math.random() - 0.5));
			tempX = x - temp1 * Math.cos(Math.toRadians(tempAngle + 45));
			tempY = y - temp1 * Math.sin(Math.toRadians(tempAngle + 45));
			tempX2 = x - temp3 * Math.cos(Math.toRadians(tempAngle - 45));
			tempY2 = y - temp3 * Math.sin(Math.toRadians(tempAngle - 45));
			tri.lineTo(tempX, tempY);
			tri.curveTo(tempX - temp4 * Math.cos(Math.toRadians(tempAngle)), tempY - temp4 * Math.sin(Math.toRadians(tempAngle)), tempX2 - temp4 * Math.cos(Math.toRadians(tempAngle)), tempY2 - temp4 * Math.sin(Math.toRadians(tempAngle)), tempX2, tempY2);
			tri.closePath();
			g2.setPaint(new Color(255, 127, 0));
			g2.fill(tri);
			tri = new GeneralPath();
			tri.moveTo(x, y);
			temp1 = 5 + (3 * (Math.random() - 0.5));
			temp2 = 20 + (15 *(Math.random() - 0.5));
			tri.lineTo(x - temp1 * Math.cos(Math.toRadians(tempAngle + 30)), y - temp1 * Math.sin(Math.toRadians(tempAngle + 30)));
			tri.lineTo(x - temp2 * Math.cos(Math.toRadians(tempAngle)), y - temp2 * Math.sin(Math.toRadians(tempAngle)));
			tri.lineTo(x - temp1 * Math.cos(Math.toRadians(tempAngle - 30)), y - temp1 * Math.sin(Math.toRadians(tempAngle - 30)));
			tri.closePath();
			g2.setPaint(new Color(255, 255, 127));
			g2.fill(tri);
			Graphics2D g2_2 = (Graphics2D)imgSmall2[b].getGraphics();
			g2_2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2_2.drawImage(imgSmall[b], filter, 0, 0);
			imgSmall[b] = null;
		}
		for (int b = 0; b < 5; b++) {
			imgShotgun[b] = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
			imgShotgun2[b] = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
			x = 500 - 10 * Math.cos(Math.toRadians(tempAngle));
			y = 300 - 10 * Math.sin(Math.toRadians(tempAngle));
			Graphics2D g2 = (Graphics2D)imgShotgun[b].getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			GeneralPath tri = new GeneralPath();
			double temp1;
			double tempX, tempY, tempX2, tempY2;
			double temp2;
			double temp3;
			double temp4;
			tri.moveTo(x, y);
			temp1 = 15 + (7 * (Math.random() - 0.5));
			temp2 = 70 + (30 *(Math.random() - 0.5));
			temp3 = 15 + (7 * (Math.random() - 0.5));
			temp4 = 50 + (30 *(Math.random() - 0.5));
			tempX = x - temp1 * Math.cos(Math.toRadians(tempAngle + 45));
			tempY = y - temp1 * Math.sin(Math.toRadians(tempAngle + 45));
			tempX2 = x - temp3 * Math.cos(Math.toRadians(tempAngle - 45));
			tempY2 = y - temp3 * Math.sin(Math.toRadians(tempAngle - 45));
			tri.lineTo(tempX, tempY);
			//tri.quadTo(x - temp2 * Math.cos(Math.toRadians(tempAngle)), y - temp2 * Math.sin(Math.toRadians(tempAngle)), tempX2, tempY2);
			tri.curveTo(tempX - temp4 * Math.cos(Math.toRadians(tempAngle)), tempY - temp4 * Math.sin(Math.toRadians(tempAngle)), tempX2 - temp4 * Math.cos(Math.toRadians(tempAngle)), tempY2 - temp4 * Math.sin(Math.toRadians(tempAngle)), tempX2, tempY2);
			tri.closePath();
			g2.setPaint(new Color(255, 127, 0));
			g2.fill(tri);
			tri = new GeneralPath();
			tri.moveTo(x, y);
			temp1 = 5 + (3 * (Math.random() - 0.5));
			temp2 = 50 + (15 *(Math.random() - 0.5));
			tri.lineTo(x - temp1 * Math.cos(Math.toRadians(tempAngle + 30)), y - temp1 * Math.sin(Math.toRadians(tempAngle + 30)));
			tri.lineTo(x - temp2 * Math.cos(Math.toRadians(tempAngle)), y - temp2 * Math.sin(Math.toRadians(tempAngle)));
			tri.lineTo(x - temp1 * Math.cos(Math.toRadians(tempAngle - 30)), y - temp1 * Math.sin(Math.toRadians(tempAngle - 30)));
			tri.closePath();
			g2.setPaint(new Color(255, 255, 127));
			g2.fill(tri);
			Graphics2D g2_2 = (Graphics2D)imgShotgun2[b].getGraphics();
			g2_2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2_2.drawImage(imgShotgun[b], filter, 0, 0);
			imgShotgun[b] = null;
		}
	}
	public void fadeIn( final float amount, final double change, final double changesChange, int type) {
		fadeOut = false;
		if (type == 0) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					double tempFade = fadeAmount;
					float changeAmount = amount;
					double changeSpeed = change;
					while(tempFade - (changeAmount + (changeSpeed + changesChange)) > 0) {
						changeSpeed += changesChange;
						changeAmount += changeSpeed;
						tempFade -= changeAmount;
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
			float changeAmount = amount;
			while(tempFade - (changeAmount - change) > 0) {
				changeAmount -= change;
				tempFade -= changeAmount;
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
	}
	public void featherAnim() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				float modifier = 1;
				while (GameFrame.isGaming && SettingsConfig.vignetting) {	
					if (modifier < 0.95 && modifier > 0.8) {
						if (Math.random() >= 0.5) {
							modifier += 0.005;
						} else {
							modifier -= 0.005;
						}
					} else if (modifier > 0.95) {
						modifier -= 0.01;
					} else if (modifier < 0.8) {
						modifier += 0.01;
					}
					featherModifier = modifier;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
				}
			}
		});
		thread.start();
	}
	public void repainter() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (GameFrame.isGaming) {					
					repaint();
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		thread.start();
	}
	public void DataManager() {

		Thread t = new Thread(new Runnable() {
			public void run() {
				Line2D playerGunLine = new Line2D.Double();
				while (main.GameFrame.isGaming) {
					AngleChanger();
					for (int i = 0; i < map.getWallSize(); i++) {
						if (playerGunLine.intersects(map.getWallRectangle(i))) {
							gunVoided = true;
							break;
						} else  {
							gunVoided = false;
						}
					}
					shootX = (pX + 4 * Math.cos(Math.toRadians(shootAngle - 90))) + 44 * Math.cos(Math.toRadians(shootAngle));
					shootY = (pY + 4 * Math.sin(Math.toRadians(shootAngle - 90))) + 44 * Math.sin(Math.toRadians(shootAngle));
					playerGunLine.setLine(shootX, shootY, pX, pY);
					if (!sprint && !gunVoided) {
						if (!(gunManager.getGunRapid())) {
							if (shooting)
							{
								gunManager.GunShoot( shootX, shootY, shootAngle );
								shooting = false;
							}
						} else if (gunManager.getGunRapid()) {
							if ((shooting) && (System.currentTimeMillis() - lastShot >= gunManager.getGunRate())) {
								gunManager.GunShoot( shootX, shootY, shootAngle );
								lastShot = System.currentTimeMillis();
							}
						}
					}
					if ((!Movement) && Velocity > 0)
						Velocity -= 0.15;
					if (Velocity < 0)
						Velocity = 0;
					if ((Velocity < maxSpeed) && (Movement))
						Velocity += 0.15;
					if ((Velocity >= maxSpeed) && (Movement))
						Velocity-=0.15;
					if (pressed.contains(KeyEvent.VK_R)) {
						if (!pressReload) {
							pressReload = true;
							gunManager.reload();
						}
					}
					if (!movementType) {
						if (pressed.contains(KeyEvent.VK_W)) {
							if (pressed.contains(KeyEvent.VK_D)) {
								walkAngle = 135;
								Movement = true;
							} else if (pressed.contains(KeyEvent.VK_A)) {
								walkAngle = 45;
								Movement = true;
							} else {
								walkAngle = 90;
								Movement = true;
							}
						} else if (pressed.contains(KeyEvent.VK_S)) {
							if (pressed.contains(KeyEvent.VK_D)) {
								walkAngle = 225;
								Movement = true;
							} else if (pressed.contains(KeyEvent.VK_A)) {
								walkAngle = 315;
								Movement = true;
							} else {
								walkAngle = 270;
								Movement = true;
							}
						} else if (pressed.contains(KeyEvent.VK_D)) {
							walkAngle = 180;
							Movement = true;
						} else if (pressed.contains(KeyEvent.VK_A) ) {
							walkAngle = 0;
							Movement = true;
						}
					} else {
						if (pressed.contains(KeyEvent.VK_W)) {
							Movement = true;
							direction="UP";
						}
					}
					if (pressed.contains(KeyEvent.VK_SHIFT)) {
						sprint = true;
						maxSpeed = 4.5;
					}

					if (gunManager.getGunRecoil() > 0) {
						gunManager.setGunRecoil((gunManager.getGunRecoil() - gunManager.getGunRecoilReduction()));
					} else if (gunManager.getGunRecoil() < 0) {
						gunManager.setGunRecoil(0);
					}
					gunManager.setDynamicRecoil(relativeVelocity * 1.5);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {}
					while(pause) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		t.start();
	}
	public double distance( double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	public double angleBetween( double x1, double y1, double x2, double y2 ) {  
		double t = Math.toDegrees(Math.atan2( (y2 - y1) , (x2 - x1)));
		if (t < 0)
			return t + 360;
		else
			return t;
	}
	public double wallDistance( double x, double y, int w ) {
		double angle = angleBetween(x, y, map.getWallRectangle(w).getCenterX(), map.getWallRectangle(w).getCenterY());//Math.toDegrees( Math.atan2( (y - map.getWallRectangle(w).getCenterY()) , (x - map.getWallRectangle(w).getCenterX())));
		double returnAngle;
		if (Math.abs(angle) % 90 == 0)
			returnAngle = 0;
		else
			if (Math.abs(angle) > 315)
				returnAngle = angle -  360;
			else if (Math.abs(angle) > 225)
				returnAngle = angle -  270;
			else if (Math.abs(angle) > 135)
				returnAngle = angle -  180;
			else if (Math.abs(angle) > 45)
				returnAngle = angle - 90;
			else
				returnAngle = angle;
		return 10/Math.cos(Math.toRadians(returnAngle));
	}

	public void AngleChanger() {
		//System.out.println(map.map.size());
		double wX = 0, wY = 0, m;
		wallCollision = false;
		enemyWallCollision = false;
		mouseAngle = angleBetween(pX - tlcx, pY - tlcy, MouseX, MouseY); //Math.toDegrees(Math.atan2((300 - MouseY),(500 - MouseX)))
		temporaryX = ((pX - tlcx) + 12.5 * Math.cos(Math.toRadians(mouseAngle + 90))) + 42 * Math.cos(Math.toRadians(mouseAngle));
		temporaryY = ((pY - tlcy) + 12.5 * Math.sin(Math.toRadians(mouseAngle + 90))) + 42 * Math.sin(Math.toRadians(mouseAngle));
		Vector<EnemyTrack> tempEnemies = new Vector<EnemyTrack>(spawnEnemies.enemyList);
		enemyTempLine = new Line2D[tempEnemies.size()];
		enemyTempLine2 = new Line2D[tempEnemies.size()];
		enemyTempLine3 = new Line2D[tempEnemies.size()];
		enemyTempLine4 = new Line2D[tempEnemies.size()];
		for (int i = 0; i < tempEnemies.size(); i++) {
			if (!tempEnemies.get(i).collisionBox.contains(pX, pY)) {
				enemyTempLine[i] = new Line2D.Double(0, 0, 0, 0);
				enemyTempLine2[i] = new Line2D.Double(0, 0, 0, 0);
				enemyTempLine3[i] = new Line2D.Double(0, 0, 0, 0);
				enemyTempLine4[i] = new Line2D.Double(0, 0, 0, 0);
				enemyTempAngle = angleBetween(tempEnemies.get(i).collisionBox.getCenterX(), tempEnemies.get(i).collisionBox.getCenterY(), pX, pY);//Math.toDegrees(Math.atan2( (pY - map.getWallRectangle(i).getCenterY()) , (pX - map.getWallRectangle(i).getCenterX())));
				enemyTempLine[i].setLine(
						tempEnemies.get(i).collisionBox.getCenterX(),
						tempEnemies.get(i).collisionBox.getCenterY(),
						tempEnemies.get(i).collisionBox.getCenterX() + ((wallDistance(pX, pY, i) + 15) * Math.cos(Math.toRadians(enemyTempAngle))),
						tempEnemies.get(i).collisionBox.getCenterY() + ((wallDistance(pX, pY, i) + 15) * Math.sin(Math.toRadians(enemyTempAngle))));
				enemyTempAngle2 = Math.round(enemyTempAngle / 90.0) * 90;

				if (enemyTempAngle2 % 180 == 0) {
					if ((walkAngle >= 0) && (walkAngle <= 179)) {
						enemyTempAngle3 = 90;
					} else {
						enemyTempAngle3 = 270;
					}
				} else {
					if(((walkAngle >= 0) && (walkAngle <= 89)) || ((walkAngle >= 270) && (walkAngle <= 360))) {
						enemyTempAngle3 = 180;
					} else {
						if ((Math.toDegrees(Math.atan2((enemyTempLine[i].getY1() - enemyTempLine[i].getY2()),(enemyTempLine[i].getX1() - enemyTempLine[i].getX2()))) <= -45) && (Math.toDegrees(Math.atan2((enemyTempLine[i].getY1() - enemyTempLine[i].getY2()),(enemyTempLine[i].getX1() - enemyTempLine[i].getX2()))) >= -135)) {
							enemyTempAngle3 = 360;
						} else {
							enemyTempAngle3 = 0;
						}
					}
				}
				enemyTempLine4[i].setLine(
						tempEnemies.get(i).collisionBox.getCenterX(),
						tempEnemies.get(i).collisionBox.getCenterY(),
						tempEnemies.get(i).collisionBox.getCenterX() - ((-25) * Math.cos(Math.toRadians(-enemyTempAngle3))),
						tempEnemies.get(i).collisionBox.getCenterY() - ((-25) * Math.sin(Math.toRadians(-enemyTempAngle3))));
				enemyTempLine2[i].setLine(
						tempEnemies.get(i).collisionBox.getCenterX(),
						tempEnemies.get(i).collisionBox.getCenterY(),
						tempEnemies.get(i).collisionBox.getCenterX() - ((-25) * Math.cos(Math.toRadians(enemyTempAngle2))),
						tempEnemies.get(i).collisionBox.getCenterY() - ((-25) * Math.sin(Math.toRadians(enemyTempAngle2))));
				if (enemyTempAngle2 == 0 || enemyTempAngle2 == 360) {
					if (pY < tempEnemies.get(i).collisionBox.getX()) {
						wX = tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth();
						wY = tempEnemies.get(i).collisionBox.getY();
					} else if (pY > tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth()) {
						wX = tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth();
						wY = tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth();
					} else {
						wX = tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth();
						wY = pY;
					}
				} else if (enemyTempAngle2 == 90) {
					if (pX < tempEnemies.get(i).collisionBox.getX()) {
						wX = tempEnemies.get(i).collisionBox.getX();
						wY = tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth();
					} else if (pX > tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth()) {
						wX = tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth();
						wY = tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth();
					} else {
						wX = pX;
						wY = tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth();
					}
				} else if (Math.abs(enemyTempAngle2) == 180) {
					if (pY < tempEnemies.get(i).collisionBox.getY()) {
						wX = tempEnemies.get(i).collisionBox.getX();
						wY = tempEnemies.get(i).collisionBox.getY();
					} else if (pY > tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth()) {
						wX = tempEnemies.get(i).collisionBox.getX();
						wY = tempEnemies.get(i).collisionBox.getY() + tempEnemies.get(i).collisionBox.getWidth();
					} else {
						wX = tempEnemies.get(i).collisionBox.getX();
						wY = pY;
					}
				} else if (enemyTempAngle2 == 270) {
					if (pX < tempEnemies.get(i).collisionBox.getX()) {
						wX = tempEnemies.get(i).collisionBox.getX();
						wY = tempEnemies.get(i).collisionBox.getY();
					} else if (pX > tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth()) {
						wX = tempEnemies.get(i).collisionBox.getX() + tempEnemies.get(i).collisionBox.getWidth();
						wY = tempEnemies.get(i).collisionBox.getY();
					} else {
						wX = pX;
						wY = tempEnemies.get(i).collisionBox.getY();
					}
				}
				enemyTempLine3[i].setLine(
						wX,
						wY,
						wX + (15 * Math.cos(Math.toRadians(angleBetween(wX, wY, pX, pY)))),
						wY + (15 * Math.sin(Math.toRadians(angleBetween(wX, wY, pX, pY)))));
				if (distance(wX, wY, pX, pY) <= distance(enemyTempLine3[i].getX1(), enemyTempLine3[i].getY1(), enemyTempLine3[i].getX2(), enemyTempLine3[i].getY2())) {
					if (distance(wX, wY, pX - Velocity * Math.cos( Math.toRadians( walkAngle )), pY - Velocity * Math.sin( Math.toRadians( walkAngle ) ) ) < distance(wX, wY, pX, pY)) {
						enemyWallCollision = true;
						spX = enemyTempLine3[i].getX2();
						spY = enemyTempLine3[i].getY2();
						nwalkAngle = Math.abs(walkAngle - enemyTempAngle3);
						ActualWalkAngle = enemyTempAngle3;
						nVelocity = Velocity * Math.cos(Math.toRadians(nwalkAngle));
					}
				}
			}
		}
		for (int i = 0; i < map.getWallSize(); i++) {
			tempAngle = angleBetween(map.getWallRectangle(i).getCenterX(), map.getWallRectangle(i).getCenterY(), pX, pY);//Math.toDegrees(Math.atan2( (pY - map.getWallRectangle(i).getCenterY()) , (pX - map.getWallRectangle(i).getCenterX())));
			tempLine[i].setLine(
					map.getWallRectangle(i).getCenterX(),
					map.getWallRectangle(i).getCenterY(),
					map.getWallRectangle(i).getCenterX() + ((wallDistance(pX, pY, i) + 15) * Math.cos(Math.toRadians(tempAngle))),
					map.getWallRectangle(i).getCenterY() + ((wallDistance(pX, pY, i) + 15) * Math.sin(Math.toRadians(tempAngle))));
			tempAngle2 = Math.round(tempAngle / 90.0) * 90;

			if (tempAngle2 % 180 == 0) {
				if ((walkAngle >= 0) && (walkAngle <= 179)) {
					tempAngle3 = 90;
				} else {
					tempAngle3 = 270;
				}
			} else {
				if(((walkAngle >= 0) && (walkAngle <= 89)) || ((walkAngle >= 270) && (walkAngle <= 360))) {
					tempAngle3 = 180;
				} else {
					if ((Math.toDegrees(Math.atan2((tempLine[i].getY1() - tempLine[i].getY2()),(tempLine[i].getX1() - tempLine[i].getX2()))) <= -45) && (Math.toDegrees(Math.atan2((tempLine[i].getY1() - tempLine[i].getY2()),(tempLine[i].getX1() - tempLine[i].getX2()))) >= -135)) {
						tempAngle3 = 360;
					} else {
						tempAngle3 = 0;
					}
				}
			}
			tempLine4[i].setLine(
					map.getWallRectangle(i).getCenterX(),
					map.getWallRectangle(i).getCenterY(),
					map.getWallRectangle(i).getCenterX() - ((-25) * Math.cos(Math.toRadians(-tempAngle3))),
					map.getWallRectangle(i).getCenterY() - ((-25) * Math.sin(Math.toRadians(-tempAngle3))));
			tempLine2[i].setLine(
					map.getWallRectangle(i).getCenterX(),
					map.getWallRectangle(i).getCenterY(),
					map.getWallRectangle(i).getCenterX() - ((-25) * Math.cos(Math.toRadians(tempAngle2))),
					map.getWallRectangle(i).getCenterY() - ((-25) * Math.sin(Math.toRadians(tempAngle2))));
			if (tempAngle2 == 0 || tempAngle2 == 360) {
				if (pY < map.getMapY(i)) {
					wX = map.getMapX(i) + 20;
					wY = map.getMapY(i);
				} else if (pY > map.getMapY(i) + 20) {
					wX = map.getMapX(i) + 20;
					wY = map.getMapY(i) + 20;
				} else {
					wX = map.getMapX(i) + 20;
					wY = pY;
				}
			} else if (tempAngle2 == 90) {
				if (pX < map.getMapX(i)) {
					wX = map.getMapX(i);
					wY = map.getMapY(i) + 20;
				} else if (pX > map.getMapX(i) + 20) {
					wX = map.getMapX(i) + 20;
					wY = map.getMapY(i) + 20;
				} else {
					wX = pX;
					wY = map.getMapY(i) + 20;
				}
			} else if (Math.abs(tempAngle2) == 180) {
				if (pY < map.getMapY(i)) {
					wX = map.getMapX(i);
					wY = map.getMapY(i);
				} else if (pY > map.getMapY(i) + 20) {
					wX = map.getMapX(i);
					wY = map.getMapY(i) + 20;
				} else {
					wX = map.getMapX(i);
					wY = pY;
				}
			} else if (tempAngle2 == 270) {
				if (pX < map.getMapX(i)) {
					wX = map.getMapX(i);
					wY = map.getMapY(i);
				} else if (pX > map.getMapX(i) + 20) {
					wX = map.getMapX(i) + 20;
					wY = map.getMapY(i);
				} else {
					wX = pX;
					wY = map.getMapY(i);
				}
			}
			tempLine3[i].setLine(
					wX,
					wY,
					wX + (15 * Math.cos(Math.toRadians(angleBetween(wX, wY, pX, pY)))),
					wY + (15 * Math.sin(Math.toRadians(angleBetween(wX, wY, pX, pY)))));

			if (distance(wX, wY, pX, pY) <= distance(tempLine3[i].getX1(), tempLine3[i].getY1(), tempLine3[i].getX2(), tempLine3[i].getY2())) {
				if (!enemyWallCollision) {
					if (distance(wX, wY, pX - Velocity * Math.cos( Math.toRadians( walkAngle )), pY - Velocity * Math.sin( Math.toRadians( walkAngle ) ) ) < distance(wX, wY, pX, pY)) {
						wallCollision = true;
						pX = tempLine3[i].getX2();
						pY = tempLine3[i].getY2();
						nwalkAngle = Math.abs(walkAngle - tempAngle3);
						ActualWalkAngle = tempAngle3;
						nVelocity = Velocity * Math.cos(Math.toRadians(nwalkAngle));
					}
				} else {
					wallCollision = true;
					pX = tempLine3[i].getX2();
					pY = tempLine3[i].getY2();
					nwalkAngle = Math.abs(walkAngle - tempAngle3);
					ActualWalkAngle = tempAngle3;
					nVelocity = Velocity * Math.cos(Math.toRadians(nwalkAngle));
				}

			}
		}
		ppX = pX;
		ppY = pY;
		if (wallCollision && enemyWallCollision) {
		} else if (wallCollision || enemyWallCollision) {
			if (enemyWallCollision) {
				pX = spX;
				pY = spY;
			}
			pX -= nVelocity * Math.cos( Math.toRadians( ActualWalkAngle ) ) ;
			pY -= nVelocity * Math.sin( Math.toRadians( ActualWalkAngle ) ) ;
		} else {
			pX -= Velocity * Math.cos( Math.toRadians( walkAngle ) ) ;
			pY -= Velocity * Math.sin( Math.toRadians( walkAngle ) ) ;
		}
		relativeVelocity = distance( ppX, ppY, pX, pY );
		if (relativeVelocity != 0) {
			if (!sprint) {
				if (System.currentTimeMillis() - timeSinceLastStep >= 500) {
					timeSinceLastStep = System.currentTimeMillis();
					double shakeAngle;
					double shakeV = 1;
					if (pD) {
						pD = false;
						shakeAngle = mouseAngle + 90;
					} else {
						pD = true;
						shakeAngle = mouseAngle - 90;
					}
					setMoveShake((shakeV * Math.cos(Math.toRadians(shakeAngle))), (shakeV * Math.sin(Math.toRadians(shakeAngle))));
				}
			} else {
				if (System.currentTimeMillis() - timeSinceLastStep >= 333) {
					timeSinceLastStep = System.currentTimeMillis();
					double shakeAngle;
					double shakeV = 1;
					if (pD) {
						pD = false;
						shakeAngle = mouseAngle + 90;
					} else {
						pD = true;
						shakeAngle = mouseAngle - 90;
					}
					setMoveShake((shakeV * Math.cos(Math.toRadians(shakeAngle))), (shakeV * Math.sin(Math.toRadians(shakeAngle))));
				}
			}
		}		
		tMovexShake += vMovexShake;
		tMoveyShake += vMoveyShake;
		vMovexShake /= 1.05;
		vMoveyShake /= 1.05;
		tMovexShake /= 1.05;
		tMoveyShake /= 1.05;



		tShootxShake += vShootxShake;
		tShootyShake += vShootyShake;
		vShootxShake /= 1.2;
		vShootyShake /= 1.2;
		tShootxShake /= 1.2;
		tShootyShake /= 1.2;
		Distance = Math.sqrt((Math.pow(((pX - tlcx) - MouseX), 2)) + (Math.pow(((pY-tlcy) - MouseY), 2)));


		double smoothDistance = Math.sqrt((Math.pow((smoothMouseX - MouseX), 2)) + (Math.pow((smoothMouseY - MouseY), 2)));
		double smoothAngle = angleBetween(smoothMouseX, smoothMouseY, MouseX, MouseY);
		smoothMouseX += ((smoothDistance / 4.0) * Math.cos(Math.toRadians(smoothAngle)));
		smoothMouseY += ((smoothDistance / 4.0) * Math.sin(Math.toRadians(smoothAngle)));
		gunManager.setMouseRecoil((smoothDistance / 4.0) / 6.0);
		double usedDistance = Math.sqrt((Math.pow(((pX - tlcx) - smoothMouseX), 2)) + (Math.pow(((pY-tlcy) - smoothMouseY), 2)));
		smoothAngle = angleBetween((pX - tlcx), (pY - tlcy), smoothMouseX, smoothMouseY);
		if (smooth) {
			shootAngle = smoothAngle;
		} else {
			shootAngle = mouseAngle;
		}

		double smoothDistance2 = Math.sqrt((Math.pow((smoothMouseX2 - MouseX), 2)) + (Math.pow((smoothMouseY2 - MouseY), 2)));
		double smoothAngle2 = angleBetween(smoothMouseX2, smoothMouseY2, MouseX, MouseY);
		smoothMouseX2 += ((smoothDistance2 / 50.0) * Math.cos(Math.toRadians(smoothAngle2)));
		smoothMouseY2 += ((smoothDistance2 / 50.0) * Math.sin(Math.toRadians(smoothAngle2)));
		double usedDistance2 = Math.sqrt((Math.pow(((pX - tlcx) - smoothMouseX2), 2)) + (Math.pow(((pY-tlcy) - smoothMouseY2), 2)));
		smoothAngle2 = angleBetween((pX - tlcx), (pY - tlcy), smoothMouseX2, smoothMouseY2);

		increaseHealth();

		tlcx=(int) ((pX-(this.getWidth()/ 2.0)) + (((usedDistance2 * 0.2) * Math.cos(Math.toRadians(smoothAngle2))) + tShootxShake + tMovexShake));
		tlcy=(int) ((pY-(this.getHeight()/ 2.0)) + (((usedDistance2 * 0.2) * Math.sin(Math.toRadians(smoothAngle2))) + tShootyShake + tMoveyShake));
		ArrayList<Integer> possibleWallPos = new ArrayList<Integer>();
		ArrayList<Line2D> possibleLinePos = new ArrayList<Line2D>();
		ArrayList<Line2D> willCollide = new ArrayList<Line2D>();
		double bulletCollisionX, bulletCollisionY;
		int closestWall;
		Line2D closestLine;
		double wallCollisionX, wallCollisionY, wallCollisionT;
		double startX = shootX, startY = shootY;
		double bangle = shootAngle;
		Line2D line;
		double length;


		if (bangle < 0) {
			bangle = bangle + 360;
		} else if (bangle > 360) {
			bangle = bangle - 360;
		}
		possibleWallPos.clear();
		willCollide.clear();
		possibleLinePos.clear();
		double referanceAngle;
		bulletCollisionX = (500 * Math.cos(Math.toRadians( bangle )) * 50000) + startX;
		bulletCollisionY = (500 * Math.sin(Math.toRadians( bangle )) * 50000) + startY;
		Line2D initialCollisionLine;
		initialCollisionLine = new Line2D.Double(bulletCollisionX, bulletCollisionY, startX, startY);
		for (int i = 0; i < GameFrame.game.map.getWallSize(); i++) {
			if (initialCollisionLine.intersects( GameFrame.game.map.getWallRectangle(i))) {
				possibleWallPos.add(i);
			}
		}
		int closest = 0;
		for (int i = 0; i < possibleWallPos.size(); i++) {
			if (distance(startX, startY, GameFrame.game.map.getWallRectangle(possibleWallPos.get(i)).getCenterX(), GameFrame.game.map.getWallRectangle(possibleWallPos.get(i)).getCenterY()) < distance(startX, startY, GameFrame.game.map.getWallRectangle(possibleWallPos.get(closest)).getCenterX(), GameFrame.game.map.getWallRectangle(possibleWallPos.get(closest)).getCenterY()))
				closest = i;
		}
		closestWall = possibleWallPos.get(closest);
		double x1 = GameFrame.game.map.getWallRectangle(closestWall).getX();
		double y1 = GameFrame.game.map.getWallRectangle(closestWall).getY();
		double x2 = GameFrame.game.map.getWallRectangle(closestWall).getX() + GameFrame.game.map.getWallRectangle(closestWall).getWidth();
		double y2 = GameFrame.game.map.getWallRectangle(closestWall).getY() + GameFrame.game.map.getWallRectangle(closestWall).getHeight();
		possibleLinePos.add(new Line2D.Double(x1, y1, x2, y1));
		possibleLinePos.add(new Line2D.Double(x2, y1, x2, y2));
		possibleLinePos.add(new Line2D.Double(x2, y2, x1, y2));
		possibleLinePos.add(new Line2D.Double(x1, y2, x1, y1));
		for (int i = 0; i < possibleLinePos.size(); i++) {
			if (initialCollisionLine.intersectsLine(possibleLinePos.get(i))) {
				willCollide.add((Line2D) possibleLinePos.get(i).clone());
			}
		}
		if (willCollide.size() > 2) {
			int furthest = 0;
			for (int i = 0; i < willCollide.size(); i++) {

				if (distance(startX, startY, getLineCenterX(willCollide.get(i)), getLineCenterY(willCollide.get(i))) > distance(startX, startY, getLineCenterX(willCollide.get(furthest)), getLineCenterY(willCollide.get(furthest)))) {
					furthest = i;
				}
			}
			willCollide.remove(furthest);
		}
		if (willCollide.size() > 1){
			if (distance(startX, startY, getLineCenterX(willCollide.get(0)), getLineCenterY(willCollide.get(0))) < distance(startX, startY, getLineCenterX(willCollide.get(1)), getLineCenterY(willCollide.get(1)))) {
				willCollide.remove(1);
			} else {
				willCollide.remove(0);
			}
		}
		closestLine = (Line2D) willCollide.get(0).clone();
		if (angleBetween(closestLine.getX1(), closestLine.getY1(), closestLine.getX2(), closestLine.getY2()) == 0 || angleBetween(closestLine.getX1(), closestLine.getY1(), closestLine.getX2(), closestLine.getY2()) == 180 || angleBetween(closestLine.getX1(), closestLine.getY1(), closestLine.getX2(), closestLine.getY2()) == 360) {
			wallCollisionY = closestLine.getY1();
			wallCollisionT = ((1.0 / Math.sin(Math.toRadians( bangle ))) * (startY - wallCollisionY)) / 500;
			wallCollisionX = (500 * Math.cos(Math.toRadians( bangle )) * -wallCollisionT) + startX;
			if (startY > wallCollisionY) {
				referanceAngle = 270;
			} else {
				referanceAngle = 90;
			}
		} else {
			wallCollisionX = closestLine.getX1();
			wallCollisionT = ((1.0 / Math.cos(Math.toRadians( bangle ))) * (startX - wallCollisionX)) / 500;
			wallCollisionY = (500 * Math.sin(Math.toRadians( bangle )) * -wallCollisionT) + startY;
			if (startX > wallCollisionX) {
				referanceAngle = 180;
			} else {
				if (startY > wallCollisionY) {
					referanceAngle = 360;
				} else {
					referanceAngle = 0;
				}

			}
		}
		line = new Line2D.Double(startX, startY, wallCollisionX, wallCollisionY);
		length = distance(line.getX1(), line.getY1(), line.getX2(), line.getY2());
		int enemySize = GameFrame.game.spawnEnemies.enemyList.size();
		double collisionX[][] = new double[enemySize][2];
		double collisionY[][] = new double[enemySize][2];
		boolean collisionB[] = new boolean[enemySize];
		for (int i = 0; i <enemySize; i++) {
			for (int a = 0; a < 2; a++) {
				collisionX[i][a] = 0;
				collisionY[i][a] = 0;
			}
			collisionB[i] = false;
		}
		for (int i = 0; i < enemySize; i++) {
			Line2D l = (Line2D) line.clone();
			Ellipse2D c = new Ellipse2D.Double(GameFrame.game.spawnEnemies.enemyList.get(i).getEnemyX() - GameFrame.game.spawnEnemies.enemyList.get(i).getCollisionBox().getWidth()/2.0, GameFrame.game.spawnEnemies.enemyList.get(i).getEnemyY() - GameFrame.game.spawnEnemies.enemyList.get(i).getCollisionBox().getHeight()/2.0, GameFrame.game.spawnEnemies.enemyList.get(i).getCollisionBox().getWidth(), GameFrame.game.spawnEnemies.enemyList.get(i).getCollisionBox().getHeight());
			x1 = l.getX1() - c.getCenterX();
			y1 = l.getY1() - c.getCenterY();
			x2 = l.getX2() - c.getCenterX();
			y2 = l.getY2() - c.getCenterY();
			double dx = x2 - x1;
			double dy = y2 - y1;
			double dr = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			double D = (x1 * y2) - (x2 * y1);
			double r = c.getWidth() / 2.0;
			double delta = Math.pow(r, 2) * Math.pow(dr, 2) - Math.pow(D, 2);
			if (delta >= 0) {
				double ix1 = ((D * dy + sgn(dy) * dx * Math.sqrt(delta)) / (Math.pow(dr, 2))) + c.getCenterX();
				double ix2 = ((D * dy - sgn(dy) * dx * Math.sqrt(delta)) / (Math.pow(dr, 2))) + c.getCenterX();
				double iy1 = ((-D * dx + Math.abs(dy) * Math.sqrt(delta)) / (Math.pow(dr, 2))) + c.getCenterY();
				double iy2 = ((-D * dx - Math.abs(dy) * Math.sqrt(delta)) / (Math.pow(dr, 2))) + c.getCenterY();
				double distance1 = Point.distance(startX, startY, ix1, iy1);
				double distance2 = Point.distance(startX, startY, ix2, iy2);
				if (distance1 > distance2) {
					collisionX[i][0] = ix2;
					collisionY[i][0] = iy2;
					collisionX[i][1] = ix1;
					collisionY[i][1] = iy1;
				} else {
					collisionX[i][0] = ix1;
					collisionY[i][0] = iy1;
					collisionX[i][1] = ix2;
					collisionY[i][1] = iy2;
				}
				collisionB[i] = (GameFrame.game.spawnEnemies.enemyList.get(i).getCollisionBox().intersectsLine(line));
			}
		}
		int closestEnemyI = 0;
		double closestEnemyCollisionX = 6000000000000000000.0;
		double closestEnemyCollisionY = 6000000000000000000.0;
		boolean tempCheck = false;
		for (int i = 0; i < enemySize; i++) {
			if (collisionB[i]) {
				if (!tempCheck) {
					tempCheck = true;
					closestEnemyCollisionX = collisionX[i][0];
					closestEnemyCollisionY = collisionY[i][0];
					closestEnemyI = i;
				} else {
					if (distance(closestEnemyCollisionX, closestEnemyCollisionY, startX, startY) > distance(collisionX[i][0], collisionY[i][0], startX, startY)) {
						closestEnemyCollisionX = collisionX[i][0];
						closestEnemyCollisionY = collisionY[i][0];
						closestEnemyI = i;
					}
				}
			}
		}
		if (distance(closestEnemyCollisionX, closestEnemyCollisionY, startX, startY) <= length && tempCheck) {
			length = distance(closestEnemyCollisionX, closestEnemyCollisionY, startX, startY);
			line = new Line2D.Double(startX, startY, closestEnemyCollisionX, closestEnemyCollisionY);
		}
		lazerSightLine = (Line2D) line.clone();
	}
	public double getLineCenterX( Line2D line ) {
		return (line.getX1() + line.getX2()) / 2.0;
	}
	public double getLineCenterY( Line2D line ) {
		return (line.getY1() + line.getY2()) / 2.0;
	}
	public byte sgn( double x ) {
		if (x < 0) {
			return -1;
		} else {
			return 1;
		}
	}
	public void increaseHealth() {
		if(lastHit==-1)
			lastHit=System.currentTimeMillis();

		if(System.currentTimeMillis()-lastHit>=500){		
			if (GameFrame.game.pHealth + 1 >= 100) {
				GameFrame.game.pHealth = 100;
			}else {
				//System.out.println("hit!");
				GameFrame.game.pHealth += 1;
			}
			lastHit=-1;
		}
		//System.out.println(pHealth);
	}
	public void settings() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				presets = SettingsConfig.presets;
				AA = SettingsConfig.AA;
				vignetting = SettingsConfig.vignetting;
				flares = SettingsConfig.flares;
				fancyMenu = SettingsConfig.fancyMenu;
				sound = SettingsConfig.sound;
				fullScreen = SettingsConfig.fullScreen;
				CPUs = SettingsConfig.CPUs;
				checkButtons();
				settings = true;
			}
		});
		t.start();
	}
	public void checkButtons() {
		System.out.println("Preset = " + presets);
		System.out.println("Anti-Aliasing = " + AA);
		System.out.println("Vignetting = " + vignetting);
		System.out.println("Muzzle Flares = " + flares);
		System.out.println("Fancy Menu = " + fancyMenu);
		System.out.println("Sound = " + sound);
		System.out.println("fullScreen = " + fullScreen);
		if (presets.equals("Custom")) {
			presetButtons[0].setSelected(false);
			presetButtons[1].setSelected(false);
			presetButtons[2].setSelected(true);
		} else if (presets.equals("High")) {
			presetButtons[0].setSelected(true);
			presetButtons[1].setSelected(false);
			presetButtons[2].setSelected(false);
		} else if (presets.equals("Low")) {
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
	public void saveSettings() {
		byte invokeVignetting = 0;
		byte invokeSound = 0;
		byte invokeFullScreen = 0;
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
		if (invokeVignetting == 1) {
			initVignetting();
		} else if (invokeVignetting == 2) {
			disableVignetting();
		}

		if (invokeFullScreen == 1) {
			GameFrame.gframe.changeAFrame(true);
		} else if (invokeFullScreen == 2) {
			GameFrame.gframe.changeAFrame(false);
		}
		settings = false;
	}
	public void initVignetting() {
		featherAnim();
	}
	public void disableVignetting() {
		// doesn't need to do anything... chillbrah.
	}
	public double getRadius(double A) {
		return (double)(Distance * Math.sin(Math.toRadians(A)))/(Math.sin(Math.toRadians(180 - (90 + A))));
	}
	public void drawEnemy(Graphics g, Graphics2D g2, int enemyID){
		AffineTransform at = new AffineTransform();
		at.translate(spawnEnemies.enemyList.get( enemyID - 1 ).getEnemyX() - tlcx, spawnEnemies.enemyList.get( enemyID - 1 ).getEnemyY()  - tlcy);
		at.rotate(Math.toRadians(-(270 - spawnEnemies.enemyList.get( enemyID - 1 ).getEnemyAngle()) + 180));
		if (spawnEnemies.enemyList.get( enemyID - 1 ).getType().equals("brute")) {
			at.translate(-33, -18);
		} else {
			at.translate(-17, -15);
		}
		g2.drawImage(enemyImg[spawnEnemies.enemyList.get( enemyID - 1 ).getEnemyID()], at, null);
		//g2.setPaint( new Color( 255, 0, 0 ) );
		//double tempX = spawnEnemies.enemyList.get( enemyID - 1 ).getEnemyX() ;
		//double tempY = spawnEnemies.enemyList.get( enemyID - 1 ).getEnemyY() ;
		//g2.fill( new Ellipse2D.Double(tempX - 10 - tlcx, tempY - 10 - tlcy, 20 , 20) );
	}
	//Used to cross blend vignetting  
	public static Color colorBlend (Color color1, Color color2, double ratio)
	{
		float r  = (float) ratio;
		float ir = (float) 1.0 - r;

		float rgb1[] = new float[3];
		float rgb2[] = new float[3];    

		color1.getColorComponents (rgb1);
		color2.getColorComponents (rgb2);    

		Color color = new Color (rgb1[0] * r + rgb2[0] * ir, 
				rgb1[1] * r + rgb2[1] * ir, 
				rgb1[2] * r + rgb2[2] * ir);

		return color;
	}

	public void drawHUD(Graphics g, Graphics2D g2) {
		Color tempC = colorBlend(Color.black, Color.red, pHealth/100.0);
		if (SettingsConfig.vignetting) {
			g2.setPaint(new RadialGradientPaint(new Rectangle(-250, -250, this.getWidth() + 500, this.getHeight() + 500), new float[] {0.0f, featherModifier}, new Color[] {new Color(tempC.getRed(), tempC.getGreen(), tempC.getBlue(), 0), tempC}, CycleMethod.NO_CYCLE));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g2.setFont( font );
		g2.setPaint(Color.white);
		String scoreString = "$" + money;
		g2.drawString(scoreString, (int) (windowWidth - fontMetrics.getStringBounds(scoreString, g2).getWidth()), 20);
		if (!pause) {
			double usedX;
			double usedY;
			if (smooth) {
				usedX = smoothMouseX;
				usedY = smoothMouseY;
			} else {
				usedX = MouseX;
				usedY = MouseY;
			}
			//g2.setPaint(new Color(0, 0, 0, 200));
			//g2.fill(new Ellipse2D.Double((MouseX - 3), (MouseY - 3), 6, 6));
			//g2.setPaint(new Color(255, 255, 255, 200));
			//g2.fill(new Ellipse2D.Double((MouseX - 2), (MouseY - 2), 4, 4));
			if (gunManager.getGunType().contains("Barrett")) {
				crosshairType = 2;
			} else {
				crosshairType = 1;
			}
			if ( crosshairType == 0 ) {
				// Circle
				g2.setStroke(new BasicStroke(4));
				g2.setPaint( new Color( 0, 0, 0, 200 ) );
				g2.draw( new Ellipse2D.Double(usedX - getRadius(gunManager.getTotalRecoil()), usedY - getRadius(gunManager.getTotalRecoil()), getRadius(gunManager.getTotalRecoil()) * 2, getRadius(gunManager.getTotalRecoil()) * 2));
				g2.setStroke(new BasicStroke(2));
				if (((sprint) && (Velocity != 0)) || (gunVoided))
					g2.setPaint( new Color(255, 0, 0, 200) );
				else
					g2.setPaint( new Color(gunManager.getCrosshairColour().getRed(), gunManager.getCrosshairColour().getGreen(), gunManager.getCrosshairColour().getBlue(), 200) );
				g2.draw( new Ellipse2D.Double(usedX - getRadius(gunManager.getTotalRecoil()), usedY - getRadius(gunManager.getTotalRecoil()), getRadius(gunManager.getTotalRecoil()) * 2, getRadius(gunManager.getTotalRecoil()) * 2));
			} else if ( crosshairType == 1 ) {
				// Cross
				g2.setStroke(new BasicStroke(4));
				g2.setPaint( new  Color( 0, 0, 0, 200 ) );
				g2.draw( new Line2D.Double( usedX + getRadius(gunManager.getTotalRecoil()), usedY, usedX + getRadius(gunManager.getTotalRecoil()) + 15, usedY ));
				g2.draw( new Line2D.Double( usedX - getRadius(gunManager.getTotalRecoil()), usedY, usedX - getRadius(gunManager.getTotalRecoil()) - 15, usedY ));
				g2.draw( new Line2D.Double( usedX, usedY + getRadius(gunManager.getTotalRecoil()), usedX, usedY + getRadius(gunManager.getTotalRecoil()) + 15 ));
				g2.draw( new Line2D.Double( usedX, usedY - getRadius(gunManager.getTotalRecoil()), usedX, usedY - getRadius(gunManager.getTotalRecoil()) - 15 ));

				g2.setStroke(new BasicStroke(2));
				if (((sprint) && (Velocity != 0)) || (gunVoided))
					g2.setPaint( new Color(255, 0, 0, 200) );
				else
					g2.setPaint( new Color(gunManager.getCrosshairColour().getRed(), gunManager.getCrosshairColour().getGreen(), gunManager.getCrosshairColour().getBlue(), 200) );
				g2.draw( new Line2D.Double( usedX + getRadius(gunManager.getTotalRecoil()), usedY, usedX + getRadius(gunManager.getTotalRecoil()) + 15, usedY ));
				g2.draw( new Line2D.Double( usedX - getRadius(gunManager.getTotalRecoil()), usedY, usedX - getRadius(gunManager.getTotalRecoil()) - 15, usedY ));
				g2.draw( new Line2D.Double( usedX, usedY + getRadius(gunManager.getTotalRecoil()), usedX, usedY + getRadius(gunManager.getTotalRecoil()) + 15 ));
				g2.draw( new Line2D.Double( usedX, usedY - getRadius(gunManager.getTotalRecoil()), usedX, usedY - getRadius(gunManager.getTotalRecoil()) - 15 ));
			} else if ( crosshairType == 2 ) {
				// Simplistic
				if (((sprint) && (Velocity != 0)) || (gunVoided))
					g2.setPaint(new RadialGradientPaint((float)usedX, (float)usedY, 3.5f, new float[] {0.2f, 1}, new Color[] {new Color(255, 0, 0), new Color(255, 0, 0, 0)}));
				else
					g2.setPaint(new RadialGradientPaint((float)usedX, (float)usedY, 3.5f, new float[] {0.2f, 1}, new Color[] {new Color(255, 255, 255), new Color(255, 255, 255, 0)}));
				g2.fill(new Ellipse2D.Double(usedX - 3.5, usedY - 3.5, 7, 7));
			}
		}
		if ( gunManager.getGunType().equals("Pistol") ) {
			g2.drawImage(HUDPistol, (int)((windowWidth - 30) - (HUDPistol.getWidth(null))), (int)((windowHeight - 70) - (HUDPistol.getHeight(null))), null);
		} else if (gunManager.getGunType().equals("ShotGun")) {
			g2.drawImage(HUDShotgun, (int)((windowWidth - 30) - (HUDShotgun.getWidth(null))), (int)((windowHeight - 70) - (HUDShotgun.getHeight(null))), null);
		}else if (gunManager.getGunType().equals("M16")) {
			g2.drawImage(HUDM16, (int)((windowWidth - 30) - (HUDM16.getWidth(null))), (int)((windowHeight - 70) - (HUDM16.getHeight(null))), null);
		}else if (gunManager.getGunType().equals("MP5")) {
			g2.drawImage(HUDMP5, (int)((windowWidth - 30) - (HUDMP5.getWidth(null))), (int)((windowHeight - 70) - (HUDMP5.getHeight(null))), null);
		}else if (gunManager.getGunType().equals( "Barrett")) {
			g2.drawImage(HUDSniper, (int)((windowWidth - 30) - (HUDSniper.getWidth(null))), (int)((windowHeight - 70) - (HUDSniper.getHeight(null))), null);
		}else if (gunManager.getGunType().equals("RocketLauncher")) {
			g2.drawImage(HUDRocketLauncher, (int)((windowWidth - 30) - (HUDRocketLauncher.getWidth(null))), (int)((windowHeight - 70) - (HUDRocketLauncher.getHeight(null))), null);
		}
		for (int a = 1; a <= gunManager.getClipSize(); a++) {
			if ((a <= gunManager.getClipSize() * 0.25) && (gunManager.getCurrentAmmo() <= gunManager.getClipSize() * 0.25) && (a <= gunManager.getCurrentAmmo())) {
				g2.setPaint( new Color( 200, 0, 0, 150 ) );
			} else {
				g2.setPaint( new Color( 200, 200, 200, 100 ) );
			}
			if (gunManager.getShellType().equals("Small")) {
				g2.setStroke(new BasicStroke(2));
				g2.draw( new Line2D.Double(windowWidth - 60 - (5 * a), windowHeight - 20 - (fontMetrics.getHeight() / 1.5), windowWidth - 60 - (5 * a), windowHeight - 20));
			} else if (gunManager.getShellType().equals("Large")) {
				g2.setStroke(new BasicStroke(3));
				g2.draw( new Line2D.Double(windowWidth - 60 - (6 * a), windowHeight - 20 - (fontMetrics.getHeight() / 1.5), windowWidth - 60 - (6 * a), windowHeight - 20));
			} else if (gunManager.getShellType().equals("Shell")) {
				g2.setStroke(new BasicStroke(7));
				g2.draw( new Line2D.Double(windowWidth - 60 - (10 * a), windowHeight - 20 - (fontMetrics.getHeight() / 1.5), windowWidth - 60 - (10 * a), windowHeight - 20));
			}
		}
		if (gunManager.getCurrentAmmo() <= gunManager.getClipSize() * 0.25) {
			g2.setPaint( new Color( 255, 0, 0, 150 ) );
		} else {
			g2.setPaint( new Color( 255, 255, 255, 150 ) );
		}
		for (int a = 1; a <= gunManager.getCurrentAmmo(); a++) {
			if (gunManager.getShellType().equals("Small")) {
				g2.setStroke(new BasicStroke(1));
				g2.draw( new Line2D.Double(windowWidth - 60 - (5 * a), windowHeight - 20 - (fontMetrics.getHeight() / 1.5), windowWidth - 60 - (5 * a), windowHeight - 20));
			} else if (gunManager.getShellType().equals("Large")) {
				g2.setStroke(new BasicStroke(2));
				g2.draw( new Line2D.Double(windowWidth - 60 - (6 * a), windowHeight - 20 - (fontMetrics.getHeight() / 1.5), windowWidth - 60 - (6 * a), windowHeight - 20));
			} else if (gunManager.getShellType().equals("Shell")) {
				g2.setStroke(new BasicStroke(5));
				g2.draw( new Line2D.Double(windowWidth - 60 - (10 * a), windowHeight - 20 - (fontMetrics.getHeight() / 1.5), windowWidth - 60 - (10 * a), windowHeight - 20));
			}
		}
		if (gunManager.getRemainingAmmo() <= gunManager.getClipSize()) {
			g2.setPaint( new Color( 255, 0, 0, 150 ) );
		} else {
			g2.setPaint( new Color( 255, 255, 255, 150 ) );
		}
		if (!gunManager.getGunType().contains("Pistol")) {
			g2.drawString(Integer.toString(gunManager.getRemainingAmmo()), windowWidth - 5 - fontMetrics.stringWidth(Integer.toString(gunManager.getRemainingAmmo())), windowHeight - 20);
		} else {
			g2.drawString("INF", windowWidth - 5 - fontMetrics.stringWidth(Integer.toString(gunManager.getRemainingAmmo())), windowHeight - 20);
		}
	}

	public void drawConsol(Graphics g, Graphics2D g2) {

		g2.setPaint(Color.white);
		g2.drawString("Angle = " + shootAngle, 5, 10);
		g2.drawString("MouseX = " + MouseX, 5, 20);
		g2.drawString("MouseY = " + MouseY, 5, 30);
		g2.drawString("Velocity = " + Velocity, 5, 40);
		g2.drawString("Distance = " + Distance, 5, 50);
		g2.drawString("Movement = " + Movement, 5, 60);
		g2.drawString("PX = " + pX, 5, 70);
		g2.drawString("PY = " + pY, 5, 80);
		g2.drawString("gunType = " + gunManager.getGunType(), 5, 90);
		g2.drawString("Shoot = " + shooting, 5, 100);
		g2.drawString("Recoil = " + gunManager.getTotalRecoil(), 5, 110);
		g2.drawString("Direction = " + direction, 5, 120);
		g2.drawString("Pressed Size = " + pressed.size(), 5, 130);
		g2.drawString("WalkAngle = " + walkAngle, 5, 140);
		g2.drawString("MouseRotation = " + mouseRotation, 5, 150);
		g2.drawString("Wave = " + spawnEnemies.waveNumber,5, 160);
		g2.setPaint( new  Color( 255, 0, 0, 100 ) );
		g2.setStroke(new BasicStroke(1));
		g2.draw(new Line2D.Double(shootX - tlcx, shootY - tlcy, Math.round(shootX + 1500 * Math.cos(Math.toRadians(shootAngle))) - tlcx, Math.round(shootY + 1500 * Math.sin( Math.toRadians(shootAngle))) - tlcy));
		g2.setPaint( new  Color( 0, 0, 0, 100 ) );
		g2.setStroke(new BasicStroke(1));
		if ( gunManager.getTotalRecoil() != 0 ) {
			g2.draw(new Line2D.Double(shootX - tlcx, shootY - tlcy, Math.round(shootX + 1500 * Math.cos(Math.toRadians(shootAngle + gunManager.getTotalRecoil()))) - tlcx, Math.round(shootY + 1500 * Math.sin( Math.toRadians(shootAngle + gunManager.getTotalRecoil()))) - tlcy));
			g2.draw(new Line2D.Double(shootX - tlcx, shootY - tlcy, Math.round(shootX + 1500 * Math.cos(Math.toRadians(shootAngle - gunManager.getTotalRecoil()))) - tlcx, Math.round(shootY + 1500 * Math.sin( Math.toRadians(shootAngle - gunManager.getTotalRecoil()))) - tlcy));
		}

	}
	public void drawLazerSight( Graphics g, Graphics2D g2 ) {
		g2.setPaint( new  Color( 255, 0, 0, 100 ) );
		g2.setStroke(new BasicStroke(1));
		g2.draw(new Line2D.Double(lazerSightLine.getX1() - tlcx, lazerSightLine.getY1() - tlcy, lazerSightLine.getX2() - tlcx, lazerSightLine.getY2() - tlcy));
	}
	public void drawDebugLines(Graphics g, Graphics2D g2 ) {
		if (showDebugLines[0]) {
			for (int i = 0; i < map.getWallSize(); i++) {
				g2.setPaint(new Color(255, 0, 0));
				g2.draw(new Line2D.Double(tempLine[i].getX1() - tlcx, tempLine[i].getY1() - tlcy, tempLine[i].getX2() - tlcx, tempLine[i].getY2() - tlcy));
			}
		}
		if (showDebugLines[1]) {
			for (int i = 0; i < map.getWallSize(); i++) {
				g2.setPaint(new Color(255, 255, 0));
				g2.draw(new Line2D.Double(tempLine2[i].getX1() - tlcx, tempLine2[i].getY1() - tlcy, tempLine2[i].getX2() - tlcx, tempLine2[i].getY2() - tlcy));
			}
		}
		if (showDebugLines[2]) {
			for (int i = 0; i < map.getWallSize(); i++) {
				//if (i == 28) {
				g2.setPaint(new Color(255, 255, 255));
				g2.draw(new Line2D.Double(tempLine3[i].getX1() - tlcx, tempLine3[i].getY1() - tlcy, tempLine3[i].getX2() - tlcx, tempLine3[i].getY2() - tlcy));
			}
		}
		if (showDebugLines[3]) {
			for (int i = 0; i < map.getWallSize(); i++) {
				//if (i == 28) {
				g2.setPaint(new Color(255, 255, 255));
				g2.draw(new Line2D.Double(tempLine4[i].getX1() - tlcx, tempLine4[i].getY1() - tlcy, tempLine4[i].getX2() - tlcx, tempLine4[i].getY2() - tlcy));
			}
		}
	}
	public void paint( Graphics g ) {
		paintComponent(g);
		paintBorder(g);
		if (shopFrame != null) {
			shopFrame.paintComponent(g);
		}
		paintOverlay(g);
		paintChildren(g);
	}
	public void paintOverlay( Graphics g ) {
		Graphics2D g2 = ( Graphics2D )g;
		if (AA) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		if (dead) {
			boolean hasGreened=false;
			g2.setPaint(Color.black);
			g2.fillRect(0, 0, windowWidth, windowHeight);
			g2.setPaint(Color.white);
			g2.setFont(this.fontExtraLarge);
			g2.drawString("Game Over", (float)(windowWidth / 2.0 - this.getFontMetrics(this.fontExtraLarge).stringWidth("Game Over")/2.0), (float)(windowHeight / 5.0 - this.getFontMetrics(this.fontExtraLarge).getHeight()/2.0));
			g2.setFont(this.fontLarge);
			g2.drawString("Score = " + score, (float)(windowWidth / 2.0 - this.getFontMetrics(this.fontLarge).stringWidth("Score = " + score)/2.0), (float)(windowHeight / 5.0 - this.getFontMetrics(this.fontLarge).getHeight()/2.0) + 30);			
			g2.drawString(deathMessage, (float)(windowWidth / 2.0 - this.getFontMetrics(this.fontLarge).stringWidth(deathMessage)/2.0), (float)(windowHeight / 5.0 - this.getFontMetrics(this.fontLarge).getHeight()/2.0) + 90);
			String[] scores=hs.getScores();
			for(int i=0;i<scores.length && i<10;++i){
				if(Integer.parseInt(scores[i].substring(3))==(int)score && !hasGreened){
					g2.setPaint(Color.green);
					hasGreened=true;
				}
				else
					g2.setPaint(Color.white);
				
				if(i>4)
					g2.drawString((i+1)+". "+scores[i].substring(2), (float)(windowWidth / 2.0  + 150-20), (float)(windowHeight / 2.0 - this.getFontMetrics(this.fontLarge).getHeight()/2.0)+100+40*(i-5));
				else
					g2.drawString((i+1)+". "+scores[i].substring(2), (float)(windowWidth / 2.0 - 150-20), (float)(windowHeight / 2.0 - this.getFontMetrics(this.fontLarge).getHeight()/2.0)+100+40*i);
			}
		}
		g2.setStroke(new BasicStroke(1));
		if (shop || menu && !dead) {
			g2.setPaint(new RadialGradientPaint(MouseX, MouseY, 3.5f, new float[] {0.2f, 1}, new Color[] {new Color(198, 219, 216), new Color(198, 219, 216, 0)}));
			g2.fill(new Ellipse2D.Double(MouseX - 3.5, MouseY - 3.5, 7, 7));
			g2.setPaint(new RadialGradientPaint(new Point2D.Double(MouseX, MouseY), (float)(Math.sqrt(Math.pow(this.getWidth() / 2.0, 2) + Math.pow(this.getHeight() / 2.0, 2))), new float[] {0.3f, 1}, new Color[] {new Color(198, 219, 216, 0), new Color(198, 219, 216)}, CycleMethod.NO_CYCLE));
			g2.drawLine(0, MouseY, this.getWidth(), MouseY);
			g2.drawLine(MouseX, 0, MouseX, this.getHeight());
		}
	}
	public void paintComponent( Graphics g ) {
		windowWidth = this.getWidth();
		windowHeight = this.getHeight();
		Graphics2D g2 = ( Graphics2D )g;
		this.grabFocus();
		if (AA) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g2.setClip(0, 0, this.getWidth(), this.getHeight());
		if (initialized) {
			g.setColor( Color.gray );
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g2.setStroke(new BasicStroke(1));
			for ( int i = 0; i < gunManager.bulletList.size(); i ++) {
				if ( !( gunManager.bulletList.get( i ).getActive( ) ) ) 
					gunManager.bulletList.remove(gunManager.bulletList.get( i ));
				else {
					Color color = 
							new Color(gunManager.bulletList.get(i).getBulletColor().getRed(),
									gunManager.bulletList.get(i).getBulletColor().getGreen(),
									gunManager.bulletList.get(i).getBulletColor().getBlue());
					g2.setPaint(color);
					g2.setStroke( new BasicStroke( (float) gunManager.bulletList.get(i).getBSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
					g2.draw( new Line2D.Double( gunManager.bulletList.get(i).getLine().getX1() - tlcx, gunManager.bulletList.get(i).getLine().getY1() - tlcy, gunManager.bulletList.get(i).getLine().getX2() - tlcx, gunManager.bulletList.get(i).getLine().getY2() - tlcy) );
				}
			}
			if (gunManager.getGunType().contains("Barrett")) {
				drawLazerSight(g, g2);
			}
			for ( int enemyID = 1; enemyID <= spawnEnemies.enemyList.size(); enemyID++) {
				if ( spawnEnemies.enemyList.get( enemyID - 1 ).getKilled( ) ) 
					spawnEnemies.enemyList.remove( spawnEnemies.enemyList.get( enemyID - 1 ) );
				else {
					drawEnemy(g,g2,enemyID);
					//g2.fill( new Ellipse2D.Double( EnemySpawner.enemyList.get( enemyID - 1 ).getEnemyX() - 10, EnemySpawner.enemyList.get( i - 1 ).getEnemyY() - 10, 20, 20 ) );
				}
			}
			g2.setStroke( new BasicStroke(1));
			g2.setPaint(Color.black);
			for (int i = 0; i < map.getWallSize(); i++) {
				g2.fill(new Rectangle2D.Double(map.getMapX(i) - tlcx, map.getMapY(i) - tlcy, 20, 20));
			}
			AffineTransform at = new AffineTransform();
			if (gunVoided) {
				at.translate(pX - tlcx, pY - tlcy);
				at.rotate(Math.toRadians(-(90 - shootAngle)));
				at.translate(-19, -12);
				g2.drawImage(playerEmptyImg, at, null);
			} else {
				at.translate(pX - tlcx, pY - tlcy);
				at.rotate(Math.toRadians(-(90 - shootAngle)));
				at.translate(-17, -15);
				if (gunManager.getGunType().equals("Pistol")) {
					g2.drawImage(playerPistolImg, at, null);
				} else if (gunManager.getGunType().equals("ShotGun")) {
					g2.drawImage(playerShotgunImg, at, null);
				} else {
					g2.drawImage(playerM16Img, at, null);
				}
			}
			for ( int i = 0; i < gunManager.muzzleFlash.size(); i ++) {
				if ( !( gunManager.muzzleFlash.get( i ).getActive( ) ) ) 
					gunManager.muzzleFlash.remove(gunManager.muzzleFlash.get( i ));
				else {
					g2.drawImage(gunManager.muzzleFlash.get( i ).getImg(), (int)((pX - tlcx) - this.getWidth() / 2.0), (int)((pY - tlcy) - this.getHeight() / 2.0), null);
				}
			}

			for ( int i = 1; i <= gunManager.explosionList.size(); i ++) {
				if (gunManager.explosionList.get( i - 1 ).getActive()) {
					g2.drawImage(gunManager.explosionList.get( i - 1 ).getCurrentImg(), gunManager.explosionList.get( i - 1 ).getCurrentX() - (int)tlcx, gunManager.explosionList.get( i - 1 ).getCurrentY() - (int)tlcy, null);
					g.drawString(gunManager.explosionList.get( i - 1 ).getCurrentPicID() + ", " + gunManager.explosionList.get( i - 1 ).getCurrentX() + ", " + gunManager.explosionList.get( i - 1 ).getCurrentY(), 200, 200);
					g2.setPaint(new Color(255, 255, 255, gunManager.explosionList.get( i - 1 ).getCurrentAlpha()));
					g2.fill( new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight()));
				} else {
					gunManager.explosionList.remove(gunManager.explosionList.get( i - 1 )); 	
				}
			}



			drawDebugLines(g, g2);
			drawHUD(g, g2);
			g2.setPaint(new Color(0, 0, 0, fadeAmount));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			if (menu){
				if (settings) {
					g2.setPaint(new Color(0, 0, 0, 200));
					g2.fillRect(0, 0, this.getWidth(), this.getHeight());
					saveButton.setLocation(10, windowHeight - 35);
					backButton.setLocation(210, windowHeight - 35);
					saveButton.drawComponent(g, g2);
					backButton.drawComponent(g, g2);

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

					g2.setPaint(new Color(255, 255, 255, 200));
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
				} else {
					g2.setPaint(new Color(112, 128, 144, 100));
					g2.fillRect(0, 0, this.getWidth(), this.getHeight());
					g2.setPaint(new Color(0, 0, 0, 200));
					g2.fill(new RoundRectangle2D.Double(this.getWidth()/2.0 - 120, this.getHeight()/2.0 - 152.5, 240, 305, 20, 20));

					resumeButton.setLocation(this.getWidth() / 2.0 - 100, this.getHeight() / 2.0 - 152.5 + 20);
					resumeButton.drawComponent(g, g2);

					settingsButton.setLocation(this.getWidth() / 2.0 - 100, resumeButton.getBounds2D().getY() + resumeButton.getBounds2D().getHeight() + 20);
					settingsButton.drawComponent(g, g2);

					exitButton.setLocation(this.getWidth() / 2.0 - 100, settingsButton.getBounds2D().getY() + settingsButton.getBounds2D().getHeight() + 20);
					exitButton.drawComponent(g, g2);
				}
			}
			if ( consol ) {
				drawConsol(g, g2);
			}
		} else {
			g2.setPaint(Color.black);
			g2.drawRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
	public boolean isOpaque() {
		return true;
	}
	synchronized public void keyPressed( KeyEvent k ) {
		if (!pause && !dead)
			pressed.add(k.getKeyCode());

	}

	synchronized public void keyReleased( KeyEvent k ) {
		pressed.remove(k.getKeyCode());
		if (!dead){
			if (!movementType) {
				if ((!(pressed.contains(KeyEvent.VK_W))) && (!(pressed.contains(KeyEvent.VK_D))) && (!(pressed.contains(KeyEvent.VK_S))) && (!(pressed.contains(KeyEvent.VK_A)))) {
					Movement = false;
				}
			} else
				if (!(pressed.contains(KeyEvent.VK_W))) {
					Movement = false;
				}
			if (k.getKeyCode() == KeyEvent.VK_SHIFT ){
				sprint = false;
				maxSpeed = 2;
			}
			if (!pause) {

				if (k.getKeyCode() == KeyEvent.VK_R ){
					pressReload = false;
				}
				if (k.getKeyCode() == KeyEvent.VK_E)
				{
					gunManager.nextGun();
				}
				if (k.getKeyCode() == KeyEvent.VK_Q)
				{
					gunManager.prevGun();
				}
				if (debug) {
					if (k.getKeyCode() == KeyEvent.VK_BACK_QUOTE ) {
						if (consol) {
							consol = false;
						} else if (!consol) {
							consol = true;
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_EQUALS ) {
						if (crosshairType == 2) {
							crosshairType = 0;
						} else {
							crosshairType++;
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_SPACE ) {
						smooth = !smooth;
					}
					if (k.getKeyCode() == KeyEvent.VK_1 ) {
						if (showDebugLines[0]) {
							showDebugLines[0] = false;
						} else if (!showDebugLines[0]) {
							showDebugLines[0] = true;
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_2 ) {
						if (showDebugLines[1]) {
							showDebugLines[1] = false;
						} else if (!showDebugLines[1]) {
							showDebugLines[1] = true;
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_3 ) {
						if (showDebugLines[2]) {
							showDebugLines[2] = false;
						} else if (!showDebugLines[2]) {
							showDebugLines[2] = true;
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_4 ) {
						if (showDebugLines[3]) {
							showDebugLines[3] = false;
						} else if (!showDebugLines[3]) {
							showDebugLines[3] = true;
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_CONTROL) {
						if (shop) {	
							stopShop();
						} else {
							startShop();
						}
					}
					if (k.getKeyCode() == KeyEvent.VK_K) {
						GameFrame.game.money+=10000;
					}
				}
			}
			if (k.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (pause) {
					if (settings) {
						settings = false;
					} else if (shop) {
						shop = false;
						stopShop();
						menu = true;
					} else {
						pause = false;
						menu = false;
					}
				} else {
					pause = true;
					menu = true;
				}
			}
		}
	}
	public void startShop() {
		pause = true;
		shop = true;
		shopFrame = new Shop();
		shopFrame.setSize(800, 500);
		shopFrame.setLocation((int)((this.getWidth() / 2.0) - (shopFrame.w / 2.0)), (int)((this.getHeight() / 2.0) - (shopFrame.h / 2.0)));
	}
	public void stopShop() {
		shopFrame = null;
		pause = false;
		shop = false;
	}

	public void keyTyped( KeyEvent k ) {

	}
	public void mouseDragged( MouseEvent e ) {
		if (shopFrame != null) {
			shopFrame.setMouseCoordinates(e, "Dragged");
		}
		MouseX = e.getX();
		MouseY = e.getY();
		if (!dead) {
			if (!menu) {
				if (!pause) {
					if(e.getModifiers()==InputEvent.BUTTON1_MASK) {
						if (!(gunManager.getGunRapid())) {
							if ((allowedShoot) && (System.currentTimeMillis() - lastShot >= gunManager.getGunRate())) {
								lastShot = System.currentTimeMillis();
								shooting = true;
								allowedShoot = false;
							}
						} else if (gunManager.getGunRapid()) {
							allowedShoot = true;
							shooting = true;
						}
					}

					if(e.getModifiers()==InputEvent.BUTTON3_MASK) {   
						isknifing=1000;
					}
				}
			} else {
				if (settings){
					for (int i = 0; i < settingsButtonList.size(); i++) {
						for (int p = 0; p < settingsButtonList.get(i).length; p++) {
							if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
								settingsButtonList.get(i)[p].setContains(true);
							} else {
								settingsButtonList.get(i)[p].setContains(false);
							}
						}
					}
					if (saveButton.getBounds2D().contains(e.getX(), e.getY())) {
						saveButton.setContains(true);
					} else {
						saveButton.setContains(false);
					}
					if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
						exitButton.setContains(true);
					} else {
						exitButton.setContains(false);
					}
					saveButton.setContains(saveButton.getBounds2D().contains(e.getX(), e.getY()));
					backButton.setContains(backButton.getBounds2D().contains(e.getX(), e.getY()));
				} else {
					resumeButton.setContains(resumeButton.getBounds2D().contains(e.getX(), e.getY()));
					settingsButton.setContains(settingsButton.getBounds2D().contains(e.getX(), e.getY()));
					exitButton.setContains(exitButton.getBounds2D().contains(e.getX(), e.getY()));
				}
			}
		}
	}

	public void mouseMoved( MouseEvent e ) {
		if (shopFrame != null) {
			shopFrame.setMouseCoordinates(e, "Moved");
		}
		MouseX = e.getX();
		MouseY = e.getY();
		if (!dead) {
			if (menu) {
				if (settings){
					for (int i = 0; i < settingsButtonList.size(); i++) {
						for (int p = 0; p < settingsButtonList.get(i).length; p++) {
							if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
								settingsButtonList.get(i)[p].setContains(true);
							} else {
								settingsButtonList.get(i)[p].setContains(false);
							}
						}
					}
					if (saveButton.getBounds2D().contains(e.getX(), e.getY())) {
						saveButton.setContains(true);
					} else {
						saveButton.setContains(false);
					}
					if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
						exitButton.setContains(true);
					} else {
						exitButton.setContains(false);
					}
					saveButton.setContains(saveButton.getBounds2D().contains(e.getX(), e.getY()));
					backButton.setContains(backButton.getBounds2D().contains(e.getX(), e.getY()));
				} else {
					resumeButton.setContains(resumeButton.getBounds2D().contains(e.getX(), e.getY()));
					settingsButton.setContains(settingsButton.getBounds2D().contains(e.getX(), e.getY()));
					exitButton.setContains(exitButton.getBounds2D().contains(e.getX(), e.getY()));
				}
			}
		}
	}

	public void mouseClicked( MouseEvent e ) {
	}
	public void mouseEntered( MouseEvent e ) {
	}
	public void mouseExited( MouseEvent e ) {
	}
	public void mousePressed( MouseEvent e ) {
		if (shopFrame != null) {
			shopFrame.setMouseCoordinates(e, "Pressed");
		}
		MouseX = e.getX();
		MouseY = e.getY();
		if (!dead) {
			if (!menu) {
				if (!pause) {
					if(e.getModifiers()==InputEvent.BUTTON1_MASK) {
						if (!(gunManager.getGunRapid())) {
							if ((allowedShoot) && (System.currentTimeMillis() - lastShot >= gunManager.getGunRate())) {
								lastShot = System.currentTimeMillis();
								shooting = true;
								allowedShoot = false;
							}
						} else if (gunManager.getGunRapid()) {
							allowedShoot = true;
							shooting = true;
						}
					}

					if(e.getModifiers()==InputEvent.BUTTON3_MASK) {
						isknifing=1000;
					}
				}
			} else {
				if (settings){
					for (int i = 0; i < settingsButtonList.size(); i++) {
						for (int p = 0; p < settingsButtonList.get(i).length; p++) {
							if (settingsButtonList.get(i)[p].getBounds2D().contains(e.getX(), e.getY())) {
								settingsButtonList.get(i)[p].setContains(true);
							} else {
								settingsButtonList.get(i)[p].setContains(false);
							}
						}
						saveButton.setContains(saveButton.getBounds2D().contains(e.getX(), e.getY()));
						backButton.setContains(backButton.getBounds2D().contains(e.getX(), e.getY()));
					}
				} else {
					resumeButton.setContains(resumeButton.getBounds2D().contains(e.getX(), e.getY()));
					settingsButton.setContains(settingsButton.getBounds2D().contains(e.getX(), e.getY()));
					exitButton.setContains(exitButton.getBounds2D().contains(e.getX(), e.getY()));
				}
			}
		}
	}
	public void mouseReleased( MouseEvent e ) {
		if (shopFrame != null) {
			shopFrame.setMouseCoordinates(e, "Released");
		}
		MouseX = e.getX();
		MouseY = e.getY();
		shooting = false;
		allowedShoot = true;
		if (!dead) {
			if (menu) {
				if (settings) {
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
					if (saveButton.getBounds2D().contains(e.getX(), e.getY())) {
						saveSettings();
					} else if (backButton.getBounds2D().contains(e.getX(), e.getY())) {
						settings = false;
					}
				} else {
					if (resumeButton.getBounds2D().contains(e.getX(), e.getY())) {
						pause = false;
						menu = false;
					} else if (settingsButton.getBounds2D().contains(e.getX(), e.getY())) {
						settings();
					} else if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
						GameFrame.gframe.tryExit();
					}
				}
			}
		}
	}

	public void mouseWheelMoved( MouseWheelEvent e ) {
		if (!pause) {
			if (e.getWheelRotation() < 0) {
				if (!mouseRotationUpOrDown)
					mouseRotation = 0;
				mouseRotation += 1;
				mouseRotationUpOrDown = true;
				if (mouseRotation == 2) {
					gunManager.nextGun();
					mouseRotation = 0;
				}
			} else if (e.getWheelRotation() > 0) {
				if (mouseRotationUpOrDown)
					mouseRotation = 0;
				mouseRotation += 1;
				mouseRotationUpOrDown = false;
				if (mouseRotation == 2) {
					gunManager.prevGun();
					mouseRotation = 0;
				}
			}
		}
	}
	public double getPlayerX() {
		return pX;
	}
	public double getPlayerY() {
		return pY;
	}
	public double getShootAngle() {
		return shootAngle;
	}
	public void terminateGame() {
		System.out.println("	Terminating Enemies");
		spawnEnemies.terminateEnemies();
	}
	public Map getMap() {
		return map;
	}
	public void setShootShake( double txShake, double tyShake) {
		this.vShootxShake = txShake;
		this.vShootyShake = tyShake;
	}
	public void setMoveShake( double txShake, double tyShake) {
		this.vMovexShake = txShake;
		this.vMoveyShake = tyShake;
	}
	public void Ricochet( int s) { 
		if (SettingsConfig.sound) {
			if (s == 0) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 2.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
			} else if (s == 1) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 3.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
			} else if (s == 2) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 4.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
			}  else if (s == 3) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 5.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
			} else if (s == 4) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 6.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
			} else if (s == 5) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 7.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
			} else if (s == 6) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 8.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();

			}
			else if (s == 7) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/Ricochet/Bullet Ricochet 1.mp3");
						} catch (FileNotFoundException e1) {}
						try {
							clip = new Player(is);
						} catch (JavaLayerException e){ }
						try {
							clip.play();
						} catch (JavaLayerException e) {}
					}
				});
				thread.start();
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
	public FontMetrics getGameFontMetrics ( Font f) {
		return getFontMetrics(f); 
	}
	public void killPlayer( String deathMessage) {
		pause = true;
		if (!dead) {
			this.deathMessage = deathMessage;
			dead = true;
			
			Object[] option = { "Exit to Menu", "Exit to Desktop", "Restart" };
			int answer = JOptionPane.showOptionDialog(GameFrame.f, "What would you like to do?", "You're dead.", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, option, option[0]);  		
			if (answer == 2) {
				GameFrame.gframe.startMenu();
				try {
					GameFrame.gframe.startGame();
				} catch (IOException e) {}
			} else if (answer == 0) {
				GameFrame.gframe.startMenu();
			} else if (answer == 1 || answer == -1) {
				System.exit(0);
			}
		}
	}
}


class Shop  {

	boolean shopVisible = true;
	boolean movingFrame = false;
	boolean exitSelected = false;
	double clickedX, clickedY;
	double x, y, w, h;
	double actualY, actualHeight;
	double pmx = 0, pmy = 0, mx = 0, my = 0;
	boolean temp = true;
	private byte menuSetting = 0; 
	private SRadioButton pistolMain = new SRadioButton(0, 0, 150, 50, "Pistol", 20f, false); 
	private SRadioButton shotgunMain = new SRadioButton(0, 0, 150, 50, "Shotgun", 20f, false);
	private SRadioButton mp5Main = new SRadioButton(0, 0, 150, 50, "MP5", 20f, false);
	private SRadioButton m16Main  = new SRadioButton(0, 0, 150, 50, "M16", 20f, false);;
	private SRadioButton sniperMain  = new SRadioButton(0, 0, 150, 50,  "Barrett", 20f, false);;
	private SButton buyAmmo = new SButton(0, 0, 300, 25, "Buy Ammo Clip", 20f, new Color(255, 255, 255), new Color(0, 200, 0));
	private SRadioButton buyGun = new SRadioButton(0, 0, 300, 25, "Buy Gun", 20f, false, new Color(255, 255, 255), new Color(0, 200, 0));;
	private SRadioButton[] shopButtons = {pistolMain, shotgunMain,m16Main, mp5Main, sniperMain};
	String title = "Shop";
	GeneralPath topBar = new GeneralPath();
	GeneralPath bottomSegment = new GeneralPath();
	GeneralPath totalFrame = new GeneralPath();
	Ellipse2D exitButton = new Ellipse2D.Double(x + w - 20, y + 20, 20, 20);
	private FontMetrics fontMetrics; 
	private Font fontNormal = new Font("Trebuchet MS", 0, 20 );
	private Font fontSlighlyLargerButNotTooLarge = new Font("Trebuchet MS", 0, 30 );
	private Font fontLarge = new Font("Trebuchet MS", 0, 40 );
	private Font fontExtraLarge = new Font("Trebuchet MS", 0, 50 );
	private Font fontSmall = new Font("Trebuchet MS", 0, 15 );
	private Font fontSmaller= new Font("Trebuchet MS", 0, 10 );
	private Font fontSmallest = new Font("Trebuchet MS", 0, 2 );
	Font[] font = {fontSmallest, fontSmaller,fontSmall, fontNormal, fontSlighlyLargerButNotTooLarge, fontLarge, fontExtraLarge};

	public void paintComponent( Graphics g ) {
		Graphics2D g2 = (Graphics2D)g;
		String tempString;
		if (SettingsConfig.AA) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g2.setFont(GameFrame.game.font);
		Graphics2D tempg = (Graphics2D) g2.create();
		tempg.setStroke(new BasicStroke(1));
		tempg.setPaint(new Color(40, 40, 40, 240));
		tempg.fill(topBar);
		tempg.setPaint(new Color(10, 10, 10, 240));
		tempg.fill(bottomSegment);
		tempg.setPaint(new Color(127, 127, 127, 240));
		tempg.draw(totalFrame);
		if (!exitSelected) {
			tempg.setPaint(new Color(160, 160, 160, 240));
		} else {
			tempg.setPaint(new Color(255, 255, 255, 240));
		}
		tempg.fill(exitButton);
		tempg.setPaint(new Color(40, 40, 40, 240));
		tempg.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		tempg.draw(new Line2D.Double(exitButton.getCenterX() - 3, exitButton.getCenterY() - 4, exitButton.getCenterX() + 3, exitButton.getCenterY() + 3));
		tempg.draw(new Line2D.Double(exitButton.getCenterX() - 3, exitButton.getCenterY() + 3, exitButton.getCenterX() + 3, exitButton.getCenterY() - 4));
		tempg.setPaint(Color.white);
		tempg.setStroke(new BasicStroke(1));
		for (int a = 0; a <shopButtons.length; a++) {
			shopButtons[a].drawComponent(g, tempg);
		}
		tempg.setPaint(new RadialGradientPaint((float)(x + 75), (float)(actualY + (actualHeight/2.0)), (float) (actualHeight /2.0) + 25, new float[] {0.0f, 1.0f}, new Color[] {new Color(255, 255, 255, 75), new Color(255, 255, 255, 0) }));
		tempg.draw(new Line2D.Double(x + 200, shopButtons[0].getBounds2D().getY() - 10, x + 200, shopButtons[4].getBounds2D().getY() + shopButtons[4].getBounds2D().getHeight() + 10));
		tempg.setPaint(new Color(255, 255, 255, 200));
		tempg.setFont(font[2]);
		fontMetrics = GameFrame.game.getFontMetrics(font[2]);
		tempg.drawString("Shop", (float) (x + fontMetrics.getStringBounds("Shop", g).getWidth() / 2.0), (float) (actualY - 20 + fontMetrics.getStringBounds ("Shop", g).getWidth()/2.0));
		tempg.setFont(font[3]);
		fontMetrics = GameFrame.game.getFontMetrics(font[3]);
		if (menuSetting == 0) {
			tempg.setFont(font[6]);
			fontMetrics = GameFrame.game.getFontMetrics(font[6]);
			tempg.drawString("Welcome to the shop!", (float) (x + 500 - fontMetrics.getStringBounds("Welcome to the shop!", g).getWidth() / 2.0), (float) (actualY + 10 + fontMetrics.getStringBounds("Welcome to the shop!", g).getHeight()) );
			tempg.setFont(font[3]);
			fontMetrics = GameFrame.game.getFontMetrics(font[3]);
			tempg.drawString("Click on a gun to the left to see it's",
					(float) (x + 500 - fontMetrics.getStringBounds("Click on a gun to the left to see it's", g).getWidth() / 2.0),
					(float) (actualY + 100 + fontMetrics.getStringBounds("Click on a gun to the left to see it's", g).getHeight()) );
			tempg.drawString("stats and purchase the gun and/or ammo.",
					(float) (x + 500 - fontMetrics.getStringBounds("stats and purchase the gun and/or ammo.", g).getWidth() / 2.0),
					(float) (actualY + 120 + fontMetrics.getStringBounds("stats and purchase the gun and/or ammo.", g).getHeight()) );
			tempString = "Credits = $" + (int)GameFrame.game.money;
			tempg.drawString(tempString, (float)(x + 200 + 300 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
		}else{
			if (menuSetting == 1) {
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.drawString("Pistol", (float) (x + 250), (float) (shopButtons[0].getBounds2D().getY() + fontMetrics.getStringBounds("Pistol", g).getHeight()));
				tempg.drawImage(GameFrame.game.HUDPistol, (int)(x + w - 10 - GameFrame.game.HUDPistol.getWidth()), (int) (shopButtons[0].getBounds2D().getY()), null);
				tempg.setFont(font[2]);
				fontMetrics = GameFrame.game.getFontMetrics(font[2]);
				tempg.drawString("Damage: " + Pistol.getGunDamage(), (float) (x + 250), (float) (actualY + 75));
				tempg.drawString("Fire Rate: " + roundTwoDecimals(1000.0/Pistol.getRate()) + " Rounds per Minute", (float) (x + 250), (float) (actualY + 95));
				tempg.drawString("Clip Size: " + Pistol.getClipSize(), (float) (x + 250), (float) (actualY + 115));
				tempg.drawString("Recoil Rating: Low", (float) (x + 250), (float) (actualY) + 135);
				tempg.drawString("Fire Mode: Semi-Automatic" , (float) (x + 250), (float) (actualY) + 155);
				tempg.drawString("Price: " , (float) (x + 250), (float) (actualY) + 175);
				tempg.setPaint(new Color(0, 200, 0, 200));
				tempg.drawString("Free" , (float) (x + 250 + fontMetrics.getStringBounds("Price: ", g).getWidth()), (float) (actualY) + 175);
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.drawString("Ammo Price: " , (float) (x + 250), (float) (actualY) + 195);
				tempg.setPaint(new Color(0, 200, 0, 200));
				tempg.drawString("Unlimited" , (float) (x + 250 + fontMetrics.getStringBounds("Ammo Price: ", g).getWidth()), (float) (actualY) + 195);
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.setPaint(new Color(255, 255, 255, 200));
				if (Pistol.isUnlocked()) {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 150 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					tempString = "Ammo = Infinite";
					tempg.drawString(tempString, (float)(x + 200 + 450 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyGun.setLocation(x + 200 + 150 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setSelected(true);
					buyGun.setText("Bought");
					buyGun.drawComponent(g, tempg);
				} else {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 300 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyGun.setLocation(x + 200 + 300 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setText("Buy Gun");
					buyGun.drawComponent(g, tempg);
				}
			}else if (menuSetting == 2) {
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.drawString("Shotgun", (float) (x + 250), (float) (shopButtons[0].getBounds2D().getY() + fontMetrics.getStringBounds("Shotgun", g).getHeight()));
				tempg.drawImage(GameFrame.game.HUDShotgun, (int)(x + w - 10 - GameFrame.game.HUDShotgun.getWidth()), (int) (shopButtons[0].getBounds2D().getY()), null);
				tempg.setFont(font[2]);
				fontMetrics = GameFrame.game.getFontMetrics(font[2]);
				tempg.drawString("Damage: " + ShotGun.getGunDamage() + " x 6", (float) (x + 250), (float) (actualY + 75));
				tempg.drawString("Fire Rate: " + roundTwoDecimals(1000.0/ShotGun.getRate()) + " Rounds per Minute", (float) (x + 250), (float) (actualY + 95));
				tempg.drawString("Clip Size: " + ShotGun.getClipSize(), (float) (x + 250), (float) (actualY + 115));
				tempg.drawString("Recoil Rating: High", (float) (x + 250), (float) (actualY) + 135);
				tempg.drawString("Fire Mode: Pump Action" , (float) (x + 250), (float) (actualY) + 155);
				tempg.drawString("Price: " , (float) (x + 250), (float) (actualY) + 175);
				if (GameFrame.game.money >= 500) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyGun.setSelectedTextColor(new Color(0, 200, 0, 200));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyGun.setSelectedTextColor(new Color( 200, 0, 0, 200 ));
				}
				tempg.drawString("$500.00" , (float) (x + 250 + fontMetrics.getStringBounds("Price: ", g).getWidth()), (float) (actualY) + 175);
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.drawString("Ammo Price: " , (float) (x + 250), (float) (actualY) + 195);
				if (GameFrame.game.money >= 20) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyAmmo.setSelectedTextColor(new Color(0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyAmmo.setSelectedTextColor(new Color( 200, 0, 0));
				}
				tempg.drawString("$20.00/clip" , (float) (x + 250 + fontMetrics.getStringBounds("Ammo Price: ", g).getWidth()), (float) (actualY) + 195);
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.setPaint(new Color(255, 255, 255, 200));
				if (ShotGun.isUnlocked()) {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 150 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					tempString = "Ammo = " + (ShotGun.getCurrentAmmo() + ShotGun.getRemainingAmmo());
					tempg.drawString(tempString, (float)(x + 200 + 450 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyAmmo.setLocation(x + 200 + 450 - buyAmmo.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyAmmo.drawComponent(g, tempg);
					buyGun.setLocation(x + 200 + 150 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setSelected(true);
					buyGun.setText("Bought");
					buyGun.drawComponent(g, tempg);
				} else {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 300 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyGun.setLocation(x + 200 + 300 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setText("Buy Gun");
					buyGun.drawComponent(g, tempg);
				}
			}else if (menuSetting == 3) {
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.drawString("M16", (float) (x + 250), (float) (shopButtons[0].getBounds2D().getY() + fontMetrics.getStringBounds("Shotgun", g).getHeight()));
				tempg.drawImage(GameFrame.game.HUDM16, (int)(x + w - 10 - GameFrame.game.HUDM16.getWidth()), (int) (shopButtons[0].getBounds2D().getY()), null);
				tempg.setFont(font[2]);
				fontMetrics = GameFrame.game.getFontMetrics(font[2]);
				tempg.drawString("Damage: " + M16.getGunDamage(), (float) (x + 250), (float) (actualY + 75));
				tempg.drawString("Fire Rate: " + roundTwoDecimals(1000.0/M16.getRate()) + " Rounds per Minute", (float) (x + 250), (float) (actualY + 95));
				tempg.drawString("Clip Size: " + M16.getClipSize(), (float) (x + 250), (float) (actualY + 115));
				tempg.drawString("Recoil Rating: Medium", (float) (x + 250), (float) (actualY) + 135);
				tempg.drawString("Fire Mode: 3 Round Semi-Automatic Burst" , (float) (x + 250), (float) (actualY) + 155);
				tempg.drawString("Price: " , (float) (x + 250), (float) (actualY) + 175);
				if (GameFrame.game.money >= 1500) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyGun.setSelectedTextColor(new Color( 0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyGun.setSelectedTextColor(new Color( 200, 0, 0));
				}
				tempg.drawString("$1500.00" , (float) (x + 250 + fontMetrics.getStringBounds("Price: ", g).getWidth()), (float) (actualY) + 175);
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.drawString("Ammo Price: " , (float) (x + 250), (float) (actualY) + 195);
				if (GameFrame.game.money >= 60) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyAmmo.setSelectedTextColor(new Color( 0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyAmmo.setSelectedTextColor(new Color( 200, 0, 0));
				}
				tempg.drawString("$60.00/clip" , (float) (x + 250 + fontMetrics.getStringBounds("Ammo Price: ", g).getWidth()), (float) (actualY) + 195);
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.setPaint(new Color(255, 255, 255, 200));
				if (M16.isUnlocked()) {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 150 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					tempString = "Ammo = " + (M16.getCurrentAmmo() + M16.getRemainingAmmo());
					tempg.drawString(tempString, (float)(x + 200 + 450 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyAmmo.setLocation(x + 200 + 450 - buyAmmo.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyAmmo.drawComponent(g, tempg);
					buyGun.setLocation(x + 200 + 150 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setSelected(true);
					buyGun.setText("Bought");
					buyGun.drawComponent(g, tempg);
				} else {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 300 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyGun.setLocation(x + 200 + 300 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setText("Buy Gun");
					buyGun.drawComponent(g, tempg);
				}
			}else if (menuSetting == 4) {
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.drawString("MP5", (float) (x + 250), (float) (shopButtons[0].getBounds2D().getY() + fontMetrics.getStringBounds("Shotgun", g).getHeight()));
				tempg.drawImage(GameFrame.game.HUDMP5, (int)(x + w - 10 - GameFrame.game.HUDMP5.getWidth()), (int) (shopButtons[0].getBounds2D().getY()), null);
				tempg.setFont(font[2]);
				fontMetrics = GameFrame.game.getFontMetrics(font[2]);
				tempg.drawString("Damage: " + MP5.getGunDamage(), (float) (x + 250), (float) (actualY + 75));
				tempg.drawString("Fire Rate: " + roundTwoDecimals(1000.0/MP5.getRate()) + " Rounds per Minute", (float) (x + 250), (float) (actualY + 95));
				tempg.drawString("Clip Size: " + MP5.getClipSize(), (float) (x + 250), (float) (actualY + 115));
				tempg.drawString("Recoil Rating: High", (float) (x + 250), (float) (actualY) + 135);
				tempg.drawString("Fire Mode: Fully Automatic" , (float) (x + 250), (float) (actualY) + 155);
				tempg.drawString("Price: " , (float) (x + 250), (float) (actualY) + 175);
				if (GameFrame.game.money >= 1500) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyGun.setSelectedTextColor(new Color( 0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
				}
				tempg.drawString("$1500.00" , (float) (x + 250 + fontMetrics.getStringBounds("Price: ", g).getWidth()), (float) (actualY) + 175);
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.drawString("Ammo Price: " , (float) (x + 250), (float) (actualY) + 195);
				if (GameFrame.game.money >= 40) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyAmmo.setSelectedTextColor(new Color( 0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyAmmo.setSelectedTextColor(new Color( 200, 0, 0));
				}
				tempg.drawString("$40.00/clip" , (float) (x + 250 + fontMetrics.getStringBounds("Ammo Price: ", g).getWidth()), (float) (actualY) + 195);
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.setPaint(new Color(255, 255, 255, 200));
				if (MP5.isUnlocked()) {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 150 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					tempString = "Ammo = " + (MP5.getCurrentAmmo() + MP5.getRemainingAmmo());
					tempg.drawString(tempString, (float)(x + 200 + 450 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyAmmo.setLocation(x + 200 + 450 - buyAmmo.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyAmmo.drawComponent(g, tempg);
					buyGun.setLocation(x + 200 + 150 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setSelected(true);
					buyGun.setText("Bought");
					buyGun.drawComponent(g, tempg);
				} else {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 300 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyGun.setLocation(x + 200 + 300 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setText("Buy Gun");
					buyGun.drawComponent(g, tempg);
				}
			}else if (menuSetting == 5) {
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.drawString( "Barrett", (float) (x + 250), (float) (shopButtons[0].getBounds2D().getY() + fontMetrics.getStringBounds("Shotgun", g).getHeight()));
				tempg.drawImage(GameFrame.game.HUDSniper, (int)(x + w - 10 - GameFrame.game.HUDSniper.getWidth()), (int) (shopButtons[0].getBounds2D().getY()), null);
				tempg.setFont(font[2]);
				fontMetrics = GameFrame.game.getFontMetrics(font[2]);
				tempg.drawString("Damage: " + Barrett.getGunDamage(), (float) (x + 250), (float) (actualY + 75));
				tempg.drawString("Fire Rate: " + roundTwoDecimals(1000.0/Barrett.getRate()) + " Rounds per Minute", (float) (x + 250), (float) (actualY + 95));
				tempg.drawString("Clip Size: " + Barrett.getClipSize(), (float) (x + 250), (float) (actualY + 115));
				tempg.drawString("Recoil Rating: Very High", (float) (x + 250), (float) (actualY) + 135);
				tempg.drawString("Fire Mode: Semi Automatic" , (float) (x + 250), (float) (actualY) + 155);
				tempg.drawString("Price: " , (float) (x + 250), (float) (actualY) + 175);
				if (GameFrame.game.money >= 3000) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyGun.setSelectedTextColor(new Color( 0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyGun.setSelectedTextColor(new Color( 200, 0, 0));
				}
				tempg.drawString("$3000.00" , (float) (x + 250 + fontMetrics.getStringBounds("Price: ", g).getWidth()), (float) (actualY) + 175);
				tempg.setPaint(new Color(255, 255, 255, 200));
				tempg.drawString("Ammo Price: " , (float) (x + 250), (float) (actualY) + 195);
				if (GameFrame.game.money >= 80) {
					tempg.setPaint(new Color(0, 200, 0, 200));
					buyAmmo.setSelectedTextColor(new Color( 0, 200, 0));
				} else {
					tempg.setPaint( new Color( 200, 0, 0, 200 ) );
					buyAmmo.setSelectedTextColor(new Color( 200, 0, 0));
				}
				tempg.drawString("$80.00/clip" , (float) (x + 250 + fontMetrics.getStringBounds("Ammo Price: ", g).getWidth()), (float) (actualY) + 195);
				tempg.setFont(font[3]);
				fontMetrics = GameFrame.game.getFontMetrics(font[3]);
				tempg.setPaint(new Color(255, 255, 255, 200));
				if (Barrett.isUnlocked()) {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 150 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					tempString = "Ammo = " + (Barrett.getCurrentAmmo() + Barrett.getRemainingAmmo());
					tempg.drawString(tempString, (float)(x + 200 + 450 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyAmmo.setLocation(x + 200 + 450 - buyAmmo.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyAmmo.drawComponent(g, tempg);
					buyGun.setLocation(x + 200 + 150 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setSelected(true);
					buyGun.setText("Bought");
					buyGun.drawComponent(g, tempg);
				} else {
					tempString = "Credits = $" + (int)GameFrame.game.money;
					tempg.drawString(tempString, (float)(x + 200 + 300 - fontMetrics.getStringBounds(tempString, g).getWidth() / 2.0), (float)(shopButtons[4].getBounds2D().getMaxY()));
					buyGun.setLocation(x + 200 + 300 - buyGun.getBounds2D().getWidth()/2.0, shopButtons[4].getBounds2D().getMaxY() - 50);
					buyGun.setText("Buy Gun");
					buyGun.drawComponent(g, tempg);
				}
			}
		}

	}
	private double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
	public void setSize( double w, double h ) {
		this.w = w;
		this.h = h;
		setBars();

	}
	public void setLocation( double x, double y ) {
		if (x > GameFrame.game.getWidth() - w - 1) {
			this.x = GameFrame.game.getWidth() - w - 1;
		} else if (x < 0) {
			this.x = 0;
		} else {
			this.x = x;
		}
		if (y > GameFrame.game.getHeight() - h - 1) {
			this.y = GameFrame.game.getHeight() - h - 1;
		} else if (y < 0) {
			this.y = 0;
		} else {
			this.y = y;
		}
		setBars();
		setButtons();
	}
	private void setButtons() {
		shopButtons[0].setLocation(x + 25, actualY + ((actualHeight / 5.0) * 1) - ((actualHeight / 5.0)/2.0) - (pistolMain.getBounds2D().getHeight()/2.0));
		shopButtons[1].setLocation(x + 25, actualY + ((actualHeight / 5.0) * 2) - ((actualHeight / 5.0)/2.0) - (pistolMain.getBounds2D().getHeight()/2.0));
		shopButtons[2].setLocation(x + 25, actualY + ((actualHeight / 5.0) * 3) - ((actualHeight / 5.0)/2.0) - (pistolMain.getBounds2D().getHeight()/2.0));
		shopButtons[3].setLocation(x + 25, actualY + ((actualHeight / 5.0) * 4) - ((actualHeight / 5.0)/2.0) - (pistolMain.getBounds2D().getHeight()/2.0));
		shopButtons[4].setLocation(x + 25, actualY + ((actualHeight / 5.0) * 5) - ((actualHeight / 5.0)/2.0) - (pistolMain.getBounds2D().getHeight()/2.0));
		buyGun.setSelected(Pistol.isUnlocked());
		buyGun.setSelected(ShotGun.isUnlocked());
		buyGun.setSelected(M16.isUnlocked());
		buyGun.setSelected(MP5.isUnlocked());
		buyGun.setSelected(Barrett.isUnlocked());
	}
	private void setBars() {
		GeneralPath tempTopBar = new GeneralPath();
		tempTopBar.append(new Arc2D.Double(x, y, 20, 20, 90, 90, Arc2D.PIE), false);
		tempTopBar.append(new Rectangle2D.Double(x + 10, y, w - 20, 10), false);
		tempTopBar.append(new Rectangle2D.Double(x, y + 10, w , 10), false);
		tempTopBar.append(new Arc2D.Double(x + w - 20, y, 20, 20, 0, 90, Arc2D.PIE), false);
		topBar = (GeneralPath) tempTopBar.clone();
		GeneralPath tempBottomSegment = new GeneralPath();
		tempBottomSegment.append(new Rectangle2D.Double(x, y + 20, w, h - 30), false);
		tempBottomSegment.append(new Rectangle2D.Double(x + 10, y + h - 10, w - 20, 10), false);
		tempBottomSegment.append(new Arc2D.Double(x, y + h - 20, 20, 20, 180, 90, Arc2D.PIE), false);
		tempBottomSegment.append(new Arc2D.Double(x + w - 20, y + h - 20, 20, 20, 270, 90, Arc2D.PIE), false);
		bottomSegment = (GeneralPath) tempBottomSegment.clone();
		GeneralPath tempTotalFrame = new GeneralPath();
		tempTotalFrame.append(new RoundRectangle2D.Double(x, y, w, h, 20, 20), false);
		totalFrame = (GeneralPath) tempTotalFrame.clone();
		exitButton = new Ellipse2D.Double(x + w - 17, y + 2.5, 15, 15);
		actualHeight = h - 20;
		actualY = y + 20;
	}
	public void setMouseCoordinates(MouseEvent e, String string) {
		double MouseX = e.getX(), MouseY = e.getY();
		mx = MouseX;
		my = MouseY;
		if (topBar.contains(mx, my) || movingFrame) {
			if (exitButton.contains(MouseX, MouseY) && !movingFrame) {
				if (string.equals("Released")) {
					GameFrame.game.stopShop();
				} else {
					exitSelected = true;
				}
			} else {
				exitSelected = false;
				if (string.contains("Pressed") ) {
					movingFrame = true;
					if (temp) {
						temp = false;
						clickedX = MouseX - x;
						clickedY = MouseY - y;
					}
				} else if (string.contains("Released")) {
					temp = true;
					movingFrame = false;
				}
				if (movingFrame) {
					dragShop();
				}
			}
		}
		for (int a = 0; a < shopButtons.length; a++) {
			shopButtons[a].setContains(shopButtons[a].getBounds2D().contains(mx, my));
			if (string.contains("Released")) {
				for (int i = 0; i < shopButtons.length; i++) {
					for (int p = 0; p < shopButtons.length; p++) {
						if (shopButtons[a].getBounds2D().contains(e.getX(), e.getY())) {
							shopButtons[a].setSelected(true);
							menuSetting = (byte) (a + 1);
							setButtons();
						} else {
							shopButtons[a].setSelected(false);
						}
					}
				}
			}
		}
		buyGun.setContains(buyGun.getBounds2D().contains(mx, my));
		if (menuSetting != 1)
			buyAmmo.setContains(buyAmmo.getBounds2D().contains(mx, my));
		if (string.contains("Released")) {
			if (menuSetting == 1) {
				if (buyGun.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 0 && !Pistol.isUnlocked()) {
						buyPistol();
						GameFrame.game.money -= 0;
					}
				}
				if (buyAmmo.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 20) {
						buyPistolAmmo();
						GameFrame.game.money -= 20;
					}
				}
			} else if (menuSetting == 2) {
				if (buyGun.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 500 && !ShotGun.isUnlocked()) {
						buyShotGun();
						GameFrame.game.money -= 500;
					}
				}
				if (buyAmmo.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 20) {
						buyShotGunAmmo();
						GameFrame.game.money -= 20;
					}
				}
			} else if (menuSetting == 3) {
				if (buyGun.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 1500 && !M16.isUnlocked()) {
						buyM16();
						GameFrame.game.money -= 1500;
					}
				}
				if (buyAmmo.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 60) {
						buyM16Ammo();
						GameFrame.game.money -= 60;
					}
				}
			} else if (menuSetting == 4) {
				if (buyGun.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 1500 && !MP5.isUnlocked()) {
						buyMP5();
						GameFrame.game.money -= 1500;
					}
				}
				if (buyAmmo.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 40) {
						buyMP5Ammo();
						GameFrame.game.money -= 40;
					}
				}
			} else if (menuSetting == 5) {
				if (buyGun.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 3000 && !Barrett.isUnlocked()) {
						buyBarret();
						GameFrame.game.money -= 3000;
					}
				}
				if (buyAmmo.getBounds2D().contains(MouseX, MouseY)) {
					if (GameFrame.game.money >= 80) {
						buyBarretAmmo();
						GameFrame.game.money -= 80;
					}
				}
			}

		}
	}
	private void buyPistolAmmo() {
		Pistol.remainingTotal += Pistol.clipSize;
	}
	private void buyPistol() {
		Pistol.unlocked = true;
	}
	private void buyShotGunAmmo() {
		ShotGun.remainingTotal += ShotGun.clipSize;
	}
	private void buyShotGun() {
		ShotGun.unlocked = true;
	}
	private void buyM16() {
		M16.unlocked = true;
	}
	private void buyM16Ammo() {
		M16.remainingTotal += M16.clipSize;
	}
	private void buyMP5Ammo() {
		MP5.remainingTotal += MP5.clipSize;
	}
	private void buyMP5() {
		MP5.unlocked = true;
	}
	private void buyBarretAmmo() {
		Barrett.remainingTotal += Barrett.clipSize;
	}
	private void buyBarret() {
		Barrett.unlocked = true;
	}
	private void dragShop() {
		setLocation(x + (mx - (clickedX + x)), y + (my - (clickedY + y)));
	}
}
