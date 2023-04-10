package co.dalicious.domain.paycheck.service;

import co.dalicious.domain.paycheck.entity.MakersPaycheck;

public interface ExcelService {
    void createMakersPaycheckExcel(MakersPaycheck makersPaycheck);
}
