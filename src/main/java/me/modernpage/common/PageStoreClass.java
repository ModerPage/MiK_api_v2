package me.modernpage.common;

import org.springframework.data.domain.Page;

//@Component
public class PageStoreClass {
	public static ThreadLocal<Page> page = new ThreadLocal<>();

//	public synchronized Page getPage() {
//		return page;
//	}

//	public synchronized void setPage(Page page) {
//		this.page = page;
//	}
	
}
