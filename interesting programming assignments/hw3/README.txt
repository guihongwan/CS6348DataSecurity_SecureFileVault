0.Environment
   prepare for the input-file, the content should be an integer each line, e.g.,
   2
   3
   4

1. run playground.jar
$ java -jar playground.jar

2. then type one of the following commands
-keygen -outputPK public-key-file -outputPr private-key-file
-encrypt -pk public-keyfile -input input-file -output encrypted-file
-process -pk public-key-file -input encrypted-file -output processed-file
-decryt -pr private-key-file -input processed-file -output output-file

You also can run in this way:
$ java -jar playground.jar -keygen -outputPK public-key-file -outputPr private-key-file