package tmoney.gbi.bms.mapper;

import org.apache.ibatis.annotations.Mapper;
import tmoney.gbi.bms.model.EncryptedLocationDto;

import java.util.List;

@Mapper
public interface SampleMapper extends CommonInsertMapper<List<EncryptedLocationDto>> {

    @Override
    void insert(List<EncryptedLocationDto> encryptedLocationDto);

}
