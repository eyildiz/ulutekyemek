package pojo;

/**
 * Created by ersinyildiz on 16/11/16.
 */

public class Comment {
    private String Nickname;
    private String CommentText;

    public Comment(String commentText, String nickname) {
        CommentText = commentText;
        Nickname = nickname;
    }

    public Comment() {
    }

    public String getCommentText() {
        return CommentText;
    }

    public void setCommentText(String commentText) {
        CommentText = commentText;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }
}
