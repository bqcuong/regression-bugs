package edu.harvard.h2ms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User findUserByResetToken(String resetToken) {
		return userRepository.findByResetToken(resetToken);
	}
	
	@Override
	public void save(User user) {
		userRepository.save(user);
	}
	
	

}
