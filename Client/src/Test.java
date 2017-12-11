import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int[] arr = new int[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        int[] arr2 = Arrays.copyOfRange(arr, 1, arr.length);
        System.out.println(Arrays.toString(arr2));
    }
}
