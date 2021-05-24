package com.ignite.models;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SportClub implements Serializable {
    private String name;
    private Integer creationYear;
}
