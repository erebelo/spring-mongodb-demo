package com.erebelo.springmongodbdemo.mock;

import com.erebelo.springmongodbdemo.domain.entity.ProfileContact;
import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.ProfileLocation;
import com.erebelo.springmongodbdemo.domain.entity.SpouseProfile;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.ContactTypeEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.HealthLevelEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileContactDTO;
import com.erebelo.springmongodbdemo.domain.request.ProfileLocationDTO;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.request.SpouseProfileDTO;
import com.erebelo.springmongodbdemo.domain.response.ProfileContactResponse;
import com.erebelo.springmongodbdemo.domain.response.ProfileLocationResponse;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.domain.response.SpouseProfileResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileMock {

    private static final String ID = "653307bb6dadb04fa2dc6f77";
    private static final String HASH_OBJECT = "426f65b70a600a628be40394f77bf887216aab2f719b784725bd82fc0c4e18ed";
    public static final String USER_ID = "12345";
    private static final String CREATED_BY = "12345";
    private static final String MODIFIED_BY = "12345";
    private static final LocalDateTime CREATED_DATE_TIME = LocalDateTime.of(2024, 1, 2, 19, 30, 54, 873);
    private static final LocalDateTime MODIFIED_DATE_TIME = LocalDateTime.of(2024, 1, 2, 19, 30, 54, 873);
    private static final Long VERSION = 0L;
    public static final String FIRST_NAME = "Bruce";
    public static final String NEW_FIRST_NAME = "Lemon";
    private static final String LAST_NAME = "Wayne";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1980, 10, 22);
    public static final LocalDate NEW_DATE_OF_BIRTH = LocalDate.of(1995, 4, 22);
    private static final Integer NUMBER_OF_DEPENDENTS = 1;
    private static final BigDecimal ESTIMATED_ANNUAL_INCOME = BigDecimal.valueOf(230410.05).setScale(2, RoundingMode.HALF_UP);
    public static final BigDecimal NEW_ESTIMATED_ANNUAL_INCOME = BigDecimal.valueOf(475000.80).setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal ESTIMATED_NET_WORTH = BigDecimal.valueOf(1800900.01).setScale(2, RoundingMode.HALF_UP);
    private static final GenderEnum GENDER = GenderEnum.MALE;
    private static final MaritalStatusEnum MARITAL_STATUS = MaritalStatusEnum.MARRIED;
    private static final EmploymentStatusEnum EMPLOYMENT_STATUS = EmploymentStatusEnum.EMPLOYED;
    private static final HealthLevelEnum HEALTH_LEVEL = HealthLevelEnum.AVERAGE;
    public static final HealthLevelEnum NEW_HEALTH_LEVEL = HealthLevelEnum.ABOVE_AVERAGE;
    public static final ContactTypeEnum CONTACT_TYPE = ContactTypeEnum.EMAIL;
    private static final String CONTACT_VALUE = "bruce.wayne@corp.com";
    public static final String NEW_CONTACT_VALUE = "lemon@mail.com";
    private static final String ADDRESS = "200 7th Ave";
    public static final String NEW_ADDRESS = "Test Street";
    private static final String CITY = "New York";
    private static final String STATE = "NY";
    private static final String COUNTRY = "USA";
    private static final String POSTAL_CODE = "10009";
    private static final String SPOUSE_FIRST_NAME = "Diana";
    private static final String SPOUSE_LAST_NAME = "Themyscira";
    private static final LocalDate SPOUSE_DATE_OF_BIRTH = LocalDate.of(1983, 7, 15);
    private static final GenderEnum SPOUSE_GENDER = GenderEnum.FEMALE;
    private static final EmploymentStatusEnum SPOUSE_EMPLOYMENT_STATUS = EmploymentStatusEnum.NOT_EMPLOYED;

    public static Optional<ProfileEntity> getOptionalProfileEntity() {
        return Optional.ofNullable(getProfileEntity());
    }

    public static ProfileEntity getProfileEntity() {
        return ProfileEntity.builder()
                .id(ID)
                .hashObject(HASH_OBJECT)
                .userId(USER_ID)
                .createdBy(CREATED_BY)
                .modifiedBy(MODIFIED_BY)
                .createdDateTime(CREATED_DATE_TIME)
                .modifiedDateTime(MODIFIED_DATE_TIME)
                .version(VERSION)
                .profile(UserProfile.builder()
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .dateOfBirth(DATE_OF_BIRTH)
                        .numberOfDependents(NUMBER_OF_DEPENDENTS)
                        .estimatedAnnualIncome(ESTIMATED_ANNUAL_INCOME)
                        .estimatedNetWorth(ESTIMATED_NET_WORTH)
                        .gender(GENDER)
                        .maritalStatus(MARITAL_STATUS)
                        .employmentStatus(EMPLOYMENT_STATUS)
                        .healthLevel(HEALTH_LEVEL)
                        .contactNumbers(Collections.singletonList(ProfileContact.builder()
                                .contactType(CONTACT_TYPE)
                                .contactValue(CONTACT_VALUE)
                                .build()))
                        .currentLocation(ProfileLocation.builder()
                                .address(ADDRESS)
                                .city(CITY)
                                .state(STATE)
                                .country(COUNTRY)
                                .postalCode(POSTAL_CODE)
                                .build())
                        .spouseProfile(SpouseProfile.builder()
                                .firstName(SPOUSE_FIRST_NAME)
                                .lastName(SPOUSE_LAST_NAME)
                                .dateOfBirth(SPOUSE_DATE_OF_BIRTH)
                                .gender(SPOUSE_GENDER)
                                .employmentStatus(SPOUSE_EMPLOYMENT_STATUS)
                                .build())
                        .build())
                .build();
    }

    public static ProfileEntity getProfileEntityPatch() {
        var entity = getProfileEntity();
        entity.getProfile().setFirstName(NEW_FIRST_NAME);
        entity.getProfile().setDateOfBirth(NEW_DATE_OF_BIRTH);
        entity.getProfile().setHealthLevel(NEW_HEALTH_LEVEL);
        entity.getProfile().getContactNumbers().get(0).setContactValue(NEW_CONTACT_VALUE);
        entity.getProfile().getCurrentLocation().setAddress(NEW_ADDRESS);
        entity.getProfile().getCurrentLocation().setPostalCode(null);

        return entity;
    }

    public static ProfileRequest getProfileRequest() {
        var request = getProfileEntity().getProfile();
        return ProfileRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .numberOfDependents(request.getNumberOfDependents())
                .estimatedAnnualIncome(request.getEstimatedAnnualIncome())
                .estimatedNetWorth(request.getEstimatedNetWorth())
                .gender(request.getGender())
                .maritalStatus(request.getMaritalStatus())
                .employmentStatus(request.getEmploymentStatus())
                .healthLevel(request.getHealthLevel())
                .contactNumbers(Collections.singletonList(ProfileContactDTO.builder()
                        .contactType(request.getContactNumbers().get(0).getContactType())
                        .contactValue(request.getContactNumbers().get(0).getContactValue())
                        .build()))
                .currentLocation(ProfileLocationDTO.builder()
                        .address(request.getCurrentLocation().getAddress())
                        .city(request.getCurrentLocation().getCity())
                        .state(request.getCurrentLocation().getState())
                        .country(request.getCurrentLocation().getCountry())
                        .postalCode(request.getCurrentLocation().getPostalCode())
                        .build())
                .spouseProfile(SpouseProfileDTO.builder()
                        .firstName(request.getSpouseProfile().getFirstName())
                        .lastName(request.getSpouseProfile().getLastName())
                        .dateOfBirth(request.getSpouseProfile().getDateOfBirth())
                        .gender(request.getSpouseProfile().getGender())
                        .employmentStatus(request.getSpouseProfile().getEmploymentStatus())
                        .build())
                .build();
    }

    public static Map<String, Object> getProfileRequestMapPatch() {
        return new LinkedHashMap<>() {{
            put("firstName", NEW_FIRST_NAME);
            put("dateOfBirth", NEW_DATE_OF_BIRTH);
            put("healthLevel", NEW_HEALTH_LEVEL);
            put("contactNumbers", List.of(new LinkedHashMap<>() {{
                put("contactType", CONTACT_TYPE);
                put("contactValue", NEW_CONTACT_VALUE);
            }}));
            put("currentLocation", new LinkedHashMap<>() {{
                put("address", NEW_ADDRESS);
                put("postalCode", null);
            }});
        }};
    }

    public static ProfileResponse getProfileResponse() {
        var response = getProfileRequest();
        return ProfileResponse.builder()
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .dateOfBirth(response.getDateOfBirth())
                .numberOfDependents(response.getNumberOfDependents())
                .estimatedAnnualIncome(response.getEstimatedAnnualIncome())
                .estimatedNetWorth(response.getEstimatedNetWorth())
                .gender(response.getGender())
                .maritalStatus(response.getMaritalStatus())
                .employmentStatus(response.getEmploymentStatus())
                .healthLevel(response.getHealthLevel())
                .contactNumbers(Collections.singletonList(ProfileContactResponse.builder()
                        .contactType(response.getContactNumbers().get(0).getContactType())
                        .contactValue(response.getContactNumbers().get(0).getContactValue())
                        .build()))
                .currentLocation(ProfileLocationResponse.builder()
                        .address(response.getCurrentLocation().getAddress())
                        .city(response.getCurrentLocation().getCity())
                        .state(response.getCurrentLocation().getState())
                        .country(response.getCurrentLocation().getCountry())
                        .postalCode(response.getCurrentLocation().getPostalCode())
                        .build())
                .spouseProfile(SpouseProfileResponse.builder()
                        .firstName(response.getSpouseProfile().getFirstName())
                        .lastName(response.getSpouseProfile().getLastName())
                        .dateOfBirth(response.getSpouseProfile().getDateOfBirth())
                        .gender(response.getSpouseProfile().getGender())
                        .employmentStatus(response.getSpouseProfile().getEmploymentStatus())
                        .build())
                .build();
    }

    public static ProfileResponse getProfileResponsePatch() {
        var response = getProfileResponse();
        response.setFirstName(NEW_FIRST_NAME);
        response.setDateOfBirth(NEW_DATE_OF_BIRTH);
        response.setHealthLevel(NEW_HEALTH_LEVEL);
        response.getContactNumbers().get(0).setContactValue(NEW_CONTACT_VALUE);
        response.getCurrentLocation().setAddress(NEW_ADDRESS);
        response.getCurrentLocation().setPostalCode(null);

        return response;
    }
}
