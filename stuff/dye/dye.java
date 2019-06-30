import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.util.Vector;

public class dye extends Applet implements ActionListener, ItemListener {
    Panel box;
    TextArea textBox;
    Button calc;
    TextField incrementText;
    TextField toleranceText;
    TextField time1Text;
    TextField time2Text;
    TextField distanceText;
    Checkbox showMeTheMoney;
    Checkbox optimize;
    Label label1, label2, label3, label4, label5, label6, label7, label8;
    double minTime, maxTime;
    Vector results;
    double increment;
    double tolerance;
    double time1;
    double time2;
    double distance;
    
    /**
     * Initializes the applet.  You never need to call this directly; it is
     * called automatically by the system once the applet is created.
     */
    public void init() {
	box = new Panel (new GridLayout (8, 2, 5, 5));
	box.setSize (new Dimension (100, 50));
	setLayout (new BorderLayout ());
	textBox = new TextArea ();
	calc = new Button ("Calculate now");
	incrementText = new TextField ("0.01");
	toleranceText = new TextField ("0.01");
	time1Text = new TextField ("1686");
	time2Text = new TextField ("1964");
	distanceText = new TextField ("636.65");
	showMeTheMoney = new Checkbox ("", false);
	optimize = new Checkbox ("", true);
	optimize.addItemListener (this);
	
	
	box.add (incrementText);
	label1 = new Label ("Value to start time iterations at");
	box.add (label1);
	box.add (toleranceText);
	label2 = new Label ("Value to start decimal accuracy iterations at");
	box.add (label2);
	box.add (time1Text);
	label3 = new Label ("Time one");
	box.add (label3);
	box.add (time2Text);
	label4 = new Label ("Time two");
	box.add (label4);
	box.add (distanceText);
	label5 = new Label ("Distance");
	box.add (label5);
	box.add (showMeTheMoney);
	label6 = new Label ("Show me diffusivity in Java Console (eek!)");
	box.add (label6);
	box.add (optimize);
	label7 = new Label ("Optimize on time then accuracy");
	box.add (label7);
	box.add (calc);
	label8 = new Label ("Enter values and press to calculate");
	box.add (label8);
	box.setVisible (true);
	add (box, BorderLayout.SOUTH);
	add (textBox, BorderLayout.CENTER);
	calc.addActionListener (this);
    }

    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void start() {
    }

    /**
     * Called to stop the applet.  This is called when the applet's document is
     * no longer on the screen.  It is guaranteed to be called before destroy()
     * is called.  You never need to call this method directly
     */
    public void stop() {
    }

    /**
     * Cleans up whatever resources are being held.  If the applet is active
     * it is stopped.
     */
    public void destroy() {
    }
    
    public void actionPerformed (ActionEvent ae) {
	if (ae.getSource () == calc) {
	    putUpResults ();
	}
    }
    
    public void putUpResults () {
	
	textBox.setText ("");
	
	try {
	    increment =  (Double.valueOf(incrementText.getText())).doubleValue();
	    tolerance = (Double.valueOf(toleranceText.getText())).doubleValue();
	    time1 = (Double.valueOf(time1Text.getText())).doubleValue();
	    time2 = (Double.valueOf(time2Text.getText())).doubleValue();
	    distance = (Double.valueOf(distanceText.getText())).doubleValue();
	} catch (Exception e) {
	    textBox.append ("Please enter a numerical value in all the boxes\n");
	    return;
	}
	if ((increment == 0 || tolerance == 0) || (time1Text.getText () == null || time2Text.getText () == null)) {
	    textBox.append ("Please enter a numerical value in all the boxes\n");
	    return;
	}
	
	
	minTime = time1;
	maxTime = time2;
	results = new Vector ();
	Vector oldResults = new Vector ();
	int size;
	
	if (!(optimize.getState ())) {
	    iterate (time1, time2, increment, tolerance);
	} else {
	    boolean firstTime = true;
	    for (;;) {
		size = oldResults.size();
	        oldResults = results;
	        results.removeAllElements();
	        iterate (time1, time2, increment, tolerance);
	        if ((results.size () == 0) || ((results.size () >= size ) && (!firstTime))) {
		    results = oldResults;
		    break;
	        }
	        increment = increment / 10;
		firstTime = false;
	    }
	     
	    tolerance = tolerance / 10;
	    firstTime = true;
	    
	    for (;;) {
		size = oldResults.size();
	        oldResults = results;
	        results.removeAllElements();
	        iterate (time1, time2, increment, tolerance);
	        if ((results.size () == 0) || (results.size () >= size )) {
		    results = oldResults;
		    if (firstTime) {
			tolerance *= 10;
		    }
		    break;
	        }
	        tolerance = tolerance / 10;
		firstTime = false;
	    } 
	
	
	}
	
	if (results.size () > 300) {
	    textBox.append ("Greater than 300 results");
	} else {
	    textBox.append ("Peak event time,  Dispersion, Time Iterations, Tolerance \n");
	    for (int j = 0; j < results.size();) {
		textBox.append (results.elementAt(j) + "," + results.elementAt(j+1) + "," + increment + 
		    "," + tolerance + "\n");
		j += 2;
	    }
	}
	results.removeAllElements(); 
	
    } // End of put up result
    
    public void iterate (double firstTime, double lastTime, double increment, double tolerance) {
	
	boolean first;
	first = true;
	
	
	for (int i = 0; i < ((maxTime - minTime) / increment); i++) {
	    
	    double peakEventTime = minTime + (increment * i); 
	    
	    double left = diffusivity (distance, peakEventTime, time1);
	    double right = diffusivity (distance, peakEventTime, time2);
	    
	    if (left >= (right - tolerance)) {
		if (first) {
		    minTime = peakEventTime;
		    first = false;
		}
		if (left <= (right + tolerance)) {
		    results.addElement (new Double (peakEventTime));
		    results.addElement (new Double (left));
		} else {
		    maxTime = peakEventTime;
		    return;
		}
	    }
	}
    } // End of iterate
    
    
    
    
    
    public double diffusivity (double distance, double peakEventTime, double time) {
	double sqRoot = java.lang.Math.sqrt(peakEventTime / time);
	double logVar = java.lang.Math.log(2* sqRoot);
	double diffuse = (    (distance * distance)*((peakEventTime - time)*(peakEventTime - time)) / 
			    ((4 * (peakEventTime * peakEventTime)) * time * logVar)  );
	
	if (showMeTheMoney.getState()) {
	    System.out.println ("Calculating diffusivity as " + diffuse);
	}
	return diffuse;
    }
    
    public void itemStateChanged(ItemEvent ie) { 
	if (ie.getSource () == optimize) {
	    if (optimize.getState()) {
		label1.setText("Value to start time iterations at");
		label2.setText("Value to start decimal accuracy iterations at"); 
	    } else {
		label1.setText("Value of time iterations");
		label2.setText("Value of decimal accuracy");
	    }
	}
    }
    
    
    
}

