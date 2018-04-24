package com.app.instashare.ui.post.model;

/**
 * Created by Pitisflow on 24/4/18.
 */

public class Tag {

    private String tagName;
    private boolean deletable;

    public Tag() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
}
