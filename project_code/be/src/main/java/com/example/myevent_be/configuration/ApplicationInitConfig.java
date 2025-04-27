package com.example.myevent_be.configuration;

import com.example.myevent_be.entity.Role;
import com.example.myevent_be.entity.User;

import com.example.myevent_be.repository.RoleRepository;
import com.example.myevent_be.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin("http://localhost:3000")
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
//    @ConditionalOnProperty(
//            prefix = "spring",
//            value = "datasource.driverClassName",
//            havingValue = "driverClassName: \"com.mysql.cj.jdbc.Driver"
//    )
    @ConditionalOnMissingBean(ApplicationRunner.class)
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository){
        return args -> {
            // Kiểm tra nếu người dùng với email 'admin123@gmail.com' chưa tồn tại
            if (userRepository.findByEmail("admin123@gmail.com").isEmpty()) {
                // Lấy vai trò ADMIN từ cơ sở dữ liệu
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseGet(() -> {
                            // Tạo vai trò ADMIN nếu chưa tồn tại
                            Role newRole = new Role();
                            newRole.setName("ADMIN");
                            return roleRepository.save(newRole);
                        });

                // Kiểm tra nếu role SUPPLIER chưa tồn tại, nếu không có thì tạo
                Role supplierRole = roleRepository.findByName("SUPPLIER")
                        .orElseGet(() -> {
                            // Tạo vai trò SUPPLIER nếu chưa tồn tại
                            log.info("Role SUPPLIER chưa tồn tại, tiến hành tạo mới...");
                            Role newRole = new Role();
                            newRole.setName("SUPPLIER");
                            return roleRepository.save(newRole);
                        });

                // Kiểm tra nếu role SUPPLIER chưa tồn tại, nếu không có thì tạo
                Role managerRole = roleRepository.findByName("MANAGER")
                        .orElseGet(() -> {
                            // Tạo vai trò MANAGER nếu chưa tồn tại
                            log.info("Role MANAGER chưa tồn tại, tiến hành tạo mới...");
                            Role newRole = new Role();
                            newRole.setName("MANAGER");
                            return roleRepository.save(newRole);
                        });

                // Tạo người dùng mới với vai trò ADMIN
                User user = User.builder()
                        .first_name("admin")
                        .last_name("admin")
                        .email("admin123@gmail.com")
                        .avatar("D:\\myevent\\myevent-be\\myevent-be\\src\\img")
                        .password(passwordEncoder.encode("admin"))
                        .role(adminRole) // Gán vai trò ADMIN cho người dùng
                        .build();
    
                // Lưu người dùng vào cơ sở dữ liệu
                userRepository.save(user);

                log.warn("Admin user has been created with password: admin, please change it");
            }
        };
    }
}
