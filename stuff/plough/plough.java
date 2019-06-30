package uk.ac.leeds.ccg.modeling.glacier.ploughing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Applet and Application to model the ploughing of a clast.<P>
 * @author  <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Dr Andrew Evans</A>
 * @version 2.0
 */
public class Plough extends java.applet.Applet implements ActionListener {

    // User interface elements.
    
    private Label helpLabel = new Label("This mode calculates a single plough event's geometry");
    private TextField inflowText = new TextField("100", 10);
    private Label inflowLabel = new Label("Inflow percentage");
    private double inflow = 100;    
    private TextField clastWidthText = new TextField("1.75", 10);
    private Label clastWidthLabel = new Label("Clast width");
    private double clastWidth = 1.75;
    private TextField clastHeightText = new TextField("1.0", 10);
    private Label clastHeightLabel = new Label("Clast height");    
    private double clastHeight = 1;
    private TextField clastThermalText = new TextField("10318.13856", 10); // per day. 429.92244 = per hour  0.1194229 joules per degree C per sec per cm, then divided by 100 for path length increase and x 10000 for area increase }
    private Label clastThermalLabel = new Label("Clast thermal conductivity (per day)"); 
    private double clastThermalConductivity = 10318.13856;
    private TextField tillResidualText = new TextField("5000", 10);
    private Label tillResidualLabel = new Label("Till residual strength (Pa)"); 
    private double tillResidualStrength = 5000;
    private TextField tillFrictionText = new TextField("22", 2); //{from B+H'87}
    private Label tillFrictionLabel = new Label("Till internal friction (degrees)");
    private double tillFriction = 22;      
    private TextField bedSlopeText = new TextField("3.6", 2); 
    private Label bedSlopeLabel = new Label("Slope the clast ploughs into (degrees)");   
    private double bedSlope = 3.6;      
    private TextField iceVelocityText = new TextField("20", 10); // per day. per hour = 0.002276867 {0.00000063246m/s} {20m a-1} 
    private Label iceVelocityLabel = new Label("Initial ice velocity (m a-1)"); 
    private double iceVelocity = 20; 
    private TextField velocityStartText = new TextField("0", 10);
    private Label velocityStartLabel = new Label("Starting velocity (ma-1)");
    private double velocityStart = 0;
    private TextField velocityEndText = new TextField("2000", 10);
    private Label velocityEndLabel = new Label("Ending velocity (ma-1)");
    private double velocityEnd = 2000;
    private TextField velocityStepText = new TextField("10.0", 10);
    private Label velocityStepLabel = new Label("Step between velocities (ma-1)");
    private double velocityStep = 10.0;
    
    private TextField strengthStartText = new TextField("0", 10);
    private Label strengthStartLabel = new Label("Starting till strength (Pa)");
    private double strengthStart = 0;
    private TextField strengthEndText = new TextField("50000.0", 10);
    private Label strengthEndLabel = new Label("Ending till strength (Pa)");
    private double strengthEnd = 50000.0;
    private TextField strengthStepText = new TextField("1000.0", 10);
    private Label strengthStepLabel = new Label("Step between strengths (Pa)");
    private double strengthStep = 1000.0;
    
    private TextField distanceStartText = new TextField("10", 10);
    private Label distanceStartLabel = new Label("Minimum ploughed distance (m)");
    private double distanceStart = 10;
    private TextField distanceEndText = new TextField("11", 10);
    private Label distanceEndLabel = new Label("Maximum ploughed distance (m)");
    private double distanceEnd = 11;
        
    private TextField debrisFractionText = new TextField("0.2", 10); //{vol of debris / vol of basal ice}
    private Label debrisFractionLabel = new Label("Initial debris fraction"); 
    private double debrisFraction = 0.2;    

    private TextArea output = new TextArea("", 10, 30, TextArea.SCROLLBARS_BOTH);
    private Button modeButton = new Button("Switch modes");
    private Button calculateButton = new Button("Calculate for one speed/strength"); 
    private Button clearButton = new Button("Clear screen"); 
    private Button fileButton = new Button("Set output to file"); 

