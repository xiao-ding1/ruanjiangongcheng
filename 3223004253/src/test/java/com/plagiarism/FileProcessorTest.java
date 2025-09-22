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
 * FileProcessor单元测试类
 * 测试文件处理器的各种功能
 */
public class FileProcessorTest {

    private FileProcessor fileProcessor;

    @BeforeEach
    void setUp() {
        fileProcessor = new FileProcessor();
    }

    @Test
    @DisplayName("测试读取文件内容")
    void testReadFile(@TempDir Path tempDir) throws IOException {
        // 创建测试文件
        String testContent = "这是测试内容\n包含中文和English";
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, testContent.getBytes("UTF-8"));

        // 读取文件
        String content = fileProcessor.readFile(testFile.toString());

        assertEquals(testContent, content, "读取的文件内容应该与写入的内容一致");
    }

    @Test
    @DisplayName("测试读取不存在的文件")
    void testReadNonExistentFile() {
        String nonExistentPath = "non_existent_file.txt";

        assertThrows(IOException.class, () -> {
            fileProcessor.readFile(nonExistentPath);
        }, "读取不存在的文件应该抛出IOException");
    }

    @Test
    @DisplayName("测试写入结果到文件")
    void testWriteResult(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("result.txt");
        double similarity = 0.85;

        // 写入结果
        fileProcessor.writeResult(outputFile.toString(), similarity);

        // 验证文件内容
        String content = new String(Files.readAllBytes(outputFile), "UTF-8");
        assertEquals("0.85", content, "写入的相似度应该正确");
    }

    @Test
    @DisplayName("测试检查文件是否存在")
    void testFileExists(@TempDir Path tempDir) throws IOException {
        // 创建测试文件
        Path existingFile = tempDir.resolve("existing.txt");
        Files.write(existingFile, "test".getBytes());

        Path nonExistingFile = tempDir.resolve("non_existing.txt");

        assertTrue(fileProcessor.fileExists(existingFile.toString()),
                "存在的文件应该返回true");
        assertFalse(fileProcessor.fileExists(nonExistingFile.toString()),
                "不存在的文件应该返回false");
    }

    @Test
    @DisplayName("测试验证文件路径")
    void testIsValidPath() {
        // 有效路径
        assertTrue(fileProcessor.isValidPath("C:\\test\\file.txt"),
                "Windows路径应该有效");
        assertTrue(fileProcessor.isValidPath("/home/user/file.txt"),
                "Unix路径应该有效");
        assertTrue(fileProcessor.isValidPath("file.txt"),
                "相对路径应该有效");

        // 无效路径
        assertFalse(fileProcessor.isValidPath(null),
                "null路径应该无效");
        assertFalse(fileProcessor.isValidPath(""),
                "空字符串路径应该无效");
        assertFalse(fileProcessor.isValidPath("   "),
                "空白字符串路径应该无效");
    }

    @Test
    @DisplayName("测试获取文件大小")
    void testGetFileSize(@TempDir Path tempDir) throws IOException {
        // 创建测试文件
        String testContent = "这是测试内容";
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, testContent.getBytes("UTF-8"));

        // 获取文件大小
        long fileSize = fileProcessor.getFileSize(testFile.toString());

        assertTrue(fileSize > 0, "文件大小应该大于0");
        assertEquals(testContent.getBytes("UTF-8").length, fileSize,
                "文件大小应该与内容字节数一致");
    }

    @Test
    @DisplayName("测试获取不存在文件的大小")
    void testGetFileSizeOfNonExistentFile() {
        String nonExistentPath = "non_existent_file.txt";

        assertThrows(IOException.class, () -> {
            fileProcessor.getFileSize(nonExistentPath);
        }, "获取不存在文件的大小应该抛出IOException");
    }

    @Test
    @DisplayName("测试UTF-8编码处理")
    void testUTF8Encoding(@TempDir Path tempDir) throws IOException {
        // 包含中文、英文、特殊字符的测试内容
        String testContent = "中文测试 English Test 特殊字符：!@#$%^&*()";
        Path testFile = tempDir.resolve("utf8_test.txt");
        Files.write(testFile, testContent.getBytes("UTF-8"));

        // 读取并验证
        String content = fileProcessor.readFile(testFile.toString());
        assertEquals(testContent, content, "UTF-8编码的文件应该正确读取");
    }

    @Test
    @DisplayName("测试大文件处理")
    void testLargeFileProcessing(@TempDir Path tempDir) throws IOException {
        // 创建大文件（1MB）
        StringBuilder sb = new StringBuilder();
        String line = "这是一行测试内容，用于测试大文件处理能力。";
        for (int i = 0; i < 10000; i++) {
            sb.append(line).append("\n");
        }

        Path largeFile = tempDir.resolve("large_file.txt");
        Files.write(largeFile, sb.toString().getBytes("UTF-8"));

        // 测试读取大文件
        long startTime = System.currentTimeMillis();
        String content = fileProcessor.readFile(largeFile.toString());
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        assertTrue(executionTime < 5000, "大文件读取时间应该小于5秒");
        assertTrue(content.length() > 0, "大文件内容应该成功读取");
    }

    @Test
    @DisplayName("测试写入不同精度的相似度")
    void testWriteDifferentPrecisions(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("precision_test.txt");

        // 测试不同精度的相似度
        double[] similarities = { 0.0, 0.123456, 0.5, 0.999999, 1.0 };
        String[] expected = { "0.00", "0.12", "0.50", "1.00", "1.00" };

        for (int i = 0; i < similarities.length; i++) {
            Path testFile = tempDir.resolve("test_" + i + ".txt");
            fileProcessor.writeResult(testFile.toString(), similarities[i]);

            String content = new String(Files.readAllBytes(testFile), "UTF-8");
            assertEquals(expected[i], content,
                    "相似度" + similarities[i] + "应该格式化为" + expected[i]);
        }
    }
}

