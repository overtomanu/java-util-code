package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* command:
java -Dfile.encoding=UTF-8 \
-classpath /Users/manobhat/MyFiles/Workspaces/eclipse-learning/java-util-code/bin \
util.ReplaceListOfWordsInFile \
"/Users/manobhat/Downloads/currentAndPendingTaskRelated/Import projects FBDI template translation/EnglishColumns.txt" \
"/Users/manobhat/Downloads/currentAndPendingTaskRelated/Import projects FBDI template translation/FrenchCanadianColumns.txt" \
"/Users/manobhat/Downloads/currentAndPendingTaskRelated/Import projects FBDI template translation/GenCSVMacroEnglishCols.txt"
*/

public class ReplaceListOfWordsInFile {
	public static void main(String[] args) throws IOException {
		if(args.length<3) {
			System.out.println(
			"""
			Enter 3 file paths, first file containing list of strings to be replaced
			(Each word should be on a new line)
			Second file containing strings to be replaced with
			Third file is the target file where replacing will be done
			Files should be UTF-8 encoded
			Creates and writes output to file named "<inputFile>_output"
			""");
			System.exit(0);
		}
		List<String[]> strPairList = new ArrayList<>();
		Files.lines(Paths.get(args[0])).forEach(x->strPairList.add(new String[] {x,null}));
		AtomicInteger i = new AtomicInteger();
		Files.lines(Paths.get(args[1])).forEach(x->strPairList.get(i.getAndIncrement())[1]=x);
		
		// we need to sort strings as per their length because smaller strings can be
		// prefix or suffix of larger strings. so find and replace larger strings first
		Collections.sort((List<String[]>) strPairList, (o1, o2) -> {
			int size1 = ((String[])o1)[0].length();
			int size2 = ((String[])o2)[0].length();
			return size2-size1;
		});
		//strPairList.forEach(x->System.out.println(x[0]+","+x[1]));
		Path sourceFile = Paths.get(args[2]);
		String[] fileContent = new String[] { Files.readString(sourceFile) };
		strPairList.forEach(x->{
			fileContent[0]=fileContent[0].replace(x[0], x[1]);
		});
		String sourceFileName = sourceFile.getFileName().toString();
		String outputFilePath = sourceFile.getParent().toAbsolutePath().toString()+File.separator;
		if(sourceFileName.lastIndexOf('.')!=-1) {
			outputFilePath+=sourceFileName.substring(0, sourceFileName.lastIndexOf('.'))+"_output";
			outputFilePath+=sourceFileName.substring(sourceFileName.lastIndexOf('.'), sourceFileName.length());
		}else {
			outputFilePath=sourceFileName+"_output";
		}
		//System.out.println(outputFilePath);
		Files.writeString(Paths.get(outputFilePath), fileContent[0],StandardOpenOption.CREATE);
	}
}
