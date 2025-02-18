package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import java.util.Map;

public interface ProfilePatchService {

    ProfileResponse patchProfile(String userId, Map<String, Object> profileRequestMap);

}
