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

import touch.TouchFrame;
import java.awt.Font;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;




public class SurfaceSlider extends SurfaceWidget {


   protected double max;
   protected double min;
   protected double value;
   protected boolean down;
   protected String unit;
   protected ButtonListener listener;
   protected int decimalSpaces;
   protected int digitSpaces;
   protected DecimalFormat decimalValue;
   protected double increment;
   protected int gutter;
   
//   DecimalFormat oneDec = new DecimalFormat("#.0");
//   DecimalFormat twoDecs = new DecimalFormat("#.00");
//   DecimalFormat threeDecs = new DecimalFormat("#.000");
//   DecimalFormat fourDecs = new DecimalFormat("#.0000");
//   DecimalFormat fiveDecs = new DecimalFormat("#.00000");
   
   

   public SurfaceSlider(String text) {
	  
      super(text);
      this.max = 30;
      this.min = 0;
      this.value = 10;
      this.down = false;
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

   public double getMaxValue() {
      return this.max;
   }

   public void setMaxValue(double maxvalue) {
      this.max = maxvalue;
   }

   public double getMinValue() {
      return this.min;
   }

   public void setMinValue(double minvalue) {
      this.min = minvalue;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = value;
   }

   public double getIncrement() {
	return increment;
}

public void setIncrement(double increment) {
	this.increment = increment;
}

public void setUnit(String unit) {
      this.unit = unit;
   }

   public String getUnit() {
      return this.unit;
   }
   
   public void setDecimals(int decimals)
   {
	   // set decimals
	   this.decimalSpaces = decimals;	   
	   // set the decimal format
	   switch(decimals){
	   case 1 : decimalValue = new DecimalFormat("0.#");
	   break;
	   case 2 : decimalValue = new DecimalFormat("0.##");
	   break;
	   case 3 : decimalValue = new DecimalFormat("0.###");
	   break;
	   case 4 : decimalValue = new DecimalFormat("0.####");
	   break;
	   case 5 : decimalValue = new DecimalFormat("0.#####");
	   break;
	   case 6 : decimalValue = new DecimalFormat("0.######");
	   break;
	   default : decimalValue = new DecimalFormat();
	   break;

	   }
	   

	   
	   
   }
   
   public int getDecimals()
   {
	   return decimalSpaces;
   }

   public int getDigitSpaces() {
	return digitSpaces;
}

public void setDigitSpaces(int digitSpaces) {
	this.digitSpaces = digitSpaces;
}

protected boolean onBall(double tx, double ty) {

      double bw = getBallWidth() * 2;
      double bx = getBallX() - bw/2;
      double by = getBallY() - bw/2;

      return (tx >= bx && tx <= bx + bw && ty >= by && ty <= by + bw);
   }
   
   public void draw(Graphics2D g) {
      int w = getWidth();
      int h = getHeight();

      RoundRectangle2D box = new RoundRectangle2D.Float(
         0, 0, w, h, 20, 20);
      g.setColor(background);
      g.fill(box);

      GradientPaint paint;
      
      /*
      GradientPaint paint = new GradientPaint(
         0, 0, Color.DARK_GRAY, 0, h, Color.LIGHT_GRAY);
      g.setPaint(paint);
      */

      int bx = (int)getBallX();
      int by = (int)getBallY();
      int bw = (int)getBallWidth();
      int minX = (int)getBallX(this.min);
      int maxX = (int)getBallX(this.max);

      
      g.setColor(isEnabled() ? Color.GRAY : background);
      g.setStroke(new BasicStroke(1.5f));
      g.drawLine(minX, by, maxX, by);
      g.drawLine(maxX + (int)getMargin(), 0, maxX + (int)getMargin(), h);

      g.setColor(isEnabled() ? Color.LIGHT_GRAY : background);
      g.setStroke(new BasicStroke(1.5f));
      g.draw(box);
      
      // Draw ticks
      //g.drawLine(minX, by - bw/4, minX, by + bw/4);
      //g.drawLine(maxX, by - bw/4, maxX, by + bw/4);

      Ellipse2D ball = new Ellipse2D.Double(
         bx - bw/2, by - bw/2, bw, bw);

      g.setColor(isEnabled() ? Color.GRAY : background);
      g.fill(ball);
      int y1 = by - bw/2;
      int y2 = down? by + bw * 2 : by + bw/2;

      if (isEnabled()) {
         paint = new GradientPaint(
            0, y1, new Color(0xddffffff, true),
            0, y2, new Color(0x00ffffff, true));
         g.setPaint(paint);
         g.fill(ball);

         paint = new GradientPaint(
            0, by - bw/2, Color.LIGHT_GRAY,
            0, by + bw/2, Color.BLACK);
         g.setPaint(paint);
         g.draw(ball);
      }

      g.setColor(Color.LIGHT_GRAY);
      g.setFont(font);
      String s = decimalValue.format(value) + unit;
      g.drawString(s, w - getGutter() + 5, h - 17);

      g.setFont(new Font(null, 0, 14));
      g.drawString(getText(), minX, h - 4);
      
   }

   private double getMargin() {
      return getBallWidth() / 2 + 4;
   }

   private int getGutter() {
      return gutter;
   }
   private void setGutter(int gutter)
   {
	   this.gutter = gutter;
   }
   
   // this calculates the value of the slider based on the position of the ball.
   private double getBallX(double min2) {
      double w = getWidth() - getMargin() * 2 - getGutter();
      double range = max - min;
      double scale = (range / w);
      // it needs to take into account the number of decimals, and the increments
      //return (min2 - min) / scale + getMargin();
      double rawValue = (min2 - min) / scale + getMargin();
      // find remainder of value divided by increment,
      // then round off to nearest increment value
      double remainder = rawValue % this.increment;
      // if the remainder is is bigger than half the increment, we need add the last bit to get it up to next remainder
      // if it is smaller, then we need to subtract it
      double number = (remainder >= remainder / 2) ? rawValue + increment - remainder : rawValue - remainder;
      System.out.println(this.text +  " increment: " + increment);
	return number; 
   }
   
   private double getBallX() {
      return getBallX(this.value);
   }

   private double getBallY() {
      return getHeight() / 2 - 6;
   }
   
   private double getValue(double bx) {
      double w = getWidth() - getMargin() * 2 - getGutter();
      double range = max - min;
      double scale = (range / w);
      return ((bx - getMargin()) * scale) + min;
   }

   private double getBallWidth() {
      return 20;
   }

   private void computeValue(double tx) {
	   
	   // The rounding we do needs to be rounded to nearest increment
	   // it also needs to take into consideration that we may have different numbers of decimals
	   this.value = (double)getValue(tx);

	   double multiplier = Math.pow(10,  decimalSpaces);
	   // here we multiply with 10 to the power of number of decimals
	   value *= multiplier;

	   value = Math.round(value);
	   //	   System.out.println(value);	   
	   value /= multiplier;

	   double remainder = value % this.increment;
	   // if the remainder is is bigger than half the increment, we need add the last bit to get it up to next remainder
	   // if it is smaller, then we need to subtract it
	   value = (remainder >= remainder / 2) ? value + increment - remainder : value - remainder;
//	   System.out.println(this.text +  " increment: " + increment);	   


	   if (value < min) value = min;
      if (value > max) value = max;
      
   }

   public void touchDown(TouchFrame frame) {
      if (onBall(frame.getLocalX(), frame.getLocalY())) {
         this.down = true;
      }
   }

   public void touchDrag(TouchFrame frame) {
      if (down && isEnabled()) {
         computeValue(frame.getLocalX());
      }
   }

   public void touchRelease(TouchFrame frame) {
      if (down && isEnabled()) buttonReleased();
      this.down = false;
   }

   public void mousePressed(MouseEvent e) {
      double tx = screenToObjectX(e.getX(), e.getY());
      double ty = screenToObjectY(e.getX(), e.getY());
      if (onBall(tx, ty)) {
         this.down = true;
      }
   }
   
   public void mouseReleased(MouseEvent e) {
      if (down && isEnabled()) buttonReleased();
      this.down = false;
   }

   public void mouseDragged(MouseEvent e) {
      if (down && isEnabled()) {
         computeValue(screenToObjectX(e.getX(), e.getY()));
      }
   }

   public void buttonReleased() {
      if (listener != null) {
         listener.buttonReleased(this);
      }
   }
   
   public void resize()
   {
	   // set size to 15 px per digit, decimal, point space, and each char in the unit + 5 px for space to the left 
	   int gutter = ((digitSpaces + decimalSpaces + 1 + this.unit.length()) * 15) + 5;
	   this.setGutter(gutter);
   }
}