    // Constants
    
    private int densityIce = 990; // kg per m squared
    private double C = 0.0742; //degrees celceus per Pa
    private double heatOfFusion = 333333.333; //joules per kg
    private double heatLoss = 1.0; //proportion
    private double B =  4.752e-19 ; //per day, 1.98e-20 per hr 5.5e-15 s-1 kpa-1 from Glen's law. Value from Paterson table 3.1 zero celceus
    private double n = 3.0; //from Glen's law
      
    // Globals
    
    private double dragWhenLodged = 0;
    private double clastIceContactHeight = 0;
    private double clastTillContactHeight = 0;
    private double movementTotal = 0;
    private double moveDistance = 0;
    private double volumeTotal = 0;    
    private double forceFromResidualStrength = 0;
    private double stressTransfered = 0;
    private double meltFillHeight = 0;
    private double inflowFillHeight = 0;
    private double fillHeight = 0;
    private double sedimentLength = 0;
    private double maxForce = 0;
    private boolean continuing = true;
    private boolean appletMode = true;
    private BufferedWriter fileWriter = null;
    private int time = 0;
    private String cut = "Ploughing";
    
    
    
    
    
    /** 
     *  Called when the applet is loaded into the browser.
     */
    public void init() {
    
	setBackground(Color.white);
        setLayout();   
        validate();
        
    }
    
    
    
    
    
