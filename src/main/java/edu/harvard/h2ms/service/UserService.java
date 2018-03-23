package edu.harvard.h2ms.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.UserRepository;

@Service("userDetailsService")
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findOneByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException(String.format("Unknown user: %s", username));
		} else {
			return user;
		}
	}

}
