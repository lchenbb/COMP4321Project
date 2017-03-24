javac -cp ./:./lib/jdbm-1.0.jar:./lib/guava-21.0.jar ./helper/IDConvertTable.java
javac -cp ./lib/jdbm-1.0.jar ./helper/InvertedIndex.java
javac ./helper/Porter.java
javac -cp ./:./lib/htmlparser.jar Indexer.java
javac -cp ./:./lib/htmlparser.jar Tester.java