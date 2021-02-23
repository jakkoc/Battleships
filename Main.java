package battleship;

public class Main {

    public static void main(String[] args) {
        Battleship firstPlayer = new Battleship(1);
        Battleship secondPlayer = new Battleship(2);

        firstPlayer.play(secondPlayer);
   }
}
