package guns;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import tracking.BulletTrack;
import tracking.MuzzleFlashTrack;
import explosions.ExplosionTrack;


public class Guns {
	public Guns() {
	}
	//public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	public Random rand = new Random();

	private String gunType = "Pistol";
	public double gunRecoil;
	double dynamicRecoil;
	double ambientRecoil = 1;
	public double mouseRecoil;
	public List<BulletTrack> bulletList = Collections.synchronizedList(new ArrayList<BulletTrack>());
	public List<ExplosionTrack> explosionList = Collections.synchronizedList(new ArrayList<ExplosionTrack>());
	public List<MuzzleFlashTrack> muzzleFlash = Collections.synchronizedList(new ArrayList<MuzzleFlashTrack>());
	public void addMF( double a, String gun ) {
		muzzleFlash.add(new MuzzleFlashTrack( a, gun ));
	}
	public void nextGun() {
		if ( gunType.equals("Pistol") ) {
			if (Pistol.getReloading()) {
				Pistol.interruptReload();
			}
			gunType = "ShotGun";
			if (!ShotGun.isUnlocked()) {
				nextGun();
			}
		} else if (gunType.equals("ShotGun")) {
			if (ShotGun.getReloading()) {
				ShotGun.interruptReload();
			}
			gunType = "M16";
			if (!M16.isUnlocked()) {
				nextGun();
			}
		}else if (gunType.equals("M16")) {
			if (M16.getReloading()) {
				M16.interruptReload();
			}
			gunType = "MP5";
			if (!MP5.isUnlocked()) {
				nextGun();
			}
		}else if (gunType.equals("MP5")) {
			if (MP5.getReloading()) {
				MP5.interruptReload();
			}
			gunType =  "Barrett";
			if (!Barrett.isUnlocked()) {
				nextGun();
			}
		}else if (gunType.equals( "Barrett")) {
			if (Barrett.getReloading()) {
				Barrett.interruptReload();
			}
			gunType = "Pistol";
			if (!Pistol.isUnlocked()) {
				nextGun();
			}
		}
	}

