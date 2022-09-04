package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person,Long> {
}
