package wm.com.danteater.Play;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 01-09-2014.
 */
public class TextLines {

    @SerializedName("LineType")
    public String LineType;

    @SerializedName("LineText")
    public String LineText;

    public TextLines(String lineType, String lineText) {
        LineType = lineType;
        LineText = lineText;
    }

    public String getLineType() {
        return LineType;
    }

    public String getLineText() {
        return LineText;
    }
}
