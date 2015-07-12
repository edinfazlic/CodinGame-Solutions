import java.util.Scanner;

class Solution {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int attractionSize = in.nextInt();
		int C = in.nextInt();
		int numberOfGroups = in.nextInt();
		int[] P = new int[numberOfGroups];
		for (int i = 0; i < numberOfGroups; i++) {
			P[i] = in.nextInt(); // group size
		}

		int queueStart = 0;
		long earnings = 0;
		for(int i = 0; i < C; i++) {
			int currentCapacity = 0;
			int currentGroup = queueStart;
			boolean fullCircle = false;

			while(P[currentGroup] + currentCapacity <= attractionSize && !(fullCircle && currentGroup == queueStart)) {
				currentCapacity += P[currentGroup];

				if(++currentGroup == numberOfGroups) {
					currentGroup = 0;
					fullCircle = true;
				}
			}
			queueStart = currentGroup;
			earnings += currentCapacity;
		}
		System.out.println(earnings);
	}
}