package me.seon.bootjpaboard.domain;

import org.springframework.data.domain.Sort;

public class PageRequest {

	private int page;

	private int size;

	private Sort.Direction direction;

	public void setPage(int page) {
		this.page = page <= 0 ? 1 : page;
	}

	public void setSize(int size) {
		int DEFAULT_SIZE = 5;
		int MAX_SIZE = 20;

		this.size = size > MAX_SIZE ? MAX_SIZE : size;
	}

	public void setDirection(Sort.Direction direction) {
		this.direction = direction;
	}

	public org.springframework.data.domain.PageRequest of() {
		return org.springframework.data.domain.PageRequest.of(page -1, size, direction, "createDate");
	}
}
