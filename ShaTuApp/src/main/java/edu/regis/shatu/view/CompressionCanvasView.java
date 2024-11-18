
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

import edu.regis.shatu.model.StepCompletion;
import edu.regis.shatu.model.aol.NewExampleRequest;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Displays a single round of the SHA-256 compression algorithm.
 *
 * The location of child components is absolute, hence, no layout manager.
 *
 * @author rickb
 */
public class CompressionCanvasView extends UserRequestView {
    /**
     * There eight working variables 'a' through 'h'.
     */
    private static final int WORKING_VARS_LEN = 8;
    
    /**
     * There are six modulo 256 additions to display.
     */
    private static final int MOD_ADDITIONS_LEN = 6;

    /**
     * The left pixel location of the 'a' working variable, which is the
     * visually leftmost component in this canvas.
     */
    private static final int LEFT_INDENT = 20;

    /**
     * The int value of 'a' in ASCII is 97.
     */
    private static final int ASCII_A_VAL = 97;

    /**
     * The eight input working variables that are displayed.
     */
    private WorkingVarLabel[] inWorkingVars;

    /**
     * The eight output working variables that are displayed
     */
    private WorkingVarLabel[] outWorkingVars;

    /**
     * The eight modulo 256 additions that are displayed.
     * 
     * 0 - connects 'd' to 'e' working variable
     * 1 - has 'h' and 'Ch' inputs
     * 2 - has W and K inputs
     * 3 - has Sigma1 input
     */
    private AddMod256Label[] modAdditions;

    private BitOpLabel sigma0Label;
    private BitOpLabel sigma1Label;
    private BitOpLabel chLabel;
    private BitOpLabel majLabel;
    private VariableLabel wLabel;
    private VariableLabel kLabel;
    private VariableLabel temp1Label;
    private VariableLabel temp2Label;

    public CompressionCanvasView() {
        setLayout(null);

        initializeComponents();
        layoutComponents();
    }

    /**
     * The working vars, modulo adds and bit op JLabel child components are
     * painted by the super, then we draw the connection lines.
     * 
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Point p;
        Point p2;
        int x, y, x2, y2;

        this.setForeground(Color.BLACK);

        // Lines connecting the in to out working variables
        for (int i = 0; i < inWorkingVars.length - 1; i++) {
            p = inWorkingVars[i].getLocation();

            x = p.x + WorkingVarLabel.HALF_SIZE;
            y = p.y + WorkingVarLabel.SIZE;

            g.drawLine(x, y, x, y + 300);
            
            p = outWorkingVars[i + 1].getLocation();
            x2 = p.x + WorkingVarLabel.HALF_SIZE;
            drawArrowLine(g, x, y + 300, x2, p.y, 6, 6);
        }
        
        // Lines going into working variables
        for (int i = 0; i < inWorkingVars.length; i++) {
            p = inWorkingVars[i].getLocation();

            x = p.x + WorkingVarLabel.HALF_SIZE;
            y = p.y + WorkingVarLabel.SIZE;

            g.drawLine(x, y, x, y - 60);
            
            drawArrowLine(g, x, y -60, x, p.y, 6, 6);
        }
        
        // Lines going out of working variables at the bottom
        for (int i = 0; i < inWorkingVars.length; i++) {
            p = outWorkingVars[i].getLocation();

            x = p.x + WorkingVarLabel.HALF_SIZE;
            y = p.y + WorkingVarLabel.SIZE * 2;

            drawArrowLine(g, x, y - 60, x, y, 6, 6);
        }
        
        // hide the 'd' to 'e' connection behind the modulo addition
        modAdditions[0].repaint();
        
        // The arrow from 'h' to mod modAdditions[1]
        p = inWorkingVars[7].getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p.y + WorkingVarLabel.SIZE;
        p = modAdditions[1].getLocation();
        drawArrowLine(g, x, y, p.x, p.y, 6, 6);
        
        // The arrow from 'e' line to Ch
        p = inWorkingVars[4].getLocation();
        p2 = chLabel.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + 5;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'f' line to Ch
        p = inWorkingVars[5].getLocation();
        p2 = chLabel.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + BitOpLabel.HALF_SIZE;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'g' line to Ch
        p = inWorkingVars[6].getLocation();
        p2 = chLabel.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + BitOpLabel.SIZE - 5;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'e' line to Sigma1
        p = inWorkingVars[4].getLocation();
        p2 = sigma1Label.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + BitOpLabel.HALF_SIZE;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'a' line to Maj
        p = inWorkingVars[0].getLocation();
        p2 = majLabel.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + 5;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'b' line to Maj
        p = inWorkingVars[1].getLocation();
        p2 = majLabel.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + BitOpLabel.HALF_SIZE;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'c' line to Maj
        p = inWorkingVars[2].getLocation();
        p2 = majLabel.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + BitOpLabel.SIZE - 5;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // The arrow from 'a' line to Sigma0
        p = inWorkingVars[0].getLocation();
        p2 = sigma0Label.getLocation();
        x = p.x + WorkingVarLabel.HALF_SIZE;
        y = p2.y + BitOpLabel.HALF_SIZE;
        drawArrowLine(g, x, y, p2.x, y, 6, 6);
        
        // Arrow from Ch to modAdditions[1]
        p = chLabel.getLocation();
        x = p.x + BitOpLabel.SIZE;
        y = p.y + BitOpLabel.HALF_SIZE;
        
        p = modAdditions[1].getLocation();
        x2 = p.x;
        y2 = p.y + AddMod256Label.HALF_SIZE;
        
        drawArrowLine(g, x, y, x2, y2, 6, 6);
        
        // Arrow from modAddtions[2] to modAddiotions[1]
        p = modAdditions[2].getLocation();
        x = p.x;
        x2 += AddMod256Label.SIZE;
        drawArrowLine(g, x, y, x2, y2, 6, 6);
        
        // Arrow from modAdditions[1] to modAdditions[3]
        p = modAdditions[1].getLocation();
        x = p.x + AddMod256Label.HALF_SIZE;
        y = p.y + AddMod256Label.SIZE;
        p = modAdditions[3].getLocation();
        drawArrowLine(g, x, y, x, p.y, 6, 6);
        
        // Arrow from modAdditions[3] to modAdditions[5]
        p = modAdditions[3].getLocation();
        y = p.y + AddMod256Label.SIZE;
        p = modAdditions[5].getLocation();
        drawArrowLine(g, x, y, x, p.y, 6, 6);
        
        // Arrow from modAdditions[5] to 'a'
        y += AddMod256Label.SIZE;
        y2 = p.y + 40;
        g.drawLine(x, p.y, x, y2);
        
        y = y2;        
        p = outWorkingVars[0].getLocation();
        x2 = p.x + WorkingVarLabel.HALF_SIZE;
        y2 = p.y - 30;
        g.drawLine(x, y, x2, y2);
        
        drawArrowLine(g, x2, y2, x2, p.y , 6, 6);
        
        // Arrow from Wt to modAdditions[2]
        p = modAdditions[2].getLocation();
        x = p.x + AddMod256Label.HALF_SIZE;
        y = p.y;
        g.drawLine(x, y, x, y - 40);
        drawArrowLine(g, x, y -40, x, p.y, 6, 6);
        
        // Arrow from Kt to modAdditions[2]
        p = modAdditions[2].getLocation();
        x = p.x + 60;
        y = p.y + AddMod256Label.HALF_SIZE;
        drawArrowLine(g, x, y, x - 40, y, 6, 6);
        
        
        // Arrow from Temporary Variable 1 to modAdditions[0]
        p = modAdditions[3].getLocation();
        x = p.x + AddMod256Label.HALF_SIZE;
        p = modAdditions[0].getLocation();
        x2 = p.x + AddMod256Label.SIZE;
        y = p.y + AddMod256Label.HALF_SIZE;
        drawArrowLine(g, x, y, x2, y, 6, 6);
        
        // Arrow from Sigma0 to modAdditions[4]
        p = sigma0Label.getLocation();
        x = p.x + BitOpLabel.SIZE;
        y = p.y + BitOpLabel.HALF_SIZE;
        p = modAdditions[4].getLocation();
        x2 = p.x;
        drawArrowLine(g, x, y, x2, y, 6, 6);
        
        // Arrow from modAdditions[4] to modAdditions[5]
        p = modAdditions[4].getLocation();
        x = p.x + AddMod256Label.SIZE;
        y = p.y + AddMod256Label.HALF_SIZE;
        p = modAdditions[5].getLocation();
        x2 = p.x;
        drawArrowLine(g, x, y, x2, y, 6, 6);
        
        // Arrow from Maj to modAdditions[4]
        p = majLabel.getLocation();
        x = p.x + BitOpLabel.SIZE;
        y = p.y + BitOpLabel.HALF_SIZE;
        p = modAdditions[4].getLocation();
        x2 = p.x + AddMod256Label.HALF_SIZE;
        y2 = p.y;
        g.drawLine(x, y, x2, y);
        drawArrowLine(g, x2, y, x2, y2, 6, 6);
    }

    /**
     * Create the child components used in this frame.
     */
    private void initializeComponents() {
        inWorkingVars = new WorkingVarLabel[WORKING_VARS_LEN];
        outWorkingVars = new WorkingVarLabel[WORKING_VARS_LEN];

        for (int i = 0; i < WORKING_VARS_LEN; i++) {
            String ch = String.valueOf((char) (i + ASCII_A_VAL)); // 'a' to 'h'

            inWorkingVars[i] = new WorkingVarLabel(ch);
            outWorkingVars[i] = new WorkingVarLabel(ch);
        }

        modAdditions = new AddMod256Label[MOD_ADDITIONS_LEN];
        for (int i = 0; i < modAdditions.length; i++) {
            modAdditions[i] = new AddMod256Label();
        }
        int sigma = 931;
        sigma1Label = new BitOpLabel((char)sigma + "\u2081");
        sigma0Label = new BitOpLabel((char) sigma + "\u2080");
        chLabel = new BitOpLabel("Ch");
        majLabel = new BitOpLabel("Maj");
        
        sigma1Label.setFont(new Font("", Font.PLAIN, 16));
        sigma0Label.setFont(new Font("", Font.PLAIN, 16));
        
        wLabel = new VariableLabel("W\u209C");
        kLabel = new VariableLabel("K\u209C");
        
        
        wLabel.setFont(new Font("", Font.PLAIN, 16));
        kLabel.setFont(new Font("", Font.PLAIN, 16));
        
        temp1Label = new VariableLabel("T\u2081");
        temp2Label = new VariableLabel("T\u2082");
        
        temp1Label.setFont(new Font("", Font.PLAIN, 16));
        temp2Label.setFont(new Font("", Font.PLAIN, 16));
    }

