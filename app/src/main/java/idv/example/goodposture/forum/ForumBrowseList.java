package idv.example.goodposture.forum;

public class ForumBrowseList {
    private String likes;
    private String messages;
    private Integer ivLike;
    private Integer ivMessage;
    private String title;
    private String author;
    private String time;


    public ForumBrowseList() {
    }

    public ForumBrowseList(String title, String author, String time, Integer ivLike,String likes, Integer ivMessage, String messages) {
        this.likes = likes;
        this.messages = messages;
        this.ivLike = ivLike;
        this.ivMessage = ivMessage;
        this.title = title;
        this.author = author;
        this.time = time;
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
}
