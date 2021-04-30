package me.modernpage.response;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FollowPage<T> extends PageImpl<T> {
    private Boolean followed;
    private Boolean requested;

    public FollowPage(List<T> content, Pageable pageable, long total, Boolean followed, Boolean requested) {
        super(content, pageable, total);
        this.followed = followed;
        this.requested = requested;
    }

    public FollowPage(List<T> content, Boolean followed, Boolean requested) {
        super(content);
        this.followed = followed;
        this.requested = requested;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public Boolean getRequested() {
        return requested;
    }

    public void setRequested(Boolean requested) {
        this.requested = requested;
    }
}
