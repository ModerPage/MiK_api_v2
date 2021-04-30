package me.modernpage.response;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class MemberPage<T> extends PageImpl<T> {
    private Boolean joined;

    public MemberPage(List<T> content, Pageable pageable, long total, Boolean joined) {
        super(content, pageable, total);
        this.joined = joined;
    }

    public MemberPage(List<T> content, Boolean joined) {
        super(content);
        this.joined = joined;
    }

    public Boolean getJoined() {
        return joined;
    }

    public void setJoined(Boolean joined) {
        this.joined = joined;
    }
}
