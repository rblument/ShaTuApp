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
package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

/**
 * Checks the live red/green feedback on the Initialize Variables fields.
 */
class InitVarViewTest {

    private static final Color GREEN = Color.GREEN.darker().darker();

    private static JTextField[] fields(InitVarView view) throws Exception {
        Field field = InitVarView.class.getDeclaredField("hVars");
        field.setAccessible(true);
        return (JTextField[]) field.get(view);
    }

    @Test
    void typingShowsRedUntilTheAnswerIsCorrect() throws Exception {
        AtomicReference<InitVarView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(new InitVarView()));
        JTextField[] hVars = fields(ref.get());

        SwingUtilities.invokeAndWait(() -> {
            hVars[0].setText("0x6a09");
            assertEquals(Color.RED, hVars[0].getForeground());

            hVars[0].setText("0x6a09e667");
            assertEquals(GREEN, hVars[0].getForeground());

            hVars[7].setText("5BE0CD19");
            assertEquals(GREEN, hVars[7].getForeground());
        });
    }
}
