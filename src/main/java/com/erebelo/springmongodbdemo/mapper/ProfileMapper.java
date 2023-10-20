package com.erebelo.springmongodbdemo.mapper;

import com.erebelo.springmongodbdemo.domain.entity.SpouseProfile;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.request.SpouseProfileDTO;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.isNull;
import static org.mapstruct.ReportingPolicy.WARN;

@Mapper(componentModel = "spring", unmappedTargetPolicy = WARN)
public interface ProfileMapper {

    ProfileResponse entityToResponse(UserProfile entity);

    @Mapping(target = "estimatedAnnualIncome", expression = "java(roundUpBigDecimalValue(request.getEstimatedAnnualIncome()))")
    @Mapping(target = "estimatedNetWorth", expression = "java(roundUpBigDecimalValue(request.getEstimatedNetWorth()))")
    UserProfile requestToEntity(ProfileRequest request);

    @Mapping(target = "employmentStatus", expression = "java(checkSpouseEmploymentStatus(spouseProfile.getEmploymentStatus()))")
    SpouseProfile map(SpouseProfileDTO spouseProfile);

    ProfileRequest entityToRequest(UserProfile entity);

    default BigDecimal roundUpBigDecimalValue(BigDecimal bigDecimalValue) {
        if (!isNull(bigDecimalValue)) {
            return bigDecimalValue.setScale(2, RoundingMode.HALF_EVEN);
        }

        return null;
    }

    default EmploymentStatusEnum checkSpouseEmploymentStatus(EmploymentStatusEnum spouseEmploymentStatus) {
        if (isNull(spouseEmploymentStatus)) {
            return EmploymentStatusEnum.NOT_EMPLOYED;
        }

        return spouseEmploymentStatus;
    }
}
