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
package edu.regis.shatu.svc;

<<<<<<< HEAD
import edu.regis.shatu.view.AddTwoBitView;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
=======
>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
import org.junit.jupiter.api.BeforeAll;
//import org.mockito.Mockito;
//import static org.mockito.Mockito.when;

<<<<<<< HEAD
=======
import edu.regis.shatu.view.AddTwoBitView;

>>>>>>> e729936a04f120488f7da9a1bd02ddd370b85ec3
/**
 *
 * @author damianmichalec
 */
public class AddTwoBitViewTest {

    private AddTwoBitView addTwoBitView;

    @BeforeAll
    public void setUp() {
        addTwoBitView = new AddTwoBitView();
    }

    /*
    @Test
    public void testHandleKeyPressWithEmptyAnswer() {
        assertEquals(true, true);
        JTextField textField = new JTextField();
        textField.setText("test");
        //addTwoBitView.setAnswerField(textField);

        KeyEvent mockEvent = Mockito.mock(KeyEvent.class);
        when(mockEvent.getKeyCode()).thenReturn(KeyEvent.VK_ENTER);

        addTwoBitView.keyPressed(mockEvent);
        String expectedAnswer = "1111111";
        //String actualAnswer = addTwoBitView.getCorrectAnswer();

        
        assertTrue("This will succeed.", true);
    }
*/
}
