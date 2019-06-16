package me.seon.bootjpaboard.domain;

import java.util.List;

public interface UserCustomRepository {

	List<User> findRecentlyRegistered(int limit);

}


