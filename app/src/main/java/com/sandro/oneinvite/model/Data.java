package com.sandro.oneinvite.model;

public class Data {

    private final String credits;
    private final String kid;
    private final String parent;
    private final String rank;
    private final String ref_count;
    private final String total;

    public Data(String credits, String kid, String parent, String rank, String ref_count, String total) {
        this.credits = credits;
        this.kid = kid;
        this.parent = parent;
        this.rank = rank;
        this.ref_count = ref_count;
        this.total = total;
    }

    public String getCredits() {
        return credits;
    }

    public String getKid() {
        return kid;
    }

    public String getParent() {
        return parent;
    }

    public String getRank() {
        return rank;
    }

    public String getRef_count() {
        return ref_count;
    }

    public String getTotal() {
        return total;
    }

}
