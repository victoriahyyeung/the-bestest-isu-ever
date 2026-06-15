import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
public class Ticket {
	private Customer customer;
	private Order order;
	private int x,y;
	private int width=100;
	private int height=100;
	private boolean isSelected=false;//FOR DRAGGING
	private HashMap<String, Image>icons;


	// Description: Constructor for Ticket class
	// Parameters: the customer associated with this ticket,  x coordinate on ticket board,  y coordinate on ticket board, mapping of ingredient names to icon images
	// Return: none
	public Ticket(Customer customer,int boardX,int boardY, HashMap<String,Image>icons) {
		this.customer=customer;
		this.order=customer.getOrder();
		this.x=boardX;
		this.y=boardY;//where on tix board
		this.icons=icons;
	}

	// Description: Returns the rectangular boundaries of the ticket for collision detection
		// Parameters: None
		// Return: Rectangle boundary of the ticket
	public Rectangle getBorders() {//to check cliciking
		return new Rectangle(x,y,width,height);
	}
	
	// Description: Checks if a point (mouse click) is within the ticket's boundaries
		// Parameters: mouse x coordinate,  mouse y coordinate
		// Return: true if point is inside ticket, false otherwise
	public boolean contains (int mx,int my) {
		return getBorders().contains(mx,my);
	}
	
	
	//GETTERS
	// Description: Returns the order associated with this ticket
		// Parameters: None
		// Return: the customer's order
	public Order getOrder() {
		return order;
	}
	
	// Description: Returns the customer associated with this ticket
		// Parameters: None
		// Return: the customer who placed the order
	public Customer getCustomer() {
		return customer;
	}
	//SETTERS
	
	// Description: Sets the ticket's position on screen (used when dragging)
		// Parameters: new x coordinate,  new y coordinate
		// Return: void
	public void setPosition(int newX,int newY) {
		this.x=newX;
		this.y=newY;
	}
	
	// Description: Sets whether the ticket is currently selected/dragged (changes color to pink)
		// Parameters: true if selected, false if deselected
		// Return: void
	public void setSelected(boolean s) {//from main
		isSelected=s;
	}

	//DRAW
	
	// Description: Draws the ticket with white background, order text, and ingredient icons
		// Parameters: graphics object for drawing
		// Return: void
	public void draw(Graphics g) {
		Graphics2D d=(Graphics2D) g;
		d.setColor(Color.WHITE);
		d.fill(new RoundRectangle2D.Float(x,y,width,height,15,15));
		d.setColor(Color.BLACK);
		d.draw(new RoundRectangle2D.Float(x,y,width,height,15,15));
//
//		if (isSelected) {//WOWOWOWOW CHANGES COLOR WHEN SELECTED!!!
//			d.setColor(Color.PINK);
//			d.fill(new RoundRectangle2D.Float(x,y,width,height,15,15));
//		}

		d.setFont(new Font("Times New Roman",Font.BOLD,10));
		d.setColor(Color.BLACK);
		String orderText="Order: "+order.getDrinks().size()+ " drink(s)";
		d.drawString(orderText,x+10,y+10);

		int rowY=y+30;//each row
		int iconSize=20;
		int drinkInd=0;
		int totalDrinks=order.getDrinks().size();
		for(int row=0;row<6;row++) {
			if(drinkInd<totalDrinks) {
				Drink drink=order.getDrinks().get(drinkInd);
				int currentX=x+10;
				//FRUITS
				for(String fruit:drink.getFruits()) {
					Image img=icons.get(fruit);
					if(img!=null) {
						d.drawImage(img, currentX, rowY, iconSize, iconSize, null);
						currentX+=iconSize+5;
					}
				}
				//TOPPINGS
				for(String topping:drink.getToppings()) {
					Image img=icons.get(topping);
					if (img!=null) {
						d.drawImage(img, currentX, rowY, iconSize, iconSize, null);
						currentX+=iconSize+5;
					}
				}
				drinkInd++;
			}
			rowY+=22;
		}


	}

}
