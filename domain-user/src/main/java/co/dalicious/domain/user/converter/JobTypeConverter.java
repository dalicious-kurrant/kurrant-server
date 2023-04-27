package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.JobType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class JobTypeConverter implements AttributeConverter<JobType, Integer> {


    @Override
    public Integer convertToDatabaseColumn(JobType jobType) {
        return jobType.getCode() ;
    }

    @Override
    public JobType convertToEntityAttribute(Integer dbData) {
        return JobType.ofCode(dbData);
    }

}
