import java.util.Scanner;

class Solution {
	private static int minLength = Integer.MAX_VALUE;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int N = in.nextInt();
		String[] input = new String[N];
		for (int i = 0; i < N; i++) {
			input[i] = in.next();
		}
		permutation("", input);

		System.out.println(minLength);
	}

	private static void permutation(String prefix, String[] list) {
		int listSize = list.length;
		if (listSize == 0) {
			minLength = prefix.length();
			System.err.println("Current shortest sequence: " + prefix);
		} else {
			for (int i = 0; i < listSize; i++) {
				String newWord = list[i];
				if (prefix.contains(newWord)) {
					permutation(prefix, createNewList(list, listSize, i));
				} else {
					// if minLength is 8, and length of prefix is 6, and of newWord is 5; newWord must match at least 4
					for (int j = newWord.length() - 1; j > newWord.length() - (minLength - prefix.length()); j--) {
						if (prefix.endsWith(newWord.substring(0, j))) { // it's permutations, every word will be first
							permutation(prefix + newWord.substring(j), createNewList(list, listSize, i));
							break;
						}
					}
				}
			}
		}
	}

	private static String[] createNewList(String[] list, int listSize, int i) {
		String[] newList = new String[list.length - 1];
		System.arraycopy(list, 0, newList, 0, i);
		System.arraycopy(list, i + 1, newList, i, listSize - (i + 1));
		return newList;
	}
}