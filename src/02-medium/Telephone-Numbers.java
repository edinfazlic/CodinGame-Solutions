import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        int result = 0;
        List<String> allPhoneNumbers = new ArrayList<String>();
        for (int i = 0; i < N; i++) {
            String phoneNumber = in.next();
            List<String> reducedList = reduceList(allPhoneNumbers, phoneNumber.charAt(0));
            int k; // matched digits
            for (k = 0; k < phoneNumber.length(); k++) {
                // inverse because of List#remove
                for (int j = reducedList.size() - 1; j >= 0; j--) {
                    // if existing phone number has no more digits or current digit doesn't match
                    if (reducedList.get(j).length() == k || reducedList.get(j).charAt(k) != phoneNumber.charAt(k)) {
                        reducedList.remove(j);
                    }
                }
                if (reducedList.size() == 0) {
                    break;
                }
            }

            int unmatchedDigits = phoneNumber.length() - k;
            result += unmatchedDigits;
            allPhoneNumbers.add(phoneNumber);
        }
        System.out.println(result); // The number of elements (referencing a number) stored in the structure.
    }

    private static List<String> reduceList(List<String> phoneNumbers, char firstDigit) {
        List<String> reducedList = new ArrayList<String>();
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumber.charAt(0) == firstDigit) {
                reducedList.add(phoneNumber);
            }
        }
        return reducedList;
    }
}