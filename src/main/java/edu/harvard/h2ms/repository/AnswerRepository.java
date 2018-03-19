package edu.harvard.h2ms.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.harvard.h2ms.domain.core.Answer;

public interface AnswerRepository extends PagingAndSortingRepository<Answer, Long> {

}
