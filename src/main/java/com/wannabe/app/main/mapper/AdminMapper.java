package com.wannabe.app.main.mapper;

import com.wannabe.app.main.data.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    Admin findAdminById(long id);
}
