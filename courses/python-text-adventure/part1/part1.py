import random
import time 
import sys



def fight(monster_strength, monster_name):
    global player_strength
    
    count = 0
    while player_strength > 0 and monster_strength > 0: 
            count = count + 1            
            print("You FIGHT the", monster_name)
            print("SMASH!")
            time.sleep(1)
            print("BASH!")
            time.sleep(1)
            print("Oo!")
            time.sleep(1)
            random_number = random.randint(0,10)

            if player_strength > random_number:
                print("You won! Gain one strength!")
                player_strength = player_strength + 1
                monster_strength = monster_strength - 1
            else:
                print("You lost! Lose one strength!")
                player_strength = player_strength - 1
                monster_strength = monster_strength + 1
            if player_strength < 1:
                print("Oh no! You died!")
                sys.exit()
            else:
                answer = input("Do you want to run away? [Y][N]")
                if answer == "Y" or answer == "y":
                    break
    return count


player_strength = 3

name = input("Enter your name to start: ")
print("Welcome " + name)

print("After many days wandering the forest, you come across " + 
"the entrance to what looks like a cave or mine - " + 
"can this be what you are looking for?") 
print("You've been told the golden crown lies in the depths " + 
"of the old mines, guarded by a terrible beast.")


decision = input("Do you want to enter? [Y,N]: ")
if decision == "N" or decision == "n":
    print("Your quest is at an end, goodbye!")
elif decision == "Y" or decision == "y":
    print("You enter the mines.")
    print("The way is lit by candles on the wall.")
    
    
    
    decision = input("You come to a junction. You can turn left or right [L,R]: ")
    if decision == "L" or decision == "l":
        print("You turn left and see a terrible ogre and a troll!")
        count = fight(2, "Ogre")
        print("You killed it in",count,"rounds")
        count = fight(3, "Troll")
        print("You killed it in",count,"rounds")
    
    
    
    
    
    
    
    
    
    
    
else:
    print("A hole opens up below you, and you plunge into darkness. Goodbye!")
