package com.plagiarism;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TextSimilarityCalculator单元测试类
 * 测试文本相似度计算器的各种功能
 */
public class TextSimilarityCalculatorTest {

    private TextSimilarityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TextSimilarityCalculator();
    }

    @Test
    @DisplayName("测试完全相同的文本")
    void testIdenticalTexts() {
        String text1 = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String text2 = "今天是星期天，天气晴，今天晚上我要去看电影。";

        double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);

        assertEquals(1.0, similarity, 0.01, "完全相同的文本相似度应该为1.0");
    }

    @Test
    @DisplayName("测试完全不同的文本")
    void testCompletelyDifferentTexts() {
        String text1 = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String text2 = "明天是星期一，天气阴，明天晚上我要去图书馆。";

        double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);

        assertTrue(similarity > 0.0, "不同文本的相似度应该大于0");
        assertTrue(similarity < 1.0, "不同文本的相似度应该小于1");
    }

    @Test
    @DisplayName("测试空文本")
    void testEmptyTexts() {
        String text1 = "";
        String text2 = "";

        double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);

        assertEquals(1.0, similarity, 0.01, "两个空文本的相似度应该为1.0");
    }

    @Test
    @DisplayName("测试一个空文本和一个非空文本")
    void testOneEmptyText() {
        String text1 = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String text2 = "";

        double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);

        assertEquals(0.0, similarity, 0.01, "空文本与非空文本的相似度应该为0");
    }

    @Test
    @DisplayName("测试null文本")
    void testNullTexts() {
        String text1 = null;
        String text2 = "今天是星期天，天气晴，今天晚上我要去看电影。";

        double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);

        assertEquals(0.0, similarity, 0.01, "null文本的相似度应该为0");
    }

    @Test
    @DisplayName("测试轻微修改的文本")
    void testSlightlyModifiedText() {
        String text1 = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String text2 = "今天是周天，天气晴朗，我晚上要去看电影。";

        double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);

        assertTrue(similarity > 0.3, "轻微修改的文本相似度应该大于0.3");
        assertTrue(similarity < 0.9, "轻微修改的文本相似度应该小于0.9");
    }

    @Test
    @DisplayName("测试余弦相似度计算")
    void testCosineSimilarity() {
        String text1 = "今天天气很好";
        String text2 = "今天天气很好";

        double similarity = calculator.calculateCosineSimilarity(text1, text2);

        assertEquals(1.0, similarity, 0.01, "相同文本的余弦相似度应该为1.0");
    }

    @Test
    @DisplayName("测试编辑距离相似度计算")
    void testEditDistanceSimilarity() {
        String text1 = "今天天气很好";
        String text2 = "今天天气好";

        double similarity = calculator.calculateEditDistanceSimilarity(text1, text2);

        assertTrue(similarity > 0.8, "轻微删除的文本编辑距离相似度应该大于0.8");
        assertTrue(similarity < 1.0, "有差异的文本编辑距离相似度应该小于1.0");
    }

    @Test
    @DisplayName("测试字符级相似度计算")
    void testCharacterSimilarity() {
        String text1 = "今天天气很好";
        String text2 = "今天天气很好";

        double similarity = calculator.calculateCharacterSimilarity(text1, text2);

        assertEquals(1.0, similarity, 0.01, "相同文本的字符级相似度应该为1.0");
    }

    @Test
    @DisplayName("测试文本预处理功能")
    void testPreprocessText() {
        String originalText = "今天天气很好！！！";
        String expectedText = "今天天气很好";

        String processedText = calculator.preprocessText(originalText);

        assertEquals(expectedText, processedText, "预处理应该去除标点符号");
    }

    @Test
    @DisplayName("测试语言检测功能")
    void testDetectLanguage() {
        String chineseText = "今天天气很好";
        String englishText = "Today is a good day";
        String mixedText = "今天天气很好 Today is good";

        assertEquals(TextSimilarityCalculator.TextLanguage.CHINESE,
                calculator.detectLanguage(chineseText), "应该检测为中文");
        assertEquals(TextSimilarityCalculator.TextLanguage.ENGLISH,
                calculator.detectLanguage(englishText), "应该检测为英文");
        assertEquals(TextSimilarityCalculator.TextLanguage.MIXED,
                calculator.detectLanguage(mixedText), "应该检测为混合语言");
    }

    @Test
    @DisplayName("测试长文本性能")
    void testLongTextPerformance() {
        // 构造长文本
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        String baseText = "今天天气很好，我要去看电影。";
        for (int i = 0; i < 100; i++) {
            sb1.append(baseText);
            if (i % 2 == 0) {
                sb2.append(baseText);
            } else {
                sb2.append("明天天气很好，我要去看电影。");
            }
        }

        long startTime = System.currentTimeMillis();
        double similarity = calculator.calculateComprehensiveSimilarity(sb1.toString(), sb2.toString());
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;

        assertTrue(executionTime < 1000, "长文本处理时间应该小于1秒");
        assertTrue(similarity > 0.0, "长文本相似度应该大于0");
        assertTrue(similarity < 1.0, "长文本相似度应该小于1");
    }

    @Test
    @DisplayName("测试边界情况")
    void testBoundaryConditions() {
        // 测试单个字符
        double similarity1 = calculator.calculateComprehensiveSimilarity("a", "a");
        assertEquals(1.0, similarity1, 0.01, "单个相同字符相似度应该为1.0");

        // 测试单个不同字符
        double similarity2 = calculator.calculateComprehensiveSimilarity("a", "b");
        assertTrue(similarity2 < 1.0, "单个不同字符相似度应该小于1.0");

        // 测试只有标点符号
        double similarity3 = calculator.calculateComprehensiveSimilarity("!!!", "???");
        assertEquals(1.0, similarity3, 0.01, "只有标点符号的文本相似度应该为1.0");
    }
}

