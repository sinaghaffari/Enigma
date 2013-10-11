package tracking;

import guns.Guns;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import tools.StackBlurFilter;

import main.GameFrame;
import main.Game;

public class MuzzleFlashTrack {
	double a;
	int index = (int)Math.round(Math.random() * 4);
	boolean active = true;
	String gun;
	public MuzzleFlashTrack(double a, String gun) {
		this.a = a;
		this.gun = gun;
		runFlash();
	}
	public void runFlash() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {}
				active = false;
			}
		});
		t.start();
	}
	public BufferedImage getImg() {
		if (gun.equals("Shotgun") || gun.equals("Sniper") ) {
			GameFrame.game.imgShotgun[index] = new BufferedImage(GameFrame.game.getWidth(), GameFrame.game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)GameFrame.game.imgShotgun[index].getGraphics();
			AffineTransform at = new AffineTransform();
			at.translate((GameFrame.game.getWidth()/2.0 + 4 * Math.cos(Math.toRadians(GameFrame.game.getShootAngle() - 90))) + 35 * Math.cos(Math.toRadians(GameFrame.game.getShootAngle())), (GameFrame.game.getHeight()/2.0 + 4 * Math.sin(Math.toRadians(GameFrame.game.getShootAngle() - 90))) + 35 * Math.sin(Math.toRadians(GameFrame.game.getShootAngle())));
			at.rotate(Math.toRadians(-(- a)));
			at.translate(-500, -300);
			g.drawImage(GameFrame.game.imgShotgun2[index], at, null);
			return GameFrame.game.imgShotgun[index];
		} else if (gun.equals("Pistol") || gun.equals("MP5") || gun.equals("M16")) {
			GameFrame.game.imgSmall[index] = new BufferedImage(GameFrame.game.getWidth(), GameFrame.game.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)GameFrame.game.imgSmall[index].getGraphics();
			AffineTransform at = new AffineTransform();
			at.translate((GameFrame.game.getWidth()/2.0 + 4 * Math.cos(Math.toRadians(a - 90))) + 30 * Math.cos(Math.toRadians(a)), (GameFrame.game.getHeight()/2.0 + 4 * Math.sin(Math.toRadians(a - 90))) + 30 * Math.sin(Math.toRadians(a)));
			at.rotate(Math.toRadians(-(- a)));
			at.translate(-500, -300);
			g.drawImage(GameFrame.game.imgSmall2[index], at, null);
			return GameFrame.game.imgSmall[index];
		}
		return new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
	}
	public boolean getActive() {
		return active;
	}
}
