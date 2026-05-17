package communication;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import enums.NewsTopic;
import users.User;

public class News implements Serializable, Comparable<News> {
    private NewsTopic topic;
    private String title;
    private String content;
    private User author;
    private Date postDate;
    private List<Comment> comments;
    private boolean pinned;

    public News() {
        this.comments = new ArrayList<>();
    }

    public News(NewsTopic topic, String title, String content, User author) {
        this.topic = topic;
        this.title = title;
        this.content = content;
        this.author = author;
        this.postDate = new Date();
        this.comments = new ArrayList<>();
        this.pinned = (topic == NewsTopic.RESEARCH);
    }

    public void addComment(Comment c) {
        comments.add(c);
    }

    public void pin() { pinned = true; }
    public void unpin() { pinned = false; }

    @Override
    public int compareTo(News other) {
        if (this.pinned && !other.pinned) return -1;
        if (!this.pinned && other.pinned) return 1;
        return other.postDate.compareTo(this.postDate);
    }

    public NewsTopic getTopic() { return topic; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public User getAuthor() { return author; }
    public Date getPostDate() { return postDate; }
    public List<Comment> getComments() { return comments; }
    public boolean isPinned() { return pinned; }

    @Override
    public String toString() {
        return (pinned ? "[PINNED] " : "") + "[" + topic + "] " + title +
               " by " + (author != null ? author.getFullName() : "system");
    }
}
