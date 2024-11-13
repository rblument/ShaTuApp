/*
 * SHATU: SHA-256 Tutor
 * 
 *  (C) Johanna & Richard Blumenthal, All rights reserved
 * 
 *  Unauthorized use, duplication or distribution without the authors'
 *  permission is strictly prohibted.
 * 
 *  Unless required by applicable law or agreed to in writing, this
 *  software is distributed on an "AS IS" basis without warranties
 *  or conditions of any kind, either expressed or implied.
 */
package edu.regis.shatu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Amanda Roskelley
 */
public class VariableLabel extends JLabel implements MouseListener {
    /**
     * The pixel width and height of this component.
     */
    public static final int SIZE = 48;
    
    public static final int HALF_SIZE = SIZE / 2;
    
        /**
     * This empty border serves as an inset so the text doesn't touch the edges.
     */
    private static final Border EMPTY_BORDER = new EmptyBorder(5, 10, 5, 10);

    /**
     * A simple black line surrounds this label when not highlighted.
     */
    private static final Border NORMAL_BORDER
            = new CompoundBorder(BorderFactory.createLineBorder(Color.BLUE), EMPTY_BORDER);

    /**
     * A red line with thickness two surrounds this label when highlighted.
     */
    private static final Border HIGHLIGHT_BORDER
            = new CompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), EMPTY_BORDER);
    
    /**
     * The background of this label is white when not highlighted.
     */
    private static final Color NORMAL_BACKGROUND = new Color(255,255,255);
    
    /**
     * The background of this label is a light yellow when highlighted.
     */
    private static final Color HIGHLIGHT_BACKGROUND = new Color(210,248,210);
    
    /**
     * The background of this label when selected.
     */
    private static final Color SELECTED_BACKGROUND = new Color(212,235,242);
    
    /**
     * The view that is displayed when this label is selected.
     */
    private StepSelection stepSelection;
    
    private boolean isSelected;

    /**
     * Initialize this label with the given text and a size that was determined
     * empirically.
     * 
     * @param text the text of this label
     */
    public VariableLabel(String text) {
        super(text, SwingConstants.CENTER);
        
        isSelected = false;
        
        Dimension d = new Dimension(SIZE, SIZE); // Determined empirically
        
        setOpaque(false);
        //setBackground(NORMAL_BACKGROUND);
        //setBorder(NORMAL_BORDER);
        setMinimumSize(d);
        setSize(d);

        this.addMouseListener(this);
    }
    
    public void select() {
        GuiController.instance().getStepSelectorView().displayStep(stepSelection);
        isSelected = true;
        //setBackground(SELECTED_BACKGROUND);
    }
    
    public void deselect() {
        isSelected = false;
       // setBackground(NORMAL_BACKGROUND);
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (!isSelected) {
            select();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * When the mouse enters this label, highlight it.
     * 
     * @param e ignored
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (!isSelected) {
            //setBorder(HIGHLIGHT_BORDER);
            //setBackground(HIGHLIGHT_BACKGROUND);
        }
    }

    /**
     * When the mouse exits this label, unhighlight it.
     * 
     * @param e ignored
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (!isSelected) {
            //setBorder(NORMAL_BORDER);
            //setBackground(NORMAL_BACKGROUND);
        }
    }
}