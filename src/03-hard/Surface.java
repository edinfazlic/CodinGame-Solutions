import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

// on initialization: assign lake number to lake coordinate using neighbours lake number, or new number if neighbours
// are not lakes, and create map of lake parts representing same lake, and map of sizes of each part of the lake
class Solution {
	private static int newLakeNumber = 0;
	private static int[][] surface;
	private static Map<Integer, Set<Integer>> sameLakes = new HashMap<Integer, Set<Integer>>();
	private static Map<Integer, Integer> lakeSizes = new HashMap<Integer, Integer>();

	public static void main(String args[]) {
		long time = System.currentTimeMillis();
		Scanner in = new Scanner(System.in);
		int L = in.nextInt();
		int H = in.nextInt();
		initializeSurface(in, H, L);

		int N = in.nextInt();
		for (int i = 0; i < N; i++) {
			int X = in.nextInt();
			int Y = in.nextInt();
			Integer lakeNumber = surface[Y][X];
			if(lakeNumber == -1) {
				System.out.println(0);
			} else {
				int sum = 0;
				for (Integer partLakeNumber : sameLakes.get(lakeNumber)) {
					sum += lakeSizes.get(partLakeNumber);
				}
				System.out.println(sum);
			}
		}
		System.err.println((System.currentTimeMillis() - time) + "ms");
	}

	private static void initializeSurface(Scanner in, int H, int L) {
		surface = new int[H][L];
		for (int y = 0; y < H; y++) {
			char[] row = in.next().toCharArray();
			for(int x = 0; x < L; x++) {
				int left = x == 0 ? -1 : surface[y][x - 1];
				int up = y == 0 ? -1 : surface[y - 1][x];
				surface[y][x] = getLakeNumber(left, up, row[x]);
			}
		}
	}

	private static int getLakeNumber(int left, int up, char c) {
		if(c == '#') {
			return -1;
		}
		if(up == -1 && left == -1) { // if up and left are not lakes
			lakeSizes.put(++newLakeNumber, 1);
			HashSet<Integer> emptyLakePart = new HashSet<Integer>();
			emptyLakePart.add(newLakeNumber);
			sameLakes.put(newLakeNumber, emptyLakePart);
			return newLakeNumber;
		}
		if(left == -1) { // if left is not lake, then up is for sure
			lakeSizes.put(up, lakeSizes.get(up) + 1);
			return up;
		}
		if(up != -1 && up != left && !sameLakes.get(up).contains(left)) {
			// if up is a lake, then both are, and upper and left lakes are different, but they are not yet merged
			Set<Integer> upLakes = sameLakes.get(up);
			Set<Integer> leftLakes = sameLakes.get(left);
			mergeLakeToLake(up, upLakes, leftLakes);
			mergeLakeToLake(left, leftLakes, upLakes);
		}
		lakeSizes.put(left, lakeSizes.get(left) + 1);
		return left;
	}

	private static void mergeLakeToLake(int currentLakesSource, Set<Integer> currentLakes, Set<Integer> newLakes) {
		for(Integer currentLake : currentLakes) {
			if(currentLake != currentLakesSource) {
				sameLakes.get(currentLake).addAll(newLakes);
			}
		}
		currentLakes.addAll(newLakes);
		newLakes.add(currentLakesSource);
	}
}

/*
// example 20x20
########OOOO#OOO#### lake part 1 / lake part 2
########OOOOOO###### lake part 1 and then merged with lake part 2
########OO########## lake part 1
########OO########## lake part 1
#######OOO########## lake part 3 and then merged with lake part 1 and lake part 2
#################O## lake part 4
#################O## lake part 4
######OOO######OOO## lake part 5 / lake part 6 and then merged with lake part 4
######OOOO####OOOO## lake part 5 / lake part 7 and then merged with lake part 6 and lake part 4
##############OOOOO# lake part 7
##############OOO#OO lake part 7 / 7
##############OOOOOO lake part 7
#############OOOO### lake part 8 and then merged with lake parts: 7,6,4
####################
####################
#####OO############# lake part 9
####################
####################
####################
##############OOOOO# lake part 10

  .  .  .  .  .  .  .  .  1  1  1  1  .  2  2  2  .  .  .  .
  .  .  .  .  .  .  .  .  1  1  1  1  1  1  .  .  .  .  .  . merge
  .  .  .  .  .  .  .  .  1  1  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  .  1  1  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  3  3  3  .  .  .  .  .  .  .  .  .  . merge
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  4  .  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  4  .  .
  .  .  .  .  .  .  5  5  5  .  .  .  .  .  .  6  6  6  .  . /merge
  .  .  .  .  .  .  5  5  5  5  .  .  .  .  7  7  7  7  .  . /merge
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  7  7  7  7  7  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  7  7  7  .  7  7
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  7  7  7  7  7  7
  .  .  .  .  .  .  .  .  .  .  .  .  .  8  8  8  8  .  .  . merge
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  9  9  .  .  .  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
  .  .  .  .  .  .  .  .  .  .  .  .  .  . 10 10 10 10 10  .
 */
