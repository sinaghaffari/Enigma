package guns;


import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import main.GameFrame;
import main.Game;
import main.SettingsConfig;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public class Pistol {
	private static double velocity=200;
	public static int clipSize = 15;
	public static int currentClip = clipSize;
	public static int remainingTotal = 1000;
	private static double damage=2;
	private static int rate = 125;
	private static boolean rapid = false;
	private static int bSize = 1;
	private static int shots = 1;
	private static int delayShots = 0;
	private static double pX;
	private static double pY;
	private static double pAngle;
	private static double recoil = 2;
	private static float maxRecoil = 5;
	private static double recoilReduction = 0.1;
	private static boolean reloading = false;
	private static String HUDShellType = "Small";
	private static Color bulletColor = new Color(255, 255, 0, 60);
	private static boolean InterruptReload = false;
	public static boolean unlocked = true;
	public static void shoot(double pX2, double pY2, double angle ) {
		pX = pX2;
		pY = pY2;
		pAngle = angle;
		burstFire();
	}

	public static void burstFire() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (int a = 1; a <= shots; a++) {
					remainingTotal = 1000;
					if ( currentClip > 0) {
						currentClip--;
						Sound("Shoot");
						GameFrame.game.gunManager.bulletShoot(pX, pY, pAngle, velocity, damage, bSize, bulletColor, true, "Pistol" );
						if (GameFrame.game.gunManager.gunRecoil < maxRecoil) {
							GameFrame.game.gunManager.gunRecoil += recoil;
						}
						double temp = (GameFrame.game.gunManager.gunRecoil + recoil);
						GameFrame.game.setShootShake((Math.random() * temp) - temp / 2.0, (Math.random() * temp) - temp / 2.0);
						try {
							Thread.sleep(delayShots);
						} catch (InterruptedException e) {
						}
					} else {
						if (remainingTotal != 0) {
							reload();
						} else {
							Sound("Click");
						}
					}
				}
			}

		});
		t.start();
	}
	public static void reload() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				if (currentClip < clipSize) {
					reloading = true;
					Sound("Reload");
					try {
						Thread.sleep(1600);
					} catch (InterruptedException e) {}
					if (!InterruptReload) {
						if (remainingTotal - clipSize < 0) {
							currentClip += remainingTotal;
							remainingTotal = 0;
						} else {
							remainingTotal -= clipSize - currentClip;
							currentClip = clipSize;
						}
					}
					reloading = false;
					InterruptReload = false;
				}
			}
		});
		t.start();
	}
	public static void Sound( String s) {
		if (SettingsConfig.sound) {
			if (s.equals("Shoot")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Pistol 2.mp3");
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
			} else if (s.equals("Reload")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Pistol Reload.mp3");
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
			} else if (s.equals("Click")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Click.mp3");
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
	public static int getRate() {
		return rate;
	}
	public static void interruptReload() {
		InterruptReload = true;
		reloading = false;
	}
	public static int getBSize() {
		return bSize;
	}
	public static boolean getRapid() {
		return rapid;
	}
	public static double getRecoil() {
		return recoil;
	}
	public static double getRecoilReduction() {
		return recoilReduction;
	}
	public static double getMaxRecoil() {
		return maxRecoil;
	}
	public static double getGunDamage() {
		return damage;		
	}
	public static boolean getReloading() {
		return reloading;
	}
	public static int getRemainingAmmo() {
		return remainingTotal;
	}
	public static int getCurrentAmmo() {
		return currentClip;
	}
	public static int getClipSize() {
		return clipSize;
	}
	public static String getBulletType() {
		return HUDShellType;
	}
	public static boolean isUnlocked() {
		return unlocked;
	}
}

