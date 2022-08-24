package edu.neu.madcourse.testmusic;

import java.util.Comparator;

public class UsersStepCompare implements Comparator<User> {
    public int compare(User s1, User s2)
    {
        if (s1.step == s2.step)
            return 0;
        else if (s1.step < s2.step)
            return 1;
        else
            return -1;
    }
}
