package com.plagiarism;

import java.io.IOException;

/**
 * 论文查重系统主类
 * 使用多种算法计算文本相似度
 */
public class PlagiarismDetector {

    private final FileProcessor fileProcessor;
    private final TextSimilarityCalculator similarityCalculator;

    public PlagiarismDetector() {
        this.fileProcessor = new FileProcessor();
        this.similarityCalculator = new TextSimilarityCalculator();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("使用方法: java PlagiarismDetector <原文文件路径> <抄袭版文件路径> <输出文件路径>");
            System.exit(1);
        }

        String originalPath = args[0];
        String plagiarizedPath = args[1];
        String outputPath = args[2];

        PlagiarismDetector detector = new PlagiarismDetector();

        try {
            // 验证输入参数
            detector.validateInputs(originalPath, plagiarizedPath, outputPath);

            // 执行查重
            double similarity = detector.detectPlagiarism(originalPath, plagiarizedPath);

            // 输出结果
            detector.fileProcessor.writeResult(outputPath, similarity);

            System.out.println("查重完成，相似度: " + String.format("%.2f", similarity * 100) + "%");

        } catch (IllegalArgumentException e) {
            System.err.println("参数错误: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("文件操作错误: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("计算错误: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 验证输入参数
     */
    private void validateInputs(String originalPath, String plagiarizedPath, String outputPath) {
        if (!fileProcessor.isValidPath(originalPath)) {
            throw new IllegalArgumentException("原文文件路径无效: " + originalPath);
        }

        if (!fileProcessor.isValidPath(plagiarizedPath)) {
            throw new IllegalArgumentException("抄袭版文件路径无效: " + plagiarizedPath);
        }

        if (!fileProcessor.isValidPath(outputPath)) {
            throw new IllegalArgumentException("输出文件路径无效: " + outputPath);
        }

        if (!fileProcessor.fileExists(originalPath)) {
            throw new IllegalArgumentException("原文文件不存在: " + originalPath);
        }

        if (!fileProcessor.fileExists(plagiarizedPath)) {
            throw new IllegalArgumentException("抄袭版文件不存在: " + plagiarizedPath);
        }
    }

    /**
     * 执行查重检测
     */
    public double detectPlagiarism(String originalPath, String plagiarizedPath) throws IOException {
        // 读取文件内容
        String originalText = fileProcessor.readFile(originalPath);
        String plagiarizedText = fileProcessor.readFile(plagiarizedPath);

        // 检查文件是否为空
        if (originalText.trim().isEmpty()) {
            throw new IllegalArgumentException("原文文件为空");
        }

        if (plagiarizedText.trim().isEmpty()) {
            throw new IllegalArgumentException("抄袭版文件为空");
        }

        // 计算相似度
        return similarityCalculator.calculateComprehensiveSimilarity(originalText, plagiarizedText);
    }
}
