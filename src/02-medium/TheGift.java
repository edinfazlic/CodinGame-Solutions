import java.util.*;

class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        int C = in.nextInt(); // space
        int sum = 0;
        List<Integer> budgets = new ArrayList<Integer>(N);
        for (int i = 0; i < N; i++) {
            int B = in.nextInt();
            budgets.add(B);
            sum += B;
        }

        if (sum < C) {
            System.out.println("IMPOSSIBLE");
            return;
        }

        Collections.sort(budgets);

        for (int i = 0; i < N; i++) {
            int budget = budgets.get(i);
            if (C < budget * (N - i)) {
                budget = C / (N - i);
            }
            System.out.println(budget);
            C -= budget;
        }
    }
}