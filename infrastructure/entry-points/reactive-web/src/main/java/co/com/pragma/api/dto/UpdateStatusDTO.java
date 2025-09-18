package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusDTO(
        @NotBlank(message = "La decisión no puede estar vacía.")
        @Pattern(regexp = "^(APPROVED|REJECTED)$", message = "La decisión debe ser 'APPROVED' o 'REJECTED'")
        String decision
) {
}
