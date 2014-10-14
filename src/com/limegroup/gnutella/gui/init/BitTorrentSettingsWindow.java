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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.limewire.i18n.I18nMarker;
import org.limewire.util.StringUtils;

import com.frostwire.gui.bittorrent.TorrentSaveFolderComponent;
import com.frostwire.gui.bittorrent.TorrentSeedingSettingComponent;
import com.frostwire.gui.theme.ThemeMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.settings.LibrarySettings;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * This class displays a setup window for allowing the user to choose
 * the directory for saving their files.
 */
class BitTorrentSettingsWindow extends SetupWindow {

    private static final long serialVersionUID = 4918724013794478084L;

    private static final String LEARN_MORE_URL = "http://www.frostwire.com/faq#fil7";

    private TorrentSaveFolderComponent _torrentSaveFolderComponent;
    private TorrentSeedingSettingComponent _torrentSeedingSettingComponent;

    /**
     * Creates the window and its components
     */
    BitTorrentSettingsWindow(SetupManager manager) {
        super(manager, I18nMarker.marktr("BitTorrent Sharing Settings"), describeText(), LEARN_MORE_URL);
    }

    private static String describeText() {
        return I18nMarker.marktr("Choose a folder where files downloaded from the BitTorrent network should be saved to.\nPlease select if you want to \"Seed\" or to not \"Seed\" finished downloads. The link below has more information about \"Seeding\".");
    }

    protected void createWindow() {
        super.createWindow();

        JPanel mainPanel = new JPanel(new GridBagLayout());

        // "Saved Torrent Data" container
        _torrentSaveFolderComponent = new TorrentSaveFolderComponent(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0.5;
        mainPanel.add(_torrentSaveFolderComponent, gbc);
        _torrentSaveFolderComponent.putClientProperty(ThemeMediator.SKIN_PROPERTY_DARK_BOX_BACKGROUND, Boolean.TRUE);

        //Torrent Seeding container
        _torrentSeedingSettingComponent = new TorrentSeedingSettingComponent(false, true);
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0.5;

        mainPanel.add(_torrentSeedingSettingComponent, gbc);
        _torrentSeedingSettingComponent.putClientProperty(ThemeMediator.SKIN_PROPERTY_DARK_BOX_BACKGROUND, Boolean.TRUE);
        _torrentSeedingSettingComponent.updateUI();

        setSetupComponent(mainPanel);
    }

    /**
     * Overrides applySettings method in SetupWindow.
     *
     * This method applies any settings associated with this setup window.
     */
    public void applySettings(boolean loadCoreComponents) throws ApplySettingsException {
        List<String> errors = new ArrayList<String>(2);

        applyTorrentDataSaveFolderSettings(errors);

        applyTorrentSeedingSeetings(errors);

        if (!errors.isEmpty()) {
            throw new ApplySettingsException(StringUtils.explode(errors, "\n\n"));
        }
    }

    private void applyTorrentSeedingSeetings(List<String> errors) {
        if (!_torrentSeedingSettingComponent.hasOneBeenSelected()) {
            errors.add(I18n.tr("<html><p>You forgot to select your finished downloads \"Seeding\" setting.</p>\n<p></p><p align=\"right\"><a href=\"{0}\">What is \"Seeding\"?</a></p></html>", "http://support.frostwire.com/hc/en-us/articles/200302295-What-is-seeding-"));
            return;
        }

        SharingSettings.SEED_FINISHED_TORRENTS.setValue(_torrentSeedingSettingComponent.wantsSeeding());
        SharingSettings.SEED_HANDPICKED_TORRENT_FILES.setValue(_torrentSeedingSettingComponent.wantsHandpickedSeeding());
    }

    private void applyTorrentDataSaveFolderSettings(List<String> errors) {
        File folder = new File(_torrentSaveFolderComponent.getTorrentSaveFolderPath());
        if (folder.exists() && folder.isDirectory() && folder.canWrite()) {
            SharingSettings.TORRENT_DATA_DIR_SETTING.setValue(folder);
        } else {
            if (!folder.mkdirs()) {
                errors.add(I18n.tr("FrostWire could not create the Torrent Data Folder {0}", folder));
            } else {
                SharingSettings.TORRENT_DATA_DIR_SETTING.setValue(folder);
            }
        }

        // setup initial library folders here
        LibrarySettings.setupInitialLibraryFolders();
    }

}
