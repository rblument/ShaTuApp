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
package edu.regis.shatu.view.act;

import java.awt.Image;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import edu.regis.shatu.util.ImgFactory;

/**
 * Abstract root for all GUI actions in the ShaTu application.
 * 
 * Provides support for loading image icons and assigning the GUI controller.
 * 
 * @author rickb
 */
public abstract class ShaTuGuiAction extends AbstractAction {
    public ShaTuGuiAction(String name) {
        super(name);
    }
    
    /**
     * Load and return the image icon specified by the given image file name,
     * as found in the "img/" directory in the root CLASSPATH.
     * 
     * @param imageFileName name of file with suffix in img/ director
     *                      (e.g., "save16.gif"
     * @param altText
     * @return 
     */
    protected ImageIcon loadIcon(String imageFileName, String altText) {
        // ToDo: Better error reporting
        Image img = ImgFactory.createImage(imageFileName);
        return new ImageIcon(img, altText);
    }
}
