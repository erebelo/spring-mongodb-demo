package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_001;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileMapper mapper;

    @Autowired
    private ProfileRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private static final String RESPONSE_BODY = "Response body: {}";

    @Override
    public ProfileResponse getProfileByUserId(String userId) {
        throw new StandardException(COMMON_ERROR_404_001, userId);
    }

    @Transactional
    @Override
    public ProfileResponse insertProfile(String userId, ProfileRequest profileRequest) {
        return null;
    }

    @Transactional
    @Override
    public ProfileResponse updateProfile(String userId, ProfileRequest profileRequest) {
        return null;
    }

    @Transactional
    @Override
    public void deleteProfile(String userId) {

    }
}
