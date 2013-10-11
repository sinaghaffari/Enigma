package guns;



import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import main.GameFrame;
import main.Game;
import main.SettingsConfig;


public class M16 {

	private static double velocity=200;
	public static int clipSize = 30;
	private static int currentClip = clipSize;
	public static int remainingTotal = clipSize * 2;
	private static int damage=8;
	private static int rate = 400;
	private static boolean rapid = false;
	private static int bSize = 2;
	private static int shots = 3;
	private static int delayShots = 50;
	private static double recoil = 3;
	private static float maxRecoil = 9;
	private static double recoilReduction = 0.13;
	private static boolean reloading = false;
	private static boolean postReload = false;
	private static String HUDShellType = "Small";
	private static Color bulletColor = new Color(255, 255, 0, 60);
	private static boolean InterruptReload = false;
	public static boolean unlocked = false;
	
	public static void shoot(double pX, double pY, double angle ) {
		burstFire();
	}
	public static void burstFire() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				postReload = false;
				for (int a = 1; a <= shots; a++) {
					if ( ( currentClip > 0 ) && (!postReload)) {
						currentClip--;
						Sound("Shoot " + a);
						GameFrame.game.gunManager.bulletShoot(GameFrame.game.shootX, GameFrame.game.shootY, GameFrame.game.getShootAngle(), velocity, damage, bSize, bulletColor, true, "M16" );
						if (GameFrame.game.gunManager.gunRecoil < maxRecoil) {
							GameFrame.game.gunManager.gunRecoil += 1;
						}
						double temp;
						if (a == shots) {
							temp = (GameFrame.game.gunManager.gunRecoil + recoil);
						} else {
							temp = (GameFrame.game.gunManager.gunRecoil / 2.0 + recoil);
						}
						GameFrame.game.setShootShake((Math.random() * temp) - temp / 2.0, (Math.random() * temp) - temp / 2.0);
						try {
							Thread.sleep(delayShots);
						} catch (InterruptedException e) {}
					} else {
						if (!postReload){
							if (remainingTotal != 0) {
								reload();
							} else {
								Sound("Click");
								postReload = true;
							}
						}
					}
				}
				/*if ((!postReload) && (remainingTotal != 0)) {
					if (GameFrame.game.gunManager.gunRecoil < maxRecoil) {
						GameFrame.game.gunManager.gunRecoil += recoil + 2;
					}
				}*/
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
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
					if (!InterruptReload) {
						if (remainingTotal - (clipSize - currentClip) < 0) {
							currentClip += remainingTotal;
							remainingTotal = 0;
						} else {
							remainingTotal -= clipSize - currentClip;
							currentClip = clipSize;
						}
					}
					reloading = false;
					postReload = true;
					InterruptReload = false;
				}
			}
		});
		t.start();
	}
	public static void Sound( String s) {
		if (SettingsConfig.sound) {
			if (s.equals("Shoot 1")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Pistol 3.mp3");
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
			} else if (s.equals("Shoot 2")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/Pistol 1.mp3");
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
			} else if (s.equals("Shoot 3")) {
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
			}  else if (s.equals("Reload")) {
				Thread thread = new Thread(new Runnable() {
					InputStream is;
					Player clip;
					public void run() {
						try {
							is = new FileInputStream("audio/GUNS/M16 Reload.mp3");
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

	public static int getGunDamage() {
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