    /**
     * Allows the Applet to run as an app.
    **/
    public static void main(String[] args) {
	
	Frame appFrame = new Frame("Plougher");

	appFrame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
		System.exit(0);
	    }
	});

        // Get round the whole "static", not an Applet thing...
        
        Plough app = new Plough();
	app.setAppletMode(false);
	
	app.init();
	app.start();

        // Work out the screensize and position the app.
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
	Dimension dimension = toolkit.getScreenSize();
	int centerX = dimension.width / 2;
	int centerY = dimension.height / 2;

	appFrame.setSize(740, 590);
        appFrame.setLocation(centerX - 345, centerY - 364);
	appFrame.setVisible(true);
        appFrame.add(app, "Center");
        appFrame.validate();
        
    } // End of main.
    
    
    
    
    
    /**
     * Notes that this run isn't an applet, if set to false.
    **/
    private void setAppletMode(boolean mode) {
	appletMode = mode;
    }
    
    
    
    
    /**
     * Sets everything running.
    **/
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	// Grab the values defined by the user in the GUI.
	
	getValues();
	
	int count = 0;
	
	// Convert degrees to radians.
	
	double conversion = (Math.PI/180);
	bedSlope = bedSlope * conversion;
	tillFriction = tillFriction * conversion;
	if (iceVelocity != 0) iceVelocity = (iceVelocity / 366.0);
        
	// If they've set either iceVelocity or tillResidualStrength to nothing,
	// calculate the potential velocity / strength combinations.
	
	if ((actionEvent.getSource().equals(calculateButton)) && 
            (calculateButton.getLabel().equals("Calculate for all speed/strengths"))) {
    
	    outputString("Search all speed / strengths for plough length production.\nStarted.", true);
            outputString("Ice velocity(ma-1), " + 
			 "Till residual strength(kPa) ," +
			 "Clast to till contact height ," +
			 "Clast to ice contact height ," +
			 "Total meltout sediment volume(m3) ," +
			 "Max force transfered to ice ," +
			 "Lodgement type", false);
	    
            // Loop through velocities.
            
            iceVelocity = 0;
 
	    
            for (double loopVelocity = velocityStart; loopVelocity <= velocityEnd; loopVelocity = loopVelocity + velocityStep) {
	
           	// Increase iceVelocity per day in 10 m pa steps.
                // Obviously this should be done with loopVelocity.

                iceVelocity = (loopVelocity/366.0);
		
                // Loop through residual strengths.

		
                for (tillResidualStrength = strengthStart; tillResidualStrength < strengthEnd; tillResidualStrength = tillResidualStrength + strengthStep) {
		    
		    ploughOne(false);

		    count++;
		    //if ((movementTotal > distanceStart) && (movementTotal < distanceEnd)) {		    
		    if ((movementTotal > distanceStart) && (movementTotal < distanceEnd)) {
	
			
                         outputString( (iceVelocity*366.0) + " ," 
                                     + (tillResidualStrength/1000) + " ," 
                                     + clastTillContactHeight + " ," 
                                     + clastIceContactHeight + " ," 
                                     + volumeTotal + " ," 
				     + maxForce + " ,"
                                     + cut, false);
                    } else {
                        /**
                         outputString( (iceVelocity*366.0) + " ," 
                                     + (tillResidualStrength/1000) + " ," 
                                     + movementTotal + " ," 
                                     + cut, false); // Not an interesting result but flag anyhow.
                         **/
                    }
		} // End of looping residual strength.
	    } // End of looping velocity.
            
	    outputString("Finished." + count, true);
	    
	}  else if (actionEvent.getSource().equals(calculateButton)) { // if calc distance...
	    
	    outputString("Single plough event mode.\nStarted.", true);
	    outputString("Time(days) ," + 
			 "Movement this iteration(m) ," +
			 "Movement total(m) ," + 
			 "Force on ice from the till ," + 
			 "Clast to till contact height ," +
			 "Clast to ice contact height ," +
			 "Total height of gouge material behind clast, " +
			 "Lodgement type", false);
	    	    
	    ploughOne(true);
	    
            if (cut == "Infinite ploughing") outputString("Infinite plough with clast front buried", false); // Interesting so flag in file if appropriate.
	    outputString("done", true);
            
	} else if (actionEvent.getSource().equals(clearButton)) { // if output file setup button pushed... 
        
            output.setText("");
            	
        } else if (actionEvent.getSource().equals(modeButton)) {
            
            if (calculateButton.getLabel().equals("Calculate for all speed/strengths")) {
                calculateButton.setLabel("Calculate for one speed/strength ");
                switchGui(false);
            } else {
                calculateButton.setLabel("Calculate for all speed/strengths");
                switchGui(true);
            }
            
        } else {
            
	    FileDialog fd = new FileDialog(new Frame(), "Pick file location and name", FileDialog.SAVE);
	    fd.setVisible(true);
	    if ((fd.getDirectory() != null) && (fd.getFile() != null)) {
		File file = new File(fd.getDirectory() + fd.getFile());
		try {
		    file.createNewFile();
		    fileWriter = new BufferedWriter(new FileWriter(file));
		} catch (IOException ioe) {
		    outputString("Cannot write to this file.", true);
		    ioe.printStackTrace();
		}

	    } // End of if cancel not pushed.
	} // End of if button = searchMode.
    } // End of ActionPerformed.
    
    
    
    
    
    /**
     * Write a string to one of two places.<P>
     * If guiNotFile is true, it writes it to the GUI. If 
     * guiNotFile is false, it trys to write it to a file. 
     * If the file doesn't exist, because this is an Applet or 
     * the user hasn't picked a file, it writes it to the GUI.
    **/
    private void outputString(String message, boolean guiNotFile) {
	
	if ((guiNotFile == true) || (fileWriter == null)) {
	    output.append(message + "\n");
	} else {
	    try {
		fileWriter.write(message);
		fileWriter.newLine();
	    } catch (IOException ioe) {
		output.append("Can't write to this file1.\n");
		ioe.printStackTrace();
	    }
	}
    } // End of outputString.
    
    
    
    
    
    /**
     * Ploughs a single boulder.
    **/
    private void ploughOne(boolean printout) {
	
	// Initialize conditions for this run.

	volumeTotal = 0;
	maxForce = 0;
	continuing = true;
	meltFillHeight = 0;
	time = 0;
	movementTotal = 0;
        moveDistance = 0;
        cut = "Ploughing";
        int buriedStep = 0;
	// Set some very small, to avoid division by 0 problems.
        
	clastTillContactHeight = 1e-12;
	clastIceContactHeight = clastHeight - 1e-12;
	forceFromResidualStrength = 1e-12;
	stressTransfered = 1e-12;
	
	// While we haven't lodged, plough the clast.
	
	while (continuing == true) {
	    
	    time++;

            // Calculate the clast movement this iteration.
            
	    calcClastMovement();
	    
            // Calculate new clast contacts with ice/till.
            
            calcNewHeights();
            
            // Calculate the resultant forces.
            
	    calcResidualForce();
            
            // Determine whether to continue based on clast geometry.
            
	    if (moveDistance <= 0) {
                cut = "Lodged with ice contact";
                continuing = false;
            }
            if (clastIceContactHeight <= 0) {
                cut = "Lodged when ice contact lost";
                continuing = false;
            }
	    if (clastTillContactHeight >= clastHeight) {
                if (buriedStep == 1) {
                    cut = "Infinite ploughing";
                    continuing = false;
                } else {
                    buriedStep++;
                }
            }
    
           
	    if (printout == true) {
		outputString(time + " ," 
                    + moveDistance + " ,"
                    + movementTotal + " ," 
                    + forceFromResidualStrength + " , " 
                    + clastTillContactHeight + " ," 
                    + clastIceContactHeight + " ," 
		    + fillHeight + " ," 
                    + cut, false);
	    }
	    
	    
	}
    } // End of ploughOne.    
    
    
    
    

    /**
     * Calculates stress melt and creep.
    **/
    private double forceEffect(double force) {

        double meltDistance = ((heatLoss*C*force*clastThermalConductivity)/(heatOfFusion*densityIce));
         // action distance = clastWidth as used by Weertman, 1957}}
        double creepDistance = (B*clastWidth*(force/Math.pow((clastWidth*clastIceContactHeight),n)));       
        return (meltDistance + creepDistance);

    }

    
    
    
    
    /**
     * Calculates clast movement.
    **/
    private void calcClastMovement() {
   
        // Calculate the effect on the movement distance of shear around the 
        // clast.

        moveDistance = (iceVelocity - forceEffect (forceFromResidualStrength));
        
        // Check we don't start un-ploughing. Ok, so this is possible, 
        // up is more likely to result in an infinite osilation at the end
        // of a normal ploughing run.
        
        if (moveDistance < 0) moveDistance = 0;
        movementTotal = movementTotal + moveDistance;
    
    } // End of calcClastMovement.
    
    
    
    
    
    /**
     * Calculate new clast and till variables.
    **/
    
    private void calcNewHeights() {
     
	// Calculate volume of melt fluid and associated debris per unit time.
	
        double meltVolume = ((heatLoss*C*forceFromResidualStrength*clastThermalConductivity)/(heatOfFusion*densityIce)); // no clast width in one version
        
        double volume = (meltVolume * debrisFraction)/clastWidth; 
	volumeTotal = volumeTotal + volume;
	
        // Calculate slump inflow from material pushed aside by the clast.
        
        double baseTillInFlow = (moveDistance * clastTillContactHeight); // Square pushed out of way.
        double extraTillInflow = 0.5 * moveDistance * moveDistance * Math.tan(bedSlope); // Extra triangle pushed out of the way given slope height increase.
        baseTillInFlow = baseTillInFlow * inflow;
        extraTillInflow = extraTillInflow * inflow;
        
        // Accounts for lack of till inflow and dividing by zero, but allows, at this stage, 
	// for unploughing.
        
        if (moveDistance <= 0) {

	    // Distribute the melt (no till inflow) down the whole slope.
	    // The following lines has been abandoned as unrealistic.
            //fillHeight = volume / movementTotal;
            //clastIceContactHeight = (clastIceContactHeight - fillHeight);
                        
        } else {
        
	    // Add the till inflow if appropriate.
	    
	    double tempVolume = volume + baseTillInFlow + extraTillInflow;
	    
	    // Calculate how far a sediment ramp of this volume would extend.
	    
	    sedimentLength =  Math.sqrt((2 * tempVolume) / Math.tan(tillFriction));
	    
	    // Calculate the sediment height assuming complete room for the ramp.
	    
	    fillHeight = (sedimentLength * Math.tan(tillFriction));
	    
	    // If the ramp is butressed at one end by previous sediment, take the 
	    // overlap and evenly redistribute it over the slope.
	    
	    if (sedimentLength > moveDistance) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
	    
                double overLapVolume = (0.5*(sedimentLength - moveDistance)*(sedimentLength - moveDistance)*Math.tan(tillFriction));
                
                fillHeight = fillHeight + ((overLapVolume/ ((sedimentLength/(Math.cos(tillFriction)))-((sedimentLength - moveDistance)/(Math.cos(tillFriction)))) )/(Math.cos(tillFriction)));
                
	    } 

            
           
	    // Find the new ice contact height.
	    
            clastIceContactHeight = (clastHeight - fillHeight); // Note difference with moveDistance <= 0 is clast not contact height.

//System.out.println(sedimentLength + " " + fillHeight + " " + clastHeight + " " + clastIceContactHeight);

            meltFillHeight = volume/moveDistance;
	    inflowFillHeight = (baseTillInFlow + extraTillInflow)/moveDistance;
        } // End of if clast has moved.
        
        // Calculate the till contact height using geometry.
	double coverDistance = (clastHeight/(Math.tan(bedSlope)));
	clastTillContactHeight = clastHeight - ((coverDistance-movementTotal)*(Math.tan(bedSlope)));

    } // End of calcNewHeights.
    
    
    
    
    
    /**
     * Calculates force of till on clast.
    **/
    private void calcResidualForce() {

	forceFromResidualStrength = (clastTillContactHeight*clastWidth*tillResidualStrength);
        if (forceFromResidualStrength > maxForce) maxForce = forceFromResidualStrength;
	stressTransfered = forceFromResidualStrength/(clastWidth*clastIceContactHeight);

    }

    
    
    
    
    /**
     * Lays out the GUI.
    **/   
    private void setLayout() {
        
        GridBagLayout gridBag = new GridBagLayout();	// Layout manager.
        GridBagConstraints c = new GridBagConstraints();	// Layout constraints.
        setLayout(gridBag);
	c.fill = GridBagConstraints.HORIZONTAL;
	c.ipadx = 50;
	c.ipady = 0;
	c.anchor = GridBagConstraints.WEST;
	c.insets = new Insets(5,5,5,5); 
        c.gridwidth = 1;
         
	// Column one.
        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(inflowText, c);
        add(inflowText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(inflowLabel, c);
        add(inflowLabel);
	
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(clastWidthText, c);
        add(clastWidthText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(clastWidthLabel, c);
        add(clastWidthLabel);
 
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(clastHeightText, c);
        add(clastHeightText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(clastHeightLabel, c);
        add(clastHeightLabel);
         
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(clastThermalText, c);
        add(clastThermalText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(clastThermalLabel, c);
        add(clastThermalLabel);
                                
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(bedSlopeText, c);
        add(bedSlopeText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(bedSlopeLabel, c);
        add(bedSlopeLabel);
                                                                
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(debrisFractionText, c);
        add(debrisFractionText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(debrisFractionLabel, c);
        add(debrisFractionLabel);
        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(tillFrictionText, c);
        add(tillFrictionText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(tillFrictionLabel, c);
        add(tillFrictionLabel);
        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(tillResidualText, c);
        add(tillResidualText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(tillResidualLabel, c);
        add(tillResidualLabel);
                                        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(iceVelocityText, c);
        add(iceVelocityText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 1;
        gridBag.setConstraints(iceVelocityLabel, c);
        add(iceVelocityLabel);

	
	// Column two.
	
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = 0;
        c.gridwidth = 2;
        gridBag.setConstraints(helpLabel, c);
	helpLabel.setVisible(true);
        add(helpLabel);
        
        c.gridwidth = 1;
        
 
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(velocityStartText, c);
	velocityStartText.setVisible(false);
        add(velocityStartText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(velocityStartLabel, c);
	velocityStartLabel.setVisible(false);
        add(velocityStartLabel);
        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(velocityEndText, c);
	velocityEndText.setVisible(false);
        add(velocityEndText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(velocityEndLabel, c);
	velocityEndLabel.setVisible(false);
        add(velocityEndLabel);
         
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(velocityStepText, c);
	velocityStepText.setVisible(false);
        add(velocityStepText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(velocityStepLabel, c);
	velocityStepLabel.setVisible(false);
        add(velocityStepLabel);
                                
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(strengthStartText, c);
	strengthStartText.setVisible(false);
        add(strengthStartText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(strengthStartLabel, c);
	strengthStartLabel.setVisible(false);
        add(strengthStartLabel);
                                                                
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(strengthEndText, c);
	strengthEndText.setVisible(false);
        add(strengthEndText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(strengthEndLabel, c);
	strengthEndLabel.setVisible(false);
        add(strengthEndLabel);
        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(strengthStepText, c);
	strengthStepText.setVisible(false);
        add(strengthStepText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(strengthStepLabel, c);
	strengthStepLabel.setVisible(false);
        add(strengthStepLabel);
        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(distanceStartText, c);
	distanceStartText.setVisible(false);
        add(distanceStartText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(distanceStartLabel, c);
	distanceStartLabel.setVisible(false);
        add(distanceStartLabel);
                                        
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 2;
	c.gridy = c.gridy + 1;
        gridBag.setConstraints(distanceEndText, c);
	distanceEndText.setVisible(false);
        add(distanceEndText);
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 3;
        gridBag.setConstraints(distanceEndLabel, c);
	distanceEndLabel.setVisible(false);
        add(distanceEndLabel);
	
	
	// Buttons and output area.
	
        c.weightx = 1;
	c.weighty = 0;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        c.fill = GridBagConstraints.NONE;
        modeButton.setSize(100,20);
        gridBag.setConstraints(modeButton, c);
        modeButton.addActionListener(this);
        add(modeButton);
        c.weightx = 0;
	c.weighty = 0;
        c.gridx = 1;
        calculateButton.setSize(100,20);
        gridBag.setConstraints(calculateButton, c);
        calculateButton.addActionListener(this);
        add(calculateButton);
        c.gridx = 2;
        clearButton.setSize(100,20);
        gridBag.setConstraints(clearButton, c);
        clearButton.addActionListener(this);
        add(clearButton);
        
        // If we're an app, let them pick an output file if they want.
        
	if (appletMode == false) {
            c.weightx = 0;
	    c.weighty = 0;
	    c.gridx = 3;
            fileButton.setSize(100,20);
	    gridBag.setConstraints(fileButton, c);
	    fileButton.addActionListener(this);
	    add(fileButton);
	}
	
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
	c.weighty = 1;
        c.gridx = 0;
	c.gridy = c.gridy + 1;
        c.gridwidth = 4;
	c.gridheight = 1;
        gridBag.setConstraints(output, c);
        add(output);
   

        
    } // End of setLayout.
    
    
    private void switchGui(boolean search) {
        
        if (search == true) {
	    velocityStartLabel.setVisible(true);
	    velocityStartText.setVisible(true);
	    velocityEndLabel.setVisible(true);
	    velocityEndText.setVisible(true);
	    velocityStepLabel.setVisible(true);
	    velocityStepText.setVisible(true);	 
	    strengthStartLabel.setVisible(true);
	    strengthStartText.setVisible(true);
	    strengthEndLabel.setVisible(true);
	    strengthEndText.setVisible(true);
	    strengthStepLabel.setVisible(true);
	    strengthStepText.setVisible(true);	
	    distanceStartLabel.setVisible(true);
	    distanceStartText.setVisible(true);
	    distanceEndLabel.setVisible(true);
	    distanceEndText.setVisible(true);
	    
	    iceVelocityLabel.setVisible(false);
	    iceVelocityText.setVisible(false);
	    tillResidualLabel.setVisible(false);
	    tillResidualText.setVisible(false);
	    
            helpLabel.setText("This mode calculates velocity / strengths for a geometry");
            
	    validate();
        } else {
            //set for single
	    velocityStartLabel.setVisible(false);
	    velocityStartText.setVisible(false);
	    velocityEndLabel.setVisible(false);
	    velocityEndText.setVisible(false);
	    velocityStepLabel.setVisible(false);
	    velocityStepText.setVisible(false);	 
	    strengthStartLabel.setVisible(false);
	    strengthStartText.setVisible(false);
	    strengthEndLabel.setVisible(false);
	    strengthEndText.setVisible(false);
	    strengthStepLabel.setVisible(false);
	    strengthStepText.setVisible(false);	
	    distanceStartLabel.setVisible(false);
	    distanceStartText.setVisible(false);
	    distanceEndLabel.setVisible(false);
	    distanceEndText.setVisible(false);
	    
	    iceVelocityLabel.setVisible(true);
	    iceVelocityText.setVisible(true);
	    tillResidualLabel.setVisible(true);
	    tillResidualText.setVisible(true);
            
            helpLabel.setText("This mode calculates a single plough event's geometry");
            
	    validate();
	}
        
    } // End of switchGui.
    
    
    
    
    
    /**
     * Sets variable values from the GUI.
    **/
    private void getValues() {
        
        inflow = getDouble(inflowText, inflow);
        if (inflow < 0) inflow = 0;
        else inflow = inflow / 100; // Convert percentage to fraction.
        clastWidth = getDouble(clastWidthText, clastWidth);
        clastHeight = getDouble(clastHeightText, clastHeight);
        clastThermalConductivity = getDouble(clastThermalText, clastThermalConductivity);
        tillFriction = getDouble(tillFrictionText, tillFriction);
        bedSlope = getDouble(bedSlopeText, bedSlope);
        debrisFraction = getDouble(debrisFractionText, debrisFraction);
        iceVelocity = getDouble(iceVelocityText, iceVelocity);
        tillResidualStrength = getDouble(tillResidualText, tillResidualStrength);
        velocityStart = getDouble(velocityStartText, velocityStart);
        velocityEnd = getDouble(velocityEndText, velocityEnd);
        velocityStep = (int)getDouble(velocityStepText, (double)velocityStep);
        if (velocityStep == 0) velocityStep = 1;
        if (velocityStep < 1) velocityStep = (velocityStep * -1);
        if (velocityStart > velocityEnd) {
            double temp = velocityEnd;
            velocityEnd = velocityStart;
            velocityStart = temp;
        }
        strengthStart = getDouble(strengthStartText, strengthStart);
        strengthEnd = getDouble(strengthEndText, strengthEnd);
        strengthStep = (int)getDouble(strengthStepText,  (double)strengthStep);
        if (strengthStep == 0) strengthStep = 1;
        if (strengthStep < 1) strengthStep = (strengthStep * -1);
        if (strengthStart > strengthEnd) {
            double temp = strengthEnd;
            strengthEnd = strengthStart;
            strengthStart = temp;
        }
        distanceStart = getDouble(distanceStartText, strengthStart);
        distanceEnd = getDouble(distanceEndText, strengthEnd);
        if (distanceStart > distanceEnd) {
            double temp = distanceEnd;
            distanceEnd = distanceStart;
            distanceStart = temp;
        }

	
    } // End of getValues.
    
    
    
    
    
    /**
     * Reads a variable from the GUI and returns a default if it doesn't 
     * find an appropriate number.
    **/
    private double getDouble(TextField box, double defaultValue) {
     
        // Here, "box" is a GUI component.
        
        String tempString = box.getText();
        double tempDouble = 0;
       
        if (tempString != null) {
            try {
                tempDouble = (new Double(tempString)).doubleValue();
            } catch (NumberFormatException nfe) {
                outputString("The value " + tempString + " you've given in one " +
                    "of the boxes is not a number: using default.", true);
                return defaultValue;
            }
            return tempDouble;
        } else {
            outputString("One of your values has not been set: using default.", true);
            return defaultValue;
        }
    } // End of getDouble.
    
    
// End of Plough class.    
}
