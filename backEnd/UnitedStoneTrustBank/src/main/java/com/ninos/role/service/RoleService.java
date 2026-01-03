package com.ninos.role.service;

import com.ninos.response.Response;
import com.ninos.role.entity.Role;
import java.util.List;


public interface RoleService {

    Response<Role> createRole(Role roleRequest);
    Response<Role> updateRole(Role roleRequest);
    Response<List<Role>> getAllRoles();
    Response<?> deleteRole(Long roleId);

}

