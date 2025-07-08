package island;

import java.util.ArrayList;
import java.util.Arrays;

import island.constants.*;
import island.std.StdRandom;

public class RandomCatIsland {

    public final Island randomIsland;
    public final ArrayList<Cat> cats = new ArrayList<>();

    public String[] questions;

    // Time == 0 means game has not started, else time must be <= endtime
    public int time;

    // Final time of the game, game ends on this turn
    public int endTime;

    public RandomCatIsland(String netID) {
        netID = netID.trim().toLowerCase();
        StdRandom.setSeed(hash(netID));
        time = 0;
        endTime = StdRandom.uniformInt(250, 300);
        randomIsland = createIsland();
        this.spawnYarn();
    }

    /**
     * Runs one single turn of the island, where each cat gets to move once
     * and yarn potentially spawns
     * 
     * Time gets incremented at the start, to equal the current turn number (1, 2,
     * 3, ... , 99, etc.)
     * 
     * Then there's a 1/10 chance balls of yarn will spawn across the island
     * 
     * @return
     */
    public boolean nextTurn() {
        time += 1;

        if (StdRandom.uniformInt(10) == 0) {
            spawnYarn();
        }

        for (Cat c : cats) {
            // 1/4 chance the cat will stay still
            if (StdRandom.uniformInt(4) == 0) {
                continue;
            }
            // 4 Directions
            ArrayList<int[]> dirs = new ArrayList<>(
                    Arrays.asList(new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }));
            while (!dirs.isEmpty()) {
                // Randomly remove one of the remaining directions and attempt to move
                int[] dir = dirs.remove(StdRandom.uniformInt(dirs.size()));
                int row = c.getRow() + dir[0];
                int col = c.getCol() + dir[1];
                if (row >= 0 && col >= 0 && row < randomIsland.getTiles().length
                        && col < randomIsland.getTiles()[0].length) {
                    // If the possible direction is land, and has no cat
                    if (this.randomIsland.getTiles()[row][col].isLand()
                            && this.randomIsland.getTiles()[row][col].cat == null) {
                        try {
                            if (row < c.getRow()) {
                                c.moveUp();
                            }
                            if (row > c.getRow()) {
                                c.moveDown();
                            }
                            if (col < c.getCol()) {
                                c.moveLeft();
                            }
                            if (col > c.getCol()) {
                                c.moveRight();
                            }
                        } catch (CatInWaterException e) {
                        }
                        break; // If the cat succesfully moves, move onto the next cat
                    }
                }
            }
        }

