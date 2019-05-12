package me.seon.bootjpaboard.web;

import lombok.RequiredArgsConstructor;
import me.seon.bootjpaboard.domain.AccountDto;
import me.seon.bootjpaboard.domain.User;
import me.seon.bootjpaboard.domain.UserRepository;
import me.seon.bootjpaboard.domain.model.Email;
import me.seon.bootjpaboard.exception.AccountNotFountException;
import me.seon.bootjpaboard.exception.EmailDuplicationException;
import me.seon.bootjpaboard.exception.UserIdlDuplicationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public void create(AccountDto.SignUpReq dto) {

		if(isExistedEmail(dto.getEmail()))
			throw new EmailDuplicationException(dto.getEmail());

		if(isExistedUserIdl(dto.getUserId()))
			throw new UserIdlDuplicationException(dto.getUserId());

		userRepository.save(dto.toEntity());
	}

	public Boolean isExistedEmail(Email email) {
		return userRepository.findByEmail(email) != null;
	}

	public Boolean isExistedUserIdl(String userId) {
		return userRepository.findByUserId(userId).orElse(null) != null;
	}

	public User findByUserId(final AccountDto.LoginReq dto) {

		return  userRepository.findByUserId(dto.getUserId()).orElseThrow(() -> new AccountNotFountException(dto.getUserId()));
	}

	public User findById(final Long id) {
		return userRepository.findById(id).orElseThrow(AccountNotFountException::new);
	}
}
