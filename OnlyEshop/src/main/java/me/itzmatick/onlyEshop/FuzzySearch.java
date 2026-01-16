package me.itzmatick.onlyEshop;

public class FuzzySearch {

    public static double getSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        String a = s1.toLowerCase(), b = s2.toLowerCase();
        int n = a.length(), m = b.length();
        if (Math.max(n, m) == 0) return 1.0;
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++)
            for (int j = 1; j <= m; j++)
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1));
        return 1.0 - ((double) d[n][m] / Math.max(n, m));
    }
}
