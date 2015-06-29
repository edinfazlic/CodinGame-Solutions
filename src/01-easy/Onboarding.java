import java.util.Scanner;

/**
 * CodinGame planet is being attacked by slimy insectoid aliens.
 */
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            String enemy1 = in.next(); // name of enemy 1
            int dist1 = in.nextInt(); // distance to enemy 1
            String enemy2 = in.next(); // name of enemy 2
            int dist2 = in.nextInt(); // distance to enemy 2

            System.out.println(dist1 < dist2 ? enemy1 : enemy2);
        }
    }
}
