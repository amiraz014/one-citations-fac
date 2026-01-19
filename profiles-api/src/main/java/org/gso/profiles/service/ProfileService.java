package org.gso.profiles.service;

import lombok.RequiredArgsConstructor;
import org.gso.profiles.exception.NotFoundException;
import org.gso.profiles.model.ProfileModel;
import org.gso.profiles.repository.CustomProfileRepository;
import org.gso.profiles.repository.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final CustomProfileRepository customProfileRepository;

    public ProfileModel createProfile(ProfileModel userModel) {
        if (userModel == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        return profileRepository.save(userModel);
    }

    public ProfileModel getProfile(String profileId) {
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile ID cannot be null or blank");
        }
        return profileRepository.findById(profileId)
                .orElseThrow(() -> NotFoundException.DEFAULT);
    }

    public ProfileModel updateProfile(ProfileModel profileToUpdate) {
        if (profileToUpdate == null || profileToUpdate.getId() == null) {
            throw new IllegalArgumentException("Profile or ID cannot be null");
        }
        ProfileModel existingProfile = this.getProfile(profileToUpdate.getId());
        existingProfile.setUserId(profileToUpdate.getUserId());
        return profileRepository.save(existingProfile);
    }

    public Page<ProfileModel> searchProfiles(Criteria criteria, Pageable pageable) {
        if (criteria == null || pageable == null) {
            throw new IllegalArgumentException("Criteria and Pageable cannot be null");
        }
        return customProfileRepository.searchProfiles(criteria, pageable);
    }

    public Page<ProfileModel> searchByMail(String mail, Pageable pageable) {
        if (mail == null || mail.isBlank() || pageable == null) {
            throw new IllegalArgumentException("Email and Pageable cannot be null or blank");
        }
        return profileRepository.findByMail(mail, pageable);
    }

    public ProfileModel getProfileByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> NotFoundException.DEFAULT);
    }

}
