import java.util.Scanner;

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int LX = in.nextInt(); // the X position of the light of power
        int LY = in.nextInt(); // the Y position of the light of power
        int TX = in.nextInt(); // Thor's starting X position
        int TY = in.nextInt(); // Thor's starting Y position
        // Upper(North) Left(West) corner is (0,0)
        // game loop
        while (true) {

            int E = in.nextInt(); // The level of Thor's remaining energy, representing the number of moves he can still make.
            String goTo = "";

            if (TY > LY) { // if Thor is lower than "the light of power"
                goTo = "N";
                TY -= 1;
            } else if (TY < LY) { // if Thor is higher than "the light of power"
                goTo = "S";
                TY += 1;
            }

            if (TX > LX) { // if Thor is to the right of "the light of power"
                goTo += "W";
                TX -= 1;
            } else if (TX < LX) { // if Thor is to the left of "the light of power"
                goTo += "E";
                TX += 1;
            }

            System.out.println(goTo);
        }
    }
}
