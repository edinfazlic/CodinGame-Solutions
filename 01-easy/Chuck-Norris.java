import java.util.*;
import java.io.*;

class Solution {

    public static void main(String args[]) throws UnsupportedEncodingException {
        Scanner in = new Scanner(System.in);
        StringBuilder binaryMessage = new StringBuilder();
        String message = in.nextLine();
        for(byte b : message.getBytes()) {
            binaryMessage.append(String.format("%7s", Integer.toBinaryString(b)).replace(' ', '0'));
        }

        System.err.println("Original message: " + message);
        System.err.println("Binary message  : " + binaryMessage);

        boolean zeroSeries = binaryMessage.charAt(0) == '0';
        StringBuilder output = new StringBuilder();
        int seriesLength = 1;

        for(int i = 1; i < binaryMessage.length(); i++) {
            if(binaryMessage.charAt(i) == binaryMessage.charAt(i - 1)){
                seriesLength++;
            } else {
                output.append(formatSeries(zeroSeries, seriesLength)).append(" ");
                zeroSeries = !zeroSeries;
                seriesLength = 1;
            }
        }

        System.out.println(output.append(formatSeries(zeroSeries, seriesLength)));
    }

    private static String formatSeries(boolean zeroSeries, int seriesLength) {
        char[] seriesLengthAsChars = new char[seriesLength];
        Arrays.fill(seriesLengthAsChars, '0');
        return (zeroSeries ? "00" : "0") + " " + new String(seriesLengthAsChars);
    }
}
