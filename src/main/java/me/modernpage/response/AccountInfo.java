package me.modernpage.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo extends RepresentationModel<AccountInfo> {
    private String username;
    private String fullname;
    private String email;
    private Date birthdate;
    private String password;
    private String avatarUrl;
}
