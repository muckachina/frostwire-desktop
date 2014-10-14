package com.frostwire.alexandria.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.frostwire.alexandria.Playlist;
import com.frostwire.alexandria.PlaylistItem;

public class PlaylistDB {

    private PlaylistDB() { } // don't allow explicit constructions

    public static void fill(LibraryDatabase db, Playlist obj) {
        List<List<Object>> result = db.query("SELECT playlistId, name, description FROM Playlists WHERE playlistId = ?", obj.getId());
        if (result.size() > 0) {
            List<Object> row = result.get(0);
            fill(row, obj);
        }
    }

    public static void fill(List<Object> row, Playlist p) {
        int id = (Integer) row.get(0);
        String name = (String) row.get(1);
        String description = (String) row.get(2);

        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.refresh();
    }

    public static void save(LibraryDatabase db, Playlist obj) {
        if (obj.getId() == LibraryDatabase.OBJECT_INVALID_ID) {
            return;
        }

        if (obj.getId() == LibraryDatabase.OBJECT_NOT_SAVED_ID) {
            int id = db.insert("INSERT INTO Playlists (name, description) VALUES (LEFT(?, 500), LEFT(?, 10000))", obj.getName(), obj.getDescription());
            obj.setId(id);
        } else {
            db.update("DELETE FROM PlaylistItems WHERE playlistId = ?", obj.getId());
            Object[] statementObjects = createPlaylistUpdateStatement(obj);
            db.update((String) statementObjects[0], (Object[]) statementObjects[1]);
        }
        
        List<PlaylistItem> items = new ArrayList<PlaylistItem>(obj.getItems());

        for (PlaylistItem item : items) {
            item.setId(LibraryDatabase.OBJECT_NOT_SAVED_ID);
            item.save();
        }
    }

    public static void delete(LibraryDatabase db, Playlist obj) {
        db.update("DELETE FROM PlaylistItems WHERE playlistId = ?", obj.getId());
        db.update("DELETE FROM Playlists WHERE playlistId = ?", obj.getId());
    }

    public static List<Playlist> getPlaylists(LibraryDatabase db) {
        List<List<Object>> result = db.query("SELECT playlistId, name, description FROM Playlists");

        List<Playlist> playlists = new ArrayList<Playlist>(result.size());

        for (List<Object> row : result) {
            Playlist playlist = new Playlist(db);
            PlaylistDB.fill(row, playlist);
            playlists.add(playlist);
        }

        return playlists;
    }

    public static Playlist getPlaylist(LibraryDatabase db, String name) {
        List<List<Object>> result = db.query("SELECT playlistId, name, description FROM Playlists WHERE name = ?", name);
        Playlist playlist = null;
        if (result.size() > 0) {
            List<Object> row = result.get(0);
            playlist = new Playlist(db);
            PlaylistDB.fill(row, playlist);
        }
        return playlist;
    }
    
    public static Playlist getStarredPlaylist(LibraryDatabase db) {
        String query = "SELECT playlistItemId, filePath, fileName, fileSize, fileExtension, trackTitle, trackDurationInSecs, trackArtist, trackAlbum, coverArtPath, trackBitrate, trackComment, trackGenre, trackNumber, trackYear, starred " + "FROM PlaylistItems WHERE starred = ?";

        List<List<Object>> result = db.query(query, true);

        Playlist playlist = new Playlist(db, LibraryDatabase.STARRED_PLAYLIST_ID, "starred", "starred");

        List<PlaylistItem> items = new ArrayList<PlaylistItem>(result.size());
        Set<String> paths = new HashSet<String>();

        for (List<Object> row : result) {
            PlaylistItem item = new PlaylistItem(playlist);
            PlaylistItemDB.fill(row, item);
            if (!paths.contains(item.getFilePath())) {
                items.add(item);
                paths.add(item.getFilePath());
            }
        }

        playlist.getItems().addAll(items);

        return playlist;
    }

    public static void updatePlaylistItemProperties(LibraryDatabase db, String filePath, String title, String artist, String album, String comment, String genre, String track, String year) {
        Object[] sqlAndValues = createPlaylistItemPropertiesUpdate(filePath, title, artist, album, comment, genre, track, year);
        db.update((String) sqlAndValues[0], (Object[]) sqlAndValues[1]);
    }

    private static Object[] createPlaylistItemPropertiesUpdate(String filePath, String title, String artist, String album, String comment, String genre, String track, String year) {
        String sql = "UPDATE PlaylistItems SET trackTitle = LEFT(?, 500), trackArtist = LEFT(?, 500), trackAlbum = LEFT(?, 500), trackComment = LEFT(?, 500), trackGenre = LEFT(?, 20), trackNumber = LEFT(?, 6), trackYear = LEFT(?, 6) WHERE filePath = LEFT(?, 10000)";

        Object[] values = new Object[] { title, artist, album, comment, genre, track, year, filePath };

        return new Object[] { sql, values };
    }
    
    private static Object[] createPlaylistUpdateStatement(Playlist obj) {
        String sql = "UPDATE Playlists SET name = LEFT(?, 500), description = LEFT(?, 10000) WHERE playlistId = ?";
        Object[] values = new Object[] { obj.getName(), obj.getDescription(), obj.getId() };
        return new Object[] { sql, values };
    }

}
