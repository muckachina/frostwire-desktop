/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
 *
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

package com.frostwire.gui.library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.frostwire.alexandria.InternetRadioStation;
import com.frostwire.alexandria.Library;
import com.frostwire.alexandria.Playlist;
import com.frostwire.alexandria.PlaylistItem;
import com.frostwire.alexandria.db.LibraryDatabase;
import com.frostwire.gui.player.DeviceMediaSource;
import com.frostwire.gui.player.InternetRadioAudioSource;
import com.frostwire.gui.player.MediaPlayer;
import com.frostwire.gui.player.MediaSource;
import com.frostwire.gui.theme.ThemeMediator;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.util.DividerLocationSettingUpdater;
import com.limegroup.gnutella.settings.LibrarySettings;
import com.limegroup.gnutella.settings.UISettings;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class LibraryMediator {

    private static final String FILES_TABLE_KEY = "LIBRARY_FILES_TABLE";
    private static final String PLAYLISTS_TABLE_KEY = "LIBRARY_PLAYLISTS_TABLE";
    private static final String INTERNET_RADIO_TABLE_KEY = "LIBRARY_INTERNET_RADIO_TABLE";
    private static final String DEVICE_TABLE_KEY = "DEVICE_FILES_TABLE";

    private static JPanel MAIN_PANEL;

    /**
     * Singleton instance of this class.
     */
    private static LibraryMediator INSTANCE;

    private LibraryExplorer libraryExplorer;
    private LibraryPlaylists libraryPlaylists;
    private LibraryCoverArt libraryCoverArt;
    private LibraryLeftPanel libraryLeftPanel;
    private LibrarySearch librarySearch;

    private static Library LIBRARY;

    private CardLayout _tablesViewLayout = new CardLayout();
    private JPanel _tablesPanel;
    private JSplitPane splitPane;

    private Map<Object, Integer> scrollbarValues;
    private Object lastSelectedKey;
    private AbstractLibraryTableMediator<?, ?, ?> lastSelectedMediator;

    private Set<Integer> idScanned;

    private AbstractLibraryTableMediator<?, ?, ?> currentMediator;

    private final DeviceDiscoveryClerk clerk;

    /**
     * @return the <tt>LibraryMediator</tt> instance
     */
    public static LibraryMediator instance() {
        if (INSTANCE == null) {
            INSTANCE = new LibraryMediator();
        }
        return INSTANCE;
    }

    public LibraryMediator() {
        GUIMediator.setSplashScreenString(I18n.tr("Loading Library Window..."));

        idScanned = new HashSet<Integer>();

        getComponent(); // creates MAIN_PANEL

        scrollbarValues = new HashMap<Object, Integer>();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLibraryLeftPanel(), getLibraryRightPanel());
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JSplitPane splitPane = (JSplitPane) evt.getSource();
                int current = splitPane.getDividerLocation();
                if (current > LibraryLeftPanel.MAX_WIDTH) {
                    splitPane.setDividerLocation(LibraryLeftPanel.MAX_WIDTH);
                } else if (current < LibraryLeftPanel.MIN_WIDTH) {
                    splitPane.setDividerLocation(LibraryLeftPanel.MIN_WIDTH);
                }

            }
        });

        DividerLocationSettingUpdater.install(splitPane, UISettings.UI_LIBRARY_MAIN_DIVIDER_LOCATION);

        MAIN_PANEL.add(splitPane);
        
        clerk = new DeviceDiscoveryClerk();
    }

    public DeviceDiscoveryClerk getDeviceDiscoveryClerk() {
        return clerk;
    }

    protected Object getSelectedKey() {
        if (getSelectedPlaylist() != null) {
            return getSelectedPlaylist();
        } else {
            return getLibraryExplorer().getSelectedDirectoryHolder();
        }
    }

    public static Library getLibrary() {
        if (LIBRARY == null) {
            LIBRARY = new Library(LibrarySettings.LIBRARY_DATABASE);
        }
        return LIBRARY;
    }

    public LibraryExplorer getLibraryExplorer() {
        if (libraryExplorer == null) {
            libraryExplorer = new LibraryExplorer();
        }
        return libraryExplorer;
    }

    public LibraryPlaylists getLibraryPlaylists() {
        if (libraryPlaylists == null) {
            libraryPlaylists = new LibraryPlaylists();
        }
        return libraryPlaylists;
    }

    /**
     * Returns null if none is selected.
     * @return
     */
    public Playlist getSelectedPlaylist() {
        return getLibraryPlaylists().getSelectedPlaylist();
    }

    public LibrarySearch getLibrarySearch() {
        if (librarySearch == null) {
            librarySearch = new LibrarySearch();
        }
        return librarySearch;
    }

    public LibraryCoverArt getLibraryCoverArt() {
        if (libraryCoverArt == null) {
            libraryCoverArt = new LibraryCoverArt();
        }
        return libraryCoverArt;
    }

    public JComponent getComponent() {
        if (MAIN_PANEL == null) {
            MAIN_PANEL = new JPanel(new BorderLayout());
            MAIN_PANEL.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeMediator.LIGHT_BORDER_COLOR));
        }
        return MAIN_PANEL;
    }

    public void showView(final String key) {
        GUIMediator.safeInvokeAndWait(new Runnable() {

            @Override
            public void run() {
                rememberScrollbarsOnMediators(key);
                _tablesViewLayout.show(_tablesPanel, key);
            }
        });

        if (key.equals(FILES_TABLE_KEY)) {
            currentMediator = LibraryFilesTableMediator.instance();
        } else if (key.equals(PLAYLISTS_TABLE_KEY)) {
            currentMediator = LibraryPlaylistsTableMediator.instance();
        } else if (key.equals(INTERNET_RADIO_TABLE_KEY)) {
            currentMediator = LibraryInternetRadioTableMediator.instance();
        } else if (key.equals(DEVICE_TABLE_KEY)) {
            currentMediator = LibraryDeviceTableMediator.instance();
        } else {
            currentMediator = null;
        }
    }

    private void rememberScrollbarsOnMediators(String key) {
        AbstractLibraryTableMediator<?, ?, ?> tableMediator = null;
        AbstractLibraryListPanel listPanel = null;

        if (key.equals(FILES_TABLE_KEY)) {
            tableMediator = LibraryFilesTableMediator.instance();
            listPanel = getLibraryExplorer();
        } else if (key.equals(PLAYLISTS_TABLE_KEY)) {
            tableMediator = LibraryPlaylistsTableMediator.instance();
            listPanel = getLibraryPlaylists();
        } else if (key.equals(DEVICE_TABLE_KEY)) {
            tableMediator = LibraryDeviceTableMediator.instance();
            listPanel = getLibraryExplorer();
        }

        if (tableMediator == null || listPanel == null) {
            //nice antipattern here.
            return;
        }

        if (lastSelectedMediator != null && lastSelectedKey != null) {
            scrollbarValues.put(lastSelectedKey, lastSelectedMediator.getScrollbarValue());
        }

        lastSelectedMediator = tableMediator;
        lastSelectedKey = getSelectedKey();

        if (listPanel.getPendingRunnables().size() == 0) {
            int lastScrollValue = scrollbarValues.containsKey(lastSelectedKey) ? scrollbarValues.get(lastSelectedKey) : 0;

            tableMediator.scrollTo(lastScrollValue);
        }
    }

    public void updateTableFiles(DirectoryHolder dirHolder) {
        clearLibraryTable();
        showView(FILES_TABLE_KEY);
        LibraryFilesTableMediator.instance().updateTableFiles(dirHolder);
    }

    public void clearDirectoryHolderCaches() {
        getLibraryExplorer().clearDirectoryHolderCaches();
    }

    public void updateTableFiles(Device device, byte fileType) {
        clearLibraryTable();
        showView(DEVICE_TABLE_KEY);
        LibraryDeviceTableMediator.instance().updateTableFiles(device, fileType);
    }

    public void updateTableItems(Playlist playlist) {
        clearLibraryTable();
        showView(PLAYLISTS_TABLE_KEY);
        LibraryPlaylistsTableMediator.instance().updateTableItems(playlist);
    }

    public void showInternetRadioStations(List<InternetRadioStation> internetRadioStations) {
        clearLibraryTable();
        showView(INTERNET_RADIO_TABLE_KEY);
        LibraryInternetRadioTableMediator.instance().updateTableItems(internetRadioStations);
    }

    public void clearLibraryTable() {
        LibraryFilesTableMediator.instance().clearTable();
        LibraryPlaylistsTableMediator.instance().clearTable();
        LibraryDeviceTableMediator.instance().clearTable();
        getLibrarySearch().clear();
    }

    public void addFilesToLibraryTable(List<File> files) {
        for (File file : files) {
            LibraryFilesTableMediator.instance().add(file);
        }
        getLibrarySearch().addResults(files.size());
    }

    public void addItemsToLibraryTable(List<PlaylistItem> items) {
        for (PlaylistItem item : items) {
            LibraryPlaylistsTableMediator.instance().add(item);
        }
        getLibrarySearch().addResults(items.size());
    }

    public void addInternetRadioStationsToLibraryTable(List<InternetRadioStation> items) {
        for (InternetRadioStation item : items) {
            LibraryInternetRadioTableMediator.instance().add(item);
        }
        getLibrarySearch().addResults(items.size());
    }

    private JComponent getLibraryLeftPanel() {
        if (libraryLeftPanel == null) {
            libraryLeftPanel = new LibraryLeftPanel(getLibraryExplorer(), getLibraryPlaylists(), getLibraryCoverArt());
        }
        return libraryLeftPanel;
    }

    private JComponent getLibraryRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        _tablesViewLayout = new CardLayout();
        _tablesPanel = new JPanel(_tablesViewLayout);

        _tablesPanel.add(LibraryFilesTableMediator.instance().getComponent(), FILES_TABLE_KEY);
        _tablesPanel.add(LibraryPlaylistsTableMediator.instance().getComponent(), PLAYLISTS_TABLE_KEY);
        _tablesPanel.add(LibraryInternetRadioTableMediator.instance().getComponent(), INTERNET_RADIO_TABLE_KEY);
        _tablesPanel.add(LibraryDeviceTableMediator.instance().getComponent(), DEVICE_TABLE_KEY);

        panel.add(getLibrarySearch(), BorderLayout.PAGE_START);
        panel.add(_tablesPanel, BorderLayout.CENTER);

        return panel;
    }

    public void setSelectedFile(File file) {
        getLibraryExplorer().selectFinishedDownloads();
        LibraryFilesTableMediator.instance().setFileSelected(file);
    }

    public void selectCurrentMedia() {
        //Select current playlist.
        Playlist currentPlaylist = MediaPlayer.instance().getCurrentPlaylist();
        final MediaSource currentMedia = MediaPlayer.instance().getCurrentMedia();

        //If the current song is being played from a playlist.
        if (currentPlaylist != null && currentMedia != null && currentMedia.getPlaylistItem() != null) {
            if (currentPlaylist.getId() != LibraryDatabase.STARRED_PLAYLIST_ID) {

                //select the song once it's available on the right hand side
                getLibraryPlaylists().enqueueRunnable(new Runnable() {
                    public void run() {
                        GUIMediator.safeInvokeLater(new Runnable() {
                            public void run() {
                                LibraryPlaylistsTableMediator.instance().setItemSelected(currentMedia.getPlaylistItem());
                            }
                        });
                    }
                });

                //select the playlist
                getLibraryPlaylists().selectPlaylist(currentPlaylist);
            } else {
                LibraryExplorer libraryFiles = getLibraryExplorer();

                //select the song once it's available on the right hand side
                libraryFiles.enqueueRunnable(new Runnable() {
                    public void run() {
                        GUIMediator.safeInvokeLater(new Runnable() {
                            public void run() {
                                LibraryPlaylistsTableMediator.instance().setItemSelected(currentMedia.getPlaylistItem());
                            }
                        });
                    }
                });

                libraryFiles.selectStarred();
            }

        } else if (currentMedia != null && currentMedia.getFile() != null) {
            //selects the audio node at the top
            LibraryExplorer libraryFiles = getLibraryExplorer();

            //select the song once it's available on the right hand side
            libraryFiles.enqueueRunnable(new Runnable() {
                public void run() {
                    GUIMediator.safeInvokeLater(new Runnable() {
                        public void run() {
                            LibraryFilesTableMediator.instance().setFileSelected(currentMedia.getFile());
                        }
                    });
                }
            });

            libraryFiles.selectAudio();
        } else if (currentMedia instanceof InternetRadioAudioSource) {
            //selects the audio node at the top
            LibraryExplorer libraryFiles = getLibraryExplorer();

            //select the song once it's available on the right hand side
            libraryFiles.enqueueRunnable(new Runnable() {
                public void run() {
                    GUIMediator.safeInvokeLater(new Runnable() {
                        public void run() {
                            LibraryInternetRadioTableMediator.instance().setItemSelected(((InternetRadioAudioSource) currentMedia).getInternetRadioStation());
                        }
                    });
                }
            });

            libraryFiles.selectRadio();
        } else if (currentMedia instanceof DeviceMediaSource) {
            //selects the audio node at the top
            LibraryExplorer libraryFiles = getLibraryExplorer();

            //select the song once it's available on the right hand side
            libraryFiles.enqueueRunnable(new Runnable() {
                public void run() {
                    GUIMediator.safeInvokeLater(new Runnable() {
                        public void run() {
                            LibraryDeviceTableMediator.instance().setItemSelected(((DeviceMediaSource) currentMedia).getFileDescriptor());
                        }
                    });
                }
            });

            libraryFiles.selectDeviceFileType(((DeviceMediaSource) currentMedia).getDevice(), ((DeviceMediaSource) currentMedia).getFileDescriptor().fileType);
        }

        //Scroll to current song.
    }

    public boolean isScanned(int id) {
        return idScanned.contains(id);
    }

    public void scan(int hashCode, File location) {
        idScanned.add(hashCode);

        if (location.isDirectory()) {
            for (File file : location.listFiles()) {
                scan(hashCode, file);
            }
        } else {
            List<MediaTypeSavedFilesDirectoryHolder> holders = getLibraryExplorer().getMediaTypeSavedFilesDirectoryHolders();
            for (MediaTypeSavedFilesDirectoryHolder holder : holders) {
                Set<File> cache = holder.getCache();
                if (holder.accept(location) && !cache.isEmpty() && !cache.contains(location)) {
                    cache.add(location);
                }
            }
        }
    }

    public long getTotalRadioStations() {
        return getLibrary().getTotalRadioStations();
    }

    public void restoreDefaultRadioStations() {
        getLibrary().restoreDefaultRadioStations();
    }

    /**
     * If a file has been selected on the right hand side, this method will select such file.
     * 
     * If there's a radio station, or if there's more than one file selected, or none, it will return null.
     * @return
     */
    public File getSelectedFile() {
        File toExplore = null;

        DirectoryHolder selectedDirectoryHolder = LibraryMediator.instance().getLibraryExplorer().getSelectedDirectoryHolder();
        boolean fileBasedDirectoryHolderSelected = selectedDirectoryHolder instanceof SavedFilesDirectoryHolder || selectedDirectoryHolder instanceof MediaTypeSavedFilesDirectoryHolder || selectedDirectoryHolder instanceof TorrentDirectoryHolder;

        if (fileBasedDirectoryHolderSelected && LibraryFilesTableMediator.instance().getSelectedLines().size() == 1) {
            toExplore = LibraryFilesTableMediator.instance().getSelectedLines().get(0).getFile();

        } else if (LibraryPlaylistsTableMediator.instance().getSelectedLines() != null && LibraryPlaylistsTableMediator.instance().getSelectedLines().size() == 1) {
            toExplore = LibraryPlaylistsTableMediator.instance().getSelectedLines().get(0).getFile();
        }
        return toExplore;
    }

    public void playCurrentSelection() {
        if (currentMediator != null) {
            currentMediator.playCurrentSelection();
        }
    }

    public void handleDeviceNew(Device device) {
        getLibraryExplorer().handleDeviceNew(device);
    }

    public void handleDeviceAlive(Device device) {
        getLibraryExplorer().handleDeviceAlive(device);
    }

    public void handleDeviceStale(Device device) {
        getLibraryExplorer().handleDeviceStale(device);
    }
}
