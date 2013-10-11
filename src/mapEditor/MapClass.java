package mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;


public class MapClass extends JComponent implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {
	int blockLength, blockHeight;
	double blockScale = 10;
	int MouseX, MouseY;
	int MouseMapX, MouseMapY;
	private static int[][] map;
	boolean repaintGrid = true;
	int length, height;
	private Set<Integer> pressed = new HashSet<Integer>();
	private Set<Point2D> mapWalls = new HashSet<Point2D>();
	String tool = "Line";
	boolean inProgress = false;
	Line2D LineTool = null;
	Rectangle2D RectangleTool = null;
	int clickedX = 0, clickedY = 0;
	MapClass(int length, int height) {
		//this.setCursor(this.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),new Point(0, 0), "null"));
		blockLength = length;
		blockHeight = height;
		setSize( (int)Math.ceil(blockLength * blockScale), (int)Math.ceil(blockHeight * blockScale ));
		setLayout(null);
		setPreferredSize( new Dimension((int)Math.ceil(blockLength * blockScale), (int)Math.ceil(blockHeight * blockScale)));
		revalidate();
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addMouseWheelListener(this);
		initializeArray();
		calculator();
		repainter();
	}

	private void initializeArray() {
		map = new int[blockLength][blockHeight];
		for (int a = 0; a < blockLength; a++) {
			for (int b = 0; b < blockHeight; b++) {
				if (a == 0 || a == blockLength - 1 || b == 0 || b == blockHeight - 1) {
					map[a][b] = 1;
					mapWalls.add(new Point2D.Double(a, b));
				} else {
					map[a][b] = 0;
				}
			}
		}
	}
	public void repainter() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {}
					repaint();
				}
			}
		});
		thread.start();
	}
	public void calculator() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				boolean exit = false;
				while (true) {
					exit = false;
					for (int a = 0; a < blockLength; a++) {
						for (int b = 0; b < blockHeight; b++) {
							if ((MouseX >= a * blockScale) && (MouseX < a * blockScale + blockScale) && (MouseY > b * blockScale) && (MouseY <= b * blockScale + blockScale)) {
								MouseMapX = a;
								MouseMapY = b;
								exit = true;
							}
							if (exit) break;
						}
						if (exit) break;
					}
					// (pressed.size());
				}
			}
		});
		thread.start();
	}
	public void paint ( Graphics g ) {
		this.grabFocus();
		Graphics2D g2 = ( Graphics2D )g;
		g2.setClip(super.getVisibleRect());
		length = (int)Math.ceil(blockLength * blockScale);
		height = (int)Math.ceil(blockHeight * blockScale);
		setSize( length, height );
		setPreferredSize( new Dimension(length, height));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(new Color(128, 128, 128));
		g2.fill(new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight()));
		List<Point2D> tempWallList = new ArrayList<Point2D>(mapWalls);
		g2.setPaint(new Color(0, 0, 0));
		for ( int a = 0; a < tempWallList.size(); a++) {
			g2.fill(new Rectangle2D.Double( tempWallList.get(a).getX() * blockScale, tempWallList.get(a).getY() * blockScale, blockScale, blockScale));
		}
		g2.setStroke(new BasicStroke(0.5f));
		g2.setPaint(Color.black);
		for (int c = 0; c < blockLength; c++) {
			g2.draw(new Line2D.Double(c * blockScale, 0, c * blockScale, this.getHeight()));
		}
		for (int d = 0; d < blockHeight; d++) {
			g2.draw(new Line2D.Double( 0, d * blockScale, this.getWidth(), d * blockScale));
		}
		g2.setPaint(Color.red);
		g2.setStroke(new BasicStroke(1f));
		g2.draw(new Rectangle2D.Double( MouseMapX * blockScale, MouseMapY * blockScale, blockScale, blockScale));
		if (LineTool != null) {
			g2.draw(LineTool);
		}
		if (RectangleTool != null) {
			g2.draw(RectangleTool);
		}

	}
	public void mouseClicked( MouseEvent e ) {
	}
	public void mouseEntered( MouseEvent e ) {
	}
	public void mouseExited( MouseEvent e ) {
	}
	public void mousePressed( MouseEvent e ) {
		mouseStuff(e);
	}
	public void mouseStuff(MouseEvent e) {
		MouseX = e.getX();
		MouseY = e.getY();

		if (tool.equals("Pencil")) {
			if(e.getModifiers()==InputEvent.BUTTON1_MASK) {
				map[MouseMapX][MouseMapY] = 1;
				mapWalls.add(new Point2D.Double(MouseMapX, MouseMapY));
			}

			if(e.getModifiers()==InputEvent.BUTTON3_MASK) {
				map[MouseMapX][MouseMapY] = 0;
				mapWalls.remove(new Point2D.Double(MouseMapX, MouseMapY));
			}
		} else if (tool.equals("Line")) {
			if (!inProgress) {
				clickedX = MouseMapX;
				clickedY = MouseMapY;
			}
			inProgress = true;
			LineTool = new Line2D.Double((clickedX * blockScale) + (blockScale / 2.0), (clickedY * blockScale) + (blockScale / 2.0), (MouseMapX * blockScale) + (blockScale / 2.0), (MouseMapY * blockScale) + (blockScale / 2.0));
			System.out.println(Math.sqrt(Math.pow(Math.abs(clickedX - MouseMapX), 2) + Math.pow(Math.abs(clickedY - MouseMapY), 2)));
		} else if (tool.equals("Rectangle")) {
			if (!inProgress) {
				clickedX = MouseMapX;
				clickedY = MouseMapY;
			}
			inProgress = true;
			double length = (MouseMapX - clickedX) * blockScale;
			double height = (MouseMapY - clickedY) * blockScale;
			if ((length < 0) && (height < 0)) {
				length = (clickedX - MouseMapX) * blockScale;
				height = (clickedY - MouseMapY) * blockScale;
				RectangleTool = new Rectangle2D.Double(
						(MouseMapX * blockScale) + (blockScale / 2.0),
						(MouseMapY * blockScale) + (blockScale / 2.0),
						(length),
						(height));
			} else if (length < 0) {
				length = (clickedX - MouseMapX) * blockScale;
				RectangleTool = new Rectangle2D.Double(
						(MouseMapX * blockScale) + (blockScale / 2.0),
						(clickedY * blockScale) + (blockScale / 2.0),
						(length),
						(height));
			} else if (height < 0) {
				height = (clickedY - MouseMapY) * blockScale;
				RectangleTool = new Rectangle2D.Double(
						(clickedX * blockScale) + (blockScale / 2.0),
						(MouseMapY * blockScale) + (blockScale / 2.0),
						(length),
						(height));
			} else {
				RectangleTool = new Rectangle2D.Double(
						(clickedX * blockScale) + (blockScale / 2.0),
						(clickedY * blockScale) + (blockScale / 2.0),
						(length),
						(height));
			}
		}
	}

	public void mouseReleased( MouseEvent e ) {
		if (tool.equals("Line")) {
			if(e.getModifiers()==InputEvent.BUTTON1_MASK) {
				for (int a = 0; a < blockLength; a++) {
					for (int b = 0; b < blockHeight; b++) {
						if (new Rectangle2D.Double(a * blockScale, b * blockScale, blockScale, blockScale).intersectsLine(LineTool)) {
							map[a][b] = 1;
							mapWalls.add(new Point2D.Double(a, b));
						}
					}
				}
			} else if(e.getModifiers()==InputEvent.BUTTON3_MASK) {
				for (int a = 0; a < blockLength; a++) {
					for (int b = 0; b < blockHeight; b++) {
						if (new Rectangle2D.Double(a * blockScale, b * blockScale, blockScale, blockScale).intersectsLine(LineTool)) {
							map[a][b] = 0;
							mapWalls.remove(new Point2D.Double(a, b));
						}
					}
				}
			}
			inProgress = false;
			LineTool = null;
		} else if (tool.equals("Rectangle")) {
			if(e.getModifiers()==InputEvent.BUTTON1_MASK) {
				for (int a = 0; a < blockLength; a++) {
					for (int b = 0; b < blockHeight; b++) {
						if (new Rectangle2D.Double(a * blockScale, b * blockScale, blockScale, blockScale).intersects(RectangleTool)) {
							map[a][b] = 1;
							mapWalls.add(new Point2D.Double(a, b));
						}
					}
				}
			} else if(e.getModifiers()==InputEvent.BUTTON3_MASK) {
				for (int a = 0; a < blockLength; a++) {
					for (int b = 0; b < blockHeight; b++) {
						if (new Rectangle2D.Double(a * blockScale, b * blockScale, blockScale, blockScale).intersects(RectangleTool)) {
							map[a][b] = 0;
							mapWalls.remove(new Point2D.Double(a, b));
						}
					}
				}
			}
			inProgress = false;
			RectangleTool = null;
		}
	}
	public void mouseDragged( MouseEvent e ) {
		mouseStuff(e);
	}
	public void mouseMoved( MouseEvent e ) {
		MouseX = e.getX();
		MouseY = e.getY();
	}
	public void mouseWheelMoved( MouseWheelEvent e ) {
		if ( e.getWheelRotation() < 0 && blockScale < 28) {
			blockScale+= 0.2;
		} else if ( e.getWheelRotation() > 0 && blockScale > 5) {
			blockScale-= 0.2;
		}
	}
	public void clearArray() {
		for (int a = 0; a < blockLength; a++) {
			for (int b = 0; b < blockHeight; b++) {
				map[a][b] = 0;
			}
		}
		mapWalls.clear();
	}
	public void setArray( int[][] i) {
		map = i;
		for (int a = 0; a < blockLength; a++) {
			for (int b = 0; b < blockHeight; b++) {
				if (map[a][b] == 1) {
					mapWalls.add(new Point2D.Double(a, b));
				}
			}
		}
	}
	public static int getArray (int a, int b ) {
		return map[a][b];
	}

	@Override
	public void keyPressed( KeyEvent k ) {
	}

	@Override
	public void keyReleased( KeyEvent k ) {
		if (k.getKeyCode() == KeyEvent.VK_1) {
			tool = "Pencil";
		} else if (k.getKeyCode() == KeyEvent.VK_2) {
			tool = "Line";
		} else if (k.getKeyCode() == KeyEvent.VK_3) {
			tool = "Rectangle";
		}
	}

	@Override
	public void keyTyped( KeyEvent arg0 ) {
		// TODO Auto-generated method stub

	}

}