    private void layoutComponents() {
        Color white = new Color(255,255,255);
        setBackground(white);
        Point p;
        int x, y;
        
        for (int i = 0; i < WORKING_VARS_LEN; i++) {
            x = LEFT_INDENT + (29 * i);

            inWorkingVars[i].setLocation(new Point(x, 100));
            add(inWorkingVars[i]);

            outWorkingVars[i].setLocation(new Point(x, 500));
            add(outWorkingVars[i]);
        }

        // The Bit operations Ch, Simga1, Maj, and Sigma2 are located a few 
        // pixels to the right of the last working variable 'h', which is
        // inWorkingVars[7] and below the input working variables.
        p = inWorkingVars[7].getLocation();
        x = p.x + WorkingVarLabel.SIZE + 40;
        y = p.y + WorkingVarLabel.SIZE + 40;
        
        chLabel.setLocation(new Point(x, y));
        add(chLabel);
        
        y += WorkingVarLabel.SIZE + 40;
        sigma1Label.setLocation(new Point(x, y));
        add(sigma1Label);
        
        // Center the first modulo addtion label in the x axis with the center 
        // of the 'd' working variable, which is inworkingVars[3] and 
        // Half way between S1 and Maj in the y axis 
        int x2 = inWorkingVars[3].getLocation().x + WorkingVarLabel.HALF_SIZE - AddMod256Label.HALF_SIZE;
        y += WorkingVarLabel.SIZE + 40; // y location of Maj
        modAdditions[0].setLocation(new Point(x2, y - 20));
        add(modAdditions[0]);    
       
        majLabel.setLocation(new Point(x, y));
        add(majLabel);

        y += WorkingVarLabel.SIZE + 40;
        sigma0Label.setLocation(new Point(x, y));
        add(sigma0Label);
        
        // Center the second mod addition with the Ch bit operation label
        x = chLabel.getLocation().x + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE + 150;
        y = chLabel.getLocation().y + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE;
        modAdditions[1].setLocation(new Point(x, y));
        add(modAdditions[1]);
        
        // Has W and K inputs
        modAdditions[2].setLocation(new Point(x+ AddMod256Label.SIZE + 100, y));
        add(modAdditions[2]);
        
        // W input location
        x = modAdditions[2].getLocation().x;
        y = modAdditions[2].getLocation().y;
        x -= VariableLabel.HALF_SIZE - 10;
        y -= VariableLabel.SIZE + 20;
        wLabel.setLocation(x, y);
        add(wLabel);
        
        // K input location
        x = modAdditions[2].getLocation().x;
        y = modAdditions[2].getLocation().y;
        x += VariableLabel.SIZE;
        y -= VariableLabel.HALF_SIZE;
        kLabel.setLocation(x, y);
        add(kLabel);
        
        // Third mod addition has Sigma1 inputs and centered on it
        x = sigma1Label.getLocation().x + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE + 150;
        y = sigma1Label.getLocation().y + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE;
        modAdditions[3].setLocation(new Point(x, y));
        add(modAdditions[3]);
        
        // Temporary Variable 1 location under third mod addition
        x = modAdditions[3].getLocation().x;
        y = modAdditions[0].getLocation().y;
        y -= AddMod256Label.SIZE;
        temp1Label.setLocation(x, y);
        add(temp1Label);
        
        // Has input from sigma0 Centered on Sigma0
        x = sigma0Label.getLocation().x + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE + 75;
        y = sigma0Label.getLocation().y + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE;
        modAdditions[4].setLocation(new Point(x, y));
        add(modAdditions[4]);
        
        x = sigma0Label.getLocation().x + BitOpLabel.HALF_SIZE - AddMod256Label.HALF_SIZE + 150;
        modAdditions[5].setLocation(new Point(x, y));
        add(modAdditions[5]);
        
        // Temporary Variable 2 location between fourth and fifth mod addition
        x = modAdditions[4].getLocation().x;
        y = modAdditions[4].getLocation().y;
        x += AddMod256Label.SIZE;
        y += AddMod256Label.HALF_SIZE - 10;
        temp2Label.setLocation(x, y);
        add(temp2Label);
    }

    /**
     * Draw an arrow line between two points.
     * 
     * Author: phibao37 of Stack Overflow
     *
     * @param g the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d the width of the arrow.
     * @param h the height of the arrow.
     */
    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.drawPolygon(xpoints, ypoints, 3); // Rickb
    }

    @Override
    /**
     * Updates the description, question, and hints from the model
     * 
     * TODO: THIS IS A PLACEHOLDER UNTIl WE HAVE HAVE THE MODEL CODE COMPLETED
     */
    protected void updateView() {
        if (model != null) {
            // ****TO-DO*****
            // Update the view's information from the model
            // Debugging dynamic updates to the model can be done here.
            System.out.println("CompressionCanvasView");
        }
    }
    
    @Override
    public NewExampleRequest newRequest() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public StepCompletion stepCompletion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
