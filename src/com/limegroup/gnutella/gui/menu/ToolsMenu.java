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

package com.limegroup.gnutella.gui.menu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.limewire.i18n.I18nMarker;
import org.limewire.util.OSUtils;

import com.frostwire.gui.updates.UpdateMediator;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.iTunesMediator;
import com.limegroup.gnutella.gui.actions.AbstractAction;

/**
 * Contains all of the menu items for the tools menu.
 */
final class ToolsMenu extends AbstractMenu {

    private final UpdateAction updateAction;

    /**
     * Creates a new <tt>ToolsMenu</tt>, using the <tt>key</tt> 
     * argument for setting the locale-specific title and 
     * accessibility text.
     *
     * @param key the key for locale-specific string resources unique
     *            to the menu
     */
    ToolsMenu() {
        super(I18n.tr("&Tools"));

        this.updateAction = new UpdateAction();

        if (OSUtils.isMacOSX() || OSUtils.isWindows()) {
            addMenuItem(new RebuildiTunesPlaylist());
        }

        addMenuItem(new ShowOptionsAction());
        addMenuItem(updateAction);
    }

    @Override
    protected void refresh() {
        updateAction.refresh();
    }

    private static class RebuildiTunesPlaylist extends AbstractAction {

        private static final long serialVersionUID = 8348355619323878579L;

        public RebuildiTunesPlaylist() {
            super(I18n.tr("Rebuild iTunes \"FrostWire\" Playlist"));
            putValue(LONG_DESCRIPTION, I18nMarker.marktr("Deletes and re-builds the \"FrostWire\" playlist on iTunes with all the audio files found on your Torrent Data Folder."));
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            int result = JOptionPane.showConfirmDialog(GUIMediator.getAppFrame(), I18n.tr("This will remove your \"FrostWire\" playlist in iTunes and replace\n" + "it with one containing all the iTunes compatible files in your \n" + "Frostwire \"Torrent Data Folder\"\n\n"
                    + "Please note that it will add the files to the iTunes library as well\n" + "and this could result in duplicate files on your iTunes library\n\n" + "Are you sure you want to continue?"), I18n.tr("Warning"), JOptionPane.WARNING_MESSAGE, JOptionPane.YES_OPTION
                    | JOptionPane.CANCEL_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                iTunesMediator.instance().resetFrostWirePlaylist();
            }
        }
    }

    private static class ShowOptionsAction extends AbstractAction {
        private static final long serialVersionUID = 6187597973189408647L;

        public ShowOptionsAction() {
            super(I18n.tr("&Options"));
            putValue(LONG_DESCRIPTION, I18nMarker.marktr("Display the Options Screen"));
        }

        public void actionPerformed(ActionEvent e) {
            GUIMediator.instance().setOptionsVisible(true);
        }
    }

    private static class UpdateAction extends AbstractAction {

        private static final long serialVersionUID = 2915214339056016808L;

        public UpdateAction() {
            super(I18n.tr("&Update FrostWire"));
            putValue(LONG_DESCRIPTION, I18n.tr("Update FrostWire to the latest version"));
        }

        public void actionPerformed(ActionEvent e) {
            if (UpdateMediator.instance().isUpdateDownloaded()) {
                UpdateMediator.instance().startUpdate();
            } else {
                UpdateMediator.instance().checkForUpdate();
            }
        }

        public void refresh() {
            if (UpdateMediator.instance().isUpdated()) {
                String text = I18n.tr("You are up to date with FrostWire") + " " + "v." + UpdateMediator.instance().getLatestVersion();
                putValue(NAME, text);
                putValue(LONG_DESCRIPTION, text);
                this.setEnabled(false);
            } else if (UpdateMediator.instance().isUpdateDownloading()) {
                String text = I18n.tr("Downloading update...");
                putValue(NAME, text);
                putValue(LONG_DESCRIPTION, text);
                this.setEnabled(false);
            } else if (UpdateMediator.instance().isUpdateDownloaded()) {
                String text = I18n.tr("Install update") + " " + "v." + UpdateMediator.instance().getLatestVersion();
                putValue(NAME, text);
                putValue(LONG_DESCRIPTION, text);
                this.setEnabled(true);
            } else {
                String text = I18n.tr("Check for update");
                putValue(NAME, text);
                putValue(LONG_DESCRIPTION, text);
                this.setEnabled(true);
            }
        }
    }
}