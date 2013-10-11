package enemies;


import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import main.GameFrame;
import main.Game;
import map.Map;

public class EnemySpawner {
	public Vector<EnemyTrack> enemyList = new Vector<EnemyTrack>();
	public short waveNumber = 1;
	public short numToSpawn=20;
	public short numToSpawnBef=20;
	public short numberOnScreen = (short) (numToSpawn/2);
	public boolean done = false;
	String[][] waveZombs=new String[11][300];
	boolean goneOnce=false;
	int enemyAt=0;


	private Random rand = new Random();
	public EnemySpawner() {
		done = true;
	}
	public void initializeSpawner() {
		EnemyMaker();
	}
	public void terminateEnemies() {
		for ( int i = 0; i < enemyList.size(); i++ ) {
			enemyList.get(i).setKilled(true);
		}
		enemyList.clear();
		enemyMakerThread.stop();
	}
	private boolean distanceBetweenEnemies( int x, int y ) {
		for ( int i = 0; i < enemyList.size(); i++ ) {
			if ( Math.sqrt((Math.pow((x - enemyList.get( i ).getEnemyX()), 2)) + (Math.pow((y - enemyList.get( i ).getEnemyY()), 2))) <= 100) {
				return false;
			}
		}
		return true;
	}
	private double distanceToPlayer(int x, int y){
		return Math.sqrt(Math.pow(GameFrame.game.getPlayerX()-x,2)+Math.pow(GameFrame.game.getPlayerY()-y,2));		
	}
	Thread enemyMakerThread = new Thread(new Runnable() {

		public void run() {
			int die;
			int currentb = 0;
			boolean isgoodx = true;
			while (main.GameFrame.isGaming) {	
				if (waveNumber==1) {
					for(int i=0;i<20;++i)
						waveZombs[1][i]="walker";
				}
				if(waveNumber==2){
					for(int i=0;i<numToSpawn;++i)
						waveZombs[2][i]="walker";
					for(int i=0;i<numToSpawn/7;++i)
						waveZombs[3][(int)(Math.random()*numToSpawn)]="striker";
				}
				if(waveNumber==3){
					for(int i=0;i<numToSpawn;++i)
						waveZombs[3][i]="walker";
					for(int i=0;i<numToSpawn/4;++i)
						waveZombs[3][(int)(Math.random()*numToSpawn)]="swarmer";
					for(int i=0;i<numToSpawn/6;++i)
						waveZombs[3][(int)(Math.random()*numToSpawn)]="striker";
				}

				if(waveNumber==4){
					for(int i=0;i<numToSpawn;++i)
						waveZombs[4][i]="walker";
					for(int i=0;i<numToSpawn/3.5;++i)
						waveZombs[4][(int)(Math.random()*numToSpawn)]="swarmer";
					for(int i=0;i<numToSpawn/5.5;++i)
						waveZombs[waveNumber][(int)(Math.random()*numToSpawn)]="striker";
				}

				if(waveNumber==5){
					for(int i=0;i<numToSpawn;++i)
						waveZombs[5][i]="walker";
					for(int i=0;i<numToSpawn/4;++i)
						waveZombs[5][(int)(Math.random()*numToSpawn)]="swarmer";
					for(int i=0;i<numToSpawn/6;++i)
						waveZombs[5][(int)(Math.random()*numToSpawn)]="brute";
					for(int i=0;i<numToSpawn/5.5;++i)
						waveZombs[waveNumber][(int)(Math.random()*numToSpawn)]="striker";
				}


				if(waveNumber==6){
					for(int i=0;i<numToSpawn;++i)
						waveZombs[6][i]="walker";
					for(int i=0;i<numToSpawn/3.5;++i)
						waveZombs[6][(int)(Math.random()*numToSpawn)]="swarmer";
					for(int i=0;i<numToSpawn/5;++i)
						waveZombs[6][(int)(Math.random()*numToSpawn)]="brute";
					for(int i=0;i<numToSpawn/5;++i)
						waveZombs[waveNumber][(int)(Math.random()*numToSpawn)]="striker";
				}

				if(waveNumber>=7){
					for(int i=0;i<numToSpawn;++i)
						waveZombs[waveNumber][i]="walker";
					for(int i=0;i<numToSpawn/3.5;++i)
						waveZombs[waveNumber][(int)(Math.random()*numToSpawn)]="swarmer";
					for(int i=0;i<numToSpawn/4;++i)
						waveZombs[waveNumber][(int)(Math.random()*numToSpawn)]="brute";
					for(int i=0;i<numToSpawn/4.5;++i)
						waveZombs[waveNumber][(int)(Math.random()*numToSpawn)]="striker";
				}



				while(numToSpawn!=0 && main.GameFrame.isGaming){
					isgoodx = true;
					die = rand.nextInt( 1 ) + 1;
					if (enemyList.size() < numberOnScreen) {
						while (isgoodx) {
							currentb = (int) (Math.random() * GameFrame.game.getMap().nblocks);
							if ( GameFrame.game.getMap().block[currentb]!=1 && GameFrame.game.getMap().block[currentb]!=9 && distanceToPlayer(GameFrame.game.getMap().idToX(currentb),GameFrame.game.getMap().idToY(currentb)) > 1000 ) {
								createEnemy( GameFrame.game.getMap().idToX(currentb),GameFrame.game.getMap().idToY(currentb) );
								isgoodx = false;
								numToSpawn--;
							}
						}
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}

					while(main.GameFrame.game != null && main.GameFrame.game.pause) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {}
					}
				}

				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {	}

				while(enemyList.size()!=0){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {	}
				}

				waveNumber++;


				if(waveNumber<19) {
					numToSpawnBef+=waveNumber;
				}
				numToSpawn=numToSpawnBef;
				numberOnScreen = (short) (numToSpawn/2);
				enemyAt=0;	
				GameFrame.game.startShop();


			}
		}
	});
	public void EnemyMaker() {
		enemyMakerThread.start();
	}
	public void createEnemy(int x, int y) {

		int waveAt=waveNumber;
		if(waveNumber>9)
			waveAt=9;


		enemyList.add( new EnemyTrack( x, y, waveZombs[waveAt][enemyAt],waveNumber ) );
		enemyAt++;
	}
}