package lk.calm.pasbaratheater01.entity;

public class MovieFeedback {
    private String textComment;
    private String userId;
    private String showTimeId;
    private String timeStamp;

    public MovieFeedback(String textComment, String userId, String showTimeId, String timeStamp) {
        this.textComment = textComment;
        this.userId = userId;
        this.showTimeId = showTimeId;
        this.timeStamp = timeStamp;
    }

    public MovieFeedback() {
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShowTimeId() {
        return showTimeId;
    }

    public void setShowTimeId(String showTimeId) {
        this.showTimeId = showTimeId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
