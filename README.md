# The Paintball Game — Turn-Based Strategy (Java)

A command-line, turn-based strategy game implemented in **Java** for the **Object-Oriented Programming** course.  
The game simulates teams competing on a rectangular grid by managing **bunkers**, **coins**, **player recruitment**, **movement**, and coordinated **team attacks** until only one team remains active.

This project was developed in **Spring 2024** as the **1st course project**.

**Final grade:** 17.8 / 20 *(as remembered)*

---

## 👥 Team

- **Ilia Taitsel**
- Taisiia Hlukha

---

## 🎯 Gameplay Summary

- The map is a **width × height** grid (minimum 10×10).
- The game has **2–8 teams**.  
- Each team starts by occupying one **abandoned bunker** and begins with **no players**.
- Teams take turns in creation order. On each turn, a team may perform **any number of queries**, but only **one state-changing action**:
  - `create` a player in a bunker
  - `move` a player (red players have up to 3 steps)
  - `attack` with all players in recruitment order

The game ends when only **one active team** remains (a team is active if it has at least one bunker or one player).

---

## 🧩 Core Entities

- **Game**: manages teams, bunkers, turns, and win conditions.
- **Map / Grid**: stores occupancy (empty / bunker / player / bunker+player).
- **Team**: name, list of owned bunkers, list of active players, turn order.
- **Bunker**: name, position, treasury (coins), owner (team or abandoned).
- **Player**: type (red/green/blue), team, position, and rules for movement/attack.

---

## 🪙 Economy & Recruitment

- Each bunker has a **treasury** of coins.
- At the end of every turn, **each bunker generates +1 coin**.
- Players are recruited inside a team-owned bunker if:
  - the bunker is **free** (no player inside),
  - and it has enough coins.

Player costs:
- **Red**: 4 coins
- **Green**: 2 coins
- **Blue**: 2 coins

---

## 🧍 Player Types & Combat Rules

### Movement
- **Blue & Green**: 1 move per `move` command
- **Red**: 3 moves per `move` command (each step resolved independently)

Directions: `north`, `south`, `east`, `west`  
- North decreases **y**
- South increases **y**
- East increases **x**
- West decreases **x**

### Combat (Rock–Paper–Scissors)
When moving into an opponent’s cell, a fight occurs:
- **Red beats Blue**
- **Blue beats Green**
- **Green beats Red**
- Same type: **attacker wins**

### Bunkers
- Moving onto a bunker **seizes** it for the team (abandoned or enemy-owned).
- If the bunker is occupied by an opponent, it is seized **only if the attacker wins**.

---

## ⚔️ Coordinated Team Attack

The `attack` command triggers all players of the current team to attack in **recruitment order**, without moving.

Attack patterns:
- **Blue**: attacks along the same **row** (same y), alternating west/east outward
- **Green**: attacks along both **diagonals** through its position, alternating directions
- **Red**: attacks a **rectangular area** expanding to the bottom-right (scan west→east, north→south)

If a player dies during its attack sequence, that player’s attack ends immediately.

---

## 🖥️ CLI Interface (Commands)

When no game is running, available commands:
- `game`, `help`, `quit`

When a game is running:
- `game` — start a new game (cancels current game if one exists)
- `move` — move a player
- `create` — recruit a player in a bunker
- `attack` — coordinated team attack
- `status` — global game state
- `map` — map view for the current team
- `bunkers` — list current team bunkers (seizure order)
- `players` — list current team players (recruitment order)
- `help`, `quit`

The program is **case-insensitive** and follows strict input/output formatting (designed for automated grading).

---

## ✅ Error Handling (Examples)

The implementation follows the specification-defined messages, including cases like:
- Invalid command / invalid position / invalid direction
- Non-existent bunker / illegal bunker access
- Insufficient coins / bunker not free
- Move off-map / position occupied
- Player eliminated / won the fight / bunker seized
- Game end: `Winner is <teamName>.`

---

## 🛠 Technologies Used

- **Java**
- Object-Oriented design with emphasis on:
  - interfaces
  - inheritance
  - (likely) abstract classes
- Clean separation between:
  - command parsing (UI)
  - domain model (game logic)
  - state representation (map/entities)

---

## 📌 What I Learned

- Designing a moderately complex **OOP domain model**
- Managing game state and turn-based rules reliably
- Implementing strict CLI I/O contracts for automated testing
- Handling many edge cases (movement, fights, seizing, team elimination)
- Writing maintainable code with reusable abstractions

---

## ▶️ How to Run (Typical)

Compile and run (example):

```bash
javac Main.java
java Main
```
Then interact using the commands defined in the specification (e.g., game, create, move, attack, etc.).
javac Main.java
java Main
