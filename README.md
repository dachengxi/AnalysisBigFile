# AnalysisBigFile
使用多线程分析大文件，使用到的依赖是[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)。

jdk版本要求1.7以上

## AnalysisBigFile类

### wordCount

`int wordCount(String fileName,String word)`用于统计字符串在大文件中出现的次数。

`fileName` 大文件的名字。

`word` 要统计的字符串。