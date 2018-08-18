package kaiqi.cn.httpx.resp;

import java.util.ArrayList;
import java.util.List;

import resp.Data;

/**
 */
public class HomeArticle implements Data {
    public String apkLink;
    public String author;
    public String chapterId;
    public String chapterName;
    public boolean collect;
    public int courseId;
    public String desc;
    public String envelopePic;
    public boolean fresh;


    public int id;
    public String link;
    public String niceDate;
    public String origin;
    public String projectLink;
    public String publishTime;
    public String superChapterId;
    public String superChapterName;
    public String title;
    public int type;
    public int userId;
    public int visible;
    public int zan;

    public List<Tag> getTags() {
        return tags == null ? new ArrayList<Tag>() : tags;
    }

    public List<Tag> tags;

    @Override
    public String toString() {
        return "HomeArticle{" +
                "apkLink='" + apkLink + '\'' +
                ", author='" + author + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", collect=" + collect +
                ", courseId=" + courseId +
                ", desc='" + desc + '\'' +
                ", envelopePic='" + envelopePic + '\'' +
                ", fresh=" + fresh +
                ", id=" + id +
                ", link='" + link + '\'' +
                ", niceDate='" + niceDate + '\'' +
                ", origin='" + origin + '\'' +
                ", projectLink='" + projectLink + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", superChapterId='" + superChapterId + '\'' +
                ", superChapterName='" + superChapterName + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", userId=" + userId +
                ", visible=" + visible +
                ", zan=" + zan +
                ", tags=" + tags +
                "} " + super.toString();
    }
}
