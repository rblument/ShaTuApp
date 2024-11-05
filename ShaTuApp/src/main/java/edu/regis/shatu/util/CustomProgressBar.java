/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibited.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JProgressBar;

/**
 * This custom progress bar is a subclass of JProgressBar.
 * This progress bar fills vertically, rather than horizontally.
 * The font and its color can be adjusted.
 * @author Ryley MacLagan
 */
public class CustomProgressBar extends JProgressBar {
    private final Font originalFont;

    public CustomProgressBar() {
        super();
        setString(null);  // Disable default string drawing
        setStringPainted(false);  // Disable default string painting

        // Capture the font set in NetBeans Design panel
        originalFont = getFont();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);  // Draw the progress bar

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the percentage string
        String progressText = getValue() + "%";
        int width = getWidth();
        int height = getHeight();

        // Set the font with the desired size
        g2d.setFont(originalFont.deriveFont(18f));

        // Get text metrics for positioning
        int textWidth = g2d.getFontMetrics().stringWidth(progressText);
        int textX = width / 2 - textWidth / 2;
        int textY = height / 2 + g2d.getFontMetrics().getAscent() / 2 - 2;

        // Set the fixed text color
        g2d.setColor(Color.WHITE);

        // Draw the text in the center of the progress bar
        g2d.drawString(progressText, textX, textY);
    }
}
