package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_003;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_409_001;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.NEW_ESTIMATED_ANNUAL_INCOME;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getOptionalProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.erebelo.spring.common.utils.serialization.ByteHandler;
import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.impl.ProfileServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileServiceImpl service;

    @Mock
    private ProfileRepository repository;

    @Spy
    private final ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);

    @Captor
    private ArgumentCaptor<ProfileEntity> entityArgumentCaptor;

    @Test
    void testGetProfileSuccessful() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        ProfileResponse response = service.getProfile(USER_ID);

        assertThat(response).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testGetProfileThrowsNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        CommonException exception = assertThrows(CommonException.class, () -> service.getProfile(USER_ID));

        assertEquals(COMMON_ERROR_404_001, exception.getErrorCode());
        assertArrayEquals(new Object[]{USER_ID}, exception.getArgs());

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testInsertProfileSuccessful() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());
        given(repository.insert(any(ProfileEntity.class))).willReturn(getProfileEntity());

        ProfileResponse response = service.insertProfile(USER_ID, getProfileRequest());

        assertThat(response).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).insert(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue()).usingRecursiveComparison().ignoringFields("id", "hashObject",
                "createdBy", "modifiedBy", "createdDateTime", "modifiedDateTime", "version")
                .isEqualTo(getProfileEntity());
    }

    @Test
    void testInsertProfileThrowsConflictException() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());
        ProfileRequest profileRequest = getProfileRequest();

        CommonException exception = assertThrows(CommonException.class,
                () -> service.insertProfile(USER_ID, profileRequest));

        assertEquals(COMMON_ERROR_409_001, exception.getErrorCode());

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).insert(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testUpdateProfileSuccessful() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        ProfileEntity profileEntity = getProfileEntity();
        profileEntity.getProfile().setEstimatedAnnualIncome(NEW_ESTIMATED_ANNUAL_INCOME);
        profileEntity.getProfile().setEmploymentStatus(EmploymentStatusEnum.RETIRED);

        given(repository.save(any(ProfileEntity.class))).willReturn(profileEntity);

        ProfileRequest profileRequest = getProfileRequest();
        profileRequest.setEstimatedAnnualIncome(NEW_ESTIMATED_ANNUAL_INCOME);
        profileRequest.setEmploymentStatus(EmploymentStatusEnum.RETIRED);

        ProfileResponse response = service.updateProfile(USER_ID, profileRequest);

        assertThat(response).usingRecursiveComparison().ignoringFields("estimatedAnnualIncome", "employmentStatus")
                .isEqualTo(getProfileResponse());
        assertEquals(NEW_ESTIMATED_ANNUAL_INCOME, response.getEstimatedAnnualIncome());
        assertEquals(EmploymentStatusEnum.RETIRED, response.getEmploymentStatus());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).save(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue()).usingRecursiveComparison().ignoringFields("hashObject")
                .isEqualTo(profileEntity);
    }

    @Test
    void testUpdateProfileWhenMatchingProfileIsFound() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        try (MockedStatic<ByteHandler> mockedStatic = Mockito.mockStatic(ByteHandler.class)) {
            mockedStatic.when(() -> ByteHandler.byteArrayComparison(any(), any())).thenReturn(true);

            ProfileResponse response = service.updateProfile(USER_ID, getProfileRequest());

            assertThat(response).usingRecursiveComparison().isEqualTo(getProfileResponse());

            verify(repository).findByUserId(USER_ID);
            verify(mapper).requestToEntity(any(ProfileRequest.class));
            verify(repository, never()).save(any(ProfileEntity.class));
            verify(mapper).entityToResponse(any(UserProfile.class));
        }
    }

    @Test
    void testUpdateProfileThrowsNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());
        ProfileRequest profileRequest = getProfileRequest();

        CommonException exception = assertThrows(CommonException.class,
                () -> service.updateProfile(USER_ID, profileRequest));

        assertEquals(COMMON_ERROR_404_002, exception.getErrorCode());
        assertArrayEquals(new Object[]{USER_ID}, exception.getArgs());

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testDeleteProfileSuccessful() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());
        willDoNothing().given(repository).delete(any(ProfileEntity.class));

        service.deleteProfile(USER_ID);

        verify(repository).findByUserId(USER_ID);
        verify(repository).delete(any(ProfileEntity.class));
    }

    @Test
    void testDeleteProfileThrowsNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        CommonException exception = assertThrows(CommonException.class, () -> service.deleteProfile(USER_ID));

        assertEquals(COMMON_ERROR_404_003, exception.getErrorCode());
        assertArrayEquals(new Object[]{USER_ID}, exception.getArgs());

        verify(repository).findByUserId(USER_ID);
        verify(repository, never()).delete(any(ProfileEntity.class));
    }
}
