package tmoney.gbi.bms.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonInsertMapper<T> {

    void insert(T t);
}