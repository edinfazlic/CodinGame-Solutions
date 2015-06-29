import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Player {
    static String SPEED = "SPEED";
    static String SLOW = "SLOW";
    static String JUMP = "JUMP";
    static String WAIT = "WAIT";

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int R = in.nextInt(); // the length of the road before the gap.
        int G = in.nextInt(); // the length of the gap.
        int L = in.nextInt(); // the length of the landing platform.

        int S = in.nextInt(); // the motorbike's speed.
        int X = in.nextInt(); // the position on the road of the motorbike.

        List<String> commands = adjustSpeedTillStartOfGap(R - 1, G + 1, S, X);
        for (String command : commands) {
            System.out.println(command);
        }
        System.out.println(JUMP);
        while (true) {
//            S = in.nextInt();
//            X = in.nextInt();
            System.out.println(SLOW); // A single line containing one of 4 keywords: SPEED, SLOW, JUMP, WAIT.
        }
    }

    private static List<String> adjustSpeedTillStartOfGap(int gapAt, int speedToJump, int speed, int x) {
        List<String> commands = new ArrayList<String>();

        while (speed < speedToJump) {
            speed++;
            x += speed;
            commands.add(SPEED);
        }
        while (speed > speedToJump) {
            speed--;
            x += speed;
            commands.add(SLOW);
        }

        while (x < gapAt) {
            x += speed;
            commands.add(WAIT);
        }
        return commands;
    }
}
