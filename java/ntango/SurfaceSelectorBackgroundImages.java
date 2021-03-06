///*
// * @(#) Model.java
// */
package ntango;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.tools.javac.util.List;

import ntango.Main;
import ntango.ModelButton;
import ntango.Palette;

import touch.TouchFrame;
import touch.Touchable;



public class SurfaceSelectorBackgroundImages extends Touchable {
	
	protected int rawWidth = 80; // this is the width that background images will have
	protected int rawHeight = 80; // this is the height that background images will have
		

	protected Main app;
	protected String name;
	protected String[] modelNames;
	protected static int MARGIN = 25;
	protected ModelButton target = null;
	protected ArrayList<BackgroundImageButton> buttons = new ArrayList<BackgroundImageButton>();
	protected String loadedModel;
	private boolean showBorder = true;
	

	/*
	 * set this to where the models are
	 */

	protected File dir = new File("classes/models/");


	/*
	 * ArrayList of all models as buttons
	 */
	protected ArrayList<BackgroundImageButton> modelButtons = new ArrayList<BackgroundImageButton>();
	private ArrayList<String> backgroundImages;

	public static Color [] WATCH_COLORS = {
		Color.RED,
		Color.YELLOW,
		Color.GREEN,
		Color.BLUE,
		Color.MAGENTA,
		Color.CYAN,
		Color.PINK };

	public static int wcindex = 0;


	public SurfaceSelectorBackgroundImages(Main app)  {
		this.app = app;
		this.name = "Select a Model";
		this.resizable = false;

		setWidth(rawWidth + MARGIN * 2); // was 540
		setHeight(rawHeight + MARGIN * 2); // was 540
		setMaxWidth(3000);
		setMaxHeight(3000);
		setMinWidth(250);
		setMinHeight(250);
		setVisible(true);
		setMovable(false);
		setResizable(false);
		setRotatable(false);

	}
	
	public void setImages(ArrayList<String> bgNames)
	{
		backgroundImages = bgNames;

	}
	/*
	 * this method layouts all of the background image buttons inside the background selector frame
	 */
	public void layout()
	{
		/*
		 * set the height to margin plus each image plus a margin plus a margin at the end
		 */
		this.height = MARGIN + backgroundImages.size() * (rawHeight + MARGIN);
		
		
		int counter = 0;
		
		
		for (String name : backgroundImages)
		{
			int x = MARGIN;
			int y = MARGIN + (rawHeight + MARGIN) * counter;					
			BackgroundImageButton button = new BackgroundImageButton(x, y, rawWidth, rawHeight, name);		
			button.setImage(Palette.createImage(name, rawWidth));
			counter ++;

			modelButtons.add(button);

		}
//		String bgname = backgroundImages.get(0);
//		BackgroundImageButton button = new BackgroundImageButton(MARGIN, MARGIN, rawWidth, rawHeight, bgname);
//		button.setImage(Palette.createImage(bgname, rawWidth));
//
//		modelButtons.add(button);
	
			
	}
	
	


	public void onClick(Button button) {
		System.out.println("click");
		//app.setBackgroundImage(button.getAction());
	}

	public void setVisible(boolean visible)
	{
		for (Button aButton : modelButtons)
		{
			aButton.setVisible(visible);
		}
		this.visible = visible;
	}


	public void showBorder() {
		this.showBorder = true;
	}

	public void hideBorder() {
		this.showBorder = false;
	}

	public void draw(Graphics2D g) {

		if(!visible){return;}
		int w = getWidth();
		int h = getHeight();

		if (showBorder) {
			g.setColor(new Color(0x33ffffff, true));
			g.fillRect(0, 0, w, h);
		}


		w -= MARGIN * 2;
		h -= MARGIN * 2;
		int x = MARGIN;
		int y = MARGIN;

		g.setColor(new Color(0x55ffffff, true));
		g.fillRect(x, y, w, h);


		if (showBorder) {
			g.setColor(Color.WHITE);
			g.setStroke(Palette.STROKE1);
			g.drawRect(x, y, w, h);
			
		}
		
		/*
		 * draw model buttons here
		 */
		

		for(BackgroundImageButton button : modelButtons)
		{

			button.draw(g);


		}

		

	}


	public void onDown() {
		this.target = null;
		for (BackgroundImageButton button : modelButtons) {
			if (button.containsTouch(touchX, touchY) && button.isEnabled()) {
//				loadedModel = button.getAction();
				app.setBackgroundImage(button.getAction());
				break;
			}
		}
	}

	public void onRelease() {
		if (target != null && target.containsTouch(touchX, touchY)) {
			onClick(target);
		}
		target = null;
	}

	public void onDrag() {

	}

	public void onHover() { }



	int mouseX;
	int mouseY;

	public void mousePressed(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
		this.touchX = screenToObjectX(e.getX(), e.getY());
		this.touchY = screenToObjectY(e.getX(), e.getY());
		onDown();
	}

	public void mouseReleased(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
		this.touchX = screenToObjectX(e.getX(), e.getY());
		this.touchY = screenToObjectY(e.getX(), e.getY());
		onRelease();
	}

	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
		this.touchX = screenToObjectX(e.getX(), e.getY());
		this.touchY = screenToObjectY(e.getX(), e.getY());
		onHover();
	}

	public void mouseDragged(MouseEvent e) {
		this.touchX = screenToObjectX(e.getX(), e.getY());
		this.touchY = screenToObjectY(e.getX(), e.getY());
		onDrag();
		translateInWorld(e.getX() - mouseX, e.getY() - mouseY);
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}
	   public void touchDown(TouchFrame frame) {
		   System.out.println("Model Selector touch");
		      onDown();
		   }
}