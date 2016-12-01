package pojo;

import java.util.List;

/**
 * Created by ersinyildiz on 16/11/16.
 */

public class DailyMenu {

    private String Soup;
    private String MainCourse;
    private String AlternativeMainCourse;
    private String WarmStarter;
    private String Desert;
    private String AlternativeDesert;
    private int Like;
    private int Dislike;
    private long DateInfo;
    private List<Comment> Comments;

    public DailyMenu() {
    }

    public DailyMenu(String alternativeDesert, String alternativeMainCourse, List<Comment> comments,
                     long dateInfo, String desert, int dislike, int like, String mainCourse,
                     String soup, String warmStarter) {
        AlternativeDesert = alternativeDesert;
        AlternativeMainCourse = alternativeMainCourse;
        Comments = comments;
        DateInfo = dateInfo;
        Desert = desert;
        Dislike = dislike;
        Like = like;
        MainCourse = mainCourse;
        Soup = soup;
        WarmStarter = warmStarter;
    }

    public String getWarmStarter() {
        return WarmStarter;
    }

    public void setWarmStarter(String warmStarter) {
        WarmStarter = warmStarter;
    }

    public String getAlternativeDesert() {
        return AlternativeDesert;
    }

    public void setAlternativeDesert(String alternativeDesert) {
        AlternativeDesert = alternativeDesert;
    }

    public String getAlternativeMainCourse() {
        return AlternativeMainCourse;
    }

    public void setAlternativeMainCourse(String alternativeMainCourse) {
        AlternativeMainCourse = alternativeMainCourse;
    }

    public List<Comment> getComments() {
        return Comments;
    }

    public void setComments(List<Comment> comments) {
        Comments = comments;
    }

    public long getDateInfo() {
        return DateInfo;
    }

    public void setDateInfo(long dateInfo) {
        DateInfo = dateInfo;
    }

    public String getDesert() {
        return Desert;
    }

    public void setDesert(String desert) {
        Desert = desert;
    }

    public int getDislike() {
        return Dislike;
    }

    public void setDislike(int dislike) {
        Dislike = dislike;
    }

    public int getLike() {
        return Like;
    }

    public void setLike(int like) {
        Like = like;
    }

    public String getMainCourse() {
        return MainCourse;
    }

    public void setMainCourse(String mainCourse) {
        MainCourse = mainCourse;
    }

    public String getSoup() {
        return Soup;
    }

    public void setSoup(String soup) {
        Soup = soup;
    }
}
