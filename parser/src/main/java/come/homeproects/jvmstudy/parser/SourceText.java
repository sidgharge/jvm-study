package come.homeproects.jvmstudy.parser;

import java.util.List;

public class SourceText {

    private static final char NULL_CHAR = '\0';

    private final List<String> lines;

    private int index = 0;

    private int lineNumber = 0;

    public SourceText(String source) {
        this.lines = source.lines().toList();
        while (lineNumber < this.lines.size() && this.lines.get(lineNumber).isEmpty()) {
            lineNumber++;
        }
    }

    public char peek() {
        if (isAtEnd()) {
            return NULL_CHAR;
        }
        return lines.get(lineNumber).charAt(index);
    }

    public boolean isAtEnd() {
        if (lineNumber >= lines.size()) {
            return true;
        }
        if (index >= lines.get(lineNumber).length()) {
            return lineNumber + 1 >= lines.size();
        }
        return false;
    }

    public char currentAndAdvance() {
        if (isAtEnd()) {
            return NULL_CHAR;
        }

        String line = lines.get(lineNumber);
        char ch = line.charAt(index);

        if (index + 1 >= line.length()) {
            lineNumber++;
            index = 0;
        } else {
            index++;
        }

        return ch;
    }

    public int index() {
        return index;
    }

    public int lineNumber() {
        return lineNumber;
    }
}
