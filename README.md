# DD2380 (2015) - Artificial Intelligence

Solutions to assignments 1 and 2 of the DD2380 (2015) course at KTH and the final project. The full list of assignments is available at https://kth.kattis.com/courses/DD2380/ai15/problems .

## #0: Sokoban pathfinding
Task: https://kth.kattis.com/problems/kth.ai.sokobanpathfinding

In this optional assignment, the goal is to move the Player to a Goal in a board full of boxes WITHOUT pushing any of them.

The correct path is found using Breadth-first search (BFS).

## #1: Checkers
Task: https://kth.kattis.com/problems/kth.ai.checkers

In this assignment, the goal is to implement a strategy that allows a Player to win (or not lose) as often as possible in Checkers by choosing the best possible move given the state of the game (i.e the Board). A skeleton is provided where the visualization and the game dynamics are already implemented (which include the calculation of the possible moves given a Board).

The solution implements alpha-beta pruning applied on the minimax algorithm's search tree up to `MAX_DEPTH = 9` with the following very simple strategy:

* If a winner state is found, choose that branch no matter what (i.e. assign `MAX_VALUE` to the heuristics).
* If no winner state is found, the heuristics is given by the number of pieces the Player has (kings count as two):

`h(state) = amount_of_pieces_except_kings + 2 * amount_of_kings  `

The final score was 18.

## #2: Duck Hunt
Task: https://kth.kattis.com/problems/kth.ai.duckhunt

In this assignment, the goal is to maximize the score of a Duck Hunt's player considering there are many species of ducks (with one that **must not** be shot) that are not idenfitiable by the state but we are just given their behaviours. That is, the solution must guess the specie of each duck to decide whether it should be shot or not. Each game consists of several rounds with the same birds.

The strategy is to sacrifice some points on the first round to get the behaviour of each bird and use it on the next rounds to try to guess the specie of each bird using Hidden Markov Models (HMM).
Then, the Viterbi algorithm is used to predict the future behaviour of each bird and a confidence threshold of `SAFE_TO_SHOOT = 0.8` is used to decide if we know the birds move so it can be shot.

## Final Project

The topic of this project is word prediction, where the specific problem will be about ​ word completion and suggestion from text,​ e.g. like in many smartphone chat apps.

There are multiple aspects considered in the prediction: previous words in the sentence, initial letters of the word to complete, word classes of the previous words (used to infer the class of the predicted word and its role in the sentence) and context of the sentence in the context (probably in a conversation). Each aspect helps to narrow down the set of possible words and assign probabilities.

The solution uses n­gram models, a corpus retrieved from Twitter and part-of-speech tags.