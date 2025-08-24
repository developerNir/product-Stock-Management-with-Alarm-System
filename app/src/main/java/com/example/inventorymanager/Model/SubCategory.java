package com.example.inventorymanager.Model;

public class SubCategory {
    private int id;
    private String name;
    private int mainCatId;

    public SubCategory(int id, String name, int mainCatId) {
        this.id = id;
        this.name = name;
        this.mainCatId = mainCatId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMainCatId() { return mainCatId; }
    public void setMainCatId(int mainCatId) { this.mainCatId = mainCatId; }
}