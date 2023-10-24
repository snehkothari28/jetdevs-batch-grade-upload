package com.jetdevs.batchgradeupload.model;

import lombok.Data;
import lombok.ToString;

@Data
public class UserDTO {
    private String name;
    private boolean status;
    @ToString.Exclude // Exclude password from printing in logs
    private String password;
    private int roleId; // default role id will be user when creating role
}
