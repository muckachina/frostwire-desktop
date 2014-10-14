/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.bittorrent;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import com.frostwire.gui.bittorrent.CryptoCurrencyTextField.CurrencyURIPrefix;
import com.frostwire.gui.theme.ThemeMediator;
import com.frostwire.torrent.PaymentOptions;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.LimeTextField;

public class PaymentOptionsPanel extends JPanel {

    private final JCheckBox confirmationCheckbox;
    private final CryptoCurrencyTextField bitcoinAddress;
    private final CryptoCurrencyTextField litecoinAddress;
    private final CryptoCurrencyTextField dogecoinAddress;
    private final LimeTextField paypalUrlAddress;
    

    public PaymentOptionsPanel() {
        initBorder();
        confirmationCheckbox = new JCheckBox("<html><strong>" + I18n.tr("I am the content creator or I have the right to collect financial contributions for this work.")+"</strong><br>"+I18n.tr("I understand that incurring in financial gains from unauthorized copyrighted works can make me liable for counterfeiting and criminal copyright infringement.")+"</html>");
        bitcoinAddress = new CryptoCurrencyTextField(CurrencyURIPrefix.BITCOIN);
        litecoinAddress = new CryptoCurrencyTextField(CurrencyURIPrefix.LITECOIN);
        dogecoinAddress = new CryptoCurrencyTextField(CurrencyURIPrefix.DOGECOIN);
        paypalUrlAddress = new LimeTextField();
        
        setLayout(new MigLayout("fill"));
        initComponents();
        initListeners();
    }

    private void initListeners() {
        confirmationCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onConfirmationCheckbox();
            }
        });
        
        bitcoinAddress.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                onCryptoAddressPressed(bitcoinAddress);
            }
        });
        litecoinAddress.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                onCryptoAddressPressed(litecoinAddress);
            }
        });
        dogecoinAddress.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                onCryptoAddressPressed(dogecoinAddress);
            }
        });
    }

    protected void onConfirmationCheckbox() {
        bitcoinAddress.setEnabled(confirmationCheckbox.isSelected());
        litecoinAddress.setEnabled(confirmationCheckbox.isSelected());
        dogecoinAddress.setEnabled(confirmationCheckbox.isSelected());
        paypalUrlAddress.setEnabled(confirmationCheckbox.isSelected());
    }

    protected void onCryptoAddressPressed(CryptoCurrencyTextField textField) {
        boolean hasValidPrefixOrNoPrefix = false;
                
        hasValidPrefixOrNoPrefix = textField.hasValidPrefixOrNoPrefix();
        
        if (!textField.hasValidAddress() || !hasValidPrefixOrNoPrefix) {
            textField.setForeground(Color.red);
        } else {
            textField.setForeground(Color.black);
        }
        
        int caretPosition = textField.getCaretPosition();
        int lengthBefore = textField.getText().length();
        int selectionStart = textField.getSelectionStart();
        int selectionEnd = textField.getSelectionEnd();
        
        textField.setText(textField.getText().replaceAll(" ", ""));
        int lengthAfter = textField.getText().length();
        if (lengthAfter < lengthBefore) {
            int delta = (lengthBefore - lengthAfter);
            caretPosition -= delta;
            selectionEnd -= delta;
        }
        textField.setCaretPosition(caretPosition);         
        textField.setSelectionStart(selectionStart);
        textField.setSelectionEnd(selectionEnd);
    }

    private void initComponents() {
        add(confirmationCheckbox, "aligny top, gapbottom 10px, wrap, span");
        add(new JLabel("<html>"+I18n.tr("<strong>Bitcoin</strong> receiving wallet address")+"</html>"),"wrap, span");
        add(new JLabel(GUIMediator.getThemeImage("bitcoin_accepted.png")),"aligny top");
        bitcoinAddress.setPrompt("bitcoin:1xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        add(bitcoinAddress,"aligny top, growx, push, wrap");
        
        add(new JLabel("<html>"+I18n.tr("<strong>Litecoin</strong> receiving wallet address")+"</html>"),"wrap, span");
        add(new JLabel(GUIMediator.getThemeImage("litecoin_accepted.png")),"aligny top");
        litecoinAddress.setPrompt("litecoin:Lxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        add(litecoinAddress, "aligny top, growx, push, wrap");

        add(new JLabel("<html>"+I18n.tr("<strong>Dogecoin</strong> receiving wallet address")+"</html>"),"wrap, span");
        add(new JLabel(GUIMediator.getThemeImage("dogecoin_accepted.png")),"aligny top");
        dogecoinAddress.setPrompt("dogecoin:Dxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        add(dogecoinAddress, "aligny top, growx, push, wrap");

        add(new JLabel("<html>"+I18n.tr("<strong>Paypal</strong> payment/donation page url")+"</html>"),"wrap, span");
        add(new JLabel(GUIMediator.getThemeImage("paypal_accepted.png")), "aligny top");
        paypalUrlAddress.setPrompt("http://your.paypal.button/url/here");
        add(paypalUrlAddress, "aligny top, growx, push");
        
        onConfirmationCheckbox();
    }

    private void initBorder() {
        Border titleBorder = BorderFactory.createTitledBorder(I18n
                .tr("\"Name your price\", \"Tips\", \"Donations\" payment options"));
        Border lineBorder = BorderFactory.createLineBorder(ThemeMediator.LIGHT_BORDER_COLOR);
        Border border = BorderFactory.createCompoundBorder(lineBorder, titleBorder);
        setBorder(border);
    }
    
    public PaymentOptions getPaymentOptions() {
        PaymentOptions result = null;

        if (confirmationCheckbox.isSelected()) {
            boolean validBitcoin = bitcoinAddress.hasValidAddress();
            boolean validLitecoin = litecoinAddress.hasValidAddress();
            boolean validDogecoin = dogecoinAddress.hasValidAddress();
                
            if (validBitcoin || validLitecoin || validDogecoin || (paypalUrlAddress.getText()!=null && !paypalUrlAddress.getText().isEmpty())) {
                String bitcoin = validBitcoin ? bitcoinAddress.normalizeValidAddress() : null;
                String litecoin = validLitecoin ? litecoinAddress.normalizeValidAddress() : null;
                String dogecoin = validDogecoin ? dogecoinAddress.normalizeValidAddress() : null;
                String paypal = (paypalUrlAddress != null && paypalUrlAddress.getText() != null && !paypalUrlAddress.getText().isEmpty()) ? paypalUrlAddress.getText() : null;
                result = new PaymentOptions(bitcoin,litecoin,dogecoin,paypal);
            }
        }
        
        return result;
    }
    
    public boolean hasPaymentOptions() {
        return getPaymentOptions() != null;
    }
}