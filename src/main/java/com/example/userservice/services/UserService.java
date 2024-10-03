package com.example.userservice.services;

import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }
    public Token loginUser(String email, String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            throw new RuntimeException("User with email:" + email + "not found in db");
        }
        User user = optionalUser.get();
        if(bCryptPasswordEncoder.matches(password,user.getHashedPassword())){
            //Generate a token
            Token token = genrateToken(user);
            Token savedToken = tokenRepository.save(token);
            return savedToken;
        }
        return null;
    }
    public User signup(String name, String email, String password){
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void logout(String tokenvalue) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryTimeGreaterThan(tokenvalue,
                false,
                new Date());
        if (optionalToken.isEmpty()) {
            //throw invalid token exception
            return;
        }
        Token token = optionalToken.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }

    public User validate(String tokenvalue) {
        //First find out that the token with the value is present in the DB or not.
        //Expiry time of the token > current time and deleted should be false.
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryTimeGreaterThan(tokenvalue,
                false,
                new Date());
        if (optionalToken.isEmpty()) {
            return null;
        }
        return optionalToken.get().getUser();
    }

    private Token genrateToken(User user){
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        //Expiry time of the token is let's say 30 days from now.
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAfterCurrentTime = today.plusDays(30);

        Date expiryAt = Date.from(thirtyDaysAfterCurrentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryTime(expiryAt);
        return token;
    }
}
