package edu.harvard.h2ms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.harvard.h2ms.domain.core.RelativeMoment;
import edu.harvard.h2ms.repository.RelativeMomentRepository;

public class RelativeMomentService {
	
	@Autowired
	private RelativeMomentRepository relativeMomentRepository;
	
	public List<RelativeMoment> getAllRelativeMoments() {
		List<RelativeMoment> relativeMoments = new ArrayList<RelativeMoment>();
		relativeMomentRepository.findAll().forEach(relativeMoments::add);
		return relativeMoments;
	}
	
	public RelativeMoment getRelativeMoment(Long id) {
		return relativeMomentRepository.findOne(id);
	}
	
	public RelativeMoment getRelativeMomentByName(String name) {
		return relativeMomentRepository.findByName(name);
	}
}
