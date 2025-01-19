/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.utilities.swing;

import hr.algebra.utilities.swing.MessageUtils;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Domi
 */

// If setInputVerifier is used outside of this this could crash and burn, use with caution!
public class SomewhatSmartValidator {
    private ArrayList<JComponent> components = new ArrayList<>();
    private ArrayList<String> errorMessages = new ArrayList<>();
    
    public boolean verify(){
        for (int i = 0; i < components.size(); i++){
            if (!components.get(i).getInputVerifier().verify(components.get(i))){
                MessageUtils.showErrorMessage("Input error", errorMessages.get(i));
                return false;
            }
        }
        
        return true;
    }
    
    public void attachCustomValidator(JComponent component, JLabel errorLabel, String errorMessage, Callable<Boolean> failCondition){
        final String WARN_TEXT = errorLabel.getText();
        
        components.add(component);
        errorMessages.add(errorMessage);
        
        errorLabel.setText("");
        component.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                try {
                    if (failCondition.call()){
                        errorLabel.setText(WARN_TEXT);
                        return false;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SomewhatSmartValidator.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                errorLabel.setText("");
                return true;
            }
            
            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                 return verify(source) || verifyTarget(target);
             }
        });
    }
    
    public void attachNotBlankValidator(JTextField textField, JLabel errorLabel, String errorMessage){
        attachCustomValidator(textField,
                            errorLabel,
                            errorMessage,
                            () -> {
                                return ((JTextField)textField).getText().isBlank();
                            }
        );
    }
    
    public void attachNotBlankValidator(JPasswordField passwordField, JLabel errorLabel, String errorMessage){
        attachCustomValidator(passwordField,
                            errorLabel,
                            errorMessage,
                            () -> {
                                return new String(passwordField.getPassword()).isBlank();
                            }
        );
    }
}
