package com.plagiarism;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * PlagiarismDetector单元测试类
 * 测试主控制类的各种功能
 */
public class PlagiarismDetectorTest {
    
    private PlagiarismDetector detector;
    
    @BeforeEach
    void setUp() {
        detector = new PlagiarismDetector();
    }
    
    @Test
    @DisplayName("测试正常查重流程")
    void testNormalPlagiarismDetection(@TempDir Path tempDir) throws IOException {
        // 创建测试文件
        String originalContent = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String plagiarizedContent = "今天是周天，天气晴朗，我晚上要去看电影。";
        
        Path originalFile = tempDir.resolve("original.txt");
        Path plagiarizedFile = tempDir.resolve("plagiarized.txt");
        Path outputFile = tempDir.resolve("result.txt");
        
        Files.write(originalFile, originalContent.getBytes("UTF-8"));
        Files.write(plagiarizedFile, plagiarizedContent.getBytes("UTF-8"));
        
        // 执行查重
        double similarity = detector.detectPlagiarism(
            originalFile.toString(), 
            plagiarizedFile.toString()
        );
        
        // 验证结果
        assertTrue(similarity > 0.0, "相似度应该大于0");
        assertTrue(similarity < 1.0, "相似度应该小于1");
        assertTrue(similarity > 0.3, "相似度应该大于0.3");
    }
    
    @Test
    @DisplayName("测试完全相同的文本")
    void testIdenticalTexts(@TempDir Path tempDir) throws IOException {
        String content = "今天是星期天，天气晴，今天晚上我要去看电影。";
        
        Path originalFile = tempDir.resolve("original.txt");
        Path plagiarizedFile = tempDir.resolve("plagiarized.txt");
        
        Files.write(originalFile, content.getBytes("UTF-8"));
        Files.write(plagiarizedFile, content.getBytes("UTF-8"));
        
        double similarity = detector.detectPlagiarism(
            originalFile.toString(), 
            plagiarizedFile.toString()
        );
        
        assertEquals(1.0, similarity, 0.01, "完全相同的文本相似度应该为1.0");
    }
    
    @Test
    @DisplayName("测试完全不同的文本")
    void testCompletelyDifferentTexts(@TempDir Path tempDir) throws IOException {
        String originalContent = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String plagiarizedContent = "明天是星期一，天气阴，明天晚上我要去图书馆。";
        
        Path originalFile = tempDir.resolve("original.txt");
        Path plagiarizedFile = tempDir.resolve("plagiarized.txt");
        
        Files.write(originalFile, originalContent.getBytes("UTF-8"));
        Files.write(plagiarizedFile, plagiarizedContent.getBytes("UTF-8"));
        
        double similarity = detector.detectPlagiarism(
            originalFile.toString(), 
            plagiarizedFile.toString()
        );
        
        assertTrue(similarity > 0.0, "不同文本的相似度应该大于0");
        assertTrue(similarity < 1.0, "不同文本的相似度应该小于1");
    }
    
    @Test
    @DisplayName("测试空文件")
    void testEmptyFiles(@TempDir Path tempDir) throws IOException {
        Path originalFile = tempDir.resolve("original.txt");
        Path plagiarizedFile = tempDir.resolve("plagiarized.txt");
        
        Files.write(originalFile, "".getBytes("UTF-8"));
        Files.write(plagiarizedFile, "".getBytes("UTF-8"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectPlagiarism(originalFile.toString(), plagiarizedFile.toString());
        }, "空文件应该抛出IllegalArgumentException");
    }
    
