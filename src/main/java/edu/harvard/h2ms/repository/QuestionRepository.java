package edu.harvard.h2ms.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import edu.harvard.h2ms.domain.core.Question;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {

}
