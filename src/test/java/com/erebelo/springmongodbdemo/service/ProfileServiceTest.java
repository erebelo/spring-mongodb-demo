package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.impl.ProfileServiceImpl;
import com.erebelo.springmongodbdemo.utils.ByteHandlerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_001;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_003;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_409_001;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.NEW_ESTIMATED_ANNUAL_INCOME;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getOptionalProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
    void givenValidParamsWhenGetProfileByUserIdThenReturnProfileResponse() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var result = service.getProfileByUserId(USER_ID);

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToResponse(any(UserProfile.class));
    }

    @Test
    void givenValidParamsWhenGetProfileByUserIdThenThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.getProfileByUserId(USER_ID))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_001)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void givenValidParamsWhenInsertProfileThenReturnProfileResponse() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());
        given(repository.insert(any(ProfileEntity.class))).willReturn(getProfileEntity());

        var result = service.insertProfile(USER_ID, getProfileRequest());

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).insert(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id", "hashObject", "createdBy", "modifiedBy", "createdDateTime", "modifiedDateTime", "version")
                .isEqualTo(getProfileEntity());
    }

    @Test
    void givenValidParamsWhenInsertProfileThenThrowConflictException() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.insertProfile(USER_ID, getProfileRequest()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_409_001);

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).insert(entityArgumentCaptor.capture());
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void givenValidParamsWhenUpdateProfileThenReturnProfileResponse() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var profileEntity = getProfileEntity();
        profileEntity.getProfile().setEstimatedAnnualIncome(NEW_ESTIMATED_ANNUAL_INCOME);
        profileEntity.getProfile().setEmploymentStatus(EmploymentStatusEnum.RETIRED);

        given(repository.save(any(ProfileEntity.class))).willReturn(profileEntity);

        var profileRequest = getProfileRequest();
        profileRequest.setEstimatedAnnualIncome(NEW_ESTIMATED_ANNUAL_INCOME);
        profileRequest.setEmploymentStatus(EmploymentStatusEnum.RETIRED);

        var result = service.updateProfile(USER_ID, profileRequest);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("estimatedAnnualIncome", "employmentStatus")
                .isEqualTo(getProfileResponse());
        assertThat(result.getEstimatedAnnualIncome()).isEqualTo(NEW_ESTIMATED_ANNUAL_INCOME);
        assertThat(result.getEmploymentStatus()).isEqualTo(EmploymentStatusEnum.RETIRED);

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).save(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue()).usingRecursiveComparison()
                .ignoringFields("hashObject")
                .isEqualTo(profileEntity);
    }

    @Test
    void givenValidParamsWhenUpdateProfileThenDoNotUpdateWhenMatchingProfileIsFound() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var byteHandlerUtilsMockedStatic = Mockito.mockStatic(ByteHandlerUtils.class);
        byteHandlerUtilsMockedStatic.when(() -> ByteHandlerUtils.byteArrayComparison(any(), any())).thenReturn(true);

        var result = service.updateProfile(USER_ID, getProfileRequest());

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        byteHandlerUtilsMockedStatic.close();
    }

    @Test
    void givenValidParamsWhenUpdateProfileThenThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.updateProfile(USER_ID, getProfileRequest()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_002)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(entityArgumentCaptor.capture());
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void givenValidParamsWhenDeleteProfileThenReturnNothing() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());
        willDoNothing().given(repository).delete(any(ProfileEntity.class));

        service.deleteProfile(USER_ID);

        verify(repository).findByUserId(USER_ID);
        verify(repository).delete(any(ProfileEntity.class));
    }

    @Test
    void givenValidParamsWhenDeleteProfileThenThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.deleteProfile(USER_ID))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_003)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(repository, never()).delete(any(ProfileEntity.class));
    }
}
