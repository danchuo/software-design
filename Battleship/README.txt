  To start the game, you need to pass the necessary command line parameters in this format:
<rows> <columns> <submarines> <destroyers> <cruisers> <battleships> <carriers> <torpedoes> <is recovering mode on>,
where <rows> and <columns> can be in the range [3;45], then the number of ships of each type,
then the number of torpedoes, which does not exceed the total number of ships,
then <is recovering mode on> where 1 means "true", 0 means "false".
For example, this command can easily run the program:
java -jar Game.jar 3 3 1 0 0 0 0 1 0

  If any of the parameters is incorrect, the program will prompt you to enter it into the console yourself.
If the program is unable to arrange the ships, it will prompt you to re-enter the parameters,
hoping that you will enter a little less ships than last time.

  Here is a small description of the game:
Battleship is a simple game. You need to sink all the ships that the computer will place.
The game implements torpedo firing and ship recovery modes.
A few notations: the symbol [o] means the ocean cell,
the symbol [x] means an ocean cell without a ship, which was hit by a rocket,
the symbol [h] means an ocean cell with a ship hit by a rocket,
the symbol [s] means an ocean cell with a completely sunk ship.
Also, if you have sunk the ship, then all the surrounding cells will be marked with the symbol [x] for your convenience, but this does not mean that you cannot hit them.
The score of the game consists of the ratio of spent rockets to the length of all ships,
respectively, 1.0 is the best score, where rockets hit all ships without misses (with the torpedo mod, the score may be less than one).