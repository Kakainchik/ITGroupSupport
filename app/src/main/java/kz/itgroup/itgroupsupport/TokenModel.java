package kz.itgroup.itgroupsupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

public class TokenModel implements Serializable {

    //Properties
    private String title;
    private String description;
    private Calendar createDate;
    private boolean isValid;

    //Constructor
    public TokenModel(String title, String description) {
        this.title = title;
        this.description = description;
        this.createDate = Calendar.getInstance();
        this.createDate.setTimeZone(TimeZone.getDefault());
        this.createDate.set(Calendar.HOUR_OF_DAY, 0);
        this.isValid = true;
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

    public Calendar getCreateDate() {
        return createDate;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}