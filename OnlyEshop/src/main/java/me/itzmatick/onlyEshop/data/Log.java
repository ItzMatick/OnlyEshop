package me.itzmatick.onlyEshop.data;

public class Log {
    private final long timestamp;
    private final String player;
    private final String action; // "BUY" nebo "SELL"
    private final String item;
    private final int amount;
    private final double price;

    public Log (String player, String action, String item, int amount, double price) {
        this.timestamp = System.currentTimeMillis();
        this.player = player;
        this.action = action;
        this.item = item;
        this.amount = amount;
        this.price = price;
    }
    public Log (long timestamp, String player, String action, String item, int amount, double price) {
        this.timestamp = timestamp;
        this.player = player;
        this.action = action;
        this.item = item;
        this.amount = amount;
        this.price = price;
    }

    public long getTimestamp() { return timestamp; }
    public String getPlayer() { return player; }
    public String getAction() { return action; }
    public String getItem() { return item; }
    public int getAmount() { return amount; }
    public double getPrice() { return price; }
}
