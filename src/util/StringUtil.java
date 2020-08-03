package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
	public static void main(String[] args) {
		System.out.println(String.format("%-50s | %-100s", "1234567890", "0987654321"));
		System.out.println();
		List<List<String>> rows = new ArrayList<>();
		List<String> headers = Arrays.asList("Database", "Maintainer", "First public release date",
				"Latest stable version", "Latest release date");
		rows.add(headers);
		rows.add(Arrays.asList("4D (4th Dimension)", "4D S.A.S.", "1984", "v16.0", "2017-01-10"));
		rows.add(Arrays.asList("ADABAS", "Software AG", "1970", "8.1", "2013-06"));
		rows.add(Arrays.asList("Adaptive Server Enterprise", "SAP AG", "1987", "16.0", "2015"));
		rows.add(Arrays.asList("Apache Derby", "Apache", "2004", "10.14.1.0", "2017-10-22"));

		System.out.println(formatAsTable(rows));
	}

	//https://stackoverflow.com/a/50649715/7293057
	public static String formatAsTable(List<List<String>> rows) {
		int[] maxLengths = new int[rows.get(0).size()];
		for (List<String> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
			}
		}

		StringBuilder formatBuilder = new StringBuilder();
		for (int maxLength : maxLengths) {
			formatBuilder.append("%-").append(maxLength + 2).append("s").append(" |");
		}
		String format = formatBuilder.toString();

		StringBuilder result = new StringBuilder();
		for (List<String> row : rows) {
			result.append(String.format(format, row.toArray(new Object[0]))).append("\n");
		}
		return result.toString();
	}
}
