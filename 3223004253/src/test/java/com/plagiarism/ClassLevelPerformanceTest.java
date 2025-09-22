package com.plagiarism;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 类级别性能测试 - 专门用于JProfiler类级别监控
 * 确保每个类都被频繁调用，便于JProfiler捕获类级别性能数据
 */
public class ClassLevelPerformanceTest {
    
    private final PlagiarismDetector detector;
    private final FileProcessor fileProcessor;
    private final TextSimilarityCalculator calculator;
    
    public ClassLevelPerformanceTest() {
        this.detector = new PlagiarismDetector();
        this.fileProcessor = new FileProcessor();
        this.calculator = new TextSimilarityCalculator();
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 类级别性能监控测试 ===");
        System.out.println("开始时间: " + java.time.LocalDateTime.now());
        
        ClassLevelPerformanceTest test = new ClassLevelPerformanceTest();
        
        // 等待JProfiler连接
        System.out.println("等待JProfiler连接...");
        Thread.sleep(3000);
        
        // 执行类级别测试
        test.runClassLevelTests();
        
        System.out.println("=== 类级别测试完成 ===");
        System.out.println("结束时间: " + java.time.LocalDateTime.now());
    }
    
    /**
     * 运行类级别性能测试
     */
    public void runClassLevelTests() {
        try {
            System.out.println("\n--- 测试1: PlagiarismDetector类测试 ---");
            testPlagiarismDetectorClass();
            
            System.out.println("\n--- 测试2: FileProcessor类测试 ---");
            testFileProcessorClass();
            
            System.out.println("\n--- 测试3: TextSimilarityCalculator类测试 ---");
            testTextSimilarityCalculatorClass();
            
            System.out.println("\n--- 测试4: 类间协作测试 ---");
            testClassInteraction();
            
            System.out.println("\n--- 测试5: 高频调用测试 ---");
            testHighFrequencyCalls();
            
        } catch (Exception e) {
            System.err.println("类级别测试出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试PlagiarismDetector类
     */
    private void testPlagiarismDetectorClass() throws IOException {
        String basePath = "src/test/resources/";
        String originalPath = basePath + "orig.txt";
        String plagiarizedPath = basePath + "orig_0.8_add.txt";
        
        if (!fileProcessor.fileExists(originalPath) || !fileProcessor.fileExists(plagiarizedPath)) {
            System.out.println("测试文件不存在，跳过PlagiarismDetector测试");
            return;
        }
        
        // 重复调用PlagiarismDetector的方法
        for (int i = 0; i < 100; i++) {
            long startTime = System.nanoTime();
            
            // 调用detectPlagiarism方法
            double similarity = detector.detectPlagiarism(originalPath, plagiarizedPath);
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒
            
            if (i % 20 == 0) {
                System.out.printf("PlagiarismDetector调用 %d: 相似度 %.2f%%, 耗时 %dms%n", 
                    i + 1, similarity * 100, duration);
            }
            
            // 短暂延迟确保类被持续调用
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 测试FileProcessor类
     */
    private void testFileProcessorClass() {
        String basePath = "src/test/resources/";
        
        // 重复调用FileProcessor的各种方法
        for (int i = 0; i < 200; i++) {
            long startTime = System.nanoTime();
            
            // 测试文件存在性检查
            boolean exists1 = fileProcessor.fileExists(basePath + "orig.txt");
            boolean exists2 = fileProcessor.fileExists(basePath + "orig_0.8_add.txt");
            
            // 测试路径验证
            boolean valid1 = fileProcessor.isValidPath(basePath + "orig.txt");
            boolean valid2 = fileProcessor.isValidPath("invalid/path");
            
            // 测试文件大小获取
            long size1 = fileProcessor.getFileSize(basePath + "orig.txt");
            long size2 = fileProcessor.getFileSize(basePath + "orig_0.8_add.txt");
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            
            if (i % 40 == 0) {
                System.out.printf("FileProcessor调用 %d: 文件存在=%b,%b, 路径有效=%b,%b, 文件大小=%d,%d, 耗时 %dms%n", 
                    i + 1, exists1, exists2, valid1, valid2, size1, size2, duration);
            }
            
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 测试TextSimilarityCalculator类
     */
    private void testTextSimilarityCalculatorClass() {
        String text1 = "这是一个测试文本，用于测试TextSimilarityCalculator类的性能。";
        String text2 = "这是另一个测试文本，用于测试相似度计算算法的性能。";
        
        // 重复调用TextSimilarityCalculator的各种方法
        for (int i = 0; i < 300; i++) {
            long startTime = System.nanoTime();
            
            // 调用各种相似度计算方法
            double cosineSimilarity = calculator.calculateCosineSimilarity(text1, text2);
            double editSimilarity = calculator.calculateEditDistanceSimilarity(text1, text2);
            double charSimilarity = calculator.calculateCharacterSimilarity(text1, text2);
            double comprehensiveSimilarity = calculator.calculateComprehensiveSimilarity(text1, text2);
            
            // 调用文本预处理方法
            String processedText1 = calculator.preprocessText(text1);
            String processedText2 = calculator.preprocessText(text2);
            
            // 调用语言检测方法
            TextSimilarityCalculator.TextLanguage lang1 = calculator.detectLanguage(text1);
            TextSimilarityCalculator.TextLanguage lang2 = calculator.detectLanguage(text2);
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            
            if (i % 60 == 0) {
                System.out.printf("TextSimilarityCalculator调用 %d: 余弦=%.2f%%, 编辑=%.2f%%, 字符=%.2f%%, 综合=%.2f%%, 语言=%s,%s, 耗时 %dms%n", 
                    i + 1, cosineSimilarity * 100, editSimilarity * 100, charSimilarity * 100, 
                    comprehensiveSimilarity * 100, lang1, lang2, duration);
            }
            
            // 修改文本以增加计算复杂度
            text2 = modifyText(text2, i);
            
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 测试类间协作
     */
    private void testClassInteraction() throws IOException {
        String basePath = "src/test/resources/";
        String originalPath = basePath + "orig.txt";
        String plagiarizedPath = basePath + "orig_0.8_dis_1.txt";
        
        if (!fileProcessor.fileExists(originalPath) || !fileProcessor.fileExists(plagiarizedPath)) {
            System.out.println("测试文件不存在，跳过类间协作测试");
            return;
        }
        
        // 模拟完整的查重流程，涉及所有类的协作
        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();
            
            // 1. FileProcessor读取文件
            String originalText = fileProcessor.readFile(originalPath);
            String plagiarizedText = fileProcessor.readFile(plagiarizedPath);
            
            // 2. TextSimilarityCalculator计算相似度
            double similarity = calculator.calculateComprehensiveSimilarity(originalText, plagiarizedText);
            
            // 3. FileProcessor写入结果
            String outputPath = "temp_result_" + i + ".txt";
            fileProcessor.writeResult(outputPath, similarity);
            
            // 4. 清理临时文件
            try {
                java.io.File tempFile = new java.io.File(outputPath);
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            } catch (Exception e) {
                // 忽略删除失败
            }
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            
            if (i % 10 == 0) {
                System.out.printf("类间协作测试 %d: 相似度 %.2f%%, 耗时 %dms%n", 
                    i + 1, similarity * 100, duration);
            }
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 高频调用测试
     */
    private void testHighFrequencyCalls() {
        String[] testTexts = {
            "短文本测试",
            "这是一个中等长度的测试文本，用于测试算法的性能表现。",
            "这是一个较长的测试文本，包含更多的词汇和句子结构，用于测试算法在处理复杂文本时的性能表现和计算效率。",
            "This is an English test text for performance testing.",
            "这是一个中英文混合的测试文本，This is a mixed language test text for performance testing."
        };
        
        // 高频调用各种方法
        for (int round = 0; round < 10; round++) {
            System.out.printf("高频调用轮次 %d...%n", round + 1);
            
            for (int i = 0; i < testTexts.length; i++) {
                for (int j = i + 1; j < testTexts.length; j++) {
                    // 快速连续调用
                    for (int k = 0; k < 20; k++) {
                        long startTime = System.nanoTime();
                        
                        // 调用所有主要方法
                        calculator.preprocessText(testTexts[i]);
                        calculator.preprocessText(testTexts[j]);
                        calculator.calculateCosineSimilarity(testTexts[i], testTexts[j]);
                        calculator.calculateEditDistanceSimilarity(testTexts[i], testTexts[j]);
                        calculator.calculateCharacterSimilarity(testTexts[i], testTexts[j]);
                        
                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime) / 1_000; // 微秒
                        
                        // 每1000次调用输出一次统计
                        if ((round * testTexts.length * testTexts.length * 20 + 
                             i * testTexts.length * 20 + j * 20 + k) % 1000 == 0) {
                            System.out.printf("高频调用统计: 轮次%d, 文本对(%d,%d), 调用%d, 耗时 %dμs%n", 
                                round + 1, i, j, k + 1, duration);
                        }
                    }
                }
            }
            
            try {
                Thread.sleep(100); // 轮次间短暂休息
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 修改文本以增加计算复杂度
     */
    private String modifyText(String text, int modification) {
        if (modification % 4 == 0) {
            return text + " 新增内容" + modification;
        } else if (modification % 4 == 1) {
            return text.replace("测试", "检验" + modification);
        } else if (modification % 4 == 2) {
            return text.substring(0, Math.min(text.length() / 2, 10)) + "修改" + modification + text.substring(Math.min(text.length() / 2, 10));
        } else {
            return text.toUpperCase() + "_" + modification;
        }
    }
}
