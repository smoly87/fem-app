package com.smoly87.fem.core.blockmatrix;

import java.util.ArrayList;
import java.util.List;

public class SystemBlockItems {
    private List<SystemBlockItem> itemsList;

    public List<SystemBlockItem> getItems() {
        return itemsList;
    }

    public void addItem(SystemBlockItem blockItem) {
        itemsList.add(blockItem);
    }

    public SystemBlockItem getItem(int i) {
        return itemsList.get(i);
    }

    public int size() {
        return itemsList.size();
    }

    public SystemBlockItems() {
        itemsList = new ArrayList<>();
    }
}
