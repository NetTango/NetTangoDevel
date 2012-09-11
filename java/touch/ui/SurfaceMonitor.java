/*
 * @(#) SurfaceSlider.java
 * 
 * NetLogo Jr.
 * Learning Sciences, School of Education and Social Policy
 * Northwestern University
 * 
 * Copyright (c) 2010, Northwestern University
 */
package touch.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import org.nlogo.headless.HeadlessWorkspace;

import touch.TouchFrame;




public class SurfaceMonitor extends SurfaceWidget {

   protected String unit;
   protected ButtonListener listener;
   protected String reporter;
   protected HeadlessWorkspace workspace;
   
   public SurfaceMonitor(String text, HeadlessWorkspace workspace) {
      super(text);
      
      this.workspace = workspace;

      this.listener = null;
      this.unit = "";
      setFont(new java.awt.Font(null, 0, 20));
      setBackground(new Color(0x44000000, true));
   }

   public ButtonListener getButtonListener() {
      return this.listener;
   }

   public void setButtonListener(ButtonListener listener) {
      this.listener = listener;
   }


   public void setUnit(String unit) {
      this.unit = unit;
   }

   public String getUnit() {
      return this.unit;
   }


   
   public void draw(Graphics2D g) {
	   

      int w = getWidth();
      int h = getHeight();

      RoundRectangle2D box = new RoundRectangle2D.Float(
         0, 0, w, h, 20, 20);
      g.setColor(background);
      g.fill(box);

      //GradientPaint paint;
      
      /*
      GradientPaint paint = new GradientPaint(
         0, 0, Color.DARK_GRAY, 0, h, Color.LIGHT_GRAY);
      g.setPaint(paint);
      */
      
      

      this.enabled = true;
      
      g.setColor(isEnabled() ? Color.GRAY : background);
      g.setStroke(new BasicStroke(1.5f));


      g.setColor(isEnabled() ? Color.LIGHT_GRAY : background);
      g.setStroke(new BasicStroke(1.5f));
      g.draw(box);
      
      g.setColor(Color.LIGHT_GRAY);
      g.setFont(font);
      
      // get reporter value from headless workspace
      String value = String.valueOf(workspace.report(reporter));
      
      

//      g.drawString(getText(), 10, h - 4);
      g.setFont(new Font(null, 0, 14));
      g.drawString(value, w - getGutter() + 5, h - 17);
      
   }



   private int getGutter() {
      return 65;
   }
   
   public void setReporter(String reporterName)
   {
	   this.reporter = reporterName;
   }
   
   public String getReporter()
   {
	   return this.reporter;
   }

   



   public void touchDown(TouchFrame frame) {
//      if (onBall(frame.getLocalX(), frame.getLocalY())) {
//         this.down = true;
//      }
   }

   public void touchDrag(TouchFrame frame) {
//      if (down && isEnabled()) {
//         computeValue(frame.getLocalX());
//      }
   }

   public void touchRelease(TouchFrame frame) {
//      if (down && isEnabled()) buttonReleased();
//      this.down = false;
   }

   public void mousePressed(MouseEvent e) {
//      double tx = screenToObjectX(e.getX(), e.getY());
//      double ty = screenToObjectY(e.getX(), e.getY());
//      if (onBall(tx, ty)) {
//         this.down = true;
//      }
   }
   
   public void mouseReleased(MouseEvent e) {
//      if (down && isEnabled()) buttonReleased();
//      this.down = false;
   }

   public void mouseDragged(MouseEvent e) {
//      if (down && isEnabled()) {
//         computeValue(screenToObjectX(e.getX(), e.getY()));
//      }
   }

   public void buttonReleased() {
//      if (listener != null) {
//         listener.buttonReleased(this);
//      }
   }
}
