package se.plushogskolan.casemanagement.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import se.plushogskolan.casemanagement.model.Team;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long>{
	
	Team findByNameContaining(String name);
	
}
