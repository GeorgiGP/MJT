import java.util.Arrays;
 
public class TextJustifier {
    public static String[] justifyText(String[] words, int maxWidth) {
        if (words.length == 0) return words;
        if (maxWidth <= 0) {
            String[] result = new String[1];
            result[0] = "";
            return result;
        }
 
        //Result array set up start
        int countRows = 1;
        int countLetters = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].isBlank()) {
                continue;
            }
            if (countLetters == 0) {
                countLetters = words[i].length();
            }
            else if (countLetters + words[i].length() + 1 > maxWidth) {
                countRows++;
                i--;
                countLetters = 0;
            }
            else {
                countLetters += 1 + words[i].length();
            }
        }
        String[] result = new String[countRows];
        Arrays.fill(result, "");
        //Result array has been set up
        //Start filling
 
 
        String[] curr = new String[maxWidth];
        Arrays.fill(curr, "");
        int currRow = 0;
        int currRowLettersCount = 0;
        int currRowWords = 0;
 
        for (int i = 0; i < words.length; i++) {
            if (words[i].isBlank()) {
                continue;
            }
            if (words[i].length() + currRowLettersCount + (currRowWords) > maxWidth) {
 
                int allSpaces = (maxWidth - currRowLettersCount);
                int countIntervals = currRowWords - 1;
 
                if (countIntervals == 0) {
                    result[currRow] = curr[0];
 
                    for (int j = 0; j < allSpaces; j++) {
                        result[currRow] += " ";
                    }
                    Arrays.fill(curr, "");
                    currRow++;
                    currRowLettersCount = 0;
                    currRowWords = 0;
                    --i;
                    continue;
                }
                int smallSpace = allSpaces / countIntervals;
                int bigSpace = smallSpace + 1;
                int countBigSpaces = (allSpaces - smallSpace * countIntervals);
                int countSmallSpaces = countIntervals - countBigSpaces;
 
                for (int j = 0; j < countBigSpaces; j++) {
                    result[currRow] += curr[j];
                    for (int k = 0; k < bigSpace; k++) {
                        result[currRow] += " ";
                    }
                }
                for (int j = 0; j < countSmallSpaces; j++) {
                    result[currRow] += curr[j + countBigSpaces];
                    for (int k = 0; k < smallSpace; k++) {
                        result[currRow] += " ";
                    }
                }
                result[currRow] += curr[countIntervals];
                Arrays.fill(curr, "");
                currRow++;
                currRowLettersCount = 0;
                currRowWords = 0;
                --i;
            }
            else {
                curr[currRowWords++]=words[i];
                currRowLettersCount += words[i].length();
            }
        }
 
        if (currRowWords >= 1) {
            for (int i = 0; i < currRowWords - 1; i++) {
                result[currRow]+= curr[i] + " ";
            }
            result[currRow]+= curr[currRowWords - 1];
            int lastSpaces = maxWidth - (currRowLettersCount + currRowWords - 1);
            for (int i = 0; i < lastSpaces; i++) {
                result[currRow] += " ";
            }
        }
        else {
            for (int i = 0; i < maxWidth; i++) {
                result[currRow] += " ";
            }
        }
 
        return result;
    }
 
    public static void main(String[] args)
    {
        String[] w = {"            ", "   "};
        String[] ans = justifyText(new String[]{"1"},11);
        for (int i = 0; i < ans.length; i++) {
            System.out.println("\"" + ans[i] + "\"");
        }
    }
}
