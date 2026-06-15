import java.awt.*;
import java.io.File;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

public class Customer {
	// boolean to track whether customer sound was played
	private boolean soundPlayed = false;
	private int x,y; // coordinates of customer
	private Image avatar; // avatar of customer
	private Order order; // customer order
	private int patienceMax=45; // max patience
	private int currentPatience;// patient stored as int
	private boolean isActive=true; // tracks if customer is still active
	private String customerType;//orangeCat
	private String currentEmotion;//happy, neutral, impatience, angry for now
	private String state;//spawn, ordering, waiting (to be served), served, leaving
	private int targetX, targetY;//where customer is walking towards 
	private boolean moving;//moving or not (t/f)
	private static final int SPEED=2;//2px/50ms (per frame)
	private int waitingSpotIndex=-1;//FOR AFTEr they order and then they are waitng to be served

	private int bubbleFrames;//bubble will show when >0;
	private Clip thinking; // sound of customer



	private static final String[]TYPES= {"orangeCat"};
	private static final String[] FRUITS = {"mango", "lychee"};
	private static final String[] TOPPINGS = {"pearl", "pudding"};


	// Description: Constructor for Customer class, creates a new customer
	// Parameters: customer's starting x coordinate, starting y coordinate, HashMap<String,HashMap of customer image mappings,ordering station x coordinate, ordering station y coordinate, audio clip for customer sound effect
	// Return: none
	public Customer(int startX, int startY, HashMap<String,HashMap<String,Image>>images, int orderX, int orderY, Clip thinkingSound) {

		this.thinking = thinkingSound; 
		this.x=startX;
		this.y=startY;
		Random rand=new Random();
		this.customerType=TYPES[rand.nextInt(TYPES.length)];
		this.currentEmotion="neutral"; // neutral expression as default
		this.avatar=images.get(customerType).get(currentEmotion);
		this.state="SPAWN";
		setTarget(orderX,orderY); // set location that customer approaches
		// Randomly generate number of drinks (1–3)
		int numDrinks=(int)(Math.random()*3)+1;
		ArrayList<Drink> drinks=new ArrayList<>();
		for(int i=0; i<numDrinks;i++) {

			// FRUIT GENERATE
			ArrayList<String> fruits =new ArrayList<>();
			int fruitType = (int) (Math.random() * 2); // Randomly choose fruit type
			String fruit;
			if (fruitType == 0) {
				fruit="mango";
			}
			else {
				fruit="lychee";
			}

			fruits.add(fruit);

			//TOPPING GENERATE
			int toppingCount=(int)(Math.random()*3); // Randomly generate 0–2 toppings
			ArrayList<String>toppings=new ArrayList<>();
			for (int t=0; t<toppingCount;t++) {
				String topping;
				if (Math.random()<0.5) {
					topping="pearl";
				}
				else {
					topping="pudding";
				}
				// Prevent duplicate toppings
				if(!toppings.contains(topping)) {
					toppings.add(topping);
				}
			}
			drinks.add(new Drink(fruits, toppings));
		}
		order=new Order(drinks);
		currentPatience=patienceMax;

	}

	// Description: Decreases the customer's patience meter while they are waiting for their order
	// Parameters: None
	// Return: void
	public void decreasePatience() {
		if (state==null||!state.equals("WAITING"))
			return;

		else if (!isActive) { // stops method if customer isn't even active
			return;
		}
		if(currentPatience>0) {
			currentPatience--; // decreases patience steadily
		}
		if (currentPatience<=0) {
			isActive=false; // sets as inactive if patience runs out
		}
	}

	// Description: Updates the customer's facial expression based on remaining patience level
	// Parameters: Hashmap that maps customer emotions to images
	// Return: void
	public void updateEmotion(HashMap<String, HashMap<String,Image>>images) {
		// change emote based on patience meter

		if(state==null||!state.equals("WAITING"))//only can update emos when waiting 2 b served
			return;

		String newEmo;
		if(state.equals("SERVED")) {
			newEmo="happy";
		}
		else if(currentPatience>patienceMax/2)
			newEmo="neutral";
		else if(currentPatience>patienceMax*0.3)
			newEmo="impatient";

		else
			newEmo="angry";

		if(!(newEmo.equals(currentEmotion))) {
			currentEmotion=newEmo;
			this.avatar=images.get(customerType).get(currentEmotion);
		}
	}


	// Description: Updates customer's speech bubble
	// Parameters: None
	// Return: void
	public void updateBubble() {
		if (bubbleFrames>0)
			bubbleFrames--;//evrey frame do this
	}

	// Description: Starts the ordering bubble animation and plays customer sound
	// Parameters: None
	// Return: void
	public void startBubble() {
		soundPlayed = false;
		bubbleFrames=30; //for 1.5 seconds its 30 frames, each 50 ms.
	}

