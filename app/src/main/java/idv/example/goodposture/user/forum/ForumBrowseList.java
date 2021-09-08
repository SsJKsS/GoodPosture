package idv.example.goodposture.user.forum;

import java.io.Serializable;
import java.util.Date;

public class ForumBrowseList implements Serializable {
    private String likes;
    private String messages;
    private Integer ivLike;
    private Integer ivMessage;
    private String title;
    private String author;
    private String time;
    private String id;
    private String context;


    public ForumBrowseList() {
    }

    public ForumBrowseList(String title, String author, String time, Integer ivLike,String likes, Integer ivMessage, String messages,String id,String context) {
        this.likes = likes;
        this.messages = messages;
        this.ivLike = ivLike;
        this.ivMessage = ivMessage;
        this.title = title;
        this.author = author;
        this.time = time;
        this.id = id;
        this.context = context;

    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getIvLike() {
        return ivLike;
    }

    public void setIvLike(Integer ivLike) {
        this.ivLike = ivLike;
    }

    public Integer getIvMessage() {
        return ivMessage;
    }

    public void setIvMessage(Integer ivMessage) {
        this.ivMessage = ivMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
