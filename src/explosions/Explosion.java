package explosions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Explosion {
	public BufferedImage img[] =  new BufferedImage[23];
	public Explosion() {
		for (int a = 0; a <= 22; a++) {
			try {
				img[a] = ImageIO.read(new File("img/EXPLOSION/Base" + a + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
