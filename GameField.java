package battleship;

import java.util.InputMismatchException;

public class GameField {
    public enum Ship {
        AircraftCarrier("Aircraft Carrier", 5),
        Battleship("Battleship", 4),
        Submarine("Submarine", 3),
        Cruiser("Cruiser", 3),
        Destroyer("Destroyer", 2);

        private final static Ship[] values = values();
        private final String name;
        private final int cells;

        Ship(String name, int cells) {
            this.name = name;
            this.cells = cells;
        }

        public String getName() {
            return name;
        }

        public int getCells() {
            return cells;
        }

        public Ship next() {
            return values[(ordinal()+1) % values.length];
        }
    }
    private static final int ROWS = 10;
    private static final int COLUMNS = 10;
    private static final char FOG_OF_WAR_SIGN = '~';
    private static final char SHIP_SIGN = 'O';
    private static final char HIT_SIGN = 'X';
    private static final char MISS_SIGN = 'M';
    private char[][] shipsField = new char[ROWS][COLUMNS];
    private char[][] shootsField = new char[ROWS][COLUMNS];

    private Ship currentShipToBePlaced = Ship.values()[0];
    private Player player;
    private int hitCounter = 0;
    private int shipCells;

    public GameField(String playerName) {
        initializeField();
        countAllShipCells();
        player = new Player(playerName);
    }

    public boolean shoot(Coordinate coordinate) {
        if (!isCoordinateValid(coordinate)) {
            throw new InputMismatchException("Error! You entered the wrong coordinate!");
        }

        int x = coordinate.getX();
        int y = coordinate.getY();

        if (shootsField[x][y] != FOG_OF_WAR_SIGN) {
            return shootsField[x][y] == HIT_SIGN;
        }

        char hitCell = shipsField[x][y];
        if (hitCell == FOG_OF_WAR_SIGN) {
            shipsField[x][y] = MISS_SIGN;
            shootsField[x][y] = MISS_SIGN;
            return false;
        }

        shipsField[x][y] = HIT_SIGN;
        shootsField[x][y] = HIT_SIGN;
        hitCounter++;

        return true;
    }

    public boolean isShipSunk(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();

        if (shootsField[x][y] != HIT_SIGN) {
            return false;
        }

        return checkSidesHorizontally(x, y) && checkSidesVertically(x, y);
    }

    public boolean areAllShipsSunk() {
        return shipCells == hitCounter;
    }

    public void placeNextShip(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        if(!areCoordinatesValid(firstCoordinate, secondCoordinate)) {
            throw new InputMismatchException("Error! You have not provided valid coordinates.");
        }

        int columnStartIndex = Math.min(firstCoordinate.getY(), secondCoordinate.getY());
        int columnEndIndex = Math.max(firstCoordinate.getY(), secondCoordinate.getY());

        int rowStartIndex = Math.min(firstCoordinate.getX(), secondCoordinate.getX());
        int rowEndIndex = Math.max(firstCoordinate.getX(), secondCoordinate.getX());

        for (int i = rowStartIndex; i <= rowEndIndex; i++) {
            for (int j = columnStartIndex; j <= columnEndIndex; j++) {
                shipsField[i][j] = SHIP_SIGN;
            }
        }

        currentShipToBePlaced = currentShipToBePlaced.next();
    }

    public String getPlayerName() {
        return player.getName();
    }

    public void printShips() {
        printField(shipsField);
    }

    public void printShoots() {
        printField(shootsField);
    }

    private void printField(char[][] array) {
        char rowSign = 'A';
        System.out.print("  ");
        for (int i = 1; i <= COLUMNS; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < ROWS; i++, rowSign++) {
            System.out.print(rowSign + " ");
            for (int j = 0; j < COLUMNS; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void initializeField() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                shipsField[i][j] = FOG_OF_WAR_SIGN;
                shootsField[i][j] = FOG_OF_WAR_SIGN;
            }
        }
    }

    private void countAllShipCells() {
        int cellCounter = 0;
        for (Ship ship : Ship.values()) {
            cellCounter += ship.getCells();
        }

        shipCells = cellCounter;
    }

