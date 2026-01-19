package org.gso.profiles.repository;

import org.gso.profiles.model.ProfileModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends PagingAndSortingRepository<ProfileModel, String>, CrudRepository<ProfileModel, String> {

    Page<ProfileModel> findByMail(String mail, Pageable pageable);

    Optional<ProfileModel> findByUserId(String userId);

}
