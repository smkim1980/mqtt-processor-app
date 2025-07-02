package tmoney.gbi.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import tmoney.gbi.bms.domain.EncryptedLocationDto;

@Mapper
public interface SampleMapper extends CommonInsertMapper<EncryptedLocationDto> {

    @Override
    void insert(EncryptedLocationDto encryptedLocationDto);

}
