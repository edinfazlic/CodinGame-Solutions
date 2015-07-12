import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {
	static int W;
	static int H;
	static int currentW = 0;
	static int currentH = -1;
	static boolean[][] matrix; //true = is black pixel, (false = white)

	static int lineWidth = 0;
	static int lineSpacing = 0;
	static int leftPadding;
	static int topLineStart;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		initMatrix(in);

		String image = in.nextLine();
		decode(image.split(" "));

		setMusicalScoreDetails();

		List<String> result = readScores();

		StringBuilder answer = new StringBuilder(result.size() * 3);
		for (String score : result) {
			answer.append(score).append(" ");
		}
		System.out.println(answer.deleteCharAt(result.size() * 3 - 1));
	}

	private static List<String> readScores() {
		List<String> result = new ArrayList<String>();
		for (int i = leftPadding; i < W; i++) { // scrolling left to right
			for (int line = 0; line < 6; line++) { // 5 staff lines + 1 ledger line

				int sliderOnLine = topLineStart + line * (lineWidth + lineSpacing) - 1;
				int sliderHalfLine = sliderOnLine + 1 - lineSpacing / 2;
				int sliderUnderLine = sliderOnLine + 1 - lineSpacing;

				if (matrix[sliderHalfLine][i]) { // if first pixel is in the space, that means note sits on the line
					boolean half = false;
					while (!matrix[sliderUnderLine][i]) { // scroll lower part of the note, until black pixel
						i++;
					}
					while (!matrix[sliderHalfLine][i]) { // scroll from the middle of the note through white pixels (if any)
						half = true;
						i++;
					}
					while (matrix[sliderHalfLine][i]) { // finish scrolling the note
						i++;
					}
					// D(line:5, res:2, code: 68), F(line:4, res:4, code: 70), A(line:3, res:6, code: 72)
					result.add(getScoreCharacter(12 - line * 2) + "" + (half ? 'H' : 'Q'));

					break;
				} else if (matrix[sliderOnLine][i]) { // if first pixel is on the line, that means line goes through the note
					int climb = 0;
					int flatLength = 0;
					int flatStart = i;
					while ((matrix[sliderOnLine][i] || matrix[sliderOnLine - 1][i]) && sliderHalfLine < sliderOnLine) {
					// if that pixel is black, or the one above (because of half notes whose line can be only 1 pixel wide),
					// and as long as the note height doesn't exceed half empty space (then it is a tail)
						while (matrix[sliderOnLine - 1][i]) { // climb up vertically on the notes edge
							sliderOnLine--;
							climb++;
							flatLength = 0;
							flatStart = i;
						}
						i++;
						flatLength++;
					}
					sliderOnLine += climb;
					boolean half = !matrix[sliderOnLine][flatStart + flatLength / 2]; // is white pixel on the line, under the flat part
					while (!matrix[sliderOnLine][i]) { // go through all white pixels (if any)
						i++;
					}
					while (matrix[sliderOnLine][i]) { // go through all black pixels
						i++;
					}
					// C(line:5, res:1, code: 67), E(line:4, res:3, code: 69), G(line:3, res:5, code: 71)...
					result.add(getScoreCharacter(11 - line * 2) + "" + (half ? 'H' : 'Q'));

					break;
				}
			}
		}
		return result;
	}

	private static char getScoreCharacter(int i) {
		return (char) (65 + (i + 1) % 7);
	}

	private static void setMusicalScoreDetails() {
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) { // scroll Top-Down, Left-Right
				if (matrix[j][i] && lineSpacing != 0) { // #4, end of line spacing
					return;
				}
				if (lineWidth != 0 && matrix[j][i]) { // #2, black pixel, continue measuring line width
					lineWidth++;
				} else if (lineWidth != 0) { // #3, end line width, now line spacing
					lineSpacing++;
				} else if (matrix[j][i]) { // #1, first black pixel, start measuring line width
					lineWidth++;
					topLineStart = j;
					leftPadding = i;
				}
			}
		}
	}

	private static void decode(String[] mes) {
		for (int i = 0; i < mes.length; i += 2) {
			addToMatrix(mes[i], Integer.valueOf(mes[i + 1]));
		}
	}

	private static void addToMatrix(String sign, Integer length) {
		boolean isBlackPixel = sign.equals("B");
		int i;
		for (i = currentW; i < length + currentW; i++) {
			if (i % W == 0) {
				currentH++;
			}
			matrix[currentH][i % W] = isBlackPixel;
		}
		currentW = i % W;
	}

	private static void initMatrix(Scanner in) {
		W = in.nextInt();
		H = in.nextInt();
		matrix = new boolean[H][W];
		in.nextLine();
	}
}