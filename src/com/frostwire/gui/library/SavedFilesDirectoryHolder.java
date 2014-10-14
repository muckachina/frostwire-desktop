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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import org.limewire.setting.FileSetting;
import org.limewire.util.FileUtils;

import com.frostwire.gui.bittorrent.TorrentUtil;
import com.limegroup.gnutella.MediaType;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.settings.LibrarySettings;
import com.limegroup.gnutella.settings.SharingSettings;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public class SavedFilesDirectoryHolder extends FileSettingDirectoryHolder {

    private final MediaType type;

    private Set<File> cache;

    public SavedFilesDirectoryHolder(FileSetting saveDir, String name) {
        super(saveDir, name);
        type = MediaType.getAnyTypeMediaType();
        cache = new HashSet<File>();
    }

    public Icon getIcon() {
        return GUIMediator.getThemeImage("save");
    }

    public boolean accept(File file) {
        return super.accept(file) && type.matches(file.getName()) && !file.isDirectory();
    }

    private Set<File> getFilesRecursively(File folder, Set<File> excludeFolders) {

        if (folder.isDirectory() && excludeFolders.contains(folder)) {
            return Collections.emptySet();
        }

        File[] listFiles = FileUtils.listFiles(folder);//folder.listFiles();

        if (listFiles == null || listFiles.length == 0) {
            return Collections.emptySet();
        }

        Set<File> results = new HashSet<File>();

        for (File f : listFiles) {
            if (f.exists()) {
                if (!f.isDirectory() && !_hideFiles.contains(f) && !f.getName().toLowerCase().contains(".ds_store")) {
                    results.add(f);
                } else if (f.isDirectory() && !excludeFolders.contains(f)) {
                    results.addAll(getFilesRecursively(f, excludeFolders));
                }
            }
        }

        return results;
    }

    public void clearCache() {
        if (cache != null) {
            cache.clear();
        }
    }

    @Override
    public File[] getFiles() {

        if (cache != null && cache.size() > 0) {
            return cache.toArray(new File[0]);
        }

        _hideFiles = TorrentUtil.getIgnorableFiles();

        Set<File> directoriesToNotInclude = LibrarySettings.DIRECTORIES_NOT_TO_INCLUDE.getValue();
        Set<File> directoriesToInclude = new HashSet<File>(Arrays.asList(SharingSettings.TORRENT_DATA_DIR_SETTING.getValue()));//LibrarySettings.DIRECTORIES_TO_INCLUDE.getValue();

        Set<File> files = new HashSet<File>();

        for (File directory : directoriesToInclude) {
            files.addAll(getFilesRecursively(directory, directoriesToNotInclude));
        }

        cache = new HashSet<File>(files);

        return cache.toArray(new File[0]);
    }

    public Collection<File> getCache() {
        return cache;
    }
}