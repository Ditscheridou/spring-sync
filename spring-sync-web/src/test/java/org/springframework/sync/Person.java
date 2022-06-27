package org.springframework.sync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

  private static final long serialVersionUID = 1L;

  private String firstName;

  private String lastName;

}
