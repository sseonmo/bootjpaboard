package me.seon.bootjpaboard.domain;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type User custom repository.
 */
@Transactional(readOnly = true)
public class UserCustomRepositoryImpl extends QuerydslRepositorySupport implements UserCustomRepository {

	public UserCustomRepositoryImpl() {
		super(User.class);
	}

	@Override
	public List<User> findRecentlyRegistered(int limit) {
		final QUser qUser = QUser.user;

		return from(qUser)
				.limit(limit)
				.orderBy(qUser.createDate.desc())
				.fetch();
	}
}
