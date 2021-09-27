package dev.peterrhodes.optionpricing.enums;

/**
 * Delimeter types used in LaTex.&nbsp;See the LaTeX2e unofficial reference manual section <a href="https://download.nus.edu.sg/mirror/ctan/info/latex2e-help-texinfo/latex2e.html#Delimiters">16.2.5 Delimiters</a>.
 */
public enum LatexDelimeterType {

    /**
     * Identity, i.e.&nbsp;delimeters shouldn't be applied.
     */
    NONE,

    /**
     * Parentheses (round brackets), e.g.&nbsp;(x + y).
     */
    PARENTHESIS,

    /**
     * Square brackets, e.g.&nbsp;[x + y].
     */
    BRACKET,

    /**
     * Curly brackets, e.g.&nbsp;{x + y}.
     */
    BRACE
}
