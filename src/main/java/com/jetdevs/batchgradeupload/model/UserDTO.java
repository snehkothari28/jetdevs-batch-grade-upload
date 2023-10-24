package com.jetdevs.batchgradeupload.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    @JsonIgnore
    private Integer id;
    private String name;
    private Boolean status;
    @ToString.Exclude // Exclude password from printing in logs
    private String password;
    private Integer roleId; // default role id will be user when creating role
}
