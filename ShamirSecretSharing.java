import org.json.JSONObject;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            // Read JSON test cases
            JSONObject testCase1 = new JSONObject(new FileReader("C:\Users\nehas\Onedrive\Desktop\testcase1.json"));
            JSONObject testCase2 = new JSONObject(new FileReader("C:\Users\nehas\Onedrive\Desktop\testcase2.json"));


            System.out.println("Secret for Test Case 1: " + findSecret(testCase1));
            System.out.println("Secret for Test Case 2: " + findSecret(testCase2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BigInteger findSecret(JSONObject testCase) {
        // Extract n and k
        JSONObject keys = testCase.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Parse roots
        List<Point> points = new ArrayList<>();
        for (String key : testCase.keySet()) {
            if (!key.equals("keys")) {
                int x = Integer.parseInt(key);
                JSONObject root = testCase.getJSONObject(key);
                int base = root.getInt("base");
                String value = root.getString("value");

                // Decode y using the given base
                BigInteger y = new BigInteger(value, base);
                points.add(new Point(x, y));
            }
        }

        // Find the constant term using Lagrange interpolation
        return lagrangeInterpolation(points, k);
    }

    private static BigInteger lagrangeInterpolation(List<Point> points, int k) {
        BigInteger constant = BigInteger.ZERO;

        // Lagrange interpolation to find f(0) = constant term
        for (int i = 0; i < k; i++) {
            Point p1 = points.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    Point p2 = points.get(j);
                    numerator = numerator.multiply(BigInteger.valueOf(-p2.x));
                    denominator = denominator.multiply(BigInteger.valueOf(p1.x - p2.x));
                }
            }

            // Add contribution of this term to the constant
            BigInteger term = p1.y.multiply(numerator).divide(denominator);
            constant = constant.add(term);
        }

        return constant;
    }

    // Helper class for storing points
    static class Point {
        int x;
        BigInteger y;

        public Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
