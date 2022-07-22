package battleship;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BattleshipGame {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int ASCII_FIRST_CHAR_DEC = 65;
    private GameField gameFieldPlayerOne;
    private GameField gameFieldPlayerTwo;
    private boolean endOfGame = false;

    public BattleshipGame() {
        gameFieldPlayerOne = new GameField("Player 1");
        gameFieldPlayerTwo = new GameField("Player 2");
    }

    public void start() {
        setFieldsForBothPlayers();
        processShooting();
    }

    private void setFieldsForBothPlayers() {
        addShipsToField(gameFieldPlayerOne);
        addShipsToField(gameFieldPlayerTwo);
    }

    private void addShipsToField(GameField field) {
        System.out.println(field.getPlayerName() + ", place your ships on the game field\n");
        field.printShips();
        for (GameField.Ship ship : GameField.Ship.values()) {
            System.out.println();
            System.out.println("Enter the coordinates of the " + ship.getName() + " (" + ship.getCells() + " cells)");
            processPlayerCoordinateInput(field);
            System.out.println();
            field.printShips();
        }
        moveToAnotherPlayerMessage();
    }

    private void processShoot(GameField shooter, GameField underFire) {
        underFire.printShoots();
        System.out.print("---------------------\n");
        shooter.printShips();
        System.out.println("\n" + shooter.getPlayerName() + ", it's your turn:");

        while (true) {
            try {
                Coordinate coordinate = convertCoordinateToObject(scanner.nextLine());
                boolean shootSuccessful = underFire.shoot(coordinate);

                if (shootSuccessful) {
                    if (underFire.areAllShipsSunk()) {
                        System.out.println("You sank the last ship. You won. Congratulations!");
                        endOfGame = true;
                    } else if (underFire.isShipSunk(coordinate)) {
                        System.out.println("You sank a ship! Specify a new target:");
                    } else {
                        System.out.println("You hit a ship!");
                    }
                } else {
                    System.out.println("You missed!");
                }
                break;
            } catch (InputMismatchException exception) {
                System.out.println(exception.getMessage() + " Try again:");
            }
        }

        if (!endOfGame) {
            moveToAnotherPlayerMessage();
        }
    }

    private void processShooting() {
        while (!endOfGame) {
            processShoot(gameFieldPlayerOne, gameFieldPlayerTwo);
            if (!endOfGame) {
                processShoot(gameFieldPlayerTwo, gameFieldPlayerOne);
            }
        }
    }

    private void processPlayerCoordinateInput(GameField board) {
        while (true) {
            try {
                String[] inputCoordinates = scanner.nextLine().trim().split("\\s+");
                if (inputCoordinates.length == 2) {
                    Coordinate firstCoordinate = convertCoordinateToObject(inputCoordinates[0]);
                    Coordinate secondCoordinate = convertCoordinateToObject(inputCoordinates[1]);

                    board.placeNextShip(firstCoordinate, secondCoordinate);
                }
                break;
            } catch (InputMismatchException exception) {
                System.out.println(exception.getMessage() + " Try again:");
            }
        }
    }

    private void moveToAnotherPlayerMessage() {
        System.out.println("\nPress Enter and pass the move to another player");
        scanner.nextLine();
        clearScreen();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private Coordinate convertCoordinateToObject(String coordinate) {
        int coordinateLength = coordinate.length();
        if ((coordinateLength == 2 || coordinateLength == 3) && Character.isLetter(coordinate.charAt(0))) {
            try {
                int x = coordinate.charAt(0) - ASCII_FIRST_CHAR_DEC;
                int y = Integer.parseInt(coordinate.substring(1)) - 1;
                return new Coordinate(x, y);
            } catch (Exception ignored) {}
        }

        return null;
    }
}