        return time < endTime;
    }

    private void spawnYarn() {
        Tile[][] tiles = randomIsland.getTiles();
        int yarn = 0;
        int land = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j].isLand()) {
                    land++;
                    if (tiles[i][j].hasYarn) {
                        yarn++;
                    }
                }
            }
        }

        if (yarn < land / 4) {
            for (int i = 0; i < tiles.length; i++) {
                currTile: for (int j = 0; j < tiles[i].length; j++) {
                    if (tiles[i][j].isLand() && !tiles[i][j].hasYarn && tiles[i][j].cat == null) {
                        // Loop around surrounding 8 tiles, if any have yarn, dont place
                        for (int di = -1; di <= 1; di++) {
                            for (int dj = -1; dj <= 1; dj++) {
                                int row = i + di;
                                int col = j + dj;
                                if (row >= 0 && col >= 0 && row < randomIsland.getTiles().length
                                        && col < randomIsland.getTiles()[0].length) {
                                    if (!this.randomIsland.getTiles()[row][col].isLand()) {
                                        continue;
                                    } else if (this.randomIsland.getTiles()[row][col].hasYarn) {
                                        continue currTile;
                                    } else if (di == 1 && dj == 1 && StdRandom.uniformInt(8) == 0) {
                                        tiles[i][j].hasYarn = true;
                                        continue currTile;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private Island createIsland() {
        Tile[][] isle = new Tile[StdRandom.uniformInt(9, 12)][StdRandom.uniformInt(9, 12)];
        for (int i = 0; i < isle.length; i++) {
            for (int j = 0; j < isle[i].length; j++) {
                isle[i][j] = new Tile("W", false, i, j);
            }
        }

        ArrayList<Tile> placed = new ArrayList<>();
        // First wave of 4 tiles, spaced 2 apart at least from others
        for (int tile = 0; tile < 4;) {
            int row = StdRandom.uniformInt(0, isle.length);
            int col = StdRandom.uniformInt(0, isle[0].length);
            boolean place = false;
            for (int di = -2; di <= 2; di++) {
                boolean skip = false;
                for (int dj = -2; dj <= 2; dj++) {
                    int i = row + di;
                    int j = col + dj;
                    if (i >= 0 && j >= 0 && i < isle.length && j < isle[0].length) {
                        if (isle[i][j].type == Tile.LAND) {
                            skip = true;
                            break;
                        }
                        if (di == 2 && dj == 2) {
                            place = true;
                            isle[row][col].type = Tile.LAND;
                            placed.add(isle[row][col]);
                        }
                    }
                }
                if (skip)
                    break;
            }
            if (place) {
                tile++;
            }
        }
        // Second wave of 2 tiles, spaced 1 apart at lease from others
        for (int tile = 0; tile < 2;) {
            int row = StdRandom.uniformInt(0, isle.length);
            int col = StdRandom.uniformInt(0, isle[0].length);
            boolean place = false;
            for (int di = -1; di <= 1; di++) {
                boolean skip = false;
                for (int dj = -1; dj <= 1; dj++) {
                    int i = row + di;
                    int j = col + dj;
                    if (i >= 0 && j >= 0 && i < isle.length && j < isle[0].length) {
                        if (isle[i][j].type == Tile.LAND) {
                            skip = true;
                            break;
                        }
                        if (di == 1 && dj == 1) {
                            place = true;
                            isle[row][col].type = Tile.LAND;
                            placed.add(isle[row][col]);
                        }
                    }
                }
                if (skip)
                    break;
            }
            if (place) {
                tile++;
            }
        }

        // Surround each placed land tile with 3x3 of land to expand island
        for (Tile t : placed) {
            int row = t.row;
            int col = t.col;
            surroundTile(isle, row, col, 2);
        }

        // Ensure that all unconnected islands are connected
        connectIslands(placed, isle);
        // Create island
        Island island = Island.createIsland(isle);
        // Add cats to the island
        populateIsland(island);

        return island;
    }

    /**
     * This method creates a square of land tiles, centered on (row,col),
     * with "width" tiles on each side.
     * 
     * This is used to create the bulk of the island, from single points.
     * 
     * @param isle
     * @param row
     * @param col
     * @param width
     */
    private void surroundTile(Tile[][] isle, int row, int col, int width) {
        for (int di = -width; di <= width; di++) {
            for (int dj = -width; dj <= width; dj++) {
                int i = row + di;
                int j = col + dj;
                if (i >= 0 && j >= 0 && i < isle.length && j < isle[0].length) {
                    isle[i][j].type = Tile.LAND;
                }
            }
        }
    }

    /**
     * Connects the tiles given in "placed", by designating one as a hub and
     * connecting the others too it.
     * 
     * This ensures all land is connected, creating one large island from many
     * smaller ones
     * 
     * @param placed
     * @param isle
     */
    private void connectIslands(ArrayList<Tile> placed, Tile[][] isle) {
        Tile hub = placed.remove(StdRandom.uniformInt(placed.size()));
        for (Tile t : placed) {
            if (t.row < hub.row) {
                for (int src = t.row; src <= hub.row; src++) {
                    surroundTile(isle, src, t.col, 1);
                }
            } else if (t.row > hub.row) {
                for (int src = t.row; src >= hub.row; src--) {
                    surroundTile(isle, src, t.col, 1);
                }
            }
            if (t.col < hub.col) {
                for (int src = t.col; src <= hub.col; src++) {
                    surroundTile(isle, hub.row, src, 1);
                }
            } else if (t.col > hub.col) {
                for (int src = t.col; src >= hub.col; src--) {
                    surroundTile(isle, hub.row, src, 1);
                }
            }
        }
    }

    /**
     * This spawns one cat of each color across the island in random spots
     * 
     * @param isle
     */
    private void populateIsland(Island island) {
        ArrayList<Color> randCol = new ArrayList<>(
                Arrays.asList(new Color[] { Color.ORANGE, Color.GREY, Color.WHITE,
                        Color.BROWN, Color.BLACK }));

        Tile[][] isle = island.getTiles();

        outer: while (!randCol.isEmpty()) {
            for (int i = 0; i < isle.length; i++) {
                inner: for (int j = 0; j < isle[0].length; j++) {
                    if (isle[i][j].type == Tile.LAND && isle[i][j].cat == null) {
                        for (int di = -1; di <= 1; di++) {
                            for (int dj = -1; dj <= 1; dj++) {
                                int row = i + di;
                                int col = j + dj;
                                if (row >= 0 && col >= 0 && row < isle.length && col < isle[0].length) {
                                    if (isle[row][col].type == Tile.LAND && isle[row][col].cat != null) {
                                        continue inner;
                                    }
                                    if (di == 1 && dj == 1 && StdRandom.uniformInt(3) == 0) {
                                        isle[i][j].cat = new Cat("Cat" + (cats.size() + 1),
                                                island, i, j,
                                                randCol.remove(StdRandom.uniformInt(randCol.size())));
                                        cats.add(isle[i][j].cat);
                                        continue outer;
                                    }
                                }

                            }

                        }
                    }
                }
            }
        }
    }

    public static final String[] possibleQuestions = new String[] {
            "1. How many land tiles are on this island?",
            "2. How many water tiles are on this island?",
            "3. How many cats spawned on the island?",
            "4. How many yarn were on the island at TIME?",
            "5. What ROWCOL was CAT in at TIME?",
            "6. How many yarn were left after the last turn?",
            "7. What is the most yarn a single cat collected?"
    };

    public String[] generateQuestions() {
        if (this.questions == null) {
            this.questions = Arrays.copyOf(possibleQuestions, possibleQuestions.length);
            for (int i = 0; i < questions.length; i++) {
                if (questions[i].contains("TIME")) {
                    questions[i] = questions[i].replaceAll("TIME", "time=" + StdRandom.uniformInt(endTime));
                }
                if (questions[i].contains("CAT")) {
                    questions[i] = questions[i].replaceAll("CAT",
                            "" + cats.get(StdRandom.uniformInt(cats.size())).getName());
                }
                if (questions[i].contains("ROWCOL")) {
                    questions[i] = questions[i].replaceAll("ROWCOL",
                            (StdRandom.uniformInt(2) == 0) ? "row" : "column");
                }
            }
        }
        return this.questions;
    }

    public static String validNetID(String id) {
        if (id == null || id.isBlank()) {
            return "Blank NetID";
        } else if (id.length() < 3 || id.length() > 8) {
            return "Invalid NetID Length";
        } else if (!Character.isLetter(id.charAt(0))) {
            return "Invalid NetID Starts with number";
        }
        // If no netID was read, or netID is incorrect length
        boolean letters = true;
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (letters == false && Character.isLetter(c)) {
                return "Invalid NetID";
            } else if (letters == true && Character.isDigit(c)) {
                letters = false;
            }
        }
        return null;
    }

    /*
     * Takes a netID and turns it into a value which can be used
     * to seed StdRandom
     * 
     * @param netID a valid 6 digit netID
     * 
     * @return an long value seed
     */
    public static long hash(String input) {// If no netID was read, or netID is incorrect length
        if (RandomCatIsland.validNetID(input) != null) {
            return 1;
        }
        String seed = "";
        for (int i = 0; i < input.length(); i++) { // Concatenates the ascii value of each netID char to an string
            int ascii = (int) input.charAt(i);
            seed = seed + String.valueOf(ascii);
        }
        if (seed.length() > 18) {
            seed = seed.substring(0, seed.length() - 18);
        }
        long seedVal = Long.parseLong(seed); // Parses string to long and returns
        return seedVal;
    }
}
