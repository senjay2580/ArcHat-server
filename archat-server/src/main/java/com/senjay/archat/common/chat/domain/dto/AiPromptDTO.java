package com.senjay.archat.common.chat.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiPromptDTO {
    @NotBlank
    String msg;
    @NotNull
    String memoryId;
    String prompt;

}
