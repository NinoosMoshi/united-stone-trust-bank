package com.ninos.role.controller;

import com.ninos.response.Response;
import com.ninos.role.entity.Role;
import com.ninos.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Response<Role>> createRole(@RequestBody Role roleRequest){
        return ResponseEntity.ok(roleService.createRole(roleRequest));
    }

    @PutMapping
    public ResponseEntity<Response<Role>> updateRole(@RequestBody Role roleRequest){
        return ResponseEntity.ok(roleService.updateRole(roleRequest));
    }

    @GetMapping
    public ResponseEntity<Response<List<Role>>> getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }


    @DeleteMapping("/{roleId}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable Long roleId){
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }



}

