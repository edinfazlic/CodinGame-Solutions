import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    static int H0 = 0;
    static int H1;
    static int W0 = 0;
    static int W1;
    static int W;
    static char prevH;
    static char prevW;
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        W = in.nextInt(); // width of the building.
        int H = in.nextInt(); // height of the building.
        int N = in.nextInt(); // maximum number of turns before game over.
        int X0 = in.nextInt(); // starting x position of Batman
        int Y0 = in.nextInt(); // starting y position of Batman
        H1 = H;
        W1 = W;

        // game loop
        while (true) {
            String BOMB_DIR = in.next(); // the direction of the bombs from batman's current location (U, UR, R, DR, D, DL, L or UL)
            int jump;
            switch(BOMB_DIR.charAt(0)) {
                case 'U': 
                    H1 = Y0;
                    jump = Y0 - (Y0 - H0) / 2;
                    if(prevH == 'U' && jump > 1) {
                        jump -= 1; // little progressive push
                        prevH = '-';
                    } else {
                        prevH = 'U';
                    }
                    Y0 = Y0 == jump ? Y0-1 : jump;
                    X0 = decideForSide(BOMB_DIR, X0);
                    break;
                case 'D': 
                    H0 = Y0;
                    jump = Y0+(H1-Y0)/2;
                    if(prevH =='D' && jump < H - 1) {
                        jump += 1; // little progressive push
                        prevH = '-';
                    } else {
                        prevH = 'D';
                    }
                    Y0 = Y0 == jump ? Y0+1 : jump;
                    X0 = decideForSide(BOMB_DIR, X0);
                    break;
                default:
                    X0 = goSide(BOMB_DIR.charAt(0), X0);
            }
            System.out.println(X0 + " " + Y0); // the location of the next window Batman should jump to.
        }
    }
    
    private static int decideForSide(String bomb, int x) {
        if(bomb.length() > 1) {
             return goSide(bomb.charAt(1), x);
        }
        return x;
    }
    
    private static int goSide(char LR, int x) {
        if(LR == 'L') {
            W1 = x;
            int jump = x - (x - W0) / 2;
            if(prevW == 'L' && jump > 1) {
                jump -= 1; // little progressive push
                prevW = '-';
            } else {
                prevW = 'L';
            }
            return (x == jump ? x - 1 : jump);
        }
        W0 = x;
        int jump = x + (W1 - x) / 2;
        if(prevW == 'R' && jump < W - 1) {
            jump += 1; // little progressive push
            prevW = '-';
        } else {
            prevW = 'R';
        }
        return (x == jump ? x + 1 : jump);
    }
}
