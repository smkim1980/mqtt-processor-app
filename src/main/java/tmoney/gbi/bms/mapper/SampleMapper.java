package tmoney.gbi.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import tmoney.gbi.bms.model.EncryptedLocationDto;

import java.util.List;

@Mapper
public interface SampleMapper extends CommonInsertMapper<List<EncryptedLocationDto>> {

    @Override
    void insert(List<EncryptedLocationDto> encryptedLocationDto);

    // [추가] 테이블의 전체 레코드 수를 반환하는 메서드
    long count();

    // [추가] 테이블의 모든 레코드를 삭제하는 메서드
    void deleteAll();

}
