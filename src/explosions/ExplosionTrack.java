package explosions;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import main.GameFrame;
import main.Game;

public class ExplosionTrack {
	private int startX, startY;
	private int explosionRadius = 150;
	private BufferedImage currentImg;
	private int currentX, currentY, currentPicID, currentAlpha;
	private boolean active = true;
	public ExplosionTrack(int x, int y) {
		startX = x;
		startY = y;
		explosion();
		explosionAnimation();
		boom();
	}
	public double angleBetween( double x1, double y1, double x2, double y2 ) {  
		double t = Math.toDegrees(Math.atan2( (y2 - y1) , (x2 - x1)));
		if (t < 0)
			return t + 360;
		else
			return t;
	}
	public void explosion() {
		double distance;
		for ( int i = 1; i <= GameFrame.game.spawnEnemies.enemyList.size(); i++ ) {
			distance = Math.sqrt(Math.pow((startX - GameFrame.game.spawnEnemies.enemyList.get( i - 1 ).getEnemyX()), 2) + (Math.pow((startY - GameFrame.game.spawnEnemies.enemyList.get( i - 1 ).getEnemyY()), 2)));
			if (distance < explosionRadius) {

				if (distance == 0) {
					GameFrame.game.spawnEnemies.enemyList.get( i - 1 ).setEnemyHealth(GameFrame.game.spawnEnemies.enemyList.get( i - 1 ).getEnemyHealth() - (30));
				} else {
					GameFrame.game.spawnEnemies.enemyList.get( i - 1 ).setEnemyHealth(GameFrame.game.spawnEnemies.enemyList.get( i - 1 ).getEnemyHealth() - (1.0/(distance/explosionRadius + 1) * 30));
				}
			}
		}
		distance = Math.sqrt(Math.pow((startX - GameFrame.game.getPlayerX()), 2) + (Math.pow((startY - GameFrame.game.getPlayerY()), 2)));
		if (distance < explosionRadius) {
			if (distance == 0) {
				if (GameFrame.game.pHealth - 60 <= 0) {
					GameFrame.game.pHealth = 0;
					int tempC = (int)Math.round(Math.random() * 8);
					if (tempC == 0) {
						GameFrame.game.killPlayer("You're family wont even be able to recognize your body... Because you know... they're zombies.");
					} else if (tempC == 1) {
						GameFrame.game.killPlayer("Boom!");
					}  else if (tempC == 2) {
						GameFrame.game.killPlayer("You used to be alive.");
					} else if (tempC == 3) {
						GameFrame.game.killPlayer("There is a difference between you and I... I'm in one piece.");
					} else if (tempC == 4) {
						GameFrame.game.killPlayer("How would you rather die? Being eaten alive or blowing up? Not like you have a choice.");
					}  else if (tempC == 5) {
						GameFrame.game.killPlayer("Yes! I got to watch you die an interesting death!");
					} else if (tempC == 6) {
						GameFrame.game.killPlayer("You blew up... kind of suckish.");
					} else if (tempC == 7) {
						GameFrame.game.killPlayer("Mmmm I bet you taste great dead.");
					} else if (tempC == 8) {
						GameFrame.game.killPlayer("I don't know about you... but I'm having a blast... Hahaha. Get it? Blast?");
					}
				}else {
					GameFrame.game.pHealth -= 60;
				}
			} else {
				if (GameFrame.game.pHealth - (1.0/(distance/explosionRadius + 1)) * 60 <= 0) {
					GameFrame.game.pHealth = 0; 
					int tempC = (int)Math.round(Math.random() * 8);
					if (tempC == 0) {
						GameFrame.game.killPlayer("You're family wont even be able to recognize your body... Because you know... they're zombies.");
					} else if (tempC == 1) {
						GameFrame.game.killPlayer("Boom!");
					}  else if (tempC == 2) {
						GameFrame.game.killPlayer("You used to be alive.");
					} else if (tempC == 3) {
						GameFrame.game.killPlayer("There is a difference between you and I... I'm in one piece.");
					} else if (tempC == 4) {
						GameFrame.game.killPlayer("How would you rather die? Being eaten alive or blowing up? Not like you have a choice.");
					}  else if (tempC == 5) {
						GameFrame.game.killPlayer("Yes! I got to watch you die an interesting death!");
					} else if (tempC == 6) {
						GameFrame.game.killPlayer("You blew up... kind of suckish.");
					} else if (tempC == 7) {
						GameFrame.game.killPlayer("Mmmm I bet you taste great dead.");
					} else if (tempC == 8) {
						GameFrame.game.killPlayer("I don't know about you... but I'm having a blast... Hahaha. Get it? Blast?");
					}
				}else {
					GameFrame.game.pHealth -= (1.0/(distance/explosionRadius + 1)) * 60;
				}
			}
			whiteOut();
		}
	}
	public void explosionAnimation() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (int a = 0; a <= 22; a++) {
					//img[a] = img[a].getScaledInstance(img[a].getWidth(null) / 2, img[a].getHeight(null) / 2, Image.SCALE_DEFAULT);
					currentImg = GameFrame.game.expl.img[a];
					currentX = startX - (currentImg.getWidth() / 2);
					currentY = startY - (currentImg.getHeight() / 2);
					currentPicID = a;
					try {
						Thread.sleep(33);
					} catch (InterruptedException e) {}
					while(main.GameFrame.game.pause) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {}
					}
				}
				active = false;
			}

		});
		t.start();
	}
	public void whiteOut() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				for (int a = 255; a >= 0; a--) {
					currentAlpha = a;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}

		});
		t.start();
	}
	public BufferedImage getCurrentImg() {
		return currentImg;
	}
	public int getCurrentX() {
		return currentX;
	}
	public int getCurrentY() {
		return currentY;
	}
	public int getCurrentPicID() {
		return currentPicID;
	}
	public int getCurrentAlpha() {
		return currentAlpha;
	}
	public boolean getActive() {
		return active;
	}
	public void boom() {
		Thread thread = new Thread(new Runnable() {
			InputStream is;
			Player clip;
			public void run() {
				try {
					is = new FileInputStream("audio/GUNS/Explosion.mp3");
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
