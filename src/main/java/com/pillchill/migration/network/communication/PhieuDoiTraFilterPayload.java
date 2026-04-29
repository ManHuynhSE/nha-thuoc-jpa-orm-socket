package com.pillchill.migration.network.communication;

import java.io.Serializable;

public class PhieuDoiTraFilterPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer month;
    private Integer year;

    public PhieuDoiTraFilterPayload() {
    }

    public PhieuDoiTraFilterPayload(Integer month, Integer year) {
        this.month = month;
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
