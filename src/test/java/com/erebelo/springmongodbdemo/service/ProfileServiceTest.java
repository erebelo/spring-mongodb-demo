package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_001;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
        given(repository.findByUserId(anyString())).willReturn((getProfileEntity()));

        var result = service.getProfileByUserId(USER_ID);

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToResponse(any(UserProfile.class));
    }

    @Test
    void givenValidParamsWhenGetProfileByUserIdThenThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class).isThrownBy(() -> service.getProfileByUserId(USER_ID)).hasFieldOrPropertyWithValue(
                "errorCode", COMMON_ERROR_404_001);

        verify(repository).findByUserId(USER_ID);
    }
}
