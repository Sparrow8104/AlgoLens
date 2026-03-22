package com.algolens.algo_lens.dtos.insight;

import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.parsing.Problem;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemsetResultDTO implements Serializable {
   private List<ProblemDTO> problems;
}
