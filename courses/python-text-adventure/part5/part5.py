# This game needs a map. This is a text file containing comma 
# separated numbers. The character moves around the map, and 
# when they find a number, a function is called.
# Your map should have zeros all around the outside, and one 
# number should be a starting number. Zeros are areas the 
# character can't go (see below for more info).

# Various variables
map_data = []               # Going to hold the map
map_file = "map.csv"        # Map file to read in. 
game_not_over = True        # We'll set this to false to end the game.
current_x = 0               # Where the player is across the map.
current_y = 0               # Where the player is up and down the map.


# List what the numbers in your map mean here. Use any whole numbers 
# but make sure they aren't repeated.
start_number = 2            # Location the player starts.
end_number = 3              # Location of route out of the game.
monster_number = 4          # Number for locations where there are monsters.



def start():
    # This is the main game function. 
    
    read_map()
    find_start_position()   # Sets the player's x and y values to start point.
    
    print("You wake to find yourself in a labyrinth. Can you find " +
           "your way out without being eaten by the minotaur?")
    
    while (game_not_over):
        move()              # Allow the player to move to new x and y.
        
        # Here, if the current location in the map has a 
        # number you've made a variable for above, direct 
        # the program to a suitable function. Here's two examples:
        if (map_data[current_y][current_x] == monster_number):
            monster()
            
        if (map_data[current_y][current_x] == end_number):
            end_game()          
            
    print("That's all folks!")
    
    
    
    
    
def read_map():
    # Reads the map file and turns it into a 2 dimensional list 
    # called map_data. See function below for what "global" means.
    import csv
    global mapdata # Not actually needed, but for complicated reasons.
    
    f = open(map_file, newline='') 
    reader = csv.reader(f, quoting=csv.QUOTE_NONNUMERIC)
    for row in reader:				
        map_line = []
        for value in row:				
            map_line.append(int(value)) 	
        map_data.append(map_line)

    f.close() 	


def find_start_position():
    # Finds the start position in the map.
    # Note that because we change the variables current_y and current_x
    # which we've made at the top, we need to say we want these 
    # using the "global" keyword. If we just wanted their value, we 
    # wouldn't need to do this.
    
    global current_y
    global current_x
    
    for y in range(len(map_data)):
        for x in range(len(map_data[y])):
            if map_data[y][x] == start_number:
                current_x = x
                current_y = y
                break;


def move():
    # Checks where the character can move, and moves the character
    # by changing current_y and current_x. These are used in the main 
    # program above to see what's at the current map_data location.
    global current_y
    global current_x
    
    # First, check what options are open to the player depending on which 
    # areas around it aren't zeros. Make a text set of choices for the user.
    move_text = ""
    move_options = ""
    if (map_data[current_y - 1][current_x] > 0):
        move_text += ("North, ")
        move_options += ("[N]")
    if (map_data[current_y + 1][current_x] > 0):
        move_text += ("South, ")
        move_options += ("[S]")
    if (map_data[current_y][current_x + 1] > 0):
        move_text += ("East, ")
        move_options += ("[E]")
    if (map_data[current_y][current_x - 1] > 0):
        move_text += ("West, ")
        move_options += ("[W]")

    direction = "X"
    while direction not in move_options:
        # The next line prints off the directions but cuts off the last 
        # comma and adds a full stop.
        print("From here, you can move " + move_text[0:len(move_text) - 2] + ".")
        
        # Find out which direction the player wants to go.
        direction = input("Which way do you want to go? " + move_options)
        # Convert it to upper case if the user has typed it lowercase.
        direction = direction.upper() 
        
    # Move the player by changing the player's current x and y.
    if (direction == "N"):
        current_y = current_y - 1
    if (direction == "S"):
        current_y = current_y + 1
    if (direction == "E"):
        current_x = current_x + 1
    if (direction == "W"):
        current_x = current_x - 1
    print(current_y, current_x)

# Below here, but your functions for different numbers on the map.

def monster():
    global game_not_over
    print("Oh no, you've been eaten!")
    game_not_over = False


def end_game():
    global game_not_over
    print("Hurrah! You've found your way out!")
    game_not_over = False


# The next lines just start everything going.
if __name__ == "__main__":
	start()

