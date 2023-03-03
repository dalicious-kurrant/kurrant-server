package co.kurrant.app.client_api.service.impl;

import co.dalicious.domain.client.dto.*;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.entity.EmployeeHistory;
import co.dalicious.domain.client.entity.enums.EmployeeHistoryType;
import co.dalicious.domain.client.mapper.EmployeeHistoryMapper;
import co.dalicious.domain.client.mapper.EmployeeMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QUserGroupRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.QUserSpotRepository;
import co.kurrant.app.client_api.dto.DeleteWaitingMemberRequestDto;
import co.kurrant.app.client_api.dto.MemberListResponseDto;
import co.kurrant.app.client_api.dto.MemberWaitingListResponseDto;
import co.kurrant.app.client_api.mapper.MemberMapper;
import co.kurrant.app.client_api.service.MemberService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final QUserSpotRepository qUserSpotRepository;
    private final CorporationRepository corporationRepository;
    private final QCorporationRepository qCorporationRepository;
    private final QSpotRepository qSpotRepository;
    private final QUserGroupRepository qUserGroupRepository;
    private final MemberMapper memberMapper;
    private final EmployeeRepository employeeRepository;
    private final QEmployeeRepository qEmployeeRepository;
    private final QUserRepository qUserRepository;
    private final GroupRepository groupRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeHistoryMapper employeeHistoryMapper;
    private final EmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public List<MemberListResponseDto> getUserList(String code) {

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger corporationId = qCorporationRepository.findOneByCode(code);

        //corporationId로 GroupName 가져오기
        String userGroupName = qUserGroupRepository.findNameById(corporationId);
            //groupID로 user목록 조회
        List<User> groupUserList = qUserGroupRepository.findAllByGroupId(corporationId);


        groupUserList.stream().filter(u -> u.getUserStatus().getCode() != 0)
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        List<MemberListResponseDto> memberListResponseList = groupUserList.stream()
                .map((user) -> memberMapper.toMemberListDto(user, userGroupName)).collect(Collectors.toList());


        return memberListResponseList;
    }

    @Override
    public List<MemberWaitingListResponseDto> getWaitingUserList(String code) {

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger corporationId = qCorporationRepository.findOneByCode(code);
        //corpId로 employee 대기유저 목록 조회
        List<Employee> employeeList = qEmployeeRepository.findAllByCorporationId(corporationId);

        List<MemberWaitingListResponseDto> waitingListResponseDtoList = employeeList.stream()
                .map(memberMapper::toMemberWaitingListDto).toList();

        return waitingListResponseDtoList;
    }

    @Override
    public void insertMemberList(ClientUserWaitingListSaveRequestDtoList dtoList) {

            //code로 CorporationId 찾기 (=GroupId)
            Corporation corporation = qCorporationRepository.findEntityByCode(dtoList.getCode());

            for (int i = 0; i < dtoList.getSaveUserList().size(); i++) {

                String email = dtoList.getSaveUserList().get(i).getEmail();
                String phone = dtoList.getSaveUserList().get(i).getPhone();
                String name = dtoList.getSaveUserList().get(i).getName();
                //있는 ID의 경우 수정
                Optional<Employee> optionalEmployee = employeeRepository.findById(dtoList.getSaveUserList().get(i).getId());
                List<Employee> employees = employeeRepository.findAllByEmail(email);
                if (optionalEmployee.isPresent() || employees.size() != 0){
                    qEmployeeRepository.patchEmployee(dtoList.getSaveUserList().get(i).getId(),phone, email, name);
                }else{
                    //ID가 없다면 생성
                    Employee employee = employeeMapper.toEntity(email, name, phone, corporation);

                    employeeRepository.save(employee);
                }

            }


    }
    @Override
    public void deleteMember(DeleteMemberRequestDto deleteMemberRequestDto) {
        //userId 리스트 가져오기
        List<BigInteger> userIdList = deleteMemberRequestDto.getUserIdList();

        //code로 CorporationId 찾기 (=GroupId)
        BigInteger groupId = deleteMemberRequestDto.getGroupId();
//                qCorporationRepository.findOneByCode(deleteMemberRequestDto.getCode());

        if (userIdList.size() == 0) throw new ApiException(ExceptionEnum.BAD_REQUEST);

        for (BigInteger userId : userIdList){
            User deleteUser = qUserRepository.findByUserId(userId);
            EmployeeHistoryType type = EmployeeHistoryType.USER;
            EmployeeHistory employeeHistory = employeeHistoryMapper.toEntity(userId, deleteUser.getName(), deleteUser.getEmail(), deleteUser.getPhone(), type);
            employeeHistoryRepository.save(employeeHistory);
            Long deleteResult = qUserGroupRepository.deleteMember(userId, groupId);
            if (deleteResult != 1) throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
        }
    }

    @Override
    public void deleteWaitingMember(DeleteWaitingMemberRequestDto deleteWaitingMemberRequestDto) {
        //받아온 Employee ID를 삭제한다
        for(BigInteger userId : deleteWaitingMemberRequestDto.getWaitMemberIdList()){
            //삭제 전에 기록 남기기
            Employee employee = employeeRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
            EmployeeHistoryType type = EmployeeHistoryType.WAIT_USER;
            EmployeeHistory employeeHistory = employeeHistoryMapper.toEntity(userId, employee.getName(), employee.getEmail(), employee.getPhone(), type);
            employeeHistoryRepository.save(employeeHistory);

            long result = qEmployeeRepository.deleteWaitingMember(userId);
            if (result != 1){
                throw new ApiException(ExceptionEnum.USER_PATCH_ERROR);
            }
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

    @Override
    public ResponseEntity<InputStreamResource> exportExcelForWaitingUserList(HttpServletResponse response, ClientUserWaitingListSaveRequestDto exportExcelWaitngUserListRequestDto) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        //시트 생성(+시트제목설정)
        SXSSFSheet sheet = workbook.createSheet("기업 가입 리스트");

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        font.setFontHeightInPoints((short) 13);
        headStyle.setFont(font);

        //시트 열 너비 설정
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        //해당 행의 첫번째 열 셀 생성
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("번호");
        //해당 행의 두번째 열 셀 생성
        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("이메일");
        //해당 행의 세번째 열 셀 생성
        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("이름");
        //해당 행의 번째 열 셀 생성
        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("휴대폰 번호");
/*
        //데이터 행 및 셀 생성후 데이터 넣어주기
        Row bodyRow = null;
        Cell bodyCell = null;
        for (int i = 0; i < exportExcelWaitngUserListRequestDto.getId().size(); i++) {
            //행 생성
            bodyRow = sheet.createRow(i+1);
            //데이터 넣기(번호)
            bodyCell = bodyRow.createCell(0);
            bodyCell.setCellValue(exportExcelWaitngUserListRequestDto.getId().get(i).intValue());
            //이메일
            bodyCell = bodyRow.createCell(1);
            bodyCell.setCellValue(exportExcelWaitngUserListRequestDto.getEmail().get(i));
            //이름
            bodyCell = bodyRow.createCell(2);
            bodyCell.setCellValue(exportExcelWaitngUserListRequestDto.getName().get(i));
            //휴대폰 번호
            bodyCell = bodyRow.createCell(3);
            bodyCell.setCellValue(exportExcelWaitngUserListRequestDto.getPhone().get(i));
        }

        File tmpFile = File.createTempFile("TMP~", ".xlsx");
        try (OutputStream fos = new FileOutputStream(tmpFile);) {
            workbook.write(fos);
        }
        InputStream res = new FileInputStream(tmpFile) {
            @Override
            public void close() throws IOException {
                super.close();
                if (tmpFile.delete()) {
                    System.out.println("임시파일 삭제 완료");
                }
            }
        };
        return ResponseEntity.ok() //
                .contentLength(tmpFile.length()) //
                .contentType(MediaType.APPLICATION_OCTET_STREAM) //
                .header("Content-Disposition", "attachment;filename=boardlist.xlsx") //
                .body(new InputStreamResource(res));

    }

 */
        return null;
    }

    @Override
    public void insertMemberListByExcel(ClientExcelSaveDtoList clientExcelSaveDtolist) {
        //code로 CorporationId 찾기 (=GroupId)
        for (ClientExcelSaveDto excel : clientExcelSaveDtolist.getSaveList()){
            Corporation corporation = corporationRepository.findById(excel.getGroupId())
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));


            Employee employee = employeeMapper.toEntity(excel.getEmail(),
                                                        excel.getName(),
                                                        excel.getPhone(),
                                                        corporation);
            employeeRepository.save(employee);



        }
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
