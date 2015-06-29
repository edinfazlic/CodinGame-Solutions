import java.util.Scanner;

class Player {

    private static final double MARS_GRAVITY = 3.711;
    public static final int REQUIRED_FLAT_GROUND = 1000;

    private static final int V_LIMIT = 40;
    private static final int MAX_POWER = 4;

    private static int FLAT_Y;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        calculateFlatHeight(in);

        double timeToBrake = initializeAndCalculateTimeToBreak(in);
        boolean brakingFullPowerReached = false;
        int time = 1;
        // game loop
        while (true) {
            int X = in.nextInt();
            int Y = in.nextInt();
            int HS = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            int VS = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int F = in.nextInt(); // the quantity of remaining fuel in liters.
            int R = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int P = in.nextInt(); // the thrust power (0 to 4).

            if (!brakingFullPowerReached && time > timeToBrake) { // brake
                if (++P == 4) {
                    brakingFullPowerReached = true;
                }
            } else if (brakingFullPowerReached) { // economize fuel
                double endSpeed = Math.sqrt(VS * VS + 2 * (Y - FLAT_Y) * (MARS_GRAVITY - (P - 1))) + 1;
                if (endSpeed < V_LIMIT) {
                    P--;
                }
            }

            System.out.println(R + " " + P); // R P. R is the desired rotation angle. P is the desired thrust power.
            time++;
        }
    }

    private static void calculateFlatHeight(Scanner in) {
        int N = in.nextInt(); // the number of points used to draw the surface of Mars.
        int LAND_PREV_X = in.nextInt();
        int LAND_PREV_Y = in.nextInt();
        for (int i = 1; i < N; i++) {
            int LAND_X = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int LAND_Y = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            if (LAND_PREV_Y == LAND_Y && LAND_X - LAND_PREV_X >= REQUIRED_FLAT_GROUND) {
                FLAT_Y = LAND_Y;
                System.err.println("Flat on: " + LAND_Y + ", start: " + LAND_PREV_X + ", length: " + (LAND_X - LAND_PREV_X));
            }
            LAND_PREV_X = LAND_X;
            LAND_PREV_Y = LAND_Y;
        }
    }

    private static double initializeAndCalculateTimeToBreak(Scanner in) {
        int X = in.nextInt();
        int Y = in.nextInt();
        int HS = in.nextInt(); // the horizontal speed (in m/s), can be negative.
        int VS = in.nextInt(); // the vertical speed (in m/s), can be negative.
        int F = in.nextInt(); // the quantity of remaining fuel in liters.
        int R = in.nextInt(); // the rotation angle in degrees (-90 to 90).
        int P = in.nextInt(); // the thrust power (0 to 4).

        double g2 = MAX_POWER - MARS_GRAVITY;
        // V^2 = 2gh  ==>  free fall
        // V0 * V0 = 2 * MARS_GRAVITY * distanceBeforeBraking
        // -----
        // V^2 = V0^2 - 2gh  ==>  linear movement with deceleration
        // V_LIMIT * V_LIMIT = V0 * V0 - 2 * g2 * ((Y - FLAT_Y) - distanceBeforeBraking)
        double distanceBeforeBraking = (V_LIMIT * V_LIMIT + 2 * g2 * (Y - FLAT_Y)) / (2 * (MARS_GRAVITY + g2));
        System.err.println("distanceBeforeBraking=" + distanceBeforeBraking);
        // h = (g * t^2) / 2  ==>  t = squareRoot(2 * h / g)
        double timeToBrake = Math.floor(Math.sqrt(2 * distanceBeforeBraking / MARS_GRAVITY)) - 3;
        System.err.println("timeToBrake=" + timeToBrake);

        System.out.println(R + " " + P);
        return timeToBrake;
    }
}
