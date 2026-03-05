# Minimax Adversarial Search Game using Java

This game models an adversarial search solution for a turn-based battle game using Minimax and Alpha-Beta Pruning algorithms. The objective is to determine the optimal sequence of actions that allows the starting player to win the battle while maximizing the remaining total health of their army.

The game consists of two players, each controlling an army of units. Every unit has a fixed health value and damage value. During each turn, a player chooses one of their units to attack an opponent’s unit. The attacked unit loses health equal to the attacker’s damage, and once a unit’s health reaches zero, it is removed from the game. The battle continues until the total health of one army reaches zero.

The solution models the problem as a search tree, where each node represents a possible game state. Using the Minimax algorithm, the agent evaluates possible sequences of moves assuming both players act optimally. To improve performance, the implementation also supports Alpha-Beta pruning, which eliminates branches of the search tree that cannot influence the final decision, significantly reducing the number of explored nodes.

The algortihm parses the initial game state and performs the search to determine the optimal plan of actions. The solver returns the sequence of attacks that leads to the best outcome, along with the resulting score and the number of nodes expanded during the search.

This project demonstrates the practical application of adversarial search techniques in artificial intelligence, highlighting how optimal decision-making strategies can be used to solve competitive game scenarios efficiently.
