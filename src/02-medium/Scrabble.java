import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {
    static int[] letterPoints = new int[]{1, 3, 3, 2, 1, 4, 2, 4, 1, 6, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 6, 4, 10};
    //                                    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P,  Q, R, S, T, U, V, W, X, Y,  Z

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        in.nextLine();
        List<String> dictionary = new ArrayList<String>(N);
        for (int i = 0; i < N; i++) {
            dictionary.add(in.nextLine());
        }
        String letters = in.nextLine();

        int maxPoints = 0;
        String resultWord = "";
        for (String word : dictionary) {
            int p = getPoints(word, new StringBuilder(letters));
            if (p > maxPoints) {
                maxPoints = p;
                resultWord = word;
            }
        }
        System.out.println(resultWord);
    }

    private static int getPoints(String word, StringBuilder letters) {
        int points = 0;
        for (char c : word.toCharArray()) {
            int index = letters.indexOf(String.valueOf(c));
            if (index == -1) {
                return 0;
            }
            points += letterPoints[c - 97];
            letters.deleteCharAt(index);
        }
        return points;
    }
}