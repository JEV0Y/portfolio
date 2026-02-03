package model.utilities;

public class StringWrapper {

	/**
	 * Search using Google AI
	 * 
	 * Date of search: September 24, 2025 at 10:25 a.m.
	 * 
	 * Search string: "return a string formatted to line length java"
	 * 
	 * Method description (also produced by the Google AI): A method can be
	 * implemented to iterate through the input string, character by character or
	 * word by word, and insert newline characters (\n) when the current line
	 * exceeds the desired lineLength.
	 * 
	 * The answer was edited to add the tabbed parameter so that an optional tab
	 * could be added before each line if the tabbed parameter is true.: it is
	 * assumed that the lineLength accounts for the tab.
	 */
	public static String wrapString(String text, int lineLength, Boolean tabbed) {

		StringBuilder wrappedText = new StringBuilder();
		int currentLineLength = 0;
		String[] words = text.split(" "); // Split by spaces to handle word wrapping

		if (tabbed)
			wrappedText.append("\t");

		for (String word : words) {
			if ((currentLineLength + word.length() + 1) > lineLength) {
				wrappedText.append(tabbed ? "\n\t" : "\n");
				currentLineLength = (tabbed ? "\t".length() : 0);
			}

			if (currentLineLength > 0) {
				wrappedText.append(" ");
				currentLineLength++;
			}

			wrappedText.append(word);
			currentLineLength += word.length();
		}
		wrappedText.append("\n");
		return wrappedText.toString();
	}
}
