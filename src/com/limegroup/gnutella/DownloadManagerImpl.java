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

package com.limegroup.gnutella;

import com.frostwire.bittorrent.BTDownload;
import com.frostwire.bittorrent.BTEngine;
import com.frostwire.bittorrent.BTEngineAdapter;
import com.frostwire.bittorrent.BTEngineListener;
import com.frostwire.logging.Logger;
import com.limegroup.gnutella.settings.UpdateSettings;

import java.io.File;

public class DownloadManagerImpl implements DownloadManager {

    private static final Logger LOG = Logger.getLogger(DownloadManagerImpl.class);

    private final ActivityCallback activityCallback;

    public DownloadManagerImpl(ActivityCallback downloadCallback) {
        this.activityCallback = downloadCallback;
    }

    private void addDownload(BTDownload dl) {
        synchronized (this) {
            activityCallback.addDownload(dl);
        }
    }

    public void loadSavedDownloadsAndScheduleWriting() {

        BTEngine engine = BTEngine.getInstance();

        engine.setListener(new BTEngineAdapter() {
            @Override
            public void downloadAdded(BTEngine engine, BTDownload dl) {
                String name = dl.getName();
                if (name != null && name.contains("fetchMagnet - ")) {
                    return;
                }

                File savePath = dl.getSavePath();

                if (savePath != null && savePath.getParentFile().getAbsolutePath().equals(UpdateSettings.UPDATES_DIR.getAbsolutePath())) {
                    LOG.info("Update download: " + savePath);
                    return;
                }

//                if (CommonUtils.isPortable()) {
//                    updateDownloadManagerPortableSaveLocation(downloadManager);
//                }

                addDownload(dl);
            }
        });

        engine.restoreDownloads();
    }

    /*
    private void updateDownloadManagerPortableSaveLocation(org.gudy.azureus2.core3.download.DownloadManager downloadManager) {
        boolean hadToPauseIt = false;
        if (downloadManager.getState() != org.gudy.azureus2.core3.download.DownloadManager.STATE_STOPPED) {
            downloadManager.pause();
            hadToPauseIt = true;
        }
        String previousSaveLocation = downloadManager.getSaveLocation().getAbsolutePath();
        String newLocationPrefix = SharingSettings.DEFAULT_TORRENT_DATA_DIR.getAbsolutePath();

        if (!previousSaveLocation.startsWith(newLocationPrefix)) {
            File newSaveLocation = new File(SharingSettings.DEFAULT_TORRENT_DATA_DIR, downloadManager.getSaveLocation().getName());
            if (newSaveLocation.exists()) {
                if (newSaveLocation.isDirectory()) {
                    downloadManager.setDataAlreadyAllocated(false); //absolutely necessary
                    downloadManager.setTorrentSaveDir(newSaveLocation.getAbsolutePath());
                } else if (newSaveLocation.isFile()) {
                    downloadManager.setTorrentSaveDir(SharingSettings.DEFAULT_TORRENT_DATA_DIR.getAbsolutePath());
                }
            }
        }

        if (hadToPauseIt) {
            downloadManager.resume();
        }
    }*/
}
