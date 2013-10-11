package map;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import main.GameFrame;

public class Map {
	private String MapName = "";
	private String MapLength = "";
	private String MapHeight = "";
	private String MapData = "";
	public boolean done = false;
	private int WallSize;
	public Rectangle2D[] map = new Rectangle2D[WallSize];
	private int[][] map2D;
	public int xblocks;
	public int yblocks;
	public int nblocks;
	public short[] block;
	
	public Map( File gameMap) throws FileNotFoundException {
		Scanner in = null;
		String cur = null;
		int counter = 0;
		try {
			in = new Scanner(new BufferedReader(new FileReader(gameMap)));
			in.useDelimiter(";\n*");
			while(in.hasNext()) {
				cur = in.next();
				// (in.next());
				if (cur.contains("MapName:")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						MapName += cur.charAt(i);
					}
					// (MapName);
				} else if (cur.contains("MapLength:")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						MapLength += cur.charAt(i);
					}
					// (MapLength);
				} else if (cur.contains("MapHeight:")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						MapHeight += cur.charAt(i);
					}
					// (MapHeight);
				} else if (cur.contains("MapData:")) {
					for (int i = cur.indexOf("\"") + 1; i < cur.lastIndexOf("\""); i++) {
						MapData += cur.charAt(i);
					}
					// (MapData);
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		Scanner sc = null;
		try {
			sc = new Scanner(MapData);
			sc.useDelimiter("\n");
			map2D = new int[Integer.parseInt(MapLength)][Integer.parseInt(MapHeight)];
			int counter2 = 0;
			while(sc.hasNext()) {
				cur = sc.next();
				for (int i = 0; i < cur.length() - 1; i++) {
					// (i + " " + counter + " " + cur.charAt(i));
					map2D[i][counter] = Integer.parseInt(String.valueOf(cur.charAt(i)));
					if (map2D[i][counter] == 1) {
						counter2++;
					}
				}
				counter++;
			}
			WallSize = counter2;
			counter2 = 0;
			int c = 0;
			map = new Rectangle2D[WallSize];
			for (int b = 0; b < Integer.valueOf(MapHeight); b++) {
				for (int a = 0; a < Integer.valueOf(MapLength); a++) {
					if (map2D[a][b] == 1) {
						map[counter2] = new Rectangle2D.Double(a * 20, c * 20, 20, 20);
						counter2++;
					}
				}
				c++;
			}
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
		/*for (int b = 0; b < Integer.valueOf(MapHeight); b++) {
			for (int a = 0; a < Integer.valueOf(MapLength); a++) {
				System.out.print(map2D[a][b]);
			}
			 ();
		}*/
		xblocks = Integer.valueOf(MapLength);
		yblocks = Integer.valueOf(MapHeight);
		block=new short[xblocks*yblocks];
		nblocks=xblocks*yblocks;
		for(int i=0;i<yblocks;++i){
			for(int j=0;j<xblocks;++j){
				for(int k=0;k<WallSize;++k){

					if(map[k].getX()/20==j && map[k].getY()/20==i){
						block[i*xblocks+j]=(short) map2D[j][i];
						break;	
					} else if(k==WallSize-1)
						block[i*xblocks+j]=(short) map2D[j][i];
				}
			}
		}
		

		for(int i=0;i<yblocks;++i){ //draw map in terms of walls and blanks
			for(int l=0;l<xblocks;++l){
				System.out.print(block[i*xblocks+l]);
			}
			System.out.println();
		}
		done = true;
	}

	public int idToX(int id){
		return (id%xblocks) * 20 + 10;
	}
	public int idToY(int id){
		return (id/xblocks) * 20 + 10;
	}
	public int toId(double x,double y){
		return (((int)y/20)*xblocks+((int)x/20));
	}
	public int getMapX( int i ) {
		return (int)map[i].getX(); 
	}
	public int getMapY( int i ) {
		return (int)map[i].getY(); 
	}
	public int getWallSize() {
		return WallSize;
	}
	public Rectangle2D getWallRectangle( int i ) {
		return map[i];
	}
	public int[][] getMap2D() {
		return map2D;
	}
}