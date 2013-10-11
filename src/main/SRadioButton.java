package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public class SRadioButton {
	private Font font;
	private double x = 0;
	private double y = 0;
	private double w = 0;
	private double fadeWidth = 0;
	private double expandedWidth;
	private boolean expanding = false;
	private boolean contracting = false;
	private double h = 0;
	private boolean contains = false;
	private boolean selected = false;
	private boolean entered = false;
	private String text = "";
	private double textX, textY;
	private FontRenderContext frc;
	private GlyphVector gv;
	private Rectangle2D bounds;
	private Rectangle2D textBounds;
	private int type = 0;
	private File f;
	private Color textColor = new Color(255, 255, 255);
	private Color selectedTextColor = new Color(210, 160, 52);
	private int textAlpha = 255;
	public SRadioButton( double x, double y, double w, double h, String t, float fontSize, File f, boolean b ) {
		font = new Font("Trebuchet MS", 0, (int)fontSize );
		this.x = x;
		this.y = y;
		this.w = w;
		expandedWidth = w;
		this.h = h;
		this.text = t;
		this.f = f;
		this.selected = b;
		setThings();
	}
	public SRadioButton( double x, double y, double w, double h, String t, float fontSize, boolean b ) {
		font = new Font("Trebuchet MS", 0, (int)fontSize );
		this.x = x;
		this.y = y;
		this.w = w;
		expandedWidth = w;
		this.h = h;
		this.text = t;
		this.selected = b;
		setThings();
	}
	public SRadioButton( double x, double y, double w, double h, String t, float fontSize, boolean b, Color textColor, Color selectedTextColor ) {
		font = new Font("Trebuchet MS", 0, (int)fontSize );
		this.x = x;
		this.y = y;
		this.w = w;
		expandedWidth = w;
		this.h = h;
		this.text = t;
		this.selected = b;
		this.textColor = textColor;
		this.selectedTextColor = selectedTextColor;
		setThings();
	}
	public void setText( String t ) {
		text = t;
		setThings();
	}
	public void setLocation( double x, double y ) {
		this.x = x;
		this.y = y;
		setThings();
	}
	public void setSize(double w, double h) {
		this.w = w;
		this.h = h;
		setThings();
	}
	public void setTextAlpha(int t) {
		textAlpha = t;
		setThings();
	}
	private void setThings() {
		bounds = new Rectangle2D.Double(x, y, expandedWidth, h);
		frc = new FontRenderContext(null, false, false);
		gv = font.createGlyphVector(frc, text);
		textBounds = gv.getVisualBounds();
		textX = getCenterX() - (textBounds.getWidth() / 2.0);
		textY = getCenterY() + (textBounds.getHeight() / 2.0);
		textBounds.setRect(textX, textY, textBounds.getWidth(), textBounds.getHeight());
	}
	public double getCenterX() {
		return x + (w/2.0);
	}
	public double getCenterY() {
		return y + (h/2.0);
	}
	public Rectangle2D getBounds2D() {
		return bounds;
	}
	public void setContains( boolean c ) {
		contains = c;
		if (contains || selected){
			if (entered) {
				entered = false;
				chk();
			}
			if (!expanding) {
				expandHorizontal();
			}
		} else {
			entered = true;
			if (!contracting)
				contractHorizontal();
		}
	}
	private void expandHorizontal() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				double modifier = 8;
				expanding = true;
				while(fadeWidth + modifier < w / 2.0) {
					fadeWidth += modifier;
					if (!contains) {
						break;
					}
					try {
						Thread.sleep(7);
					} catch (InterruptedException e) {}
				}
				if (contains || selected) {
					fadeWidth = w / 2.0;
				}
				expanding = false;
			}
		});
		thread.start();
	}
	private void contractHorizontal() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				double modifier = 8;
				contracting = true;
				while(fadeWidth - modifier > 0) {
					fadeWidth -= modifier;
					if (contains) {
						break;
					}
					try {
						Thread.sleep(7);
					} catch (InterruptedException e) {}
				}
				if (!contains) {
					fadeWidth = 0;
				}
				contracting = false;
			}
		});
		thread.start();
	}
	public void drawComponent( Graphics g, Graphics2D g2) {
		if (fadeWidth > 0) {
			//g2.setPaint(new RadialGradientPaint((float) getCenterX(), (float) getCenterY(), (float) (fadeWidth), new float[] {0, 1f}, new Color[] {new Color(255, 255, 255, 30), new Color(255, 255, 255, 0) }, CycleMethod.NO_CYCLE));
			//g2.setPaint(new RadialGradientPaint(new Rectangle2D.Double(bounds.getCenterX() - fadeWidth, bounds.getY() - 5, (bounds.getCenterX() + fadeWidth) - (bounds.getCenterX() - fadeWidth), bounds.getHeight() + 10), new float[] {0, 1f}, new Color[] {new Color(255, 255, 255, 100), new Color(255, 255, 255, 0) }, CycleMethod.NO_CYCLE));
			g2.setPaint(new RadialGradientPaint((float)bounds.getCenterX(), (float)bounds.getCenterY(), (float) (fadeWidth), new float[] {0, 1f}, new Color[] {new Color(255, 255, 255, 75), new Color(255, 255, 255, 0) }));
			g2.fill(new Rectangle2D.Double(x, y, w, h));
		}
		g2.setPaint(new RadialGradientPaint((float)bounds.getCenterX(), (float)bounds.getCenterY(), (float) (w / 2.0), new float[] {0, 1f}, new Color[] {new Color(255, 255, 255, 75), new Color(255, 255, 255, 0) }));
		g2.draw(new Line2D.Double(x, y, x + w, y));
		g2.draw(new Line2D.Double(x, y + h, x + w, y + h));
		if (contains || selected) {
			g2.setPaint(selectedTextColor);
		} else {
			g2.setPaint(textColor);
		}
		g2.setFont(font);
		if (contains || selected) {
			double tempX = textX + 2 * (Math.random() - 0.5);
			double tempY = textY + 1 * (Math.random() - 0.5);
			g2.drawString(text, (int)tempX, (int)tempY);
		} else {
			g2.drawString(text, (int)textX, (int)textY);
		}
	}
	public void chk() {
		Thread thread = new Thread(new Runnable() {
			InputStream is;
			Player clip;
			Random rand = new Random();
			public void run() {
				if (SettingsConfig.sound) {
					try {
						is = new FileInputStream("audio/click.mp3");
					} catch (FileNotFoundException e1) {}
					try {
						clip = new Player(is);
					} catch (JavaLayerException e){ }
					try {
						clip.play();
					} catch (JavaLayerException e) {}
				}
			}
		});
		thread.start();
	}
	public String getClicked() {
		return text;
	}
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean b) {
		if (selected){
			expandHorizontal();
		} else {
			contractHorizontal();
		}
		if (b && !selected)
			chk();
		selected = b;
	}
	public File getFile() {
		return f;
	}
	public SRadioButton getThis() {
		return this;
	}
	public void setSelectedTextColor( Color c) {
		selectedTextColor = c;
	}
}
