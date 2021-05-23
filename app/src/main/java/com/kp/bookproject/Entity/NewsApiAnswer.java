package com.kp.bookproject.Entity;

import java.util.List;

public class NewsApiAnswer {



    private String status;

    private List<News> sources = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<News> getSources() {
        return sources;
    }

    public void setSources(List<News> sources) {
        this.sources = sources;
    }

}
