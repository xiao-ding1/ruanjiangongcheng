@echo off
echo 编译Java程序...
javac -d . -cp . src/main/java/com/plagiarism/*.java

echo.
echo 运行测试用例...

echo.
echo 测试1: 原文 vs 添加内容版本
java -cp . com.plagiarism.PlagiarismDetector src/test/resources/orig.txt src/test/resources/orig_0.8_add.txt src/test/resources/result_add.txt

echo.
echo 测试2: 原文 vs 删除内容版本
java -cp . com.plagiarism.PlagiarismDetector src/test/resources/orig.txt src/test/resources/orig_0.8_del.txt src/test/resources/result_del.txt

echo.
echo 测试3: 原文 vs 轻微修改版本
java -cp . com.plagiarism.PlagiarismDetector src/test/resources/orig.txt src/test/resources/orig_0.8_dis_1.txt src/test/resources/result_dis_1.txt

echo.
echo 测试4: 原文 vs 中等修改版本
java -cp . com.plagiarism.PlagiarismDetector src/test/resources/orig.txt src/test/resources/orig_0.8_dis_10.txt src/test/resources/result_dis_10.txt

echo.
echo 测试5: 原文 vs 大幅修改版本
java -cp . com.plagiarism.PlagiarismDetector src/test/resources/orig.txt src/test/resources/orig_0.8_dis_15.txt src/test/resources/result_dis_15.txt

echo.
echo 测试完成！结果文件已生成在src/test/resources目录下。
pause

