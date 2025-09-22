package com.plagiarism;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 文本相似度计算器
 * 提供多种相似度计算算法
 */
public class TextSimilarityCalculator {

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[a-zA-Z]");

    /**
     * 计算综合相似度
     * 结合余弦相似度和编辑距离相似度
     */
    public double calculateComprehensiveSimilarity(String text1, String text2) {
        // 文本预处理
        String processedText1 = preprocessText(text1);
        String processedText2 = preprocessText(text2);

        // 计算余弦相似度
        double cosineSimilarity = calculateCosineSimilarity(processedText1, processedText2);

        // 计算编辑距离相似度
        double editDistanceSimilarity = calculateEditDistanceSimilarity(processedText1, processedText2);

        // 计算字符级相似度
        double characterSimilarity = calculateCharacterSimilarity(processedText1, processedText2);

        // 加权平均
        return cosineSimilarity * 0.5 + editDistanceSimilarity * 0.3 + characterSimilarity * 0.2;
    }

    /**
     * 文本预处理
     */
    public String preprocessText(String text) {
        if (text == null)
            return "";

        // 去除标点符号和特殊字符
        String cleaned = text.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9\\s]", " ");

        // 转换为小写
        cleaned = cleaned.toLowerCase();

        // 去除多余空格
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }

    /**
     * 计算余弦相似度
     */
    public double calculateCosineSimilarity(String text1, String text2) {
        Map<String, Integer> vector1 = generateWordVector(text1);
        Map<String, Integer> vector2 = generateWordVector(text2);

        return calculateCosineSimilarity(vector1, vector2);
    }

    /**
     * 计算余弦相似度（基于词频向量）
     */
    private double calculateCosineSimilarity(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        Set<String> allWords = new HashSet<>();
        allWords.addAll(vector1.keySet());
        allWords.addAll(vector2.keySet());

        if (allWords.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        for (String word : allWords) {
            int freq1 = vector1.getOrDefault(word, 0);
            int freq2 = vector2.getOrDefault(word, 0);
            dotProduct += freq1 * freq2;
        }

        double norm1 = calculateVectorNorm(vector1);
        double norm2 = calculateVectorNorm(vector2);

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (norm1 * norm2);
    }

    /**
     * 生成词频向量
     */
    private Map<String, Integer> generateWordVector(String text) {
        Map<String, Integer> wordVector = new HashMap<>();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (!word.trim().isEmpty()) {
                wordVector.put(word, wordVector.getOrDefault(word, 0) + 1);
            }
        }

        return wordVector;
    }

    /**
     * 计算向量模长
     */
    private double calculateVectorNorm(Map<String, Integer> vector) {
        double sum = 0.0;
        for (int frequency : vector.values()) {
            sum += frequency * frequency;
        }
        return Math.sqrt(sum);
    }

    /**
     * 计算编辑距离相似度
     */
    public double calculateEditDistanceSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        int editDistance = calculateEditDistance(text1, text2);
        int maxLength = Math.max(text1.length(), text2.length());

        if (maxLength == 0) {
            return 1.0;
        }

        return 1.0 - (double) editDistance / maxLength;
    }

    /**
     * 计算编辑距离（Levenshtein距离）
     */
    private int calculateEditDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        // 初始化
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // 填充dp表
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }

        return dp[m][n];
    }

    /**
     * 计算字符级相似度
     */
    public double calculateCharacterSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        // 计算最长公共子序列
        int lcsLength = calculateLCS(text1, text2);
        int maxLength = Math.max(text1.length(), text2.length());

        if (maxLength == 0) {
            return 1.0;
        }

        return (double) lcsLength / maxLength;
    }

    /**
     * 计算最长公共子序列长度
     */
    private int calculateLCS(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    /**
     * 检测文本语言类型
     */
    public TextLanguage detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return TextLanguage.UNKNOWN;
        }

        int chineseCount = 0;
        int englishCount = 0;

        for (char c : text.toCharArray()) {
            if (CHINESE_PATTERN.matcher(String.valueOf(c)).matches()) {
                chineseCount++;
            } else if (ENGLISH_PATTERN.matcher(String.valueOf(c)).matches()) {
                englishCount++;
            }
        }

        if (chineseCount > englishCount) {
            return TextLanguage.CHINESE;
        } else if (englishCount > chineseCount) {
            return TextLanguage.ENGLISH;
        } else {
            return TextLanguage.MIXED;
        }
    }

    /**
     * 文本语言枚举
     */
    public enum TextLanguage {
        CHINESE, ENGLISH, MIXED, UNKNOWN
    }
}
