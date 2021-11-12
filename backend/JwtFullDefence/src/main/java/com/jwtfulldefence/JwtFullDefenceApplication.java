package com.jwtfulldefence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JwtFullDefenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtFullDefenceApplication.class, args);
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    



    /*TODO -> Add Validation*/
    /*TODO -> Splt Security Constants Variables on their own Java Classes*/
    /*TODO -> ReStructure the roles for more flexibility*/
    /*TODO -> Change The Algorith on Jwt*/
    /*TODO -> Store The JWT TOKEN ON COOKIE SESSION*/
    /*TODO -> Add usernameandPasswordAuthenticationFilter and change the url*/
    /*TODO -> Hide the alg propertie on jwt token */
    /*TODO -> Create a Two Step Validation via localFile
              verification from device folder
              and create the email with the file which verifies the user */
    /*TODO -> Create Email Template Service*/


}
