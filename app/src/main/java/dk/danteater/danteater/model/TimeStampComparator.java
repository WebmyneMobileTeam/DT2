package dk.danteater.danteater.model;

import java.util.Comparator;

import dk.danteater.danteater.Messages.MessagesForConversation;

/**
 * Created by Android on 19-12-2014.
 */
public class TimeStampComparator  implements Comparator {

    @Override
    public int compare(Object o1,Object o2){
        MessagesForConversation s1=(MessagesForConversation)o1;
        MessagesForConversation s2=(MessagesForConversation)o2;

        if(s1.postedTimeStamp==s2.postedTimeStamp)
            return 0;
        else if(s1.postedTimeStamp>s2.postedTimeStamp)
            return 1;
        else
            return -1;
    }
}
