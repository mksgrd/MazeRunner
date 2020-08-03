package maze;

import java.io.*;
import java.util.*;

public class MazeRunner {
    private static final String WALL = "\u2588\u2588";
    private static final String PASS = "  ";
    private static final String PATH = "//";
    private static final int RANDOM_BOUND = 128;

    private final Random random = new Random();

    private Graph<Cell> mazeGraph;
    private int n = 0;
    private int m = 0;
    private Cell entry;
    private Cell exit;

    private void processMazeCellNeighbors(int i, int j) {
        Cell from = new Cell(i, j);
        for (Cell to : from.getNeighbors(1, 1, n - 2, m - 2, 2)) {
            mazeGraph.addEdge(from, to, random.nextInt(RANDOM_BOUND));
        }
    }

    private int randomIntOnDistance(int lowerBound, int upperBound, int distance) {
        return lowerBound + distance * random.nextInt((upperBound + distance - 1) / distance);
    }

    private int manhattanDistance(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private void generateEntryExit() {
        List<Edge<Cell>> entryEdges = new LinkedList<>();
        List<Edge<Cell>> exitEdges = new LinkedList<>();

        Cell up = new Cell(1, randomIntOnDistance(1, m - 2, 2));
        entryEdges.add(new Edge<>(new Cell(up.getRow() - 1, up.getCol()), up));

        Cell right = new Cell(randomIntOnDistance(1, n - 2, 2), m - 2);
        exitEdges.add(new Edge<>(right, new Cell(right.getRow(), right.getCol() + 1)));

        Cell down = new Cell(n - 2, randomIntOnDistance(1, m - 2, 2));
        exitEdges.add(new Edge<>(down, new Cell(down.getRow() + 1, down.getCol())));

        Cell left = new Cell(randomIntOnDistance(1, n - 2, 2), 1);
        entryEdges.add(new Edge<>(new Cell(left.getRow(), left.getCol() - 1), left));

        Edge<Cell> entryEdge = entryEdges.get(random.nextInt(entryEdges.size()));
        Edge<Cell> exitEdge = exitEdges.get(random.nextInt(exitEdges.size()));
        for (Edge<Cell> edge : exitEdges) {
            if (manhattanDistance(entryEdge.getFrom(), edge.getFrom()) > manhattanDistance(entryEdge.getFrom(), exitEdge.getFrom())) {
                exitEdge = edge;
            }
        }
        mazeGraph.addEdge(entryEdge);
        mazeGraph.addEdge(exitEdge);
        entry = entryEdge.getFrom();
        exit = exitEdge.getTo();
    }

    private void generateMaze(int n, int m) throws InvalidMazeDimensionsException {
        if (n < 4 || m < 4) {
            throw new InvalidMazeDimensionsException("Maze dimensions should be greater than 3");
        }
        mazeGraph = new Graph<>();
        this.n = n;
        this.m = m;
        for (int i = 1; i <= n - 2; i += 2) {
            for (int j = 1; j <= m - 2; j += 2) {
                processMazeCellNeighbors(i, j);
            }
        }
        mazeGraph = mazeGraph.findMst();
        generateEntryExit();
    }

    private void fillMazeWithEdgesList(String[][] maze, List<Edge<Cell>> edges, String str) {
        for (Edge<Cell> edge : edges) {
            for (int i = Math.min(edge.getFrom().getRow(), edge.getTo().getRow()); i <= Math.max(edge.getFrom().getRow(), edge.getTo().getRow()); ++i) {
                for (int j = Math.min(edge.getFrom().getCol(), edge.getTo().getCol()); j <= Math.max(edge.getFrom().getCol(), edge.getTo().getCol()); ++j) {
                    maze[i][j] = str;
                }
            }
        }
    }

    private void displayMaze(boolean withEscapePath) {
        String[][] maze = new String[n][m];
        for (String[] strings : maze) {
            Arrays.fill(strings, WALL);
        }
        fillMazeWithEdgesList(maze, mazeGraph.toEdgesList(), PASS);
        if (withEscapePath) {
            fillMazeWithEdgesList(maze, mazeGraph.findPath(entry, exit), PATH);
        }
        for (String[] strings : maze) {
            for (String str : strings) {
                System.out.print(str);
            }
            System.out.println();
        }
    }

    private void saveMaze(String fileName) throws InvalidFileFormatException {
        if (!fileName.endsWith(".txt")) {
            throw new InvalidFileFormatException("Cannot save the maze with this format. Please use .txt");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(mazeGraph);
            oos.writeInt(n);
            oos.writeInt(m);
            oos.writeObject(entry);
            oos.writeObject(exit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMaze(String fileName) throws FileNotFoundException, InvalidFileFormatException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("The file " + fileName + " does not exist");
        }
        if (!fileName.endsWith(".txt")) {
            throw new InvalidFileFormatException("Unsupported format. Please load .txt file with maze");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            mazeGraph = (Graph<Cell>) ois.readObject();
            n = ois.readInt();
            m = ois.readInt();
            entry = (Cell) ois.readObject();
            exit = (Cell) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startInteractionWithUser() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            if (mazeGraph != null) {
                System.out.println("3. Save the maze");
                System.out.println("4. Display the maze");
                System.out.println("5. Find the escape");
            }
            System.out.println("0. Exit");
            int command = scanner.nextInt();
            if ((mazeGraph == null && (command == 3 || command == 4 || command == 5)) || command < 0 || command > 5) {
                System.out.println("Incorrect option. Please try again");
                continue;
            }
            try {
                if (command == 1) {
                    System.out.println("Enter the size of a new maze");
                    int size = scanner.nextInt();
                    generateMaze(size, size);
                    displayMaze(false);
                } else if (command == 2) {
                    System.out.println("Enter the path to file with maze");
                    loadMaze(scanner.next());
                } else if (command == 3) {
                    System.out.println("Enter the path to a new maze file");
                    saveMaze(scanner.next());
                } else if (command == 4) {
                    displayMaze(false);
                } else if (command == 5) {
                    displayMaze(true);
                } else {
                    System.out.println("Bye");
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
