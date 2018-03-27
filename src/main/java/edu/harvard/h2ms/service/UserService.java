package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.User;

public interface UserService {
	
	public User findUserByEmail(String email);
	public User findUserByResetToken(String resetToken);
	public void save(User user);

}
