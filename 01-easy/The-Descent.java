import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        //game loop
        while (true) {
            int SX = in.nextInt();
            int SY = in.nextInt();
            int positionOfMax = 0;
            int maxHeight = 0;
            for (int i = 0; i < 8; i++) {
                int MH = in.nextInt(); // represents the height of one mountain, from 9 to 0. Mountain heights are provided from left to right.
                if(MH > maxHeight) {
                    positionOfMax = i;
                    maxHeight = MH;
                }
            }
/* Might try to fire more times in one pass, but code has Cyclomatic complexity = 5, and WORKS! */
            System.out.println(SX == positionOfMax ? "FIRE" : "HOLD");
        }
    }
}
