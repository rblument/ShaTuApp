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

import edu.regis.shatu.model.KnowledgeComponentKind;
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
import java.util.Arrays;
import java.util.stream.Collectors;

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
        MainFrame.instance().displayStep(stepSelection);
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
            BitOperation operation = BitOperation.fromLabel(labelText);
            if (operation != null) {
                JOptionPane.showMessageDialog(null,
                        operation.buildPopupMessage(),
                        "Binary value", JOptionPane.WARNING_MESSAGE);
            }
        } //end if
        
        else if (SwingUtilities.isLeftMouseButton(evt)){
            BitOperation operation = BitOperation.fromLabel(labelText);
            if (operation != null) {
                stepSelection = operation.getSelection();
                select();
            }
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

    /**
     * Metadata describing the SHA-256 bit operations rendered by this label.
     */
    private enum BitOperation {
        CHOICE("Ch", StepSelection.CHOICE_FUNCTION,
                KnowledgeComponentKind.CHOICE_FUNCTION,
                new RegisterInfo("e", 4),
                new RegisterInfo("f", 5),
                new RegisterInfo("g", 6)) {
            @Override
            protected String value() {
                return SHA_256.instance().getChoice();
            }
        },
        MAJORITY("Maj", StepSelection.MAJ_FUNCTION,
                KnowledgeComponentKind.MAJORITY_FUNCTION,
                new RegisterInfo("a", 0),
                new RegisterInfo("b", 1),
                new RegisterInfo("c", 2)) {
            @Override
            protected String value() {
                return SHA_256.instance().getMajority();
            }
        },
        BIG_SIGMA_ZERO("\u03A3\u2080", StepSelection.SHA_ZERO,
                KnowledgeComponentKind.SHA_ZERO,
                new RegisterInfo("a", 0)) {
            @Override
            protected String value() {
                return SHA_256.instance().getBigSig0Val();
            }
        },
        BIG_SIGMA_ONE("\u03A3\u2081", StepSelection.SHA_ONE,
                KnowledgeComponentKind.SHA_ONE,
                new RegisterInfo("a", 4)) {
            @Override
            protected String value() {
                return SHA_256.instance().getBigSig1Val();
            }
        };

        private final String label;
        private final StepSelection selection;
        private final KnowledgeComponentKind knowledgeComponent;
        private final RegisterInfo[] registers;

        BitOperation(String label, StepSelection selection,
                     KnowledgeComponentKind knowledgeComponent, RegisterInfo... registers) {
            this.label = label;
            this.selection = selection;
            this.knowledgeComponent = knowledgeComponent;
            this.registers = registers;
        }

        static BitOperation fromLabel(String text) {
            for (BitOperation operation : values()) {
                if (operation.label.equals(text)) {
                    return operation;
                }
            }
            return null;
        }

        StepSelection getSelection() {
            return selection;
        }

        String buildPopupMessage() {
            String registerHeader = registers.length == 1
                    ? "Incoming value"
                    : "Incoming values";
            String registerDetails = Arrays.stream(registers)
                    .map(RegisterInfo::render)
                    .collect(Collectors.joining("\n"));
            return registerHeader + "\n" +
                    registerDetails +
                    "\n\nOne outgoing value based on " + knowledgeComponent.title() + ": " +
                    value();
        }

        protected abstract String value();
    }

    /**
     * Simple value object describing the register name and index.
     */
    private static final class RegisterInfo {
        private final String name;
        private final int index;

        private RegisterInfo(String name, int index) {
            this.name = name;
            this.index = index;
        }

        private String render() {
            return "\t " + name + ": " + SHA_256.instance().getInTempValue(index);
        }
    }
}