	// Description: Checks if the ordering bubble should be visible
	// Parameters: None
	// Return:  true if bubble frames remain, false otherwise
	public boolean isBubbleVisible() {
		return bubbleFrames>0;
	}


	// Description: Checks if the customer has become angry and left due to patience running out
	// Parameters: None
	// Return: true if customer is angry/inactive, false otherwise
	public boolean isAngry() {
		return !isActive||currentPatience<=0;
	}

	// Description: Checks if the customer has finished moving to their target destination
	// Parameters: None
	// Return: true if arrived, false if still moving
	public boolean hasArrived() {//boolean to easilycheck if still moving/arrived
		return !moving; //false moving = true arrived
	}


	// Description: Updates customer position by moving towards target coordinates at constant speed
	// Parameters: None
	// Return: void
	public void updateMovement() {
		if(!moving)
			return; // Stop method if customer is not moving

		int dx=targetX-x;// horizontal distance to target
		int dy=targetY-y;// vertical distance to target
		if(Math.abs(dx)<=SPEED&&Math.abs(dy)<=SPEED) {//for when its like pretty close, dont waste more frames on moving so just snap to the target
			x=targetX;
			y=targetY;
			moving=false;
			return;
		}

		// Move horizontally toward target
		if(dx!=0) {
			// move right
			if(dx>0) {
				if (dx<SPEED) 
					x=targetX;// snap if remaining distance is small
				else
					x+=SPEED;// move right at constant speed
			}

			// Move left
			else {
				if (-dx<SPEED) 
					x=targetX; // snap if remaining distance is small

				else
					x-=SPEED;// move left at constant speed
			}
		}

		// Move vertically toward target
		if(dy!=0) {
			// Move downward
			if(dy>0) {//D
				if(dy<SPEED) // snap if remaining distance is small
					y=targetY;
				else
					y+=SPEED;  // move downward
			}
			// move upward
			else {
				if (-dy<SPEED)// snap if remaining distance is small
					y=targetY;
				else
					y-=SPEED;// move upward
			}
		}


	}


	//getters

	// Description: Returns the customer's order
	// Parameters: None
	// Return: the customer's order 
	public Order getOrder() {
		return order;
	}

	// Description: Returns the customer's current x coordinate
	// Parameters: None
	// Return:  x position
	public int getX() {
		return x;
	}

	// Description: Returns the customer's current y coordinate
	// Parameters: None
	// Return:  y position
	public int getY() {
		return y;
	}

	// Description: Returns the customer's patience as a percentage of maximum
	// Parameters: None
	// Return: patience percentage (0%-100%)
	public int getPatiencePercent() {
		return(currentPatience*100)/patienceMax;
	}

	// Description: Returns the customer's current state ("SPAWN", "ORDERING", "WAITING", "SERVED", "LEAVING")
	// Parameters: None
	// Return: String of current state
	public String getState() {
		return state;
	}

	// Description: Returns the customer's assigned waiting spot index after ordering
	// Parameters: None
	// Return: waiting spot index, or -1 if none assigned
	public int getWaitingSpotIndex() {
		return waitingSpotIndex;
	}

	//setters

	// Description: Sets the customer's target destination for movement
	// Parameters:  target x coordinate, target y coordinate
	// Return: void
	public void setTarget(int tx, int ty) {
		this.targetX=tx;
		this.targetY=ty;
		moving=true;
	}


	// Description: Sets the customer's current state
	// Parameters: new state ("SPAWN", "ORDERING", "WAITING", "SERVED", "LEAVING")
	// Return: void
	public void setState(String s) {
		this.state=s;
	}

	//<<<<<<< HEAD
	// Description: Sets the customer's waiting spot index after ordering
	// Parameters: index of waiting spot
	// Return: void
	//=======
	//>>>>>>> branch 'main' of https://github.com/victoriahyyeung/best-isu-ever.git
	public void setWaitingSpotIndex(int i) {
		this.waitingSpotIndex=i;
	}

	// Description: Draws the customer avatar, patience bar, and ordering bubble with sound
	// Parameters: graphics object for drawing
	// Return: void
	public void draw(Graphics g) {
		g.drawImage(avatar, x, y, null);
		// drawing patience bar
		int barWidth=80;
		int barHeight=8;
		int fillWidth=barWidth*getPatiencePercent()/100;
		g.setColor(Color.RED);
		g.fillRect(x, y-12,  fillWidth, barHeight);
		g.setColor(Color.BLACK);
		g.drawRect(x, y-12, barWidth, barHeight);
		if (bubbleFrames>0) {
			// plays customer sound
			if (!soundPlayed) {
				thinking.setFramePosition(0);
				thinking.start();
				soundPlayed = true;
			}
			// draw speech bubble for customer
			g.setColor(Color.WHITE);
			g.fillRoundRect(x-20, y-40, 50, 30, 10, 10);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x-20, y-40, 60, 30, 10, 10);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString("Ordering...",x-15,y-20);
		}
	}
}
