package co.com.pragma.model.loan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Status {

    Long id;
    String name;
    String description;


}
