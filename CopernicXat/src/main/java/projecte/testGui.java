package projecte;

import java.io.IOException;
import java.Util.Scanner;

public class testGui {
    int width = 40;
    int height = 9;

    char horizontalChar = '-';
    char verticalChar = '|';
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {

        String[] contingut = {
                "",
                "Benvingut a CopernicXat",
                "",
                "1.- [Sign in]",
                "2.- [Sign up]",
                "3.- [Exit]   ",
                "",
        };

        for (int i = 0; i < width; i++) {
            System.out.print(horizontalChar);
        }
        System.out.println();

        for (int i = 0; i < height - 2; i++) {
            System.out.print(verticalChar);
            if (i < contingut.length) {
                int contentLength = contingut[i].length();
                int padding = (width - 2 - contentLength) / 2;
                for (int j = 0; j < padding; j++) {
                    System.out.print(" ");
                }
                System.out.print(contingut[i]);
                for (int j = padding + contentLength; j < width - 2; j++) {
                    System.out.print(" ");
                }
            }
            System.out.println(verticalChar);
        }

        for (int i = 0; i < width; i++) {
            System.out.print(horizontalChar);
        }
        System.out.print("Introdueix una opció: ");
        int op = sc.nextInt();



    }
}