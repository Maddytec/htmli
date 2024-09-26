package com.maddytec.htmli.model;

import com.maddytec.htmli.exceptions.validations.NoHtml;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {
    //@NoHtml
    private String name;

    //@NoHtml
    private String lastname;

    @Email
    private String email;

}
