/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.rss_gui.validation;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Domi
 */
public class GenericValidators {
    public static void attachNotBlankValidator(JTextField textField, JLabel errorLabel){
        final String WARN_TEXT = errorLabel.getText();
        
        errorLabel.setText("");
        textField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                if (((JTextField)input).getText().isBlank()){
                    errorLabel.setText(WARN_TEXT);
                    return false;
                }else{
                    errorLabel.setText("");
                    return true;
                }
            }
            
            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                 return verify(source) || verifyTarget(target);
             }
        });
    }
    
    public static void attachNotBlankValidator(JPasswordField passwordField, JLabel errorLabel){
        final String WARN_TEXT = errorLabel.getText();
        
        errorLabel.setText("");
        passwordField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                if (new String(passwordField.getPassword()).isBlank()){
                    errorLabel.setText(WARN_TEXT);
                    return false;
                }else{
                    errorLabel.setText("");
                    return true;
                }
            }
            
            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                 return verify(source) || verifyTarget(target);
             }
        });
    }
    
    private GenericValidators(){}
}
