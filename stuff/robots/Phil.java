package aje;
import robocode.*;
import java.awt.Color;
import java.util.StringTokenizer;

/**
 * Phil - you feelin' tasty or wot?
 */
public class Phil extends TeamRobot
{
	
	double arenaHeight = 0;
	double arenaWidth = 0;
	double maxDistance = 0;
	boolean wallRighting = false;
	boolean right = true;
	double heightPosition = 0;
	double widthPosition = 0;
	
	// Needs - gun righting and target system.
	
	/**
	 * run: Phil's default behavior.
	 * Come on then - if you think your 'ard enough.
	 */
	public void run() {
	
		arenaWidth = getBattleFieldWidth();
		arenaHeight = getBattleFieldHeight();
		maxDistance = (arenaWidth > arenaHeight)? arenaWidth : arenaHeight;
		
		setColors(new Color(153,204,255),new Color(153,204,255),new Color(102,153,204));	
		
		turnLeft(getHeading());

		addCustomEvent(
			new Condition("UpperLeftLimit") {
				public boolean test() {
			  		return ((getY() > (arenaHeight - 50)) ||  (getX() > (arenaWidth - 50)));
				};
			}
		);

        addCustomEvent(
			new Condition("LowerRightLimit") {
		  		public boolean test() {
			  		return ((getY() < 50) ||  (getX() < 50));
				};
			}
		);			
		
		
		while(true) {
			waitFor(new TurnCompleteCondition(this));
               	if (wallRighting == false) serpentine();

			if (getEnergy() < 30) {
				try {
					sendMessage("aje.Grant", new String(getX() + " " + getY() + " I'm croakin' 'ere brov."));
				} catch (java.io.IOException ioe) {
					out.println("Bravva! Bravva! Can y' ere me bravva!?");
				}
			
			}
			
		}
	}
	
	/**
	 * onScannedRobot: Born slippy mate.
	**/
	private void serpentine() {
     	if (right) {
        	setTurnLeft(100);
            setAhead(500);
        } else {
        	setTurnRight(100);
            setAhead(500);
        }
        right = ! right;

 	}





	/**
	 * onScannedRobot: You lookin' at me bird?
	**/
	public void onScannedRobot(ScannedRobotEvent e) {

		// If it ain't y'bruv, give it some stick.
		if(e.getName().equals("aje.Grant") == false) fire(1);

	}												





	/**
	 * onScannedRobot: What to do when you hit a wall.
	**/
	public void onHitWall(HitWallEvent hwe) {
		wallRighting = true;
		stop();	
		back(60);
		turnRight(hwe.getBearing()-130 + (Math.random() * 30));
		if (hwe.getBearing() > 0)
			right = true;
		else 
			right = false;
		wallRighting = false;
		serpentine();
	}





	/**
	 * onScannedRobot: What to do when you get a bit a' bovva.
	**/
	public void onHitRobot(HitRobotEvent hre) {
		
		if(hre.getName().equals("aje.Grant") == false) {
			
			// We get a bit 'andy 'ere.
			turnGunRight(relativeAngle(hre.getBearing() + (getHeading() - getGunHeading())));
			fire(10);	
				
		}			

		// Then leggit.

		stop();	
		back(100);
		setTurnLeft(5);
		setBack(100);
		serpentine();
		execute();
	}	
	
	


	
	/**
	 * onHitByBullet: What to do when some narks fill y' wit' lead.
	**/
	public void onHitByBullet(HitByBulletEvent hbbe) {
		if(hbbe.getName().equals("aje.Grant") == false) {
			// We're giving it back 'ere.
			turnGunRight(relativeAngle(hbbe.getBearing() + (getHeading() - getGunHeading())));
			fire(3);
			
		}			

		// But let not 'ang araand.

		stop();	
		back(100);
		setTurnLeft(5);
		setBack(100);
		serpentine();
		
		if (getEnergy() < 70) {
			try {
				sendMessage("aje.Grant", new String(getX() + " " + getY() + " I'm gettin' pasted 'ere brov."));
			} catch (java.io.IOException ioe) {
				out.println("Aww na, I'm only on me blinkin' tod!");
			}
		}	
	}		
	



	/**
	 * This one's been custom made mate. Dead tasty. 
	 * Stops muggins 'ere bashin' his bonce.
	**/
	public void onCustomEvent(CustomEvent ce) {
		wallRighting = true;
		stop();	
		back(60);
		
		if (ce.getCondition().getName() == "LowerRightLimit") {
			turnRight(140 + (Math.random() * 30));
			right = false;
		} else { 
			turnLeft(140 + (Math.random() * 30));
			right = true;
		}
		wallRighting = false;
		serpentine();
    } 	

	
	
		
				
	/**
	 * onRobotDeath: If them Berkleys hav got me bruv there'll 
	 * be 'ell t' pay.
	**/
	public void onRobotDeath(RobotDeathEvent rde) {

		// If it ain't y'bruv, give it some stick.
		if(rde.getName().equals("aje.Grant") == true) {
			out.println("Bruvvaaaaaaaahhhhh!!!!");
			for (int i = 0; i < 36; i++) {
				turnLeft(10);
				fire(3);
			}
		}

	}	
		 	
						
	
	
											
	/**
	 * 'Ere, Ma, it's Grant on the blower.
	**/															
	public void onMessageReceived(MessageEvent me)	{
		
		StringTokenizer st = new StringTokenizer((String)me.getMessage());
		String xst = st.nextToken();
		String yst = st.nextToken();
		out.println(" xst = " + xst + " yst = " + yst);
		double x = Double.parseDouble(xst);
		double y = Double.parseDouble(yst);
		double bearing = absbearing(getX(),getY(),x,y);
		double distance = getrange(getX(),getY(),x,y);
		
		turnRight(bearing);  
		
		if (distance > 150){
			ahead(distance - 150);
		} 
		
		if (distance < 100) {
				back(150 - distance);
		}
		scan();		

	}	
					
									
												
	
	
	/**
	 * F' tracking daaan me bruvva. Half-inched from...
	 * http://www.ecs.soton.ac.uk/~awo101/robocode.html 
	*/	
	public double absbearing( double x1,double y1, double x2, double y2 ) {
        double xo = x2-x1;
        double yo = y2-y1;
        double h = getrange( x1,y1, x2,y2 );
        if( xo > 0 && yo > 0 )
        {
                return Math.asin( xo / h );
        }
        if( xo > 0 && yo < 0 )
        {
                return Math.PI - Math.asin( xo / h );
        }
        if( xo < 0 && yo < 0 )
        {
                return Math.PI + Math.asin( -xo / h );
        }
        if( xo < 0 && yo > 0 )
        {
                return 2.0*Math.PI - Math.asin( -xo / h );
        }
        return 0;
	}
	public double getrange( double x1,double y1, double x2,double y2 ){
        double xo = x2-x1;
        double yo = y2-y1;
        double h = Math.sqrt( xo*xo + yo*yo );
        return h;
	}
																										
	
	
	
	
	/**
	 * F'turning the shooters on 'em. 
	 */	
	public double relativeAngle(double degrees) {
		if (degrees > -180 && degrees <= 180) return degrees;
		while (degrees <= -180)	degrees += 360;
		while (degrees > 180) degrees -= 360;
		return degrees;
	}																																		
				
	
}																																																																																																																																																																																																																																																																																