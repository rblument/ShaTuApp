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

import edu.regis.shatu.svc.SHA_256;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * ToDo: Should this be a HilightLabel?
 * @author rickb
 */
public class BitOpLabel extends JLabel implements MouseListener {
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
     * The background of this label is pinkish when not highlighted.
     */
    private static final Color NORMAL_BACKGROUND = new Color(230,230,250);
    
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
    public BitOpLabel(String text) {
        super(text, SwingConstants.CENTER);
        
        isSelected = false;

        Dimension d = new Dimension(SIZE, SIZE); // Determined empirically

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
    public void mouseClicked(MouseEvent evt) {
        String labelText = getText();
        System.out.print(getText());
        if (SwingUtilities.isRightMouseButton(evt)){
            switch(labelText){
                case ("Ch"):
                    JOptionPane.showMessageDialog(null, 
                            "Three incoming values\n" +
                            "\t e: " + SHA_256.instance().getInTempValue(4) +
                            "\n\t f: " + SHA_256.instance().getInTempValue(5 )+ 
                            "\n\t g: " + SHA_256.instance().getInTempValue(6) 
                            + "\n\n One outgoing value based on Choice Function: " 
                                    + SHA_256.instance().getChoice(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break;
                case ("Maj"):
                    JOptionPane.showMessageDialog(null, 
                            "Three incoming values\n" +
                            "\t a: " + SHA_256.instance().getInTempValue(0) +
                            "\n\t b: " + SHA_256.instance().getInTempValue(1)+ 
                            "\n\t c: " + SHA_256.instance().getInTempValue(2) 
                            + "\n\n One outgoing value based on Majority Function: " 
                                    + SHA_256.instance().getMajority(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break;
                case ("\u03A3\u2080"):
                    JOptionPane.showMessageDialog(null, 
                            "Incoming value\n" +
                            "\t a: " + SHA_256.instance().getInTempValue(0) +
                            "\n\n One outgoing value based on SHA Sum 0 value Function: " 
                                    + SHA_256.instance().getBigSig0Val(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break;
                case ("\u03A3\u2081"):
                    JOptionPane.showMessageDialog(null, 
                            "Incoming value\n" +
                            "\t a: " + SHA_256.instance().getInTempValue(4) +
                            "\n\n One outgoing value based on SHA Sum 1 value Function: " 
                                    + SHA_256.instance().getBigSig1Val(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break;
            } //end case
        } //end if
        
        else if (SwingUtilities.isLeftMouseButton(evt)){
            switch(labelText){
                case ("Ch"):
                    stepSelection = StepSelection.CHOICE_FUNCTION;
                    break;
                case ("Maj"):
                    stepSelection = StepSelection.MAJ_FUNCTION;
                    break;
                case ("\u03A3\u2080"):
                    stepSelection = StepSelection.SHA_ZERO;
                    break;
                case ("\u03A3\u2081"):
                    stepSelection = StepSelection.SHA_ONE;
                    break;
            }
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