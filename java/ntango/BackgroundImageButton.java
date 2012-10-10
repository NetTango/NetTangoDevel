package ntango;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class BackgroundImageButton extends Button {

	public BackgroundImageButton(int x, int y, int w, int h, String action) {
		super(x, y, w, h, action);
		// TODO Auto-generated constructor stub
		
		
	}
	
	

	public void draw(Graphics2D g)
	{


		Rectangle rect = shape.getBounds();
		int bx = (int)rect.getX();
		int by = (int)rect.getY();
		int bw = (int)rect.getWidth();
		int bh = (int)rect.getHeight();


		int iw = image.getWidth();
		int ih = image.getHeight();
		

		g.drawImage(image, bx + bw/2 - iw/2, by + bh/2 - ih/2, null);

		

	}	
}