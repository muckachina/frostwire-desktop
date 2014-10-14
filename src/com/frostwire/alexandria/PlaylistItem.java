package com.frostwire.alexandria;

import com.frostwire.alexandria.db.LibraryDatabase;
import com.frostwire.alexandria.db.LibraryDatabaseEntity;
import com.frostwire.alexandria.db.PlaylistItemDB;

public class PlaylistItem extends LibraryDatabaseEntity {

    private Playlist playlist;
    private int id;
    private String filePath;
    private String fileName;
    private long fileSize;
    private String fileExtension;
    private String trackTitle;
    private float trackDurationInSecs;
    private String trackArtist;
    private String trackAlbum;
    private String coverArtPath;
    private String trackBitrate;
    private String trackComment;
    private String trackGenre;
    private String trackNumber;
    private String trackYear;
    private boolean starred;
    private int sortIndex;

    public PlaylistItem(Playlist playlist) {
        super(playlist != null ? playlist.getLibraryDatabase() : null);
        this.playlist = playlist;
        this.id = LibraryDatabase.OBJECT_INVALID_ID;
    }

    public PlaylistItem(Playlist playlist, int id, String filePath, String fileName, long fileSize, String fileExtension, String trackTitle, float trackDurationInSecs, String trackArtist, String trackAlbum, String coverArtPath, String trackBitrate, String trackComment, String trackGenre,
            String trackNumber, String trackYear, boolean starred) {
        super(playlist != null ? playlist.getLibraryDatabase() : null);
        this.playlist = playlist;
        this.id = id;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
        this.trackTitle = trackTitle;
        this.trackDurationInSecs = trackDurationInSecs;
        this.trackArtist = trackArtist;
        this.trackAlbum = trackAlbum;
        this.coverArtPath = coverArtPath;
        this.trackBitrate = trackBitrate;
        this.trackComment = trackComment;
        this.trackGenre = trackGenre;
        this.trackNumber = trackNumber;
        this.trackYear = trackYear;
        this.starred = starred;
        this.sortIndex = playlist != null ? (playlist.getItems().size() + 1) : 0; // set sortIndex to the last position (1-based) by default
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        setLibraryDatabase(playlist.getLibraryDatabase());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public float getTrackDurationInSecs() {
        return trackDurationInSecs;
    }

    public void setTrackDurationInSecs(float trackDurationInSecs) {
        this.trackDurationInSecs = trackDurationInSecs;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String artistName) {
        this.trackArtist = artistName;
    }

    public String getTrackAlbum() {
        return trackAlbum;
    }

    public void setTrackAlbum(String albumName) {
        this.trackAlbum = albumName;
    }

    public String getCoverArtPath() {
        return coverArtPath;
    }

    public void setCoverArtPath(String coverArtPath) {
        this.coverArtPath = coverArtPath;
    }

    public String getTrackBitrate() {
        return trackBitrate;
    }

    public void setTrackBitrate(String bitrate) {
        this.trackBitrate = bitrate;
    }

    public String getTrackComment() {
        return trackComment;
    }

    public void setTrackComment(String comment) {
        this.trackComment = comment;
    }

    public String getTrackGenre() {
        return trackGenre;
    }

    public void setTrackGenre(String genre) {
        this.trackGenre = genre;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String track) {
        this.trackNumber = track;
    }

    public String getTrackYear() {
        return trackYear;
    }

    public void setTrackYear(String year) {
        this.trackYear = year;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public void save() {
        if (db != null) {
            PlaylistItemDB.save(db, this);
        }
    }

    public void delete() {
        if (db != null) {
            PlaylistItemDB.delete(db, this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((PlaylistItem) obj).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "(" + id + ", title:" + trackTitle + ", number:" + trackNumber + ")";
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }
}