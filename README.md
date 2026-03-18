# Fun Cat 2D Platformer Game with Integrated Level Editor

This project is a tile-based **2D platformer game** developed in **Java using the Processing framework**.
The project focuses on demonstrating core game development concepts, including **player physics, collision logic, camera view, player animation, and level editing tools**.

The game revolves around a playable cat character that must navigate through levels, avoiding red blocks (collision with them restarts the current level), in order to reach the exit.
In addition to gameplay, the project includes a **fully functional level editor** that allows custom levels to be created by the player/developer and saved as CSV files.

---

## Project Overview

This project was built as a learning exercise in **game programming and object-oriented design**.

Key goals of the project:

* Implement a structured **game architecture with multiple modes**
* Implement **level loading from CSV files**
* Create a **tile-based world system**
* Develop **player movement with gravity**
* Develop **colission detection**
* Implement a **camera system** that follows the player
* Provide a **built-in level editor** for convenient map design

---

## Features

* **Tile-based level system** with external level files
* **Animated player character**
* **Physics-based movement** with gravity and jumping
* **Collision detection** with different tile types
* **Multiple level progression**
* **Camera system** that follows the player
* **Integrated level editor** with tile palette and painting tools
* **Level saving system** that exports maps to CSV files
* **Win screen** after completing all levels

---

## Controls

### Gameplay Mode

| Key           | Action             |
| ------------- | ------------------ |
| **A / D**     | Move left / right  |
| **W / Space** | Jump               |
| **N**         | Skip to next level |
| **R**         | Restart game       |
| **E**         | Toggle editor mode |

### Level Editor

| Input           | Action                   |
| --------------- | ------------------------ |
| **Left Mouse**  | Place tile               |
| **Right Mouse** | Remove tile              |
| **Mouse Drag**  | Paint tiles continuously |
| **W A S D**     | Move camera              |
| **↑ / ↓**       | Scroll tile palette      |
| **+ / -**       | Zoom in / out            |
| **P**           | Save level               |

---

## Architecture

The game is structured using a **mode-based architecture**, separating gameplay and editor functionality.

Core components include:

* **GameApp** – main application loop and mode switching 
* **PlayMode** – gameplay logic, physics, and collision handling 
* **EditorMode** – in-game level editor and tile placement tools 
* **Player** – player movement, animation, and physics behavior 
* **Level** – loads level data from CSV files 
* **Tileset** – handles tile sprites and sprite slicing 

Tile behavior (solid, deadly, exit, spawn) is defined through a rule mapping system. 

---

## Level System

Levels are stored as **CSV files**, where each value represents a tile ID from the tileset.

Example:

```csv
1,1,1,1,1
1,0,0,3,1
1,4,0,0,1
1,1,1,1,1
```

Tile types include:

| ID Type  | Description             |
| -------- | ----------------------- |
| Passable | Allows player movement  |
| Solid    | Blocks player movement  |
| Deadly   | Respawns player         |
| Exit     | Completes the level     |
| Spawn    | Player start position   |

---

## Technologies Used

* **Java**
* **Processing (graphics framework)**
* **CSV-based data storage for levels**

---

## How to Run

1. Install **Processing** or include the Processing core library in your Java project.
2. Clone the repository:

```bash
git clone https://github.com/yourusername/cat-platformer-game.git
```

3. Run the main class:

```java
Main.java
```

This launches the game window.

---

## Assets

Cat sprite:
Gray Cat Asset Pack  
Author: Krystsina Staselovich (skristi)  
Source: https://opengameart.org/content/gray-cat-asset-pack  
License: CC0 (Public Domain)


Dungeon tiles:
Simple Dungeon Tileset  
Author: James Bell (stealthix)
Source: https://opengameart.org/content/simple-dungeon-tileset  
License: CC0 (Public Domain)

---

## Author

Developed as a personal programming project while studying **Software Engineering**, focusing on building foundational **game development and software engineering skills**.
