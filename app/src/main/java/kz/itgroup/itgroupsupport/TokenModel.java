package kz.itgroup.itgroupsupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

public class TokenModel implements Serializable {

    //Properties
    private String title;
    private String description;
    private TokenState state;
    private long createdAt;

    //Constructor
    public TokenModel(String title, String description) {
        this.title = title;
        this.description = description;
        Calendar createDate = Calendar.getInstance();
        createDate.setTimeZone(TimeZone.getDefault());
        this.createdAt = createDate.getTimeInMillis();
        this.state = TokenState.IN_NOTEBOOK;
    }

    //Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateDate() {
        return createdAt;
    }

    public TokenState getState() {
        return state;
    }

    public void setState(TokenState state) {
        this.state = state;
    }
}