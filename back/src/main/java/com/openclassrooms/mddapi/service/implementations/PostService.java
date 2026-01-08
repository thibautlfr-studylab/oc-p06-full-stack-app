package com.openclassrooms.mddapi.service.implementations;

import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.service.interfaces.IPostService;

public class PostService implements IPostService {

	private PostRepository postRepository;
	
	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}
	
}
