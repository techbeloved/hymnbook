package com.techbeloved.hymnbook.hymnlisting;

import com.techbeloved.hymnbook.data.model.Hymn;

import java.util.List;

public interface HymnsState {

    class Content implements HymnsState {
        List<Hymn> content;
        public Content(List<Hymn> loadedHymns) {
            super();
            content = loadedHymns;
        }
    }
}
