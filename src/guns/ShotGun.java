package guns;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

import main.GameFrame;
import main.SettingsConfig;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class ShotGun {
	private static double velocity=200;
	public static int clipSize = 7;
	public static int currentClip = clipSize;
	public static int remainingTotal = clipSize * 2;
	private static int damage = 4;
	private static boolean rapid = false;
	private static int rate = 600;
	private static double bSize = 0.5;
	private static int shots = 6;
	private static int delayShots = 0;
	private static double pX;
	private static double pY;
	private static int pSpread;
	private static double pVelocitySpread;
	private static double pAngle;
	private static double recoil = 7;
	private static Random r = new Random();
	private static float maxRecoil = 10;
	private static double recoilReduction = 0.1;
	private static boolean reloading = false;
	private static boolean interrupt = false;
	private static boolean interruptPossible = false;
	private static String HUDShellType = "Shell";
	private static Color bulletColor = new Color(255, 255, 0, 60);
	public static boolean unlocked = false;
	
	public static void shoot(double pX2, double pY2, double angle ) {
		pX = pX2;
		pY = pY2;
		pAngle = angle;
		burstFire();
	}
	public static void burstFire() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				if ( currentClip > 0) {
					currentClip--;
					Sound("Shoot");
					for (int a = 1; a <= shots; a++) {
						pSpread = r.nextInt( 6 ) - 2;
						pVelocitySpread = Math.random() - 0.5;
						if (a == 1)
							GameFrame.game.gunManager.bulletShoot(pX, pY, pAngle + (pSpread * 1.5), velocity + pVelocitySpread, damage, bSize, bulletColor, true, "Shotgun" );
						else
							GameFrame.game.gunManager.bulletShoot(pX, pY, pAngle + (pSpread * 1.5), velocity + pVelocitySpread, damage, bSize, bulletColor, false, "Shotgun" );
						try {
							Thread.sleep(delayShots);
						} catch (InterruptedException e) {
						}
					}
					if (GameFrame.game.gunManager.gunRecoil < maxRecoil) {
						GameFrame.game.gunManager.gunRecoil += recoil;
					}
					double temp = (GameFrame.game.gunManager.gunRecoil + recoil);
					GameFrame.game.setShootShake((Math.random() * temp) - temp / 2.0, (Math.random() * temp) - temp / 2.0);
					try {
						Thread.sleep(550);
					} catch (InterruptedException e) {}
					Sound("Cock");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {}
				} else {
					if (remainingTotal != 0) {
						reload();
					} else {
						Sound("Click");
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
					Sound("ReloadStart");
					if (remainingTotal == 0) {
						interruptReload();
					} else {
						currentClip++;
						remainingTotal--;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
					while((!interrupt) && (currentClip < clipSize)) {
						Sound("ReloadFiller");
						if (remainingTotal == 0) {
							interruptReload();
						} else {
							currentClip++;
							remainingTotal--;
						}
						try {
							Thread.sleep(750);
						} catch (InterruptedException e) {}
						interruptPossible = true;
					}
					Sound("ReloadFinish");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					reloading = false;
					interrupt = false;
					interruptPossible = false;
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
							is = new FileInputStream("audio/GUNS/Shotgun Shot 1.mp3");
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
			} else if (s.equals("Cock")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Shotgun Cock.mp3");
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
			} else if (s.equals("ReloadStart")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Shotgun Start Reload.mp3");
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
			} else if (s.equals("ReloadFiller")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Shotgun Subsequent Reload.mp3");
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
			} else if (s.equals("ReloadFinish")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Shotgun Cock.mp3");
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
	public static void interruptReload() {
		interrupt = true;;
	}
	public static int getRate() {
		return rate;
	}
	public static double getBSize() {
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
	public static int getGunDamage() {
		return damage;		
	}
	public static boolean getReloading() {
		return reloading;
	}
	public static boolean getInterruptPossible() {
		return interruptPossible;
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
