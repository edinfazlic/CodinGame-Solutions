import java.util.Scanner;

class Player {
    static int H0 = 0;
    static int H1;
    static int W0 = 0;
    static int W1;
    static int W;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.
        int batX = in.nextInt(); // starting x position of Batman
        int batY = in.nextInt(); // starting y position of Batman
        H1 = H;
        W1 = W;
        /*W0        W1
        H0 ----------
           |        |
           |        |
           |        |
        H1 ----------*/

        // game loop
        while (true) {
            String BOMB_DIR = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)
            switch (BOMB_DIR.charAt(0)) {
                case 'U':
                    H1 = batY;
                    batY -= Math.round((batY - H0) / 2f);
                    batX = decideForSide(BOMB_DIR, batX);
                    break;
                case 'D':
                    H0 = batY;
                    batY += Math.round((H1 - batY) / 2f);
                    batX = decideForSide(BOMB_DIR, batX);
                    break;
                default:
                    batX = goSide(BOMB_DIR.charAt(0), batX);
            }
            System.out.println(batX + " " + batY); // the location of the next window Batman should jump to.
        }
    }

    private static int decideForSide(String bomb, int x) {
        if (bomb.length() > 1) {
            return goSide(bomb.charAt(1), x);
        }
        return x;
    }

    private static int goSide(char LR, int x) {
        if (LR == 'L') {
            W1 = x;
            return x - Math.round((x - W0) / 2f);
        }
        W0 = x;
        return x + Math.round((W1 - x) / 2f);
    }
}
