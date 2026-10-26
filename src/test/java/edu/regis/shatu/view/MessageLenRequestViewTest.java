package edu.regis.shatu.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import edu.regis.shatu.model.steps.MessageLenStep;

class MessageLenRequestViewTest {
    @Test
    void newRequestChecksLengthBeforeSendingIt() throws Exception {
        AtomicReference<MessageLenView> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(new MessageLenView()));

        SwingUtilities.invokeAndWait(() -> {
            MessageLenView view = ref.get();
            JTextField length = lengthField(view);
            length.setText("56");
            MessageLenStep request = new Gson().fromJson(view.newRequest().getData(), MessageLenStep.class);
            assertEquals(56, request.getMessageLength());

            for (String invalid : new String[] { "", "abc", "-1", "0", "1025" }) {
                length.setText(invalid);
                assertThrows(IllegalArgumentException.class, view::newRequest);
            }
        });
    }

    private static JTextField lengthField(MessageLenView view) {
        try {
            Field field = MessageLenView.class.getDeclaredField("messageLengthField");
            field.setAccessible(true);
            return (JTextField) field.get(view);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(ex);
        }
    }
}
