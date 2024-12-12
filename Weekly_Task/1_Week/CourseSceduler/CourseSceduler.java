import java.sql.Array;
import java.sql.SQLOutput;
import java.util.Arrays;
 
public class CourseScheduler {
    public static int maxNonOverlappingCourses(int[][] courses) {
        for (int i = 0; i < courses.length - 1; i++) {
            int min = courses[i][1];
            int minIndex = i;
            for (int j = i+1; j < courses.length; j++) {
                if (courses[j][1] < min) {
                    min = courses[j][1];
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int[] temp = courses[minIndex];
                courses[minIndex] = courses[i];
                courses[i] = temp;
            }
        }
 
        int count = 0;
        int currentEndHour = 0;
        for (int i = 0; i < courses.length; i++) {
            if (courses[i][0] >= currentEndHour) {
                currentEndHour = courses[i][1];
                count++;
            }
        }
        return count;
    }
 
     public static void main(String[] args) {
         int[][] arr = {{9, 11}, {10, 12}, {11, 13}, {15, 16}};
         maxNonOverlappingCourses(arr);
         for (int i = 0; i < arr.length; i++) {
             System.out.println(Arrays.toString(arr[i]));
         }
         System.out.println( maxNonOverlappingCourses(arr));
     }
}
