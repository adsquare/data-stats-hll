# data-stats-hll
This small application creates a probabilistic datastructure based on the HyperLogLog algorithm for given input files.

Example:
```
java -jar target/stats-hll-1.0-jar-with-dependencies.jar -o output.hll maids.txt
```

You can pass 1..n input files, where every line contains one MAID.
