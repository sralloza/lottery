package es.sralloza.lottery.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Lottery {
    private Integer id;
    private Date date;

    public Lottery(Integer id, Date date) {
        this.id = id;
        this.date = date;
    }

    // TODO: return date instead of datetime in API JSON response
    public Date getDate() {
        return date;
    }

    @JsonIgnore
    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public Integer getId() {
        return id;
    }

}
