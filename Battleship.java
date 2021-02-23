package battleship;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Battleship {
    private final int GAMEBOARD_SIZE = 10;
    private static final Scanner scanner = new Scanner(System.in);
    private final Character[][] gameboard;
    private int shipsRemaining;
    private final int playerNumber;

    public Battleship(int playerNumber) {
        gameboard = new Character[GAMEBOARD_SIZE][GAMEBOARD_SIZE];
        shipsRemaining = 5;
        this.playerNumber = playerNumber;

        for(int i = 0; i < GAMEBOARD_SIZE; i++) {
            for(int j = 0; j < GAMEBOARD_SIZE; j++) {
                gameboard[i][j] = '~';
            }
        }

        System.out.println("Player " + playerNumber + ", place your ships on the game field");
        System.out.println();
        displayGameboard();
        placeAShip(Ship.AIRCRAFT_CARRIER);
        placeAShip(Ship.BATTLESHIP);
        placeAShip(Ship.SUBMARINE);
        placeAShip(Ship.CRUISER);
        placeAShip(Ship.DESTROYER);
        endTurn();
    }

    public void play(Battleship opponentsBattlefield) {
        while(oneTurn(opponentsBattlefield) && opponentsBattlefield.oneTurn(this));
    }

    private boolean oneTurn(Battleship opponentsBattlefield) {
        if(shipsRemaining == 0) return false;

        opponentsBattlefield.displayGameboardWithFog();
        displayDottedLine();
        displayGameboard();
        System.out.println();
        System.out.printf("Player %d, it's your turn:%n",playerNumber);
        opponentsBattlefield.takeAShot();

        return true;
    }

    public void takeAShot() {
        Coordinates coordinates;
        boolean canBeSank;
        while(!(coordinates = parseCoordinate(scanner.next())).areValid()) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
        }

        if(gameboard[coordinates.row - 1][coordinates.column - 1].equals('~') || gameboard[coordinates.row - 1][coordinates.column - 1].equals('M')) {
            gameboard[coordinates.row - 1][coordinates.column - 1] = 'M';
            System.out.println("You missed!");
        }
        else {
            canBeSank = !gameboard[coordinates.row - 1][coordinates.column - 1].equals('X');
            gameboard[coordinates.row - 1][coordinates.column - 1] = 'X';

            if(shipSank(coordinates)) {
                if(canBeSank) shipsRemaining--;

                if(shipsRemaining == 0) {
                    System.out.println("You sank the last ship. You won. Congratulations!");
                }
                else System.out.println("You sank a ship!");
            }

            else {
                System.out.println("You hit a ship!");
            }
        }

        if(shipsRemaining > 0) endTurn();
    }

    private void endTurn() {
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
        try {
            System.in.read();
        } catch(IOException e) {
            Logger.getLogger("global").log(Level.SEVERE,e.getMessage());
        }
    }

    private void displayGameboard() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for(int i = 0; i < GAMEBOARD_SIZE; i++) {
            System.out.print((char)(65 + i) + " ");
            Arrays.stream(gameboard[i]).forEach(character -> System.out.print(character + " "));
            System.out.println();
        }
    }

    private void displayGameboardWithFog() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for(int i = 0; i < GAMEBOARD_SIZE; i++) {
            System.out.print((char)(65 + i) + " ");
            Arrays.stream(gameboard[i]).forEach(character -> System.out.print((character == '~' || character == 'X' || character == 'M' ? character : '~') + " "));
            System.out.println();
        }
    }

    private void displayDottedLine() {
        System.out.println("---------------------");
    }

    private void placeAShip(Ship ship) {
        System.out.printf("Enter the coordinates of the %s(%d cells): %n",ship,ship.getLength());
        var startingCoordinates = parseCoordinate(scanner.next());
        var endingCoordinates = parseCoordinate(scanner.next());
        while(!validateCoordinates(ship,startingCoordinates , endingCoordinates)) {
            startingCoordinates = parseCoordinate(scanner.next());
            endingCoordinates = parseCoordinate(scanner.next());
        }

        for(int i = Math.min(startingCoordinates.getRow(), endingCoordinates.getRow()); i <= Math.max(startingCoordinates.getRow(), endingCoordinates.getRow()); i++) {
            for(int j = Math.min(startingCoordinates.getColumn(), endingCoordinates.getColumn()); j <= Math.max(startingCoordinates.getColumn(), endingCoordinates.getColumn()); j++) {
                gameboard[i - 1][j - 1] = 'O';
            }
        }

        displayGameboard();
    }

    private boolean shipSank(Coordinates coordinates) {
        int counter = 0;
        Coordinates tmpCoordinates;

        while(coordinates.getColumn() - counter >= 1 && !gameboard[coordinates.row - 1][coordinates.column - counter - 1].equals('~') && !gameboard[coordinates.row - 1][coordinates.column - counter - 1].equals('M')) {
            tmpCoordinates = new Coordinates(coordinates.row,coordinates.column - counter);

            if(gameboard[tmpCoordinates.row - 1][tmpCoordinates.column - 1].equals('O')) {
                return false;
            }

            counter++;
        }

        counter = 0;

        while(coordinates.getRow() + counter <= 10 && !gameboard[coordinates.row + counter - 1][coordinates.column - 1].equals('~') && !gameboard[coordinates.row + counter - 1][coordinates.column - 1].equals('M')) {
            tmpCoordinates = new Coordinates(coordinates.row + counter,coordinates.column);

            if(gameboard[tmpCoordinates.row - 1][tmpCoordinates.column - 1].equals('O')) {
                return false;
            }

            counter++;
        }

        counter = 0;

        while(coordinates.getColumn() + counter <= 10 && !gameboard[coordinates.row - 1][coordinates.column + counter - 1].equals('~') && !gameboard[coordinates.row - 1][coordinates.column + counter - 1].equals('M')) {
            tmpCoordinates = new Coordinates(coordinates.row,coordinates.column + counter);

            if(gameboard[tmpCoordinates.row - 1][tmpCoordinates.column - 1].equals('O')) {
                return false;
            }

            counter++;
        }

        counter = 0;

        while(coordinates.getRow() - counter >= 1 && !gameboard[coordinates.row - 1 - counter][coordinates.column - 1].equals('~') && !gameboard[coordinates.row - counter - 1][coordinates.column - 1].equals('M')) {
            tmpCoordinates = new Coordinates(coordinates.row - counter,coordinates.column);

            if(gameboard[tmpCoordinates.row - 1][tmpCoordinates.column - 1].equals('O')) {
                return false;
            }

            counter++;
        }

        return true;
    }

    private Coordinates parseCoordinate(String coordinates) {
        return new Coordinates(coordinates.charAt(0) - 64,Integer.parseInt(coordinates.substring(1)));
    }

    private boolean validateCoordinates(Ship ship, Coordinates startingCoordinates, Coordinates endingCoordinates) {
        if(!startingCoordinates.areParallelToGameboard(endingCoordinates)) {
            System.out.println("Error! Wrong ship location! Try again: ");
            return false;
        }
        if(startingCoordinates.getLength(endingCoordinates) != ship.getLength()) {
            System.out.printf("Error! Wrong length of the %s! Try again: %n",ship.toString().substring(0,ship.toString().length() - 1));
            return false;
        }
        if(isColiding(startingCoordinates, endingCoordinates)) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;
        }

        return true;
    }

    private boolean isColiding(Coordinates startingCoordinates, Coordinates endingCoordinates) {
        var startingRow = Math.max(1, Math.min(startingCoordinates.getRow(), endingCoordinates.getRow()) - 1);
        var endingRow = Math.min(10, Math.max(startingCoordinates.getRow(), endingCoordinates.getRow()) + 1);
        var startingColumn = Math.max(1, Math.min(startingCoordinates.getColumn(), endingCoordinates.getColumn()) - 1);
        var endingColumn = Math.min(10, Math.max(startingCoordinates.getColumn(), endingCoordinates.getColumn()) + 1);

        for(int i = startingRow; i <= endingRow; i++) {
            for(int j = startingColumn; j < endingColumn; j++) {
                if(gameboard[i - 1][j - 1].equals('O')) return true;
            }
        }

        return false;
    }

    private static class Coordinates {
        private final int row;
        private final int column;

        public Coordinates(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public boolean areParallelToGameboard(Coordinates otherCoordinates) {
            return getRow() == otherCoordinates.getRow() || getColumn() == otherCoordinates.getColumn();
        }

        public boolean areValid() {
            return row >= 1 && row <= 10 && column >= 1 && column <= 10;
        }

        public int getLength(Coordinates otherCoordinates) {
            if(getRow() == otherCoordinates.getRow()) {
                return Math.abs(getColumn() - otherCoordinates.getColumn()) + 1;
            }
            return Math.abs(getRow() - otherCoordinates.getRow()) + 1;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        @Override
        public String toString() {
            return String.valueOf((char)(64 + row)) + column;
        }
    }
}

enum Ship {
    AIRCRAFT_CARRIER(5),
    BATTLESHIP(4),
    SUBMARINE(3),
    CRUISER(3),
    DESTROYER(2);

    private final int length;

    Ship(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        String[] splitted = name().split("_");
        StringBuilder result = new StringBuilder();
        Arrays.stream(splitted).forEach(word -> result.append(word.substring(0,1).toUpperCase()).append(word.substring(1).toLowerCase(Locale.ROOT)).append(" "));
        return result.toString();
    }
}
