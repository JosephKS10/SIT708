package com.josephb.lostandfoundapp;

public class Item {

    public static final String TYPE_LOST = "Lost";
    public static final String TYPE_FOUND = "Found";

    public static final String[] CATEGORIES = {
            "Electronics", "Pets", "Wallets", "Documents", "Clothing", "Other"
    };

    private long id;
    private String postType;       // "Lost" or "Found"
    private String name;
    private String phone;
    private String description;
    private String incidentDate;   // user-entered, e.g. "2026-05-25"
    private String location;
    private String category;       // one of CATEGORIES
    private String imagePath;      // absolute path in app internal storage
    private long createdAt;        // System.currentTimeMillis() at insert

    public Item() {
    }

    public Item(long id, String postType, String name, String phone, String description,
                String incidentDate, String location, String category, String imagePath,
                long createdAt) {
        this.id = id;
        this.postType = postType;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.incidentDate = incidentDate;
        this.location = location;
        this.category = category;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIncidentDate() { return incidentDate; }
    public void setIncidentDate(String incidentDate) { this.incidentDate = incidentDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}