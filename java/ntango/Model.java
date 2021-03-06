/*
 * @(#) Model.java
 */
package ntango;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.nlogo.api.World;
import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.nvm.Command;
import org.nlogo.nvm.Procedure;

import touch.ui.ButtonListener;
import touch.ui.SurfaceCheckbox;
import touch.ui.SurfaceMonitor;
import touch.ui.SurfaceSlider;
import touch.ui.SurfaceWidget;

import org.imgscalr.Scalr;

import com.sun.tools.javac.util.List;

public class Model implements ButtonListener {

	protected HeadlessWorkspace workspace;
	protected SimStream stream;
	protected int findex;  // index of the visible frame
	protected Main app;
	protected boolean loaded = false;
	protected Map<Integer, Color> watches;
	protected String name;

	protected boolean isTouchable; //true if there are touch procedures in netlogo code 
	protected boolean hasTouchDown;
	protected boolean hasTouchUp;
	protected boolean hasTouchDrag;
	
	/*
	 * background image variables
	 */
	protected BufferedImage backgroundImage;
	protected boolean bgLoaded = false;
	


	public static Color [] WATCH_COLORS = {
		Color.RED,
		Color.YELLOW,
		Color.GREEN,
		Color.BLUE,
		Color.MAGENTA,
		Color.CYAN,
		Color.PINK };

	public static int wcindex = 0;


	public Model(Main app) throws Exception {
		this.workspace = HeadlessWorkspace.newInstance();
		this.stream = new SimStream(1000);
		this.findex = 0;
		this.app = app;
		this.loaded = false;
		this.watches = new java.util.HashMap<Integer, Color>();
		this.name = "";
		
		

		
		
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public SimStream getStream() {
		return this.stream;
	}

	public SimFrame getCurrentFrame() {
		return stream.getFrame(findex);
	}

	public int getPlayHead() {
		return findex;
	}

	public void setPlayHead(int i) {
		i = Math.max(i, stream.getMinIndex());
		i = Math.min(i, stream.getMaxIndex());
		this.findex = i;
	}

	public BufferedImage getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImagePath) {
		this.backgroundImage = Palette.createImage(backgroundImagePath, 400);
		this.bgLoaded = true;
	}

	public boolean isBgLoaded() {
		return bgLoaded;
	}
	
	public void resizeBackground(int w, int h)
	{
//		Scalr scaler = new Scalr();
		backgroundImage = Scalr.resize(backgroundImage, w, h);
	}
	public void resizeBackground()
	{
//		backgroundImage = Scalr.resize(backgroundImage, this.width, this.height);
	}
	
	public void discardBackground()
	{
		this.backgroundImage = null;
		this.bgLoaded = false;
	}

	public void setBgLoaded(boolean bgLoaded) {
		this.bgLoaded = bgLoaded;
	}

	private void clear() {
		stream.clear();
		watches.clear();
		findex = 0;
	}

	public synchronized void load(String filename) {
		System.out.println("load");
		try {
			clear();
			this.workspace = HeadlessWorkspace.newInstance();
			this.workspace.open(filename);

			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("SLIDER")) {
					initSlider(in);
				} else if (line.startsWith("SWITCH")) {
					initSwitch(in);
				} else if (line.startsWith("PLOT")) {
					initPlot(in);
				}
				// monitor here
				else if(line.startsWith("MONITOR")){
					initMonitor(in);
				}
			}
			
			/*
			 * TESTCODE:Add a background selector to only Bug Hunt Camouflage
			 */
			
			initBackroundSelector();
			
			
			
			
			in.close();

			this.loaded = true;

			/*
			 * Get all procedures and see if there is a "to touch-down". If there isn't, set
			 * as not touchable
			 */

			Map<String,Procedure> modelProcedures = this.workspace.getProcedures();
			hasTouchDown = modelProcedures.containsKey("TOUCH-DOWN");
			hasTouchUp = modelProcedures.containsKey("TOUCH-UP");
			hasTouchDrag = modelProcedures.containsKey("TOUCH-DRAG");
			
