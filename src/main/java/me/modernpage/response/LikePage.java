package me.modernpage.response;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class LikePage<T> extends PageImpl<T> {
	@Transient
	private Boolean liked;

	public LikePage(List<T> content, Pageable pageable, long total, Boolean liked) {
		super(content, pageable, total);
		this.liked = liked;
	}

	public LikePage(List<T> content, Boolean liked) {
		super(content);
		this.liked = liked;
	}

	public Boolean getLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}
}
