package dev.ultreon.scriptic.lang.parser;

import java.math.BigDecimal;

public class Parser {
    private final String code;
    private int offset;

    public Parser(String code) {
        this.code = code.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        if (code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty!");
        }
//        System.out.println("code = " + code);
    }

    public String readUntil(String delimiter) {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (code.startsWith(delimiter, offset)) {
                    offset += delimiter.length();
                    return sb.toString();
                }
                sb.append(c);
                offset++;
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String readUntil(String delimiter, String escape) {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (delimiter.length() == 1) {
                    offset++;
                    return sb.toString();
                } else if (code.startsWith(delimiter, offset)) {
                    offset += delimiter.length();
                    return sb.toString();
                } else if (code.startsWith(escape, offset)) {
                    offset += escape.length();
                    sb.append(c);
                } else {
                    sb.append(c);
                    offset++;
                }
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String readUntil(String delimiter, String escape, String... escapes) {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (delimiter.length() == 1) {
                    offset++;
                    return sb.toString();
                } else if (code.startsWith(delimiter, offset)) {
                    offset += delimiter.length();
                    return sb.toString();
                } else if (code.startsWith(escape, offset)) {
                    offset += escape.length();
                    sb.append(c);
                } else if (escapes.length > 0) {
                    for (var esc : escapes) {
                        if (code.startsWith(esc, offset)) {
                            offset += esc.length();
                            sb.append(c);
                        }
                    else {
                        sb.append(c);
                        offset++;
                    }
                    }
                }
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public BigDecimal readNumber() {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (Character.isDigit(c)) {
                    sb.append(c);
                } else if (c == '.' || c == ',') {
                    sb.append('.');
                } else {
                    break;
                }
                offset++;
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new BigDecimal(sb.toString());
    }

    public String readString() {
        var sb = new StringBuilder();
        try {
            var c = code.charAt(offset);
            offset++;
            if (isEOF()) {
                throw new IllegalArgumentException("Unexpected end of file");
            }
            char endChar;
            if (c == '\'') {
                endChar = '\'';
            } else if (c == '"') {
                endChar = '"';
            } else if (c == '‘') {
                endChar = '’';
            } else {
                throw new RuntimeException("Expected to read text, but found " + c);
            }

            while ((c = code.charAt(offset)) != '\n') {
                if (c == endChar) {
                    offset++;
                    return sb.toString();
                }
                sb.append(c);
                offset++;
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String readIdentifier() {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (Character.isLetterOrDigit(c)) {
                    sb.append(c);
                } else {
                    break;
                }
                offset++;
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String readComment() {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                sb.append(c);
                offset++;
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public String readLine() {
        var sb = new StringBuilder();
        try {
            char c;
//            System.out.println("code[readLine] = " + code);
            while (true) {
//                System.out.println("offsetA = " + offset);
                c = code.charAt(offset);
                if (c == '\n' || code.startsWith(". ", offset)) {
                    offset++;
                    break;
                }
                sb.append(c);
                offset++;

//                System.out.println("offsetB = " + offset);

                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    
    public String readWhitespace() {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (Character.isWhitespace(c)) {
                    sb.append(c);
                } else {
                    break;
                }
                offset++;
                if (isEOF()) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    
    // Read variable, variable starts with { and ends with }. Can be escaped with \.
    // Example: {var}
    // May contain any character except \.
    public String readVariable() {
        var sb = new StringBuilder();
        try {
            char c;
            while ((c = code.charAt(offset)) != '\n') {
                if (c == '}') {
                    offset++;
                    if (isEOF()) {
                        break;
                    }
                    break;
                } else if (c == '\\') {
                    offset++;
                    sb.append(c);
                    if (isEOF()) {
                        break;
                    }
                } else {
                    sb.append(c);
                    if (isEOF()) {
                        break;
                    }
                }
                offset++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * Read am indented block with the given indentation.
     * Indent is in spaces, and cannot be tabs.
     * The reading will end if the indentation is less than the given indentation.
     * It may contain newlines in the returned string.
     * Empty or blank lines are ignored.
     */
    public String readIndentedBlock(boolean returnIndents) {
        return readIndentedBlock(2, returnIndents);
    }

    /**
     * Read am indented block with the given indentation.
     * Indent is in spaces, and cannot be tabs.
     * The reading will end if the indentation is less than the given indentation.
     * It may contain newlines in the returned string.
     * Empty or blank lines are ignored.
     */
    public String readIndentedBlock() {
        return readIndentedBlock(2);
    }

    /**
     * Read am indented block with the given indentation.
     * Indent is in spaces, and cannot be tabs.
     * The reading will end if the indentation is less than the given indentation.
     * It may contain newlines in the returned string.
     * Empty or blank lines are ignored.
     */
    public String readIndentedBlock(int indent) {
        return readIndentedBlock(indent, false);
    }

    /**
     * Read am indented block with the given indentation.
     * Indent is in spaces, and cannot be tabs.
     * The reading will end if the indentation is less than the given indentation.
     * It may contain newlines in the returned string.
     * Empty or blank lines are ignored.
     */
    public String readIndentedBlock(int indent, boolean returnIndents) {
        var sb = new StringBuilder();
        try {
            char c;
            var withinIndent = true;
            var indentation = 0;
            var lastOffsetWithNewline = offset;
            while (!isEOF()) {
                c = code.charAt(offset);
                switch (c) {
                    case ' ' -> {
                        if (withinIndent) {
                            indentation++;
                        }
                        if (returnIndents || !withinIndent || indentation > indent) {
                            sb.append(c);
                        }
                    }
                    case '\t' -> {
                        if (!returnIndents) {
                            throw new RuntimeException("Tabs are not allowed in indentation.");
                        }
                    }
                    case '\n' -> {
                        lastOffsetWithNewline = offset;
                        indentation = 0;
                        withinIndent = true;
                        sb.append('\n');
                    }
                    default -> {
                        withinIndent = false;
                        sb.append(c);
                    }
                }
                if (!withinIndent && indentation < indent) {
                    offset = lastOffsetWithNewline;
                    return sb.substring(0, sb.length() - 1);
                }
                offset++;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public boolean isEOF() {
        return offset >= code.length();
    }

    public int peek() {
        return offset;
    }

    public void skip(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("i must be >= 0");
        }
        offset += i;
    }

    public void back(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        if (offset - amount < 0) {
            throw new IllegalArgumentException("Cannot back " + amount + " characters, offset is " + offset);
        }
        offset -= amount;
    }

    public int row() {
        var i = Math.min(offset, code.length() - 1);
        var count = 1;
        while (i > 0) {
            if (code.charAt(i) == '\n') {
                count++;
            }
            i--;
        }
        return count;
    }

    public int col() {
        var i = Math.min(offset, code.length() - 1);
        var count = 1;
        while (i > 0) {
            if (code.charAt(i) == '\n') {
                break;
            }
            count++;
            i--;
        }
        return count;
    }

    public String getRow(int row) {
        String code = this.code;
        String[] split = this.code.lines().toList().toArray(new String[0]);
        if (row < 1 || row > split.length) {
            return "";
        }

        return split[row - 1];
    }
}