			System.out.println(this.name + " : down: " + hasTouchDown + ", up: " + hasTouchUp + ", drag: " + hasTouchDrag);



		} catch (Exception x) {
			x.printStackTrace();
		}

	}




	public synchronized void setup() {
		try {
			clear();
			workspace.command("setup");
			stream.addFrame(new SimFrame(workspace.world()));
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void tick() {
		if (!isLoaded()) return;
		try {
			if (!stream.isBufferFull() || findex > stream.getMinIndex()) {
				workspace.command("go");
				SimFrame last = stream.getLastFrame();
				SimFrame frame = new SimFrame(workspace.world());
				if (last != null) {
					frame.markDeadTurtles(last);
				}
				stream.addFrame(frame);

				String [] names = workspace.plotManager().getPlotNames();
				for (int i = 0; i<names.length; i++) {
					Plot plot = app.getPlot(names[i]);
					if (plot != null) {
						plot.update(workspace.plotManager().getPlot(names[i]));
					}
					//.pens().iterator(): pen.points() x() y()
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setParameter(String param, String value) {
		try {
			workspace.command("set " + param + " " + value);
			setPlayHead(stream.getMaxIndex());
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void setParameter(String name, boolean b) {
		setParameter(name, String.valueOf(b));
	}

	public void setParameter(String name, int v) {
		setParameter(name, String.valueOf(v));
	}


	public void doTouchDown(float touchX, float touchY, int touchId) {
		if (isLoaded() && hasTouchDown) {
			workspace.command("touch-down " + touchX + " " + touchY);// ignore touch ID for now + " " + touchId);
		} else {
			System.out.println("Ignoring touch-down event");
		}
	}

	public void doTouchUp(float touchX, float touchY, int touchId) {
		// commented out until touch up works as intended
//		if (isLoaded() && hasTouchUp) {
//			workspace.command("touch-up " + touchX + " " + touchY + " " + touchId);
//			System.out.println("touch-up " + touchX + " " + touchY + " " + touchId);
//		} else {
//			System.out.println("Ignoring touch-up event");
//		}
	}

	public void doTouchDrag(float touchX, float touchY, int touchId) {
		// commented out until touch draw works as intended
//		if (isLoaded() && hasTouchDrag) {
//			workspace.command("touch-drag " + touchX + " " + touchY + " " + touchId);
//		} else {
//			System.out.println("Ignoring touch-drag event");
//		}
	}

	public void addWatch(Turtle t) {
		if (t != null) {
			SimFrame frame = getCurrentFrame();
			int count = 0;
			for (Integer id : watches.keySet()) {
				if (frame.hasTurtle(id.intValue())) {
					count++;
				}
			}
			if (count < 4) {
				this.watches.put(t.getID(), getNextWatchColor());
			}
		}
	}

	public void toggleWatch(Turtle t) {
		if (t != null) {
			if (watches.containsKey(t.getID())) {
				watches.remove(t.getID());
			} else {
				addWatch(t);
			}
		}
	}

	public Set<Integer> getWatchList() {
		return this.watches.keySet();
	}

	public Color getWatchColor(int id) {
		if (watches.containsKey(id)) {
			return watches.get(id);
		} else {
			return Color.WHITE;
		}
	}

	protected Color getNextWatchColor() {
		Color c = WATCH_COLORS[wcindex];
		wcindex = (wcindex + 1) % WATCH_COLORS.length;
		return c;
	}

	public World getWorld() {
		return workspace.world();
	}

	public int getMaxPX() {
		return workspace.world.maxPxcor();
	}

	public int getMaxPY() {
		return workspace.world.maxPycor();
	}

	public int getMinPX() {
		return workspace.world.minPxcor();
	}

	public int getMinPY() {
		return workspace.world.minPycor();
	}

	public int getWorldWidth() {
		return workspace.world.worldWidth();
	}

	public int getWorldHeight() {
		return workspace.world.worldHeight();
	}

	private void initSlider(BufferedReader in) throws IOException {
		String x0 = in.readLine();
		String y0 = in.readLine();
		String x1 = in.readLine();
		String y1 = in.readLine();
		String name = in.readLine();
		in.readLine();
		String min = in.readLine();
		String max = in.readLine();
		String curr = in.readLine();
		String incr = in.readLine();
		// calculate the number of digits here
		int digits = 0;
		// if there is no point, just find the length of the incr string
		if(!incr.contains("."))
		{
			digits = incr.length();
		}// else count only up to the point
		else
		{
			int pointPosition = incr.lastIndexOf(".");
			String digitsString = incr.substring(0, pointPosition);
			digits = digitsString.length();
		}
		
		// calculate number of decimals here
		int decimals = 0;

		if(incr.contains("."))
		{
			int pointPosition = incr.lastIndexOf(".");
			String decimalsString =  incr.substring(pointPosition + 1, incr.length());
			// sometimes people include decimals in their increments, e.g. 1.0
			// make sure we don't count that as a decimal
			decimals = (Double.parseDouble(decimalsString) == 0 ) ? 0 : decimalsString.length();
		}


		in.readLine();
		String unit = in.readLine();
		if ("nil".equalsIgnoreCase(unit)) unit = "";
		String orientation = in.readLine();

		SurfaceSlider slider = new SurfaceSlider(name);
		slider.setMaxValue(toFloat(max));
		slider.setMinValue(toFloat(min));
		slider.setValue(toFloat(curr));
		slider.setHeight(50);
		slider.setWidth(300);
		slider.setPosition(toInt(x0) * 2, toInt(y0) * 2);
		slider.setUnit(unit);
		slider.setButtonListener(this);
		// set number of digits
		slider.setDigitSpaces(digits);
		// set number of decimals
		slider.setDecimals(decimals);
		// set increment
		slider.setIncrement(Double.parseDouble(incr));	
		
		// resize to take into account number of digits and decimals
		slider.resize();
		
		app.addModelWidget(slider);
	}
	
	private void initBackroundSelector() {
		
		
		SurfaceSelectorBackgroundImages bgSelector = new SurfaceSelectorBackgroundImages(app);
		/*
		 * iterate through xml file here and get each of the nodes with a background image
		 */
		ArrayList<String> bgNames = new ArrayList<String>();
		// this one will clear the background
		bgNames.add("blank");
		bgNames.add("/images/Bug Hunt Camouflage.png");
		bgNames.add("/images/Bug Hunt Camouflage.png");
		bgSelector.setImages(bgNames);
		bgSelector.layout();
		bgSelector.setMovable(false);
		bgSelector.setPosition(1200, 100);
		app.addModelWidget(bgSelector);
		
	}	
	
	
	private void initMonitor(BufferedReader in) throws IOException
	{

		String x0 = in.readLine();
		String y0 = in.readLine();
		in.readLine(); // width
		in.readLine(); // height
		String name = in.readLine();
		String reporter = in.readLine();

		// not sure what these three are, but they are in the nlogo file
		in.readLine();  
		in.readLine();  
		in.readLine();  
		
		
		
		SurfaceMonitor monitor = new SurfaceMonitor(name, workspace);
		monitor.setReporter(reporter);
		monitor.setPosition(toInt(x0) * 2, toInt(y0) * 2);		
		monitor.setButtonListener(this);
		app.addModelWidget(monitor);
		
	}
	

	private void initSwitch(BufferedReader in) throws IOException {
		String x0 = in.readLine();
		String y0 = in.readLine();
		in.readLine();
		in.readLine();
		String name = in.readLine();
		in.readLine();
		boolean checked = "0".equals(in.readLine());

		SurfaceCheckbox box = new SurfaceCheckbox(name);
		box.setChecked(checked);
		box.setPosition(toInt(x0) * 2, toInt(y0) * 2);
		box.setButtonListener(this);
		app.addModelWidget(box);
	}

	private void initPlot(BufferedReader in) throws IOException {
		//	   for (String name : workspace.plotManager().getPlotNames())
		//	   {
		//
		//			   
		//
		//
		//	   }
		String x0 = in.readLine();
		String y0 = in.readLine();
		in.readLine(); // width
		in.readLine(); // height
		String title = in.readLine();

		org.nlogo.plot.Plot nplot = 
				workspace.plotManager().getPlot(title);
		Plot plot = new Plot(nplot);

		plot.setPosition(toInt(x0) * 2, toInt(y0) * 2);
		plot.setXLabel(in.readLine());
		plot.setYLabel(in.readLine());
		plot.setMinX(toFloat(in.readLine()));
		plot.setMaxX(toFloat(in.readLine()));
		plot.setMinY(toFloat(in.readLine()));
		plot.setMaxY(toFloat(in.readLine()));
		in.readLine();  // autoplot
		in.readLine();  // show legend
		in.readLine();  // PENS


		// AH this code adds Pens to a plot, but the Plot constructor already does this. This is what caused the 
		// extra empty pens to show up in ntango 
		//      String spec = in.readLine();
		//      while (spec.length() > 0) {
		//    	  plot.addPen(spec);
		//
		//         spec = in.readLine();
		//      }

		/*
		 * PLOT
6 
199
166
319
Bugs Caught vs. Time
seconds
bugs
0.0
10.0
0.0
10.0
true
false
"" ""
PENS
"default" 1.0 0 -16777216 true "" "plotxy ticks total-caught"
		 */


		app.addModelPlot(plot);
	}
	

	
	

	public void buttonPressed(SurfaceWidget button) {
	}

	public void buttonReleased(SurfaceWidget button) {
		try { 
			if (button instanceof SurfaceCheckbox) {
				boolean checked = ((SurfaceCheckbox)button).isChecked();
				setParameter(button.getText(), checked);
			}
			else if (button instanceof SurfaceSlider) {
				double val = ((SurfaceSlider)button).getValue();
				setParameter(button.getText(), Double.toString(val));
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private int toInt(String s) {
		try {
			float f = Float.parseFloat(s);
			return (int)f;
		} catch (Exception x) {
			return 0;
		}
	}

	private float toFloat(String s) {
		try {
			return Float.parseFloat(s);
		} catch (Exception x) {
			return 0;
		}
	}
}

