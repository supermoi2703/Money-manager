package com.springboot.moneymanager.service;

import com.springboot.moneymanager.dto.AuthDTO;
import com.springboot.moneymanager.dto.ProfileDTO;
import com.springboot.moneymanager.entity.ProfileEntity;
import com.springboot.moneymanager.repository.ProfileRepository;
import com.springboot.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MailService mailService;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    // hàm này sẽ được gọi khi người dùng đăng ký tài khoản, nó sẽ tạo một ProfileEntity mới từ ProfileDTO
    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        //gui email
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate Your Account";
        String body =  "Click on link to active your account: " + activationLink;
        mailService.sendMail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    // hàm này sẽ được gọi khi người dùng click vào link kích hoạt trong email,
    // nó sẽ tìm kiếm ProfileEntity dựa trên activationToken,
    // nếu tìm thấy thì sẽ kích hoạt tài khoản bằng cách đặt isActive thành true và xóa activationToken
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profile.setActivationToken(null);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    // hàm này sẽ được gọi khi người dùng đăng nhập, nó sẽ kiểm tra xem tài khoản đã được kích hoạt chưa trước khi cho phép đăng nhập
    public boolean isAcountActivated(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    // hàm này sẽ được gọi trong các API cần xác thực người dùng,
    // nó sẽ lấy thông tin người dùng hiện tại từ SecurityContext và trả về một ProfileEntity tương ứng
    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profile not found with email: " + authentication.getName()));
    }

    // hàm này sẽ được gọi để lấy thông tin hồ sơ công khai của người dùng,
    // nếu email được cung cấp thì sẽ lấy thông tin của người dùng đó, nếu không thì sẽ lấy thông tin của người dùng hiện tại
    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null){
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Profile not found with email: " + email));
        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    // hàm này sẽ được gọi khi người dùng đăng nhập,
    // nó sẽ xác thực thông tin đăng nhập và nếu hợp lệ thì sẽ tạo một token JWT và trả về token cùng với thông tin hồ sơ công khai của người dùng
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            //Generate token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
