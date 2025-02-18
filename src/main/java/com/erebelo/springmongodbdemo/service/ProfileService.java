package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;

public interface ProfileService {

    ProfileResponse getProfile(String userId);

    ProfileResponse insertProfile(String userId, ProfileRequest profileRequest);

    ProfileResponse updateProfile(String userId, ProfileRequest profileRequest);

    void deleteProfile(String userId);

}
