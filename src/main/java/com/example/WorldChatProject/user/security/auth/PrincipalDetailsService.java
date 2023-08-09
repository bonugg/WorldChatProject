package com.example.WorldChatProject.user.security.auth;

import com.example.WorldChatProject.user.dto.UserDTO;
import com.example.WorldChatProject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		System.out.println(userName);
		UserDTO userDTO = userRepository.findByUserName(userName).get().EntityToDTO();

		return new PrincipalDetails(userDTO);
	}


}
