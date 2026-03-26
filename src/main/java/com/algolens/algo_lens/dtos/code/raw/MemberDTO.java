package com.algolens.algo_lens.dtos.code.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO implements Serializable {
    private String handle;
}
