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

import touch.TouchFrame;
import touch.Touchable;



public class ModelSelector extends Touchable {

	protected Main app;
	protected String name;
	protected String[] modelNames;
	protected static int MARGIN = 25;
	protected ModelButton target = null;
	protected HashMap<String, String> models;
	protected String loadedModel;
	private boolean showBorder = true;

	/*
	 * set this to where the models are
	 */

	protected File dir = new File("classes/models/");


	/*
	 * ArrayList of all models as buttons
	 */
	protected ArrayList<ModelButton> modelButtons = new ArrayList<ModelButton>();

	public static Color [] WATCH_COLORS = {
		Color.RED,
		Color.YELLOW,
		Color.GREEN,
		Color.BLUE,
		Color.MAGENTA,
		Color.CYAN,
		Color.PINK };

	public static int wcindex = 0;


	public ModelSelector(Main app)  {
		this.app = app;
		this.name = "Select a Model";
		this.resizable = false;

		setWidth(540 + MARGIN * 2);
		setHeight(540 + MARGIN * 2);
		setMaxWidth(3000);
		setMaxHeight(3000);
		setMinWidth(250);
		setMinHeight(250);
		setVisible(true);
		setMovable(true);
		setResizable(true);
		setRotatable(false);

		models = new HashMap<String, String>();
		// looks through a directory and creates a list of all .nlogo files
		modelNames = dir.list(
				new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						return name.endsWith(".nlogo");
					}
				});	  

		/*
		 * find total number of models and set up layout params
		 * iterate down to find first square number that is 
		 * smaller than the total number of models. Limit 225 models,
		 * though they will look REALLY small by then.
		 * If we decide to have more than 64 models, I'll code in a pagination thingy
		 */
		
		int rowsCols = 1;
		for(int j = 15; j > 0; j--)
		{
			if(modelNames.length <= (j * j))
			{
				rowsCols = j; 
			}
		}

		/* the max width of a model button is
		 * determined by how many times we can fit a 
		 * model image + border in the ModelSelector,
		 * minus one border (the top or left one)
		 */
		int modelButtonWidth = (((int) this.getWidth() - MARGIN) / rowsCols) - MARGIN;
		for (int i = 0; i < modelNames.length; i++)
		{
			String modelName = modelNames[i];

			int row = (int) Math.floor(i / rowsCols);
			int col = i;
			while (col >= rowsCols)
			{
				col -= rowsCols;
			}


			String modelUrl = dir.getPath() + "/" + modelName;			
			String imageUrl = "/images/" + modelName.substring(0, modelName.lastIndexOf('.')) + ".png";
			double x1 = MARGIN + 1 + col * (modelButtonWidth + MARGIN);
			double y1 = MARGIN + 1 + row * (modelButtonWidth + MARGIN);
			ModelButton button = new ModelButton((int)x1, (int)y1, modelButtonWidth, modelButtonWidth, modelUrl);		
			// find image
			button.setImage(Palette.createImage(imageUrl, modelButtonWidth));
			// setting the label to the name of the model file sans .nlogo
			button.setLabel(modelName.substring(0, modelName.lastIndexOf('.')));
			// add and enable the new button
			modelButtons.add(button);
			button.setEnabled(true);
			button.setVisible(true);
			
	
		}

	}

	public void onClick(Button button) {
		loadedModel = button.getAction();
		app.loadModel(button.getAction(), button.getAction());
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

		for(ModelButton button: modelButtons)
		{
			boolean highlighted = (button.getAction().equals(loadedModel)) ? true : false;
			// the button draw method just draws the image
			button.draw(g, highlighted);
			// so we need to add text too
			// - maybe I should extend Button class to a ModelButton class? 
			g.setFont(new Font(Font.SANS_SERIF, 12, 12));
			// to avoid sub stringing into empty space and get OOBException
			int endChar = 0;
			if(button.getLabel().length() < 13)
			{
				endChar = button.getLabel().length();
			}
			else
			{
				endChar = 13;
			}
			// draw name below image
			g.drawString(button.getLabel().substring(0, endChar), 
					(int)button.getShape().getBounds2D().getX() + 5,  
					(int)button.getShape().getBounds2D().getY() + button.getImage().getHeight() + MARGIN / 2);
			// if this was the last loaded model, then we draw a red rectangle around it
			if(button.getLabel().equals(loadedModel))
			{
				// todo
			}

		}

		

	}


	public void onDown() {
		this.target = null;
		for (ModelButton button : modelButtons) {
			if (button.containsTouch(touchX, touchY) && button.isEnabled()) {
				loadedModel = button.getAction();
				app.loadModel(button.getAction(), button.getAction());
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