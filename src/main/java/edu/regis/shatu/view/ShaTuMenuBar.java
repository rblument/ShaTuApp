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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.regis.shatu.view.act.SaveSessionAction;
import edu.regis.shatu.view.act.SignOutAction;

/**
 * Menu bar used in the MainFrame.
 * 
 * @author rickb
 */
public class ShaTuMenuBar extends JMenuBar {
    public ShaTuMenuBar() {
        createFileMenu();
    }
 
    /**
     * Create the File menu appearing in the menu bar.
     */
    private void createFileMenu() {
        JMenu menu = new JMenu("File");
        
        menu.add(new JMenuItem(SaveSessionAction.instance()));
        
        menu.addSeparator();
        
        menu.add(new JMenuItem(SignOutAction.instance()));
        
        add(menu);
    }
}
