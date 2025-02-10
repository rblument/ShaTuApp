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
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


/**
 * ToDo: Should this be a HilightLabel class?
 * @author rickb
 */
public class AddMod256Label extends JLabel implements MouseListener {
    /**
     * The empirically determined width and height of this component.
     */
    public static final int SIZE = 20;
    
    public static final int HALF_SIZE = SIZE / 2;

    /**
     * A simple black line surrounds this label when not highlighted.
     */
    private static final Border NORMAL_BORDER = BorderFactory.createLineBorder(Color.RED);

    /**
     * A red line with thickness two surrounds this label when highlighted.
     */
    private static final Border HIGHLIGHT_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);
    
    /**
     * The background of this label is pinkish when not highlighted.
     */
    private static final Color NORMAL_BACKGROUND = Color.WHITE;
    
    /**
     * The background of this label is a light yellow when highlighted.
     */
    private static final Color HIGHLIGHT_BACKGROUND = new Color(255, 255, 205);
    
    /**
     * The background of this label when selected.
     */
    private static final Color SELECTED_BACKGROUND = new Color(204, 255, 205);
    
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
    public AddMod256Label() {
        super("", SwingConstants.CENTER);
        
        isSelected = false;

        Dimension d = new Dimension(SIZE, SIZE); 

        
        setOpaque(true);
        setBackground(NORMAL_BACKGROUND);
        setBorder(NORMAL_BORDER);
        setMinimumSize(d);
        setSize(d);

        this.addMouseListener(this);
    }
    
    public void select() {
       // GuiController.instance().getStepSelectorView().displayStep(stepSelection);
        SplashFrame.instance().getTutoringSessionView().displayStep(stepSelection);
        isSelected = true;
        setBackground(SELECTED_BACKGROUND);
    }
    
    public void deselect() {
        isSelected = false;
        setBackground(NORMAL_BACKGROUND);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        this.setForeground(Color.RED);
        g.drawLine(HALF_SIZE, 0, HALF_SIZE, SIZE - 1); // vertical
        g.drawLine(0, HALF_SIZE, SIZE - 1, HALF_SIZE); // horizontal       
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (!isSelected) {
            select();
        }
        else if (isSelected) {
            deselect();
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
            setBorder(HIGHLIGHT_BORDER);
            setBackground(HIGHLIGHT_BACKGROUND);
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
            setBorder(NORMAL_BORDER);
            setBackground(NORMAL_BACKGROUND);
        }
    }
}

