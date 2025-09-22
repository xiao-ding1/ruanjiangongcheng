package com.plagiarism;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件处理器
 * 负责文件的读取和写入操作
 */
public class FileProcessor {

    /**
     * 读取文件内容
     * 
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 文件读取异常
     */
    public String readFile(String filePath) throws IOException {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("无法读取文件: " + filePath + ", 错误: " + e.getMessage(), e);
        }
    }

    /**
     * 写入结果到文件
     * 
     * @param filePath   输出文件路径
     * @param similarity 相似度值
     * @throws IOException 文件写入异常
     */
    public void writeResult(String filePath, double similarity) throws IOException {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.printf("%.2f", similarity);
        } catch (IOException e) {
            throw new IOException("无法写入文件: " + filePath + ", 错误: " + e.getMessage(), e);
        }
    }

    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return 文件是否存在
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 获取文件大小
     * 
     * @param filePath 文件路径
     * @return 文件大小（字节）
     * @throws IOException 文件访问异常
     */
    public long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }

    /**
     * 验证文件路径
     * 
     * @param filePath 文件路径
     * @return 路径是否有效
     */
    public boolean isValidPath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        try {
            Paths.get(filePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
