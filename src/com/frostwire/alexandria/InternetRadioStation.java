package com.frostwire.alexandria;

import com.frostwire.alexandria.db.InternetRadioStationDB;
import com.frostwire.alexandria.db.LibraryDatabase;
import com.frostwire.alexandria.db.LibraryDatabaseEntity;

public class InternetRadioStation extends LibraryDatabaseEntity {

    private int id;
    private String name;
    private String description;
    private String url;
    private String bitrate;
    private String type;
    private String website;
    private String genre;
    private String pls;
    private boolean bookmarked;

    private boolean deleted;

    public InternetRadioStation(LibraryDatabase db) {
        super(db);
        this.id = LibraryDatabase.OBJECT_INVALID_ID;
        this.deleted = false;
    }

    public InternetRadioStation(LibraryDatabase db, int id, String name, String description, String url, String bitrate, String type, String website, String genre, String pls, boolean bookmarked) {
        super(db);
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.bitrate = bitrate;
        this.type = type;
        this.website = website;
        this.genre = genre;
        this.pls = pls;
        this.bookmarked = bookmarked;
        this.deleted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getPls() {
        return pls;
    }

    public void setPls(String pls) {
        this.pls = pls;
    }
    
    public boolean isBookmarked() {
        return bookmarked;
    }
    
    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void save() {
        if (db != null) {
            InternetRadioStationDB.save(db, this);
        }
    }

    public void delete() {
        if (db != null) {
            InternetRadioStationDB.delete(db, this);
            deleted = true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof InternetRadioStation)) {
            return false;
        }

        InternetRadioStation other = (InternetRadioStation) obj;
        return other.getId() == getId();
    }
    
    @Override
    public int hashCode() {
    	return getId();
    }
}