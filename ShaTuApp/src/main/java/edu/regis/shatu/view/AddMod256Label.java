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
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import edu.regis.shatu.view.CompressionCanvasView;
import javax.swing.JOptionPane;

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
    private StepSelection stepSelection = StepSelection.ADD_TWO_BIT;
    
    private boolean isSelected;

    /**
     * Initialize this label with the given text and a size that was determined
     * empirically.
     * 
     * @param text the text of this label
     */
    public AddMod256Label(String text) {
        super(text, SwingConstants.CENTER);
        
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

    //TO DO:  Add label click functionality to show value
    @Override
    public void mouseClicked(MouseEvent evt) {
        String labelText = getText();
        
        if (SwingUtilities.isRightMouseButton(evt)){
            switch(labelText){
                case ("0"):
                    JOptionPane.showMessageDialog(null, 
                            "Two incoming values\n" +
                            "\t d: " + SHA_256.instance().getInTempValue(3) +
                            "\n\t T\u2081: " + SHA_256.instance().getT1() 
                            + "\n\n One outgoing value based on Modulo Addition: " 
                                    + SHA_256.instance().getTempOutValue(4), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break;
                case ("1"):
                    JOptionPane.showMessageDialog(null, 
                            "Three incoming values\n" +
                            "\t h: " + SHA_256.instance().getInTempValue(7) +
                            "\n\t Ch: " + SHA_256.instance().getChoice() +
                            "\n\t W\u209C % K\u209C : " + SHA_256.instance().getMod3()
                            + "\n\n One outgoing value based on Modulo Addition: " 
                                    + SHA_256.instance().getMod2(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break; 
                case ("2"):
                    JOptionPane.showMessageDialog(null, 
                        "Two incoming values\n" +
                            "\t W\u209C: " + SHA_256.instance().getWt() +
                            "\n\t K\u209C: " + SHA_256.instance().getChoice() 
                            + "\n\n One outgoing value based on Modulo Addition: " 
                                    + SHA_256.instance().getMod3(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break; 
                case ("3"):
                    JOptionPane.showMessageDialog(null, 
                            "Incoming value\n" +
                            "\t modulo 1: " + SHA_256.instance().getMod2() +
                             "\n\n One outgoing value based on Modulo Addition: "
                                    + SHA_256.instance().getT1(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE);
                    break; 
                case ("4"):
                    JOptionPane.showMessageDialog(null, 
                        "Two incoming values\n" +
                            "\t Maj: " + SHA_256.instance().getMajority() +
                            "\n\t K\u209C: " + SHA_256.instance().getBigSig0Val() 
                            + "\n\n One outgoing value based on Modulo Addition: " 
                                    + SHA_256.instance().getT2(), 
                        "Binary value", JOptionPane.WARNING_MESSAGE); 
                    break; 
                case ("5"):
                    JOptionPane.showMessageDialog(null, 
                        "Two incoming values\n" +
                            "\t T\u2081: " + SHA_256.instance().getT1() +
                            "\n\t T\u2082: "  + SHA_256.instance().getT2() 
                            + "\n\n One outgoing value based on Modulo Addition: " 
                                    + SHA_256.instance().getTempOutValue(0), 
                        "Binary value", JOptionPane.WARNING_MESSAGE); 
                    break; 
            }
        }
        else if (SwingUtilities.isLeftMouseButton(evt)){
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

