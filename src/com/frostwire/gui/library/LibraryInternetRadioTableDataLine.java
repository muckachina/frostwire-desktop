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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.limewire.util.StringUtils;

import com.frostwire.alexandria.InternetRadioStation;
import com.frostwire.gui.player.MediaPlayer;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.tables.ActionIconAndNameHolder;
import com.limegroup.gnutella.gui.tables.LimeTableColumn;
import com.limegroup.gnutella.gui.tables.NameHolder;

/**
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class LibraryInternetRadioTableDataLine extends AbstractLibraryTableDataLine<InternetRadioStation> {
	
    static final int ACTIONS_IDX = 0;
    private static final LimeTableColumn ACTIONS_COLUMN = new LimeTableColumn(ACTIONS_IDX, "INTERNET_RADIO_TABLE_ACTIONS", I18n.tr(""), 20, true, false, false, LibraryActionsHolder.class);
    
    static final int BOOKMARKED_IDX = 1;
    private static final LimeTableColumn BOOKMARKED_COLUMN = new LimeTableColumn(BOOKMARKED_IDX, "INTERNET_RADIO_TABLE_BOOKMARKED", I18n.tr("Bookmarked"), 20, true, false, false, InternetRadioBookmark.class);

    static final int NAME_IDX = 2;
    private static final LimeTableColumn NAME_COLUMN = new LimeTableColumn(NAME_IDX, "INTERNET_RADIO_TABLE_NAME", I18n.tr("Name"), 484, true, NameHolder.class);
    
    static final int GENRE_IDX = 3;
    static final LimeTableColumn GENRE_COLUMN = new LimeTableColumn(GENRE_IDX, "INTERNET_RADIO_TABLE_GENRE", I18n.tr("Genre"), 268, true, PlayableCell.class);

    static final int BITRATE_IDX = 4;
    private static final LimeTableColumn BITRATE_COLUMN = new LimeTableColumn(BITRATE_IDX, "INTERNET_RADIO_TABLE_BITRATE", I18n.tr("Bitrate"), 80, true, PlayableCell.class);

    static final int WEBSITE_IDX = 5;
    private static final LimeTableColumn WEBSITE_COLUMN = new LimeTableColumn(WEBSITE_IDX, "INTERNET_RADIO_TABLE_WEBSITE", I18n.tr("Website"), 170, true, ActionIconAndNameHolder.class);

    static final int TYPE_IDX = 6;
    private static final LimeTableColumn TYPE_COLUMN = new LimeTableColumn(TYPE_IDX, "INTERNET_RADIO_TABLE_TYPE", I18n.tr("Type"), 40, true, PlayableCell.class);

    static final int URL_IDX = 7;
    private static final LimeTableColumn URL_COLUMN = new LimeTableColumn(URL_IDX, "INTERNET_RADIO_TABLE_URL", I18n.tr("Url"), 80, false, PlayableCell.class);

    static final int DESCRIPTION_IDX = 8;
    private static final LimeTableColumn DESCRIPTION_COLUMN = new LimeTableColumn(DESCRIPTION_IDX, "INTERNET_RADIO_TABLE_DESCRIPTION", I18n.tr("Description"), 80, false, PlayableCell.class);


    /**
     * Total number of columns
     */
    static final int NUMBER_OF_COLUMNS = 9;
    
    private ActionListener detailsAction;

    /**
     * Number of columns
     */
    public int getColumnCount() {
        return NUMBER_OF_COLUMNS;
    }

    /**
     * Sets up the dataline for use with the playlist.
     */
    public void initialize(final InternetRadioStation item) {
        super.initialize(item);
        
        detailsAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUIMediator.openURL("http://" + item.getWebsite().replace("http://", ""));
            }
        };
        
        this.nameCell = new NameHolder(initializer.getName());
        this.actionsHolder = new LibraryActionsHolder(this, false);
    }
    
    private NameHolder nameCell;
    private LibraryActionsHolder actionsHolder;

    /**
     * Returns the value for the specified index.
     */
    public Object getValueAt(int idx) {
        boolean playing = isPlaying();
        switch (idx) {
        case ACTIONS_IDX:
            actionsHolder.setPlaying(playing);
            return actionsHolder;
        case BOOKMARKED_IDX:
        	return new InternetRadioBookmark(this, playing);
        case NAME_IDX:
            return nameCell;
        case DESCRIPTION_IDX:
            return new PlayableCell(this, initializer.getDescription(), playing, idx);
        case URL_IDX:
            return new PlayableCell(this, initializer.getUrl(), playing, idx);
        case BITRATE_IDX:
            return new PlayableCell(this, initializer.getBitrate(), playing, idx);
        case TYPE_IDX:
            return new PlayableCell(this, initializer.getType(), playing, idx);
        case WEBSITE_IDX:
            return new ActionIconAndNameHolder(null, detailsAction, "<html><a href=\"#\">" + initializer.getWebsite().replace("http://", "") + "</a></html>");
        case GENRE_IDX:
            return new PlayableCell(this, initializer.getGenre(), playing, idx);
        }
        return null;
    }

    private boolean isPlaying() {
        if (initializer != null) {
            return MediaPlayer.instance().isThisBeingPlayed(initializer.getUrl());
        }

        return false;
    }

    /**
     * Return the table column for this index.
     */
    public LimeTableColumn getColumn(int idx) {
        switch (idx) {
        case ACTIONS_IDX:
            return ACTIONS_COLUMN;
        case BOOKMARKED_IDX:
        	return BOOKMARKED_COLUMN;
        case NAME_IDX:
            return NAME_COLUMN;
        case DESCRIPTION_IDX:
            return DESCRIPTION_COLUMN;
        case URL_IDX:
            return URL_COLUMN;
        case BITRATE_IDX:
            return BITRATE_COLUMN;
        case TYPE_IDX:
            return TYPE_COLUMN;
        case WEBSITE_IDX:
            return WEBSITE_COLUMN;
        case GENRE_IDX:
            return GENRE_COLUMN;
        }
        return null;
    }

    public boolean isClippable(int idx) {
        return false;
    }

    public boolean isDynamic(int idx) {
        return false;
    }

    /**
     * Creates a tool tip for each row of the playlist. Tries to grab any information
     * that was extracted from the Meta-Tag or passed in to the PlaylistItem as 
     * a property map
     */
    public String[] getToolTipArray(int col) {
        List<String> list = new ArrayList<String>();
        if (!StringUtils.isNullOrEmpty(initializer.getName(), true)) {
            list.add(I18n.tr("Name") + ": " + initializer.getName());
        }
        if (!StringUtils.isNullOrEmpty(initializer.getDescription(), true)) {
            list.add(I18n.tr("Description") + ": " + initializer.getDescription());
        }
        if (!StringUtils.isNullOrEmpty(initializer.getUrl(), true)) {
            list.add(I18n.tr("Url") + ": " + initializer.getUrl());
        }
        if (!StringUtils.isNullOrEmpty(initializer.getBitrate(), true)) {
            list.add(I18n.tr("Bitrate") + ": " + initializer.getBitrate());
        }
        if (!StringUtils.isNullOrEmpty(initializer.getType(), true)) {
            list.add(I18n.tr("Type") + ": " + initializer.getType());
        }
        if (!StringUtils.isNullOrEmpty(initializer.getWebsite(), true)) {
            list.add(I18n.tr("Website") + ": " + initializer.getWebsite());
        }
        if (!StringUtils.isNullOrEmpty(initializer.getGenre(), true)) {
            list.add(I18n.tr("Genre") + ": " + initializer.getGenre());
        }

        return list.toArray(new String[0]);
    }

    public File getFile() {
        return null;//new File(initializer.getFilePath());
    }

    @Override
    public int getTypeAheadColumn() {
        return 0;
    }
}
