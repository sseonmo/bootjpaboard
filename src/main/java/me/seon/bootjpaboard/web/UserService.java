package me.seon.bootjpaboard.web;

import lombok.RequiredArgsConstructor;
import me.seon.bootjpaboard.domain.AccountDto;
import me.seon.bootjpaboard.domain.UserRepository;
import me.seon.bootjpaboard.domain.model.Email;
import me.seon.bootjpaboard.exception.EmailDuplicationException;
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

		userRepository.save(dto.toEntity());
	}

	public Boolean isExistedEmail(Email email) {
		return userRepository.findByEmail(email) != null;
	}


}
