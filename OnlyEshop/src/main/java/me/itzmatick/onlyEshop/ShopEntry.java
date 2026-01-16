package me.itzmatick.onlyEshop;

public class ShopEntry {
    private final String uuid;
    private final int priority;
    private final String title;
    private final String description;
    private final String material;
    private final String domain;

    public ShopEntry(String uuid, int priority, String title, String description, String material, String domain) {
        this.uuid = uuid;
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.material = material;
        this.domain = domain;
    }

    public int getPriority() { return priority; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUuid() { return uuid; }
    public String getMaterial() { return material; }
    public String getDomain() { return domain; }
}