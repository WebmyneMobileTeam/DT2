package wm.com.danteater.Play;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 01-09-2014.
 */
public class TextLines {

    public enum TextLineState{

        TextLineStateOpen,
        TextLineStateClosed
    }

    public enum TextLineType{

        TextLineTypeUnknown,
        TextLineTypeNote,
        TextLineTypeInfo,
        TextLineNormalLine
    }

    @SerializedName("LineType")
    public String LineType;

    @SerializedName("LineText")
    public String LineText;

    @SerializedName("AlteredLineText")
    public String alteredLineText;

    public int textLineId;

    public TextLines(){

    }

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

    public TextLineType textLineType(){

        if(this.LineType == null ||this.LineType.equalsIgnoreCase("")){
            return TextLineType.TextLineTypeUnknown;
        }else if(this.LineType.equalsIgnoreCase("Note")){
            return TextLineType.TextLineTypeNote;
        }else if(this.LineType.equalsIgnoreCase("Info")){
            return TextLineType.TextLineTypeInfo;
        }else if(this.LineType.equalsIgnoreCase("Replik")){
            return TextLineType.TextLineNormalLine;
        }

        return TextLineType.TextLineTypeUnknown;
    }

    public String currentText(){

        if(this.alteredLineText == null || this.alteredLineText.equalsIgnoreCase("")){
            return this.LineText;
        }else{
            return this.alteredLineText;
        }


    }




}
