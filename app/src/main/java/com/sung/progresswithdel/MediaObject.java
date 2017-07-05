package com.sung.progresswithdel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sung on 2017/7/4.
 */

public class MediaObject implements Serializable {
    public String outputPath;
    public String video_url;
    public long duration;
    public List<MediaPart> partList = new ArrayList();

    public MediaObject() {
    }

    public static class MediaPart{
        public int position;
        public String video_url;
        public long duration;
        public boolean remove = false;

        public MediaPart() {
        }

        public long getDuration() {
            return duration;
        }
    }

    public long getDuration() {
        duration = 0;
        for (int i = 0; i < partList.size(); i++) {
            duration = partList.get(i).duration + duration;
        }

        return duration;
    }

    public List<MediaPart> getMediaParts() {
        return partList;
    }
}