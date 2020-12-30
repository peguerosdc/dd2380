
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

public class Homework0 {
	public static int FREE_SPACE = 0;
	public static int GOAL = 2;
	public static int VISITED = 7;

	public static void main(String []args) throws java.io.IOException {
		ArrayList< ArrayList<Integer> > inputList = new ArrayList<>();
		inputList.add(new ArrayList<>());

		int pyRow = 0, pyCol = 0;
		// Start lecture of the file
		int ch;
		int rowCount = 0, colCount = 0;
		while ((ch = System.in.read() ) != -1) {
			// Map this character to my own coding
			ch = mapCharToInt(ch);

			// Check if it's a crucial case like...
			// When the player is already on the goal
			if( ch == 4) {
				System.out.println();
				return;
			}
			// When a box is in the goal
			else if(ch == 6) {
				System.out.println("no path");
				return;
			}
			// Check if a new arraylist is needed
			else if( ch == -1) {
				inputList.add(new ArrayList<>());
				rowCount++;
				colCount = 0;
				continue;
			}
			// If it's the Sokoban player, then store the position to begin
			else if( ch == 3) {
				pyRow = rowCount;
				pyCol = colCount;
			}

			// Add this character to the matrix
			inputList.get(rowCount).add( ch );
			colCount++;
		}
		// Print for debugging
		// printMatrix(inputList);

		// Start algorithm (BFS)
		Queue<Coordinate> queue = new LinkedList<Coordinate>();

		// Add initial state
		queue.add(new Coordinate(pyCol, pyRow));
		// Mark initial state as visited
		inputList.get(pyRow).set(pyCol, VISITED);

		// BFS
		HashMap<String, Character> howDidIGetHere = new HashMap<>();
		while( !queue.isEmpty() ) {
			Coordinate thisState = queue.poll();
			pyCol = thisState.x;
			pyRow = thisState.y;

			// Add every children position to the queue if it exists and it
			// has not been visited. If the position is added, then mark it
			// as VISITED.
			// If we have found the GOAL, stop.
			// There are four possible paths:
			if( (pyCol + 1) < inputList.get(pyRow).size() ) {
				Integer position = inputList.get(pyRow).get(pyCol+1);
				if(position != VISITED) {
					howDidIGetHere.put( getTableKey(pyCol+1, pyRow), 'R' );
					if( position == FREE_SPACE) {
						queue.add(new Coordinate(pyCol+1, pyRow));
						inputList.get(pyRow).set(pyCol+1, VISITED);
					}
					else if( position == GOAL) {
						printPath(howDidIGetHere, pyCol+1, pyRow);
						return;
					}
				}
			}
			if( (pyCol - 1) >= 0) {
				Integer position = inputList.get(pyRow).get(pyCol-1);
				if(position != VISITED) {
					howDidIGetHere.put( getTableKey(pyCol-1, pyRow), 'L' );
					if( position == FREE_SPACE) {
						queue.add(new Coordinate(pyCol-1, pyRow));
						inputList.get(pyRow).set(pyCol-1, VISITED);
					}
					else if( position == GOAL) {
						printPath(howDidIGetHere, pyCol-1, pyRow);
						return;
					}
				}
			}
			if( (pyRow + 1) < inputList.size()) {
				Integer position = inputList.get(pyRow+1).get(pyCol);
				if(position != VISITED) {
					howDidIGetHere.put( getTableKey(pyCol, pyRow+1), 'D' );
					if( position == FREE_SPACE) {
						queue.add(new Coordinate(pyCol, pyRow+1));
						inputList.get(pyRow+1).set(pyCol, VISITED);
					}
					else if( position == GOAL) {
						printPath(howDidIGetHere, pyCol, pyRow+1);
						return;
					}
				}
			}
			if( (pyRow - 1) >= 0) {
				Integer position = inputList.get(pyRow-1).get(pyCol);
				if(position != VISITED) {
					howDidIGetHere.put( getTableKey(pyCol, pyRow-1), 'U' );
					if( position == FREE_SPACE) {
						queue.add(new Coordinate(pyCol, pyRow-1));
						inputList.get(pyRow-1).set(pyCol, VISITED);
					}
					else if( position == GOAL) {
						printPath(howDidIGetHere, pyCol, pyRow-1);
						return;
					}
				}
			}
		}
		System.out.println("no path");
	}

	public static void printPath(HashMap<String, Character> parents, int x, int y) {
		System.out.println( getPath("", parents, x, y) );
	}

	public static String getPath(String path, HashMap<String, Character> parents, int x, int y) {
		String key = getTableKey(x, y);
		if( !parents.containsKey(key) ) {
			return path;
		}
		// Prepend this movement to the path
		Character movement = parents.get(key);
		path = movement + " " + path;
		// Look for the next movement
		if( movement == 'U') {
			y = y + 1;
		}
		else if( movement == 'D') {
			y = y - 1;
		}
		else if( movement == 'R') {
			x = x - 1;
		}
		else if( movement == 'L') {
			x = x + 1;
		}
		return getPath(path, parents, x, y);
	}

	public static void printMatrix(ArrayList< ArrayList<Integer> > matrix) {
		System.out.println("**************** MATRIX ****************");
		for(ArrayList<Integer> iArray : matrix) {
			for(Integer i : iArray) {
				System.out.print(i+" ");
			}
			System.out.println();
		}
	}

	public static int mapCharToInt(int c) {
		switch( c ) {
			case ' ': // Free space
				return FREE_SPACE;
			case '#': // Wall
				return 1;
			case '.':
				return GOAL; // Goal
			case '@':
				return 3; // Player
			case '+':
				return 4; // Player on goal
			case '$':
				return 5; // Box
			case '*':
				return 6; // Box on goal
			default:
				return -1; // End of line
			// Visited = 7
		}
	}

	public static String getTableKey(int x, int y) {
		return x+","+y;
	}

	public static class Coordinate {
		public int x, y;
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public boolean equalsTo(int x, int y) {
			return this.x == x && this.y == y;
		}
	}

}
