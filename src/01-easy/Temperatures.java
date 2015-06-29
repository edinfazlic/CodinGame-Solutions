import java.util.Scanner;

class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the number of temperatures to analyse
        if (N == 0) {
            System.out.println("0");
            return;
        }
        in.nextLine();

        String[] temps = in.nextLine().split(" "); // the N temperatures expressed as integers ranging from -273 to 5526
        int min = Integer.parseInt(temps[0]);
        for (String stringTemp : temps) {
            int temp = Integer.parseInt(stringTemp);
            if (Math.abs(temp) < Math.abs(min) || (0 < temp && -min == temp)) {
                min = temp;
            }
        }

        System.out.println(min);
    }
}
