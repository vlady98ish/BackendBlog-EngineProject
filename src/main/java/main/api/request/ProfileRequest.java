package main.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProfileRequest {
    @Nullable
    public MultipartFile photo;
    public int removePhoto;
    public String name;
    public String email;
    public String password;
}