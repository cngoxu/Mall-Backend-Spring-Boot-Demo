package xyz.cngo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateProfileDTO {
    private String username;

    private String phone;

    private Integer age;

    private String gender;

    private String avatar;
}
