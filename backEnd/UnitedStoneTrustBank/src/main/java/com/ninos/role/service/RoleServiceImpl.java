package com.ninos.role.service;

import com.ninos.exceptions.BadRequestException;
import com.ninos.exceptions.NotFoundException;
import com.ninos.response.Response;
import com.ninos.role.entity.Role;
import com.ninos.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;


    @Override
    public Response<Role> createRole(Role roleRequest) {

        if(roleRepository.findByName(roleRequest.getName()).isPresent()){
            throw new BadRequestException("Role already exists");
        }
        Role savedRole = roleRepository.save(roleRequest);
        return Response.<Role>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Role is created successfully")
                .data(savedRole)
                .build();
    }


    @Override
    public Response<Role> updateRole(Role roleRequest) {

        Role role = roleRepository.findById(roleRequest.getId())
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleRequest.getId()));

        role.setName(roleRequest.getName());
        Role updatedRole = roleRepository.save(role);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role is updated successfully")
                .data(updatedRole)
                .build();
    }


    @Override
    public Response<List<Role>> getAllRoles() {

        List<Role> roleList = roleRepository.findAll();

        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get All the Roles successfully")
                .data(roleList)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long roleId) {

        if(!roleRepository.existsById(roleId)){
            throw new NotFoundException("Role not found with id: " + roleId);
        }

        roleRepository.deleteById(roleId);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role is deleted successfully")
                .build();
    }
}

