package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.annotation.UserId;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.PROFILE;

@Validated
@RestController
@Tag(name = "Profile API")
public class ProfileController {

    @Autowired
    private ProfileService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private static final String PROFILE_RESPONSE = "Profile response: {}";

    @Operation(summary = "GET profile api")
    @GetMapping(value = PROFILE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> getProfileByUserId(@UserId String userId) {
        LOGGER.info("Getting profile by userId");
        ProfileResponse response = service.getProfileByUserId(userId);

        LOGGER.info(PROFILE_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
