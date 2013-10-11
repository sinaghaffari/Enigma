package tracking;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

import main.GameFrame;
import main.Game;
import main.SettingsConfig;
import map.Map;
import enemies.EnemySpawner;
import guns.Guns;


public class BulletTrack {  //this is the bullet manager! It controls all of the bullets functions for its life
	private double wallCollisionX, wallCollisionY, wallCollisionT;
	private double speedModifier = 1;
	private String gunType;
	private int closestWall;
	private Line2D closestLine;
	private double bangle;
	private double bdamage;
	private double ricochetChanceMod = 0;
	private double bsize;
	private double startX;
	private double startY;
	private double t = 0;
	private boolean active = true;
	private int lastEnemy;
	private double bulletAcceleration;
	private Line2D line;
	private int walltoIgnore;
	private Color bColor;
	private ArrayList<Integer> possibleWallPos = new ArrayList<Integer>();
	private ArrayList<Line2D> possibleLinePos = new ArrayList<Line2D>();
	ArrayList<Line2D> willCollide = new ArrayList<Line2D>();
	private double bulletCollisionX, bulletCollisionY;
	private Random rand = new Random();
	private double length;
	private boolean ignoreYet = false;
	private double ricochetAngle;
	private boolean noMoreRicochet = false;
	public BulletTrack( double pX, double pY, double a, double v, double damage, double s, Color c, boolean flash, String gun) {
		gunType = gun;
		startX = pX;
		startY = pY;
		bangle=a;
		if (flash && SettingsConfig.flares)
			GameFrame.game.gunManager.addMF(bangle, gun);
		bdamage=damage;
		bsize=s;
		bColor = c;
		initialCollision();
		BulletMove();
	}
	public double getCenterX( Line2D line ) {
		return (line.getX1() + line.getX2()) / 2.0;
	}
	public double getCenterY( Line2D line ) {
		return (line.getY1() + line.getY2()) / 2.0;
	}
	public double angleBetween( double x1, double y1, double x2, double y2 ) {  
		double t = Math.toDegrees(Math.atan2( (y2 - y1) , (x2 - x1)));
		if (t < 0)
			return t + 360;
		else
			return t;
	}
	private void initialCollision() {
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
				if (i != walltoIgnore || !ignoreYet)
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

				if (distance(startX, startY, getCenterX(willCollide.get(i)), getCenterY(willCollide.get(i))) > distance(startX, startY, getCenterX(willCollide.get(furthest)), getCenterY(willCollide.get(furthest)))) {
					furthest = i;
				}
			}
			willCollide.remove(furthest);
		}
		if (willCollide.size() > 1){
			if (distance(startX, startY, getCenterX(willCollide.get(0)), getCenterY(willCollide.get(0))) < distance(startX, startY, getCenterX(willCollide.get(1)), getCenterY(willCollide.get(1)))) {
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
		double collisionX[][] = new double[GameFrame.game.spawnEnemies.enemyList.size()][2];
		double collisionY[][] = new double[GameFrame.game.spawnEnemies.enemyList.size()][2];
		boolean collisionB[] = new boolean[GameFrame.game.spawnEnemies.enemyList.size()];
		for (int i = 0; i <GameFrame.game.spawnEnemies.enemyList.size(); i++) {
			for (int a = 0; a < 2; a++) {
				collisionX[i][a] = 0;
				collisionY[i][a] = 0;
			}
			collisionB[i] = false;
		}
		for (int i = 0; i < GameFrame.game.spawnEnemies.enemyList.size(); i++) {
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
		for (int i = 0; i < GameFrame.game.spawnEnemies.enemyList.size(); i++) {
			if (collisionB[i]) {
				if(gunType.contains( "Barrett")) {
					GameFrame.game.spawnEnemies.enemyList.get(i).setEnemyHealth(GameFrame.game.spawnEnemies.enemyList.get(closestEnemyI).getEnemyHealth() - bdamage);
				}
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
		if (distance(closestEnemyCollisionX, closestEnemyCollisionY, startX, startY) <= length && tempCheck  && !gunType.contains( "Barrett")) {
			length = distance(closestEnemyCollisionX, closestEnemyCollisionY, startX, startY);
			line = new Line2D.Double(startX, startY, closestEnemyCollisionX, closestEnemyCollisionY);
			GameFrame.game.spawnEnemies.enemyList.get(closestEnemyI).setEnemyHealth(GameFrame.game.spawnEnemies.enemyList.get(closestEnemyI).getEnemyHealth() - bdamage);
			noMoreRicochet = true;
		} else {
			//calculate ricochet
			double diffAngle = (Math.abs(bangle - referanceAngle));
			double chance = (diffAngle * 100)/90;
			double tA = 0;
			if (referanceAngle == 0) {
				tA = 180 - diffAngle;
			} else if (referanceAngle == 360) {
				tA = 180 + diffAngle;
			} else if (referanceAngle == 270) {
				if (angleBetween(wallCollisionX, wallCollisionY, startX, startY) > 90) {
					tA = 90 - diffAngle;
				} else {
					tA = 90 + diffAngle;
				}
			} else if (referanceAngle == 180) {
				if (angleBetween(wallCollisionX, wallCollisionY, startX, startY) > 180) {
					tA = diffAngle;
				} else {
					tA = 360 - diffAngle;
				}
			} else if (referanceAngle == 90) {
				if (angleBetween(wallCollisionX, wallCollisionY, startX, startY) > 270) {
					tA = 270 - diffAngle;
				} else {
					tA = 270 + diffAngle;
				}
			}
			if (Math.random() * (100 * (ricochetChanceMod * 5) + 200) < chance) {
				if (distance(startX, startY, wallCollisionX, wallCollisionY) < 500) {
					GameFrame.game.Ricochet(rand.nextInt(7));
				}
				ricochetAngle = tA + 2 * (Math.random() - 0.5);

				walltoIgnore = closestWall;
				ignoreYet = true;
				ricochetChanceMod += 1;
			} else {
				noMoreRicochet = true;
			}
		}
	}
	public byte sgn( double x ) {
		if (x < 0) {
			return -1;
		} else {
			return 1;
		}
	}
	public void BulletMove() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (active) {
					t += 0.1 / speedModifier;
					if (t >= 1 / speedModifier) {
						if (!noMoreRicochet)
							ricochet();
						else
							active = false;
					}
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					while(main.GameFrame.game.pause) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {}
					}
				}
			}
		});
		thread.start();
	}
	public void ricochet() {
		t = 0;
		startX = wallCollisionX;
		startY = wallCollisionY;
		wallCollisionX = 0;
		wallCollisionY = 0;
		wallCollisionT = 0;
		bangle = ricochetAngle;
		initialCollision();
	}
	public double getStartX() {
		return startX;
	}
	public double getTime() {
		return t;
	}
	public double getStartY() {
		return startY;
	}
	public double getAngle() {
		return bangle;
	}
	public boolean getActive() {
		return active;
	}
	public double getBSize() {
		return bsize;
	}
	public Line2D getLine() {
		return line;
	}
	public Color getBulletColor() {
		return bColor;
	}
	public int getCollisionX() {
		return (int) Math.round(wallCollisionX);
	}
	public int getCollisionY() {
		return (int) Math.round(wallCollisionY);
	}
	public double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
} 