	public void prevGun() {
		if ( gunType.equals("Pistol") ) {
			if (Pistol.getReloading()) {
				Pistol.interruptReload();
			}
			gunType =  "Barrett";
			if (!Barrett.isUnlocked()) {
				prevGun();
			}
		} else if (gunType.equals("ShotGun")) {
			if (ShotGun.getReloading()) {
				ShotGun.interruptReload();
			}
			gunType = "Pistol";
			if (!Pistol.isUnlocked()) {
				prevGun();
			}
		}else if (gunType.equals("M16")) {
			if (M16.getReloading()) {
				M16.interruptReload();
			}
			gunType = "ShotGun";
			if (!ShotGun.isUnlocked()) {
				prevGun();
			}
		}else if (gunType.equals("MP5")) {
			if (MP5.getReloading()) {
				MP5.interruptReload();
			}
			gunType = "M16";
			if (!M16.isUnlocked()) {
				prevGun();
			}
		}else if (gunType.equals( "Barrett")) {
			if (Barrett.getReloading()) {
				Barrett.interruptReload();
			}
			gunType = "MP5";
			if (!MP5.isUnlocked()) {
				prevGun();
			}
		}
	}
	public String getGunType() {
		return gunType;
	}
	public double getGunBulletSize() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getBSize();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getBSize();
		}else if (gunType.equals("M16")) {
			return M16.getBSize();
		}else if (gunType.equals("MP5")) {
			return MP5.getBSize();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getBSize();
		} else if (gunType.equals( "Barrett")) {
			return Barrett.getBSize();
		}
		return 0;
	}
	public double getDamage() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getGunDamage();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getGunDamage();
		}else if (gunType.equals("M16")) {
			return M16.getGunDamage();
		}else if (gunType.equals("MP5")) {
			return MP5.getGunDamage();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getGunDamage();
		}
		return 0;
	}
	public double getGunMaxRecoil() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getMaxRecoil();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getMaxRecoil();
		}else if (gunType.equals("M16")) {
			return M16.getMaxRecoil();
		}else if (gunType.equals("MP5")) {
			return MP5.getMaxRecoil();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getMaxRecoil();
		}
		return 0;
	}
	public int getGunRate() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getRate();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getRate();
		}else if (gunType.equals("M16")) {
			return M16.getRate();
		}else if (gunType.equals("MP5")) {
			return MP5.getRate();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getRate();
		}
		return 0;
	}
	public int getRemainingAmmo() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getRemainingAmmo();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getRemainingAmmo();
		}else if (gunType.equals("M16")) {
			return M16.getRemainingAmmo();
		}else if (gunType.equals("MP5")) {
			return MP5.getRemainingAmmo();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getRemainingAmmo();
		}
		return 0;
	}
	public int getCurrentAmmo() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getCurrentAmmo();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getCurrentAmmo();
		}else if (gunType.equals("M16")) {
			return M16.getCurrentAmmo();
		}else if (gunType.equals("MP5")) {
			return MP5.getCurrentAmmo();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getCurrentAmmo();
		}
		return 0;
	}
	public int getClipSize() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getClipSize();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getClipSize();
		}else if (gunType.equals("M16")) {
			return M16.getClipSize();
		}else if (gunType.equals("MP5")) {
			return MP5.getClipSize();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getClipSize();
		}
		return 0;
	}
	public double getGunRecoilReduction() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getRecoilReduction();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getRecoilReduction();
		}else if (gunType.equals("M16")) {
			return M16.getRecoilReduction();
		}else if (gunType.equals("MP5")) {
			return MP5.getRecoilReduction();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getRecoilReduction();
		}
		return 0;
	}
	public String getShellType() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getBulletType();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getBulletType();
		}else if (gunType.equals("M16")) {
			return M16.getBulletType();
		}else if (gunType.equals("MP5")) {
			return MP5.getBulletType();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getBulletType();
		}
		return "";
	}
	public boolean getGunRapid() {
		if ( gunType.equals("Pistol") ) {
			return Pistol.getRapid();
		} else if (gunType.equals("ShotGun")) {
			return ShotGun.getRapid();
		}else if (gunType.equals("M16")) {
			return M16.getRapid();
		}else if (gunType.equals("MP5")) {
			return MP5.getRapid();
		}else if (gunType.equals( "Barrett")) {
			return Barrett.getRapid();
		}
		return false;
	}

	public double getGunRecoil() {
		return gunRecoil;
	}
	public double getDynamicRecoil() {
		return dynamicRecoil;
	}
	public double getAmbientRecoil() {
		return ambientRecoil;
	}
	public double getMouseRecoil() {
		return mouseRecoil;
	}
	public void setMouseRecoil( double i ) {
		mouseRecoil = i;
	}
	public void setGunRecoil( double a ) {
		gunRecoil = a;
	}
	public double getTotalRecoil() {
		return gunRecoil + dynamicRecoil + ambientRecoil + mouseRecoil;
	}
	public void setDynamicRecoil( double a ) {
		dynamicRecoil = a;
	}
	public void reload() {
		if ( (gunType.equals("Pistol") ) && (!Pistol.getReloading())) {
			Pistol.reload();
		} else if ((gunType.equals("ShotGun")) && (!ShotGun.getReloading())) {
			ShotGun.reload();
		}else if ((gunType.equals("M16")) && (!M16.getReloading())) {
			M16.reload();
		}else if ((gunType.equals("MP5")) && (!MP5.getReloading())) {
			MP5.reload();
		}else if ((gunType.equals( "Barrett")) && (!Barrett.getReloading())) {
			Barrett.reload();
		}
	}
	public void GunShoot( double pX, double pY, double angle ) {
		if ( (gunType.equals("Pistol")) && (!Pistol.getReloading())) {
			Pistol.shoot( pX, pY, angle );
		} else if (gunType.equals("ShotGun")) {
			if (!ShotGun.getReloading()) {
				ShotGun.shoot( pX, pY, angle );
			} else if (ShotGun.getInterruptPossible()) {
				ShotGun.interruptReload();
			}
		}else if ((gunType.equals("M16")) && (!M16.getReloading())) {
			M16.shoot( pX, pY, angle );
		}else if ((gunType.equals("MP5")) && (!MP5.getReloading())) {
			MP5.shoot( pX, pY, angle );
		}else if ((gunType.equals( "Barrett")) && (!Barrett.getReloading())) {
			Barrett.shoot( pX, pY, angle );
		}
	}
	public Color getCrosshairColour() {
		if ( gunType.equals("Pistol") ) {
			if ((Pistol.getReloading())|| (Pistol.getRemainingAmmo() + Pistol.getCurrentAmmo() == 0)) {
				return new Color( 255, 0, 0);
			}
		} else if (gunType.equals("ShotGun")) {
			if ((ShotGun.getReloading()) || (ShotGun.getRemainingAmmo() + ShotGun.getCurrentAmmo() == 0)) {
				return new Color( 255, 0, 0);
			} 
		}else if (gunType.equals("M16")) {
			if ((M16.getReloading()) || (M16.getRemainingAmmo() + M16.getCurrentAmmo() == 0)) {
				return new Color( 255, 0, 0);
			}
		}else if (gunType.equals("MP5")) {
			if ((MP5.getReloading()) || (MP5.getRemainingAmmo() + MP5.getCurrentAmmo() == 0)) {
				return new Color( 255, 0, 0);
			}
		}else if (gunType.equals( "Barrett")) {
			if ((Barrett.getReloading()) || (Barrett.getRemainingAmmo() + Barrett.getCurrentAmmo() == 0)) {
				return new Color( 255, 0, 0);
			}
		}
		return new Color(255,255,255);
	}
	public void bulletShoot( double pX, double pY , double aangle, double avelocity, double damage, double asize, Color bc, boolean flash, String gun) {
		bulletList.add(new BulletTrack(pX, pY, aangle + (Math.random() * (((gunRecoil + dynamicRecoil + mouseRecoil) * 2) + 1) - ((gunRecoil + dynamicRecoil + mouseRecoil) + 1)), avelocity, damage, asize, bc, flash, gun ));
	}
	public void explosionStart( int enemyX, int enemyY) {
		explosionList.add(new ExplosionTrack(enemyX, enemyY));
	}
}
