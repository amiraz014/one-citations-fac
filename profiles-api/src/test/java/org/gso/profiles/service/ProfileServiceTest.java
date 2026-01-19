package org.gso.profiles.service;

import java.time.LocalDateTime;

import org.gso.profiles.model.ProfileModel;
import org.gso.profiles.repository.CustomProfileRepository;
import org.gso.profiles.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProfileServiceTest {

    @Autowired
    ProfileService profileService;

    @MockitoBean
    ProfileRepository profileRepository;

    @MockitoBean
    CustomProfileRepository customProfileRepository;

    private ProfileModel testProfile;

    @BeforeEach
    void setUp() {
        testProfile = ProfileModel.builder()
                .id("myId")
                .userId("toto")
                .mail("toto@example.com")
                .age(25)
                .firstName("Jean")
                .lastName("Dupont")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();
    }

    @Test
    public void testProfileCreation() {
        ProfileModel profileModel = ProfileModel.builder().userId("toto").build();
        ProfileModel createdProfile = ProfileModel.builder()
                        .id("myId")
                        .userId("toto")
                        .created(LocalDateTime.now())
                        .modified(LocalDateTime.now())
                        .build();
        when(profileRepository.save(any())).thenReturn(createdProfile);

        ProfileModel result = profileService.createProfile(profileModel);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("myId");
        assertThat(result.getUserId()).isEqualTo("toto");
        verify(profileRepository, times(1)).save(any());
    }

    @Test
    public void testGetProfile_Success() {
        when(profileRepository.findById("myId")).thenReturn(java.util.Optional.of(testProfile));

        ProfileModel result = profileService.getProfile("myId");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("myId");
        assertThat(result.getFirstName()).isEqualTo("Jean");
        verify(profileRepository, times(1)).findById("myId");
    }

    @Test
    public void testGetProfileByUserId_Success() {
        when(profileRepository.findByUserId("toto")).thenReturn(Optional.of(testProfile));

        ProfileModel result = profileService.getProfileByUserId("toto");

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("toto");
        assertThat(result.getFirstName()).isEqualTo("Jean");
        verify(profileRepository, times(1)).findByUserId("toto");
    }

    @Test
    public void testGetProfileByUserId_NotFound() {
        when(profileRepository.findByUserId("unknown")).thenReturn(null);

        assertThatThrownBy(() -> profileService.getProfileByUserId("unknown"))
                .isNotNull();
    }

    @Test
    public void testUpdateProfile_Success() {
        ProfileModel updatedData = ProfileModel.builder()
                .id("myId")
                .userId("toto-updated")
                .firstName("Pierre")
                .build();

        when(profileRepository.findById("myId")).thenReturn(java.util.Optional.of(testProfile));
        when(profileRepository.save(any())).thenReturn(testProfile);

        ProfileModel result = profileService.updateProfile(updatedData);

        assertThat(result).isNotNull();
        verify(profileRepository, times(1)).findById("myId");
        verify(profileRepository, times(1)).save(any());
    }

    @Test
    public void testSearchByMail_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProfileModel> page = new PageImpl<>(java.util.Arrays.asList(testProfile));
        when(profileRepository.findByMail("toto@example.com", pageable)).thenReturn(page);

        Page<ProfileModel> result = profileService.searchByMail("toto@example.com", pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jean");
        verify(profileRepository, times(1)).findByMail("toto@example.com", pageable);
    }

    @Test
    public void testSearchProfiles_Success() {
        Pageable pageable = PageRequest.of(0, 20);
        Criteria criteria = new Criteria();
        Page<ProfileModel> page = new PageImpl<>(java.util.Arrays.asList(testProfile));
        when(customProfileRepository.searchProfiles(any(Criteria.class), eq(pageable))).thenReturn(page);

        Page<ProfileModel> result = profileService.searchProfiles(criteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(customProfileRepository, times(1)).searchProfiles(any(Criteria.class), eq(pageable));
    }
}