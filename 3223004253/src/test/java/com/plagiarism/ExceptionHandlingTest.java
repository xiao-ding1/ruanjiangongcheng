package com.plagiarism;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 异常处理单元测试类
 * 测试计算模块中各种异常情况的处理
 */
public class ExceptionHandlingTest {

    private PlagiarismDetector detector;
    private FileProcessor fileProcessor;
    private TextSimilarityCalculator calculator;

    @BeforeEach
    void setUp() {
        this.detector = new PlagiarismDetector();
        this.fileProcessor = new FileProcessor();
        this.calculator = new TextSimilarityCalculator();
    }

    // ==================== PlagiarismDetector异常测试 ====================

    @Test
    @DisplayName("测试命令行参数数量不正确")
    void testInvalidArgumentCount() {
        // 模拟命令行参数数量错误的情况
        String[] tooFewArgs = {"only_one_arg"};
        String[] tooManyArgs = {"arg1", "arg2", "arg3", "arg4"};
        
        // 注意：main方法是静态方法，会调用System.exit(1)，无法直接测试
        // 这里主要验证参数验证逻辑
        assertDoesNotThrow(() -> {
            // 测试参数数量验证逻辑
            if (tooFewArgs.length != 3) {
                throw new IllegalArgumentException("参数数量不正确");
            }
        });
    }

    @Test
    @DisplayName("测试无效的文件路径")
    void testInvalidFilePath() {
        // 测试空路径
        assertThrows(IllegalArgumentException.class, () -> {
            detector.validateInputs("", "valid.txt", "output.txt");
        }, "空路径应该抛出IllegalArgumentException");

        // 测试null路径
        assertThrows(IllegalArgumentException.class, () -> {
            detector.validateInputs(null, "valid.txt", "output.txt");
        }, "null路径应该抛出IllegalArgumentException");

        // 测试包含非法字符的路径
        assertThrows(IllegalArgumentException.class, () -> {
            detector.validateInputs("invalid|path", "valid.txt", "output.txt");
        }, "包含非法字符的路径应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试文件不存在")
    void testFileNotExists() {
        // 测试不存在的文件
        assertThrows(IllegalArgumentException.class, () -> {
            detector.validateInputs("nonexistent.txt", "valid.txt", "output.txt");
        }, "不存在的文件应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试空文件内容")
    void testEmptyFileContent(@TempDir Path tempDir) throws IOException {
        // 创建空文件
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, "".getBytes());

        Path validFile = tempDir.resolve("valid.txt");
        Files.write(validFile, "测试内容".getBytes());

        // 测试空原文文件
        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectPlagiarism(emptyFile.toString(), validFile.toString());
        }, "空原文文件应该抛出IllegalArgumentException");

