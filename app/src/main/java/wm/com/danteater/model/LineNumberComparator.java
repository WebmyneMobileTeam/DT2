package wm.com.danteater.model;

import java.util.Comparator;

import wm.com.danteater.Play.PlayLines;

/**
 * Created by Android on 10-12-2014.
 */
public class LineNumberComparator implements Comparator {
    @Override
    public int compare(Object o1,Object o2){
        PlayLines s1=(PlayLines)o1;
        PlayLines s2=(PlayLines)o2;

        if(s1.LineCount==s2.LineCount)
            return 0;
        else if(s1.LineCount>s2.LineCount)
            return 1;
        else
            return -1;
    }

}
