package com.pillchill.migration.network.communication;

import com.pillchill.migration.dto.PhieuNhapImportItem;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapImportPayload implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<PhieuNhapImportItem> items = new ArrayList<>();

    public PhieuNhapImportPayload() {
    }

    public PhieuNhapImportPayload(List<PhieuNhapImportItem> items) {
        this.items = items;
    }

    public List<PhieuNhapImportItem> getItems() {
        return items;
    }

    public void setItems(List<PhieuNhapImportItem> items) {
        this.items = items;
    }
}
