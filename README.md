# Mancala
Mancala is an ancient 2-player board game implemented in Java using the MVC design pattern.

## Rules
The board consists of two rows of pits, each. Three pieces of stones are placed in each of the 12 holes. Each player has a large store called Mancala to the right side of the board. One player starts the game by picking up all of the stones in any one of his own pits. Moving counter-clock wise, the player places one in each pit starting with the next pit until the stones run out. If you run into your own Mancala, place one stone in it. If there are more stones to go past your own Mancala, continue placing them into the opponent's pits. However, skip your opponent's Mancala. If the last stone you drop is your own Mancala, you get a free turn. If the last stone you drop is in an empty pit on your side, you get to take that stone and all of your opponents stones that are in the opposite pit. Place all captured stones in your own Mancala. The game ends when all six pits on one side of the Mancala board are empty. The player who still has stones on his side of the board when the game ends captures all of those pieces and place them in his Mancala. The player who has the most stones in his Mancala wins.

## Architecture
**Model:** `MancalaBoard` holds the raw 14-pit int array. `MancalaModel` owns a `MancalaBoard` and contains all game logic (move distribution, capture rule, free-turn rule, end-game detection). `UndoManager` stores board snapshots and enforces the 3-undo-per-turn limit.

**View/Controller:** `MancalaView` implements `MancalaListener` to receive change notifications from the model and repaint the board. It doubles as the controller by handling pit click events and forwarding them to the model.

**Observer:** `MancalaListener` is the interface that decouples the model from the view. The model calls `boardChanged()` on all registered listeners when state changes.

**Strategy:** `BoardStyle` is the interface for pluggable visual styles. `WoodStyle` and `MetalStyle` implement it. `StyleSelectionScreen` lets players pick a style before the game starts.

**Entry Point:** `MancalaTest` wires the model and view together and launches the application.