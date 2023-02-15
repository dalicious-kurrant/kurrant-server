package co.kurrant.app.client.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.client.dto.ImportExcelWaitingUserListResponseDto;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.client.repository.QEmployeeRepository;
import co.dalicious.domain.client.repository.QSpotRepository;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.kurrant.app.client.dto.MemberListResponseDto;
import co.kurrant.app.client.dto.MemberWaitingListResponseDto;
import co.kurrant.app.client.mapper.MemberMapper;
import co.kurrant.app.client.service.MemberService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final QUserSpotRepository qUserSpotRepository;
    private final QCorporationRepository qCorporationRepository;
    private final QSpotRepository qSpotRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final MemberMapper memberMapper;
    private final QEmployeeRepository qEmployeeRepository;
    private final QUserRepository qUserRepository;

    @Override
    public List<MemberListResponseDto> getUserList(String code, OffsetBasedPageRequest pageable) {

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger corporationId = qCorporationRepository.findOneByCode(code);

        //corporationId로 GroupName 가져오기
        String userGroupName = qUserGroupRepository.findNameById(corporationId);
            //groupID로 user목록 조회
        List<User> groupUserList = qUserGroupRepository.findAllByGroupId(corporationId);

        groupUserList.stream().filter(u -> u.getUserStatus().getCode() != 0)
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<MemberListResponseDto> memberListResponseList = new ArrayList<>();
        for (User user : groupUserList){
            memberListResponseList.add(memberMapper.toMemberListDto(user, userGroupName));
        }

        return memberListResponseList;
    }

    @Override
    public List<MemberWaitingListResponseDto> getWaitingUserList(String code, OffsetBasedPageRequest pageable) {

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger corporationId = qCorporationRepository.findOneByCode(code);
        //corpId로 employee 대기유저 목록 조회
        List<Employee> employeeList = qEmployeeRepository.findAllByCorporationId(corporationId);

        List<MemberWaitingListResponseDto> waitingListResponseDtoList = new ArrayList<>();
        for (Employee employee : employeeList){
            waitingListResponseDtoList.add(memberMapper.toMemberWaitingListDto(employee));
        }

        return waitingListResponseDtoList;
    }

    @Override
    public void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto) {
        //userId 리스트 가져오기
        List<BigInteger> userIdList = deleteMemberRequestDto.getUserIdList();

        if (userIdList.size() ==0) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        for (BigInteger userId : userIdList){
            Long deleteResult = qUserRepository.deleteMember(userId);
            if (deleteResult != 1) throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
        }
    }

    @Override
    public List<ImportExcelWaitingUserListResponseDto> importExcelForWaitingUserList(MultipartFile file) throws IOException {

        List<ImportExcelWaitingUserListResponseDto> resultList = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        Tika tika = new Tika();
        String mimeType = tika.detect(inputStream);

        if (isAllowedMIMEType(mimeType)) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet worksheet = workbook.getSheetAt(0);

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 1번째 행부터 끝까지
                Row row = worksheet.getRow(i);
                ImportExcelWaitingUserListResponseDto data = new ImportExcelWaitingUserListResponseDto();
                data.setId(BigInteger.valueOf((long) row.getCell(0).getNumericCellValue()));
                data.setEmail(row.getCell(1).getStringCellValue());
                data.setName(row.getCell(2).getStringCellValue());
                data.setPhone(row.getCell(3).getStringCellValue());
                data.setCorporationId(BigInteger.valueOf((long) row.getCell(4).getNumericCellValue()));
                resultList.add(data);
            }

        }
        return resultList;
    }

    private boolean isAllowedMIMEType(String mimeType) {
        return mimeType.equals("application/x-tika-ooxml");
    }
        /*
        List<ExcelExample> dataList = new ArrayList<>();

        try(InputStream is = file.getInputStream();){
            Tika tika = new Tika();
            String mimeType = tika.detect(is);
            if (isAllowedMIMEType(mimeType)){
                Workbook workbook = new XSSFWorkbook(file.getInputStream());

                Sheet worksheet = workbook.getSheetAt(0);

                String atchFileId = null;

                for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 1번째 행부터 끝까지
                    Row row = worksheet.getRow(i);

                    ExcelExample data = new ExcelExample();
                    data.setUserId((int) row.getCell(0).getNumericCellValue());
                    data.setName(row.getCell(1).getStringCellValue());
                    data.setPhone(row.getCell(2).getStringCellValue());
                    data.setEmail(row.getCell(3).getStringCellValue());
                    data.setCorporationName(row.getCell(4).getStringCellValue());

                    dataList.add(data);
                }

                model.addAttribute("list", dataList);
            } else{
                throw new ApiException(ExceptionEnum.NOT_FOUND);
            }
        } catch (Exception e){
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }
        return dataList;
    }

    private boolean isAllowedMIMEType(String mimeType) {
        return mimeType.equals("application/x-tika-ooxml");
    }
*/
}