    @Test
    @DisplayName("测试不存在的文件")
    void testNonExistentFiles() {
        String nonExistentFile = "non_existent_file.txt";
        
        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectPlagiarism(nonExistentFile, nonExistentFile);
        }, "不存在的文件应该抛出IllegalArgumentException");
    }
    
    @Test
    @DisplayName("测试长文本处理")
    void testLongTextProcessing(@TempDir Path tempDir) throws IOException {
        // 创建长文本
        StringBuilder originalSb = new StringBuilder();
        StringBuilder plagiarizedSb = new StringBuilder();
        
        String baseText = "今天天气很好，我要去看电影。";
        for (int i = 0; i < 100; i++) {
            originalSb.append(baseText);
            if (i % 2 == 0) {
                plagiarizedSb.append(baseText);
            } else {
                plagiarizedSb.append("明天天气很好，我要去看电影。");
            }
        }
        
        Path originalFile = tempDir.resolve("long_original.txt");
        Path plagiarizedFile = tempDir.resolve("long_plagiarized.txt");
        
        Files.write(originalFile, originalSb.toString().getBytes("UTF-8"));
        Files.write(plagiarizedFile, plagiarizedSb.toString().getBytes("UTF-8"));
        
        long startTime = System.currentTimeMillis();
        double similarity = detector.detectPlagiarism(
            originalFile.toString(), 
            plagiarizedFile.toString()
        );
        long endTime = System.currentTimeMillis();
        
        long executionTime = endTime - startTime;
        
        assertTrue(executionTime < 5000, "长文本处理时间应该小于5秒");
        assertTrue(similarity > 0.0, "长文本相似度应该大于0");
        assertTrue(similarity < 1.0, "长文本相似度应该小于1");
    }
    
    @Test
    @DisplayName("测试中英文混合文本")
    void testMixedLanguageTexts(@TempDir Path tempDir) throws IOException {
        String originalContent = "Today is Sunday, 今天是星期天，天气晴，I will go to see a movie tonight.";
        String plagiarizedContent = "Today is Sunday, 今天是周天，天气晴朗，I will go to see a movie tonight.";
        
        Path originalFile = tempDir.resolve("mixed_original.txt");
        Path plagiarizedFile = tempDir.resolve("mixed_plagiarized.txt");
        
        Files.write(originalFile, originalContent.getBytes("UTF-8"));
        Files.write(plagiarizedFile, plagiarizedContent.getBytes("UTF-8"));
        
        double similarity = detector.detectPlagiarism(
            originalFile.toString(), 
            plagiarizedFile.toString()
        );
        
        assertTrue(similarity > 0.5, "中英文混合文本相似度应该大于0.5");
        assertTrue(similarity < 1.0, "中英文混合文本相似度应该小于1.0");
    }
    
    @Test
    @DisplayName("测试特殊字符处理")
    void testSpecialCharacters(@TempDir Path tempDir) throws IOException {
        String originalContent = "今天天气很好！！！@#$%^&*()";
        String plagiarizedContent = "今天天气很好？？？@#$%^&*()";
        
        Path originalFile = tempDir.resolve("special_original.txt");
        Path plagiarizedFile = tempDir.resolve("special_plagiarized.txt");
        
        Files.write(originalFile, originalContent.getBytes("UTF-8"));
        Files.write(plagiarizedFile, plagiarizedContent.getBytes("UTF-8"));
        
        double similarity = detector.detectPlagiarism(
            originalFile.toString(), 
            plagiarizedFile.toString()
        );
        
        assertTrue(similarity > 0.8, "特殊字符文本相似度应该大于0.8");
        assertTrue(similarity < 1.0, "特殊字符文本相似度应该小于1.0");
    }
    
    @Test
    @DisplayName("测试性能基准")
    void testPerformanceBenchmark(@TempDir Path tempDir) throws IOException {
        String originalContent = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String plagiarizedContent = "今天是周天，天气晴朗，我晚上要去看电影。";
        
        Path originalFile = tempDir.resolve("perf_original.txt");
        Path plagiarizedFile = tempDir.resolve("perf_plagiarized.txt");
        
        Files.write(originalFile, originalContent.getBytes("UTF-8"));
        Files.write(plagiarizedFile, plagiarizedContent.getBytes("UTF-8"));
        
        // 执行多次测试
        int iterations = 100;
        long totalTime = 0;
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            detector.detectPlagiarism(originalFile.toString(), plagiarizedFile.toString());
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        double averageTimeMs = (totalTime / iterations) / 1_000_000.0;
        
        assertTrue(averageTimeMs < 100, "平均处理时间应该小于100ms");
    }
}

