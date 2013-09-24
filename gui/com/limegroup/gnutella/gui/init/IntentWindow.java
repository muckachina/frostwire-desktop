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

package com.limegroup.gnutella.gui.init;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;
import org.limewire.i18n.I18nMarker;
import org.limewire.util.CommonUtils;

import com.frostwire.gui.theme.ThemeMediator;

/** State Your Intent. */
final class IntentWindow extends SetupWindow {

    private boolean setWillNot = false;
    private Properties properties;

    IntentWindow(SetupManager manager) {
        super(manager, I18nMarker.marktr("State Your Intent"), I18nMarker.marktr("One more thing..."));
    }

    private boolean isCurrentVersionChecked() {
        if (properties == null) {
            properties = new Properties();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(getPropertiesFile());
                properties.load(fis);
            } catch (IOException iox) {
                System.out.println("Could not load properties from property file.");
                return false;
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }

        String exists = properties.getProperty("willnot");
        return exists != null && exists.equals("true");
    }

    boolean isConfirmedWillNot() {
        return isCurrentVersionChecked() || setWillNot;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        JPanel innerPanel = new JPanel(new BorderLayout());
        final IntentPanel intentPanel = new IntentPanel();
        innerPanel.add(intentPanel, BorderLayout.CENTER);
        setSetupComponent(innerPanel);
        intentPanel.putClientProperty(ThemeMediator.SKIN_PROPERTY_DARK_BOX_BACKGROUND, Boolean.TRUE);

        setNext(null);
        intentPanel.addButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (intentPanel.hasSelection()) {
                    setNext(IntentWindow.this);
                    setWillNot = intentPanel.isWillNot();
                    _manager.enableActions(getAppropriateActions());
                }
            }
        });
    }

    @Override
    public void applySettings(boolean loadCoreComponents) {
        if (setWillNot) {
            properties.put("willnot", "true");
            try {
                properties.store(new FileOutputStream(getPropertiesFile()), "Started & Ran Versions");
            } catch (IOException ignored) {
                System.out.println(ignored);
            }
        }
    }

    private File getPropertiesFile() {
        return new File(CommonUtils.getUserSettingsDir(), "intent.props");
    }
}