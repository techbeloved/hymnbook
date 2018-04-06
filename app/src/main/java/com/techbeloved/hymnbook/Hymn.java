package com.techbeloved.hymnbook;

import java.util.ArrayList;

/**
 * Created by kennedy on 4/6/18.
 */

public class Hymn {
    private String mTitle;
    private int mNumber;

    public Hymn(int number, String title){
        mNumber = number;
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getNumber() {
        return mNumber;
    }

    public static ArrayList<Hymn> createHymnList(){
        ArrayList<Hymn> hymns = new ArrayList<>();
        for (int i = 1; i <= songList.length; i++) {
            hymns.add(new Hymn(i, songList[i-1]));
        }
        return hymns;
    }

    private static String[] songList = {
            "Oh for a thousand tongues to sing",
            "Praise to the Lord, the Almighty",
            "To God be the Glory",
            "Great is our God",
            "We praise thee oh God",
            "Praise Him, Praise Him",
            "Immortal, Invisible, God only wise",
            "Thou our father",
            "Holy, Holy, Holy",
            "O worship the King",
            "How great is your name",
            "How great thou art",
            "Great is thy faithfulness",
            "Blessed be the name",
            "All people that on earth do dwell",
            "All the earth",
            "All hail the power of Jesus name",
            "Holy God we praise your name",
            "O come thou wisdom whose decree",
            "Praise be to God",
            "A mighty fortress"
    };
}
