package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

class PreComponent extends JComponent implements MouseListener, MouseMotionListener {
	public boolean canRun = true;
	public boolean highSettings = true;
	private Vector<SRadioButton> buttonList = new Vector<SRadioButton>();
	private Font font;
	private FontRenderContext frc[] = new FontRenderContext[2];
	private GlyphVector gv[] = new GlyphVector[2];
	private Rectangle2D textBounds[] = new Rectangle2D[2];
	private SRadioButton highSettingsButton, lowSettingsButton;
	private SButton okayButton, exitButton;
	private double textX[] = new double[2], textY[] = new double[2];
	private String canRunString[] = {"It was determined that your system can run the game on high settings. ", "You may change the quality here, or in the settings menu in game."};
	private String cannotRunString[] = {"It was determined that your system cannot run the game on high settings. ", "You may change the quality here, or in the settings menu in game."};
	public PreComponent(int CPUs) {
		addMouseListener(this);
		addMouseMotionListener(this);
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("font/Erbar LT Bold Condensed.ttf"));
			font = font.deriveFont(20f);
		} catch (FontFormatException e) {} catch (IOException e) {}
		if (CPUs >= 4) {
			canRun = true;
			for (int a = 0; a < 2; a++) {
				frc[a] = new FontRenderContext(null, false, false);
				gv[a] = font.createGlyphVector(frc[a], canRunString[a]);
				textBounds[a] = gv[a].getVisualBounds();
				textBounds[a].setRect(textX[a], textY[a], textBounds[a].getWidth(), textBounds[a].getHeight());
				textX[a] = 250 - (textBounds[a].getWidth() / 2.0);
				textY[a] = (textBounds[a].getHeight() * (a + 1) + (a * 5)) + 10;
			}
			highSettingsButton = new SRadioButton(10, 75, 480, 90, "High Settings (R)", 30f, true);
			lowSettingsButton = new SRadioButton(10, 165, 480, 90, "Low Settings", 30f, false);
			add(highSettingsButton);
			add(lowSettingsButton);
		} else {
			canRun = false;
			for (int a = 0; a < 2; a++) {
				frc[a] = new FontRenderContext(null, false, false);
				gv[a] = font.createGlyphVector(frc[a], cannotRunString[a]);
				textBounds[a] = gv[a].getVisualBounds();
				textBounds[a].setRect(textX[a], textY[a], textBounds[a].getWidth(), textBounds[a].getHeight());
				textX[a] = 250 - (textBounds[a].getWidth() / 2.0);
				textY[a] = (textBounds[a].getHeight() * (a + 1) + (a * 5)) + 10;
			}
			highSettingsButton = new SRadioButton(10, 75, 480, 90, "High Settings", 30f, false);
			lowSettingsButton = new SRadioButton(10, 165, 480, 90, "Low Settings (R)", 30f, true);
			add(highSettingsButton);
			add(lowSettingsButton);
		}
		okayButton = new SButton(10, 255, 240, 35, "Play", 20f);
		exitButton = new SButton(250, 255, 240, 35, "Exit", 20f);
		highSettingsButton.setTextAlpha(200);
		lowSettingsButton.setTextAlpha(200);
		okayButton.setTextAlpha(200);
		exitButton.setTextAlpha(200);
		repainter();
	}
	public void paint( Graphics g ) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setClip(0, 0, 500, 300);
		g2.setPaint(new Color(58, 58, 58));
		g2.fillRect(0, 0, 500, 300);
		g2.setPaint(new RadialGradientPaint(new Rectangle(-50, -50, this.getWidth() + 100, this.getHeight() + 100), new float[] {0.2f, 1f}, new Color[] {new Color(0, 0, 0, 0), Color.black}, CycleMethod.NO_CYCLE));
		g2.fillRect(0, 0, 500, 300);
		g2.setFont(font);
		g2.setPaint(new Color(255, 255, 255, 200));
		if (canRun) {
			for (int a = 0; a < 2; a++) {
				g2.drawString(canRunString[a], (int)textX[a], (int)textY[a]);
			}
		} else {
			for (int a = 0; a < 2; a++) {
				g2.drawString(cannotRunString[a], (int)textX[a], (int)textY[a]);
			}
		}
		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).drawComponent(g, g2);
		}
		okayButton.drawComponent(g, g2);
		exitButton.drawComponent(g, g2);
	}
	public void add ( SRadioButton b ) {
		buttonList.add(b);
	}
	public void remove ( SRadioButton b ) {
		buttonList.remove(b);
	}
	public void play() {
		if (highSettings) {
			SettingsConfig.presets = "High";
			SettingsConfig.vignetting = true;
			SettingsConfig.CPUs = GameFrame.CPUs;
			SettingsConfig.flares = true;
			SettingsConfig.AA = true;
			SettingsConfig.fancyMenu = true;
			SettingsConfig.sound = true;
			SettingsConfig.fullScreen = true;
		} else {
			SettingsConfig.presets = "Low";
			SettingsConfig.vignetting = false;
			SettingsConfig.CPUs = GameFrame.CPUs;
			SettingsConfig.flares = false;
			SettingsConfig.AA = true;
			SettingsConfig.fancyMenu = false;
			SettingsConfig.sound = true;
			SettingsConfig.fullScreen = true;
		}
		SettingsConfig.writeConfig();
		GameFrame.startFrame(true);
	}
	public void repainter() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while (!main.GameFrame.isGaming) {	
					repaint();
					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {}
				}
			}
		});
		thread.start();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		if (okayButton.getBounds2D().contains(e.getX(), e.getY())) {
			okayButton.setContains(true);

		} else {
			okayButton.setContains(false);
		}
		if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
			exitButton.setContains(true);

		} else {
			exitButton.setContains(false);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		if (okayButton.getBounds2D().contains(e.getX(), e.getY())) {
			okayButton.setContains(true);

		} else {
			okayButton.setContains(false);
		}
		if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
			exitButton.setContains(true);

		} else {
			exitButton.setContains(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		if (okayButton.getBounds2D().contains(e.getX(), e.getY())) {
			okayButton.setContains(true);

		} else {
			okayButton.setContains(false);
		}
		if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
			exitButton.setContains(true);

		} else {
			exitButton.setContains(false);
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				for (int a = 0; a < buttonList.size(); a++) {
					if (buttonList.get(a).getSelected()) {
						buttonList.get(a).setSelected(false);
					}
				}
				buttonList.get(i).setSelected(true);
				if (buttonList.get(i).getClicked().startsWith("High")) {
					highSettings = true;
				} else if (buttonList.get(i).getClicked().startsWith("Low")) {
					highSettings = false;
				}
			}
		}
		if (okayButton.getBounds2D().contains(e.getX(), e.getY())) {
			okayButton.setContains(true);
			play();
		} else {
			okayButton.setContains(false);
		}
		if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
			exitButton.setContains(true);
			System.exit(0);
		} else {
			exitButton.setContains(false);
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		if (okayButton.getBounds2D().contains(e.getX(), e.getY())) {
			okayButton.setContains(true);

		} else {
			okayButton.setContains(false);
		}
		if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
			exitButton.setContains(true);

		} else {
			exitButton.setContains(false);
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (buttonList.get(i).getBounds2D().contains(e.getX(), e.getY())) {
				buttonList.get(i).setContains(true);

			} else {
				buttonList.get(i).setContains(false);
			}
		}
		if (okayButton.getBounds2D().contains(e.getX(), e.getY())) {
			okayButton.setContains(true);

		} else {
			okayButton.setContains(false);
		}
		if (exitButton.getBounds2D().contains(e.getX(), e.getY())) {
			exitButton.setContains(true);

		} else {
			exitButton.setContains(false);
		}
	}

}

public class PreWindow extends JFrame {
	public PreWindow(int CPUs) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		getContentPane().setPreferredSize(new Dimension(500, 300));
		setResizable( true );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setUndecorated(true);
		add( new PreComponent(CPUs) );
		pack();
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		setLocation(x, y);
		setVisible( true );
	}
}