package com.plagiarism;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 性能测试类 - 专门用于JProfiler监控
 * 通过循环测试和延迟来增加程序执行时间
 */
public class PerformanceTest {

    private final PlagiarismDetector detector;
    private final FileProcessor fileProcessor;

    public PerformanceTest() {
        this.detector = new PlagiarismDetector();
        this.fileProcessor = new FileProcessor();
    }

    /**
     * 主性能测试方法
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 论文查重系统性能测试 ===");
        System.out.println("开始时间: " + java.time.LocalDateTime.now());

        PerformanceTest test = new PerformanceTest();

        // 等待JProfiler连接（给用户时间启动JProfiler）
        System.out.println("等待JProfiler连接...");
        Thread.sleep(5000); // 等待5秒

        // 执行性能测试
        test.runPerformanceTests();

        System.out.println("=== 性能测试完成 ===");
        System.out.println("结束时间: " + java.time.LocalDateTime.now());
    }

    /**
     * 运行各种性能测试
     */
    public void runPerformanceTests() {
        try {
            // 测试1: 基础功能测试
            System.out.println("\n--- 测试1: 基础功能测试 ---");
            testBasicFunctionality();

            // 测试2: 批量测试
            System.out.println("\n--- 测试2: 批量测试 ---");
            testBatchProcessing();

            // 测试3: 内存压力测试
            System.out.println("\n--- 测试3: 内存压力测试 ---");
            testMemoryIntensive();

            // 测试4: CPU密集型测试
            System.out.println("\n--- 测试4: CPU密集型测试 ---");
            testCPUIntensive();

        } catch (Exception e) {
            System.err.println("性能测试出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 基础功能测试
     */
    private void testBasicFunctionality() throws IOException {
        String basePath = "src/test/resources/";

        // 测试各种文件组合
        String[][] testCases = {
                { "orig.txt", "orig_0.8_add.txt" },
                { "orig.txt", "orig_0.8_del.txt" },
                { "orig.txt", "orig_0.8_dis_1.txt" },
                { "orig.txt", "orig_0.8_dis_10.txt" },
                { "orig.txt", "orig_0.8_dis_15.txt" }
        };

        for (String[] testCase : testCases) {
            String originalPath = basePath + testCase[0];
            String plagiarizedPath = basePath + testCase[1];

            if (fileProcessor.fileExists(originalPath) && fileProcessor.fileExists(plagiarizedPath)) {
                long startTime = System.currentTimeMillis();
                double similarity = detector.detectPlagiarism(originalPath, plagiarizedPath);
                long endTime = System.currentTimeMillis();

                System.out.printf("测试文件: %s vs %s - 相似度: %.2f%%, 耗时: %dms%n",
                        testCase[0], testCase[1], similarity * 100, endTime - startTime);

                // 添加小延迟以便JProfiler采样
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 批量测试 - 重复执行相同测试
     */
    private void testBatchProcessing() throws IOException {
        String originalPath = "src/test/resources/orig.txt";
        String plagiarizedPath = "src/test/resources/orig_0.8_add.txt";

        if (!fileProcessor.fileExists(originalPath) || !fileProcessor.fileExists(plagiarizedPath)) {
            System.out.println("测试文件不存在，跳过批量测试");
            return;
        }

        int iterations = 50; // 重复50次
        long totalTime = 0;

        System.out.println("开始批量测试，共" + iterations + "次迭代...");

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            double similarity = detector.detectPlagiarism(originalPath, plagiarizedPath);
            long endTime = System.nanoTime();

            totalTime += (endTime - startTime) / 1_000_000; // 转换为毫秒

            if (i % 10 == 0) {
                System.out.printf("迭代 %d: 相似度 %.2f%%, 耗时 %dms%n",
                        i + 1, similarity * 100, (endTime - startTime) / 1_000_000);
            }

            // 短暂延迟
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("批量测试完成 - 平均耗时: %.2fms, 总耗时: %dms%n",
                (double) totalTime / iterations, totalTime);
    }

    /**
     * 内存密集型测试
     */
    private void testMemoryIntensive() {
        System.out.println("开始内存密集型测试...");

        // 创建大量字符串对象
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("这是一段测试文本，用于测试内存使用情况。").append(i);
        }

        String largeText = sb.toString();

        // 重复进行文本处理
        TextSimilarityCalculator calculator = new TextSimilarityCalculator();

        for (int i = 0; i < 20; i++) {
            String modifiedText = largeText + "_modification_" + i;

            try {
                long startTime = System.currentTimeMillis();
                double similarity = calculator.calculateComprehensiveSimilarity(largeText, modifiedText);
                long endTime = System.currentTimeMillis();

                System.out.printf("内存测试 %d: 相似度 %.2f%%, 耗时 %dms%n",
                        i + 1, similarity * 100, endTime - startTime);

                // 强制垃圾回收
                if (i % 5 == 0) {
                    System.gc();
                    Thread.sleep(100);
                }

            } catch (Exception e) {
                System.err.println("内存测试出错: " + e.getMessage());
            }
        }
    }

    /**
     * CPU密集型测试
     */
    private void testCPUIntensive() {
        System.out.println("开始CPU密集型测试...");

        // 创建复杂的文本进行重复计算
        String text1 = generateComplexText(1000);
        String text2 = generateComplexText(1000);

        TextSimilarityCalculator calculator = new TextSimilarityCalculator();

        for (int i = 0; i < 30; i++) {
            try {
                long startTime = System.currentTimeMillis();

                // 计算多种相似度
                double cosineSimilarity = calculator.calculateCosineSimilarity(text1, text2);
                double editSimilarity = calculator.calculateEditDistanceSimilarity(text1, text2);
                double charSimilarity = calculator.calculateCharacterSimilarity(text1, text2);
                double comprehensiveSimilarity = calculator.calculateComprehensiveSimilarity(text1, text2);

                long endTime = System.currentTimeMillis();

                System.out.printf("CPU测试 %d: 余弦%.2f%%, 编辑%.2f%%, 字符%.2f%%, 综合%.2f%%, 耗时%dms%n",
                        i + 1, cosineSimilarity * 100, editSimilarity * 100,
                        charSimilarity * 100, comprehensiveSimilarity * 100, endTime - startTime);

                // 修改文本以增加计算复杂度
                text2 = modifyText(text2, i);

                Thread.sleep(50);

            } catch (Exception e) {
                System.err.println("CPU测试出错: " + e.getMessage());
            }
        }
    }

    /**
     * 生成复杂文本
     */
    private String generateComplexText(int length) {
        StringBuilder sb = new StringBuilder();
        String[] words = { "计算机", "科学", "技术", "人工智能", "机器学习", "算法", "数据结构",
                "programming", "java", "performance", "optimization", "analysis" };

        for (int i = 0; i < length; i++) {
            sb.append(words[i % words.length]).append(" ");
            if (i % 10 == 0) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 修改文本
     */
    private String modifyText(String text, int modification) {
        if (modification % 3 == 0) {
            return text + " 新增内容" + modification;
        } else if (modification % 3 == 1) {
            return text.replace("计算机", "电脑" + modification);
        } else {
            return text.substring(0, text.length() / 2) + "修改" + modification + text.substring(text.length() / 2);
        }
    }
}
