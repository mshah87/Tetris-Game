import java.awt.*;
import java.util.ArrayList;

/**
 * This class displays text on the screen in a text area. The text will
 * be spaced out so that everything will fit inside of the text area, at least horizontally. Sentences
 * will that are too long to fit on one line will be split, and written on multiple lines.
 * Extends Text Component class.
 *
 * Limitations:
 * If any particular 'word' (a block of characters with no spaces between them) is too long,
 * it may not be able to fit inside of the text area. Therefore, the word may extend past the boundaries.
 * If a sentence/sentences are too long for the text area, they may be printed outside and below
 * of it. In other words, it will not fit vertically, and so it will extend past the bottom of the
 * text area. However, it will still nonetheless fit horizontally.
 * Finally, if the specified font is too large, lines may overlap vertically. This can be manually changed
 * by increasing the line spacing.
 */
public class TextBox extends TextComponent {

    //Colours.
    private Color backgroundColour;

    //The margins for the text.
    private int sideMargins, topMargins;

    //The vertical spacing between each line of text.
    private int lineSpacing;

    //The lines of text that need to be drawn.
    private String[] lines;

    //Whether or not to calculate how to draw the text. It is inefficient to
    //recalculate how to redraw it every time before we draw.
    private boolean recalculateTextFormat = true;

    /**
     * Constructor. Takes in position, dimensions, and text.
     * @param x The left edge.
     * @param y The top edge.
     * @param w The width.
     * @param h The height.
     * @param text The text.
     */
    public TextBox(int x, int y, int w, int h, String text) {
        super(x,y,w,h,text);

        //Set default values for the variables.
        //Because of the sheer number of variables, it is not feasible to include all of them in the
        //constructor. The properties should be set by calling the setters.
        sideMargins = 15;
        topMargins = 20;
        lineSpacing = 20;
        backgroundColour = Color.GRAY;
        setCornerSize(5);
        setClickable(false);
    }

    /**
     * Draw the textArea.
     * @param g The graphics object.
     */
    public void draw(Graphics g) {
        drawBackground(g);
        drawText(g);
    }

    /**
     * Draw the background of the text area.
     * @param g The graphics object.
     */
    private void drawBackground(Graphics g) {
        g.setColor(backgroundColour);
        g.fillRoundRect(getX(),getY(),getW(),getH(),getCornerSize(),getCornerSize());
    }

    /**
     * Draw the text on the text area.
     * @param g The graphics object.
     */
    private void drawText(Graphics g) {

        //Only recalculate how to draw text if it is needed.
        if (recalculateTextFormat == true) {
            //Separate the text into lines that each fit horizontally in the text area.
            lines = getLines(getText(), g);
            //No need to recalculate next time.
            recalculateTextFormat = false;
        }
        g.setColor(getTextColour());
        g.setFont(getTextFont());

        //Attempt to draw text in the center of the text area.
        if (getTextAlignment() == TEXT_ALIGN_CENTER) {

            //Get information on rendering text on screen.
            FontMetrics fontMetrics = g.getFontMetrics(getTextFont());

            //Loop through each line.
            for (int i = 0; i < lines.length; i ++) {
                //Calculate where to draw the text and than draw it.
                int stringWidth = fontMetrics.stringWidth(lines[i]);
                //Amount of space that needs to be added to center the text.
                int adjustment = (getW()-(sideMargins*2)-stringWidth)/2;
                int drawX = getX() + sideMargins + adjustment;
                int drawY = getY() + topMargins + (lineSpacing*i);
                g.drawString(lines[i] + " ",drawX,drawY);
            }
        }
        //Else, assume that text needs to be aligned to the left side of the text area.
        else {
            //Loop through each line.
            for (int i = 0; i < lines.length; i ++) {

                //Calculate where to draw the text and than draw it.
                int drawX = getX() + sideMargins;
                int drawY = getY() + topMargins + (lineSpacing*i);
                g.drawString(lines[i] + " ",drawX,drawY);
            }
        }
    }

    /**
     * Separate the text into multiple lines such that each line
     * does not exceed the size of the text area horizontally.
     *
     * This was one hell of a nightmare to debug!!!
     *
     * @param text The text.
     * @param g The graphics object.
     * @return The lines, stored in an array.
     */
    private String[] getLines(String text, Graphics g) {

        ArrayList<String> lines = new ArrayList<>(0);

        //Get each word in the text.
        //Do so by splitting each word around a space, which is the delimiter.
        String[] words = text.split(" ");

        //The maximum allowed width of each line of text.
        int maxWidth = getW()-(sideMargins*2);

        //Loop until the last word is added to a line.
        int index = 0;
        String currentLine = "";
        while (index < words.length) {

            //Get information on rendering text on screen.
            FontMetrics fontMetrics = g.getFontMetrics(getTextFont());
            //Get the next word and add it to a temporary string.
            String tempLine = currentLine;
            tempLine += words[index];

            //Consider space after each word too, if such word exists.
            if (index + 1 < words.length) {
                tempLine += " ";
            }

            //Get the width of the temporary string.
            int textWidth = fontMetrics.stringWidth(tempLine);

//            //TEST
//            System.out.println(textWidth + "");
//            System.out.println(tempLine);

            //The additional word makes the line exceed the max allowed width.
            if (textWidth > maxWidth) {

                //Special case: a single 'word' is too long to fit alone on one line.
                if (currentLine == "") {
                    //Just add the word to this line, and ignore the fact that it is too long to fit.
                    lines.add(tempLine);
                    index ++;
                }
                //Else, the new added word just makes the size go over the limit.
                else {
                    //Therefore, this line is now 'maxed out': no more words can be added.
                    //Add this line to the list; it is now complete.
                    lines.add(currentLine);
                    //Move on to the next line; it will start off empty.
                    currentLine = "";
                }

            }
            //Else, more words can potentially be added without going over the limit.
            else {
                //Add the new word to the current line.
                currentLine = tempLine;
                index++;

                //There are no more words available to be added. This last line is complete.
                if (index == words.length) {
                    lines.add(currentLine);
                }
            }
        }

        System.out.println(lines.size()+"");

        //Return result as an array of strings.
        return lines.toArray(new String[lines.size()]);
    }


    /*  Setters: Only contains overriden methods from parent class and some methods that are unique to this class. */

    @Override
    public void setText(String text) {
        super.setText(text);
        //Recalculation of how to draw text is now needed, since text is updated.
        recalculateTextFormat = true;
    }

    @Override
    public void setTextFont (Font font) {
        super.setTextFont(font);
        //Every time the font is changed, the recalculation of how to draw the text is needed.
        recalculateTextFormat = true;
    }

    @Override
    public void setW(int w) {
        super.setW(w);
        //The size has changed, so need to reformat text.
        recalculateTextFormat = true;
    }

    @Override
    public void setH(int h) {
        super.setH(h);
        //The size has changed, so need to reformat text.
        recalculateTextFormat = true;
    }

    public void setBackgroundColour(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public void setSideMargins(int sideMargins) {
        this.sideMargins = sideMargins;
    }

    public void setTopMargins(int topMargins) {
        this.topMargins = topMargins;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

}