        // 测试空抄袭版文件
        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectPlagiarism(validFile.toString(), emptyFile.toString());
        }, "空抄袭版文件应该抛出IllegalArgumentException");
    }

    @Test
    @DisplayName("测试只包含空白字符的文件")
    void testWhitespaceOnlyFile(@TempDir Path tempDir) throws IOException {
        // 创建只包含空白字符的文件
        Path whitespaceFile = tempDir.resolve("whitespace.txt");
        Files.write(whitespaceFile, "   \t\n\r   ".getBytes());

        Path validFile = tempDir.resolve("valid.txt");
        Files.write(validFile, "测试内容".getBytes());

        // 测试只包含空白字符的原文文件
        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectPlagiarism(whitespaceFile.toString(), validFile.toString());
        }, "只包含空白字符的文件应该抛出IllegalArgumentException");
    }

    // ==================== FileProcessor异常测试 ====================

    @Test
    @DisplayName("测试文件读取异常")
    void testFileReadException() {
        // 测试读取不存在的文件
        assertThrows(IOException.class, () -> {
            fileProcessor.readFile("nonexistent_file.txt");
        }, "读取不存在的文件应该抛出IOException");
    }

    @Test
    @DisplayName("测试文件写入异常")
    void testFileWriteException(@TempDir Path tempDir) {
        // 测试写入到不存在的目录
        assertThrows(IOException.class, () -> {
            fileProcessor.writeResult("nonexistent/directory/output.txt", 0.85);
        }, "写入到不存在的目录应该抛出IOException");
    }

    @Test
    @DisplayName("测试获取不存在文件的大小")
    void testGetSizeOfNonExistentFile() {
        // 测试获取不存在文件的大小
        assertThrows(IOException.class, () -> {
            fileProcessor.getFileSize("nonexistent_file.txt");
        }, "获取不存在文件的大小应该抛出IOException");
    }

    @Test
    @DisplayName("测试文件路径验证")
    void testFilePathValidation() {
        // 测试null路径
        assertFalse(fileProcessor.isValidPath(null), "null路径应该无效");

        // 测试空路径
        assertFalse(fileProcessor.isValidPath(""), "空路径应该无效");

        // 测试只包含空格的路径
        assertFalse(fileProcessor.isValidPath("   "), "只包含空格的路径应该无效");

        // 测试有效路径
        assertTrue(fileProcessor.isValidPath("valid/path/file.txt"), "有效路径应该返回true");
    }

    @Test
    @DisplayName("测试UTF-8编码文件处理")
    void testUTF8FileHandling(@TempDir Path tempDir) throws IOException {
        // 创建包含中文字符的UTF-8文件
        String chineseContent = "这是一个包含中文字符的测试文件。";
        Path utf8File = tempDir.resolve("utf8.txt");
        Files.write(utf8File, chineseContent.getBytes("UTF-8"));

        // 测试读取UTF-8文件
        assertDoesNotThrow(() -> {
            String content = fileProcessor.readFile(utf8File.toString());
            assertEquals(chineseContent, content, "UTF-8文件内容应该正确读取");
        }, "读取UTF-8文件不应该抛出异常");
    }

    // ==================== TextSimilarityCalculator异常测试 ====================

    @Test
    @DisplayName("测试null文本处理")
    void testNullTextHandling() {
        // 测试null文本预处理
        String result = calculator.preprocessText(null);
        assertEquals("", result, "null文本预处理应该返回空字符串");

        // 测试null文本相似度计算
        double similarity = calculator.calculateEditDistanceSimilarity(null, "测试文本");
        assertEquals(0.0, similarity, 0.001, "null文本相似度应该为0");

        // 测试两个null文本
        double similarity2 = calculator.calculateEditDistanceSimilarity(null, null);
        assertEquals(0.0, similarity2, 0.001, "两个null文本相似度应该为0");
    }

    @Test
    @DisplayName("测试空字符串处理")
    void testEmptyStringHandling() {
        // 测试空字符串预处理
        String result = calculator.preprocessText("");
        assertEquals("", result, "空字符串预处理应该返回空字符串");

        // 测试空字符串相似度计算
        double similarity = calculator.calculateEditDistanceSimilarity("", "测试文本");
        assertEquals(0.0, similarity, 0.001, "空字符串相似度应该为0");

        // 测试两个空字符串
        double similarity2 = calculator.calculateEditDistanceSimilarity("", "");
        assertEquals(1.0, similarity2, 0.001, "两个空字符串相似度应该为1");
    }

    @Test
    @DisplayName("测试特殊字符处理")
    void testSpecialCharacterHandling() {
        // 测试包含特殊字符的文本
        String textWithSpecialChars = "Hello! @#$%^&*() World! 测试文本...";
        String processed = calculator.preprocessText(textWithSpecialChars);
        
        // 预处理后应该移除特殊字符
        assertFalse(processed.contains("@"), "预处理后不应该包含@符号");
        assertFalse(processed.contains("#"), "预处理后不应该包含#符号");
        assertTrue(processed.contains("hello"), "预处理后应该包含hello（小写）");
        assertTrue(processed.contains("world"), "预处理后应该包含world（小写）");
        assertTrue(processed.contains("测试文本"), "预处理后应该包含中文字符");
    }

    @Test
    @DisplayName("测试大文本内存处理")
    void testLargeTextMemoryHandling() {
        // 创建大文本
        StringBuilder largeText = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeText.append("这是一个测试文本");
        }
        String text1 = largeText.toString();
        String text2 = largeText.toString() + "修改";

        // 测试大文本相似度计算不会抛出内存异常
        assertDoesNotThrow(() -> {
            double similarity = calculator.calculateComprehensiveSimilarity(text1, text2);
            assertTrue(similarity > 0.9, "大文本相似度应该很高");
        }, "大文本相似度计算不应该抛出异常");
    }

    @Test
    @DisplayName("测试语言检测异常情况")
    void testLanguageDetectionEdgeCases() {
        // 测试null文本语言检测
        TextSimilarityCalculator.TextLanguage lang1 = calculator.detectLanguage(null);
        assertEquals(TextSimilarityCalculator.TextLanguage.UNKNOWN, lang1, "null文本语言应该为UNKNOWN");

        // 测试空字符串语言检测
        TextSimilarityCalculator.TextLanguage lang2 = calculator.detectLanguage("");
        assertEquals(TextSimilarityCalculator.TextLanguage.UNKNOWN, lang2, "空字符串语言应该为UNKNOWN");

        // 测试只包含数字的文本
        TextSimilarityCalculator.TextLanguage lang3 = calculator.detectLanguage("123456789");
        assertEquals(TextSimilarityCalculator.TextLanguage.UNKNOWN, lang3, "只包含数字的文本语言应该为UNKNOWN");

        // 测试只包含标点符号的文本
        TextSimilarityCalculator.TextLanguage lang4 = calculator.detectLanguage("!@#$%^&*()");
        assertEquals(TextSimilarityCalculator.TextLanguage.UNKNOWN, lang4, "只包含标点符号的文本语言应该为UNKNOWN");
    }

    @Test
    @DisplayName("测试数值计算边界情况")
    void testNumericalCalculationEdgeCases() {
        // 测试相同文本的相似度
        String text = "测试文本";
        double similarity = calculator.calculateComprehensiveSimilarity(text, text);
        assertEquals(1.0, similarity, 0.001, "相同文本相似度应该为1.0");

        // 测试完全不同的文本
        double similarity2 = calculator.calculateComprehensiveSimilarity("文本A", "文本B");
        assertTrue(similarity2 >= 0.0 && similarity2 <= 1.0, "相似度应该在0-1之间");

        // 测试一个字符的文本
        double similarity3 = calculator.calculateComprehensiveSimilarity("A", "B");
        assertTrue(similarity3 >= 0.0 && similarity3 <= 1.0, "单字符文本相似度应该在0-1之间");
    }

    @Test
    @DisplayName("测试文件权限异常")
    void testFilePermissionException(@TempDir Path tempDir) throws IOException {
        // 创建文件
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "测试内容".getBytes());

        // 在Windows上设置文件为只读（如果支持的话）
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            testFile.toFile().setReadOnly();
            
            // 测试读取只读文件（应该成功）
            assertDoesNotThrow(() -> {
                String content = fileProcessor.readFile(testFile.toString());
                assertEquals("测试内容", content, "只读文件应该能正常读取");
            }, "读取只读文件不应该抛出异常");
        }
    }

    @Test
    @DisplayName("测试路径长度限制")
    void testPathLengthLimit() {
        // 测试超长路径
        StringBuilder longPath = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longPath.append("very_long_directory_name_");
        }
        longPath.append("file.txt");

        // 超长路径应该无效
        assertFalse(fileProcessor.isValidPath(longPath.toString()), "超长路径应该无效");
    }

    @Test
    @DisplayName("测试并发访问异常")
    void testConcurrentAccessException(@TempDir Path tempDir) throws IOException {
        // 创建测试文件
        Path testFile = tempDir.resolve("concurrent_test.txt");
        Files.write(testFile, "并发测试内容".getBytes());

        // 模拟并发读取（多个线程同时读取同一文件）
        assertDoesNotThrow(() -> {
            Thread[] threads = new Thread[5];
            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        String content = fileProcessor.readFile(testFile.toString());
                        assertEquals("并发测试内容", content, "并发读取内容应该正确");
                    } catch (IOException e) {
                        fail("并发读取不应该抛出异常: " + e.getMessage());
                    }
                });
                threads[i].start();
            }

            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join();
            }
        }, "并发读取文件不应该抛出异常");
    }
}
