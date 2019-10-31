package com.adsquare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import net.agkn.hll.HLL;
import picocli.CommandLine;

@CommandLine.Command(name = "createhll", mixinStandardHelpOptions = true)
public class StatsHll implements Runnable{

	@CommandLine.Parameters(arity = "1..*", paramLabel = "FILE", description = "one ore more MAID files to archive")
	File[] files;
	@CommandLine.Option(names = { "-v", "--verbose" }, description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
	private boolean[] verbose = new boolean[0];
	@CommandLine.Option(names = { "-o", "--output" }, description = "Name of the output HLL file", defaultValue = "output.hll")
	private File outputFile;


	public void run(){
		try {
			final HashFunction hasher = Hashing.murmur3_128();
			final HLL hll = new HLL(14, 5);

			for (File file : files) {
				if (verbose.length > 0) {
					System.out.println("processing file " + file);
				}
				try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
					stream.forEach(line -> hll.addRaw(hasher.hashString(line.toLowerCase(), Charset.forName("utf-8")).asLong()));
				}
			}
			System.out.println("Estimated total cardinality " + hll.cardinality());
			System.out.println("writing HLL to " + outputFile.getAbsolutePath());
			Files.write(Paths.get(outputFile.getAbsolutePath()), hll.toBytes());
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new StatsHll()).execute(args);
		System.exit(exitCode);
	}
}