    private boolean checkSidesHorizontally(int x, int y) {
        int leftIndex = (y > 0) ? y - 1 : y;
        int rightIndex = (y < COLUMNS - 1) ? y + 1 : y;

        boolean leftSideGood = true;
        boolean rightSideGood = true;

        if (leftIndex != y) {
            while (leftIndex - 1 > 0 && shipsField[x][leftIndex] == HIT_SIGN) {
                leftIndex--;
            }

            if (shipsField[x][leftIndex] == SHIP_SIGN) {
                leftSideGood = false;
            }
        }

        if (rightIndex != y) {
            while (rightIndex + 1 < COLUMNS - 1 && shipsField[x][rightIndex] == HIT_SIGN) {
                rightIndex++;
            }

            if (shipsField[x][rightIndex] == SHIP_SIGN) {
                rightSideGood = false;
            }
        }

        return leftSideGood && rightSideGood;
    }

    private boolean checkSidesVertically(int x, int y) {
        int upIndex = (x > 0) ? x - 1 : x;
        int downIndex = (x < ROWS - 1) ? x + 1 : x;

        boolean upSideGood = true;
        boolean downSideGood = true;

        if (upIndex != x) {
            while (upIndex - 1 > 0 && shipsField[upIndex][y] == HIT_SIGN) {
                upIndex--;
            }

            if (shipsField[upIndex][y] == SHIP_SIGN) {
                upSideGood = false;
            }
        }

        if (downIndex != x) {
            while (downIndex + 1 < ROWS - 1 && shipsField[downIndex][y] == HIT_SIGN) {
                downIndex++;
            }

            if (shipsField[downIndex][y] == SHIP_SIGN) {
                downSideGood = false;
            }
        }

        return upSideGood && downSideGood;
    }

    private boolean isCoordinateValid(Coordinate coordinate) {
        return coordinate != null && isCoordinateInRange(coordinate);
    }

    private boolean areCoordinatesValid(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        return firstCoordinate != null
                && secondCoordinate != null
                && isShipInRange(firstCoordinate, secondCoordinate)
                && isShipLengthValid(firstCoordinate, secondCoordinate)
                && isFieldAmongClear(firstCoordinate, secondCoordinate);
    }

    private boolean isFieldAmongClear(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        int columnStartIndex = Math.min(firstCoordinate.getY(), secondCoordinate.getY());
        int columnEndIndex = Math.max(firstCoordinate.getY(), secondCoordinate.getY());
        int rowStartIndex = Math.min(firstCoordinate.getX(), secondCoordinate.getX());
        int rowEndIndex = Math.max(firstCoordinate.getX(), secondCoordinate.getX());

        columnStartIndex = (columnStartIndex > 0) ? columnStartIndex - 1 : columnStartIndex;
        columnEndIndex = (columnEndIndex < COLUMNS - 1) ? columnEndIndex + 1 : columnEndIndex;
        rowStartIndex = (rowStartIndex > 0) ? rowStartIndex - 1 : rowStartIndex;
        rowEndIndex = (rowEndIndex < ROWS - 1) ? rowEndIndex + 1 : rowEndIndex;

        for (int i = rowStartIndex; i <= rowEndIndex; i++) {
            for (int j = columnStartIndex; j <= columnEndIndex; j++) {
                if (shipsField[i][j] != FOG_OF_WAR_SIGN) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isShipLengthValid(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        return ((firstCoordinate.getX() == secondCoordinate.getX()) && (Math.abs(firstCoordinate.getY() - secondCoordinate.getY()) + 1 == currentShipToBePlaced.getCells()))
                || ((firstCoordinate.getY() == secondCoordinate.getY()) && (Math.abs(firstCoordinate.getX() - secondCoordinate.getX()) + 1 == currentShipToBePlaced.getCells()));
    }

    private boolean isShipInRange(Coordinate firstCoordinate, Coordinate secondCoordinate) {
        return isCoordinateInRange(firstCoordinate) && isCoordinateInRange(secondCoordinate);
    }

    private boolean isCoordinateInRange(Coordinate coordinate) {
        return (coordinate.getX() >= 0 && coordinate.getX() < ROWS)
                && (coordinate.getY() >= 0 && coordinate.getY() < COLUMNS);
    }
}
