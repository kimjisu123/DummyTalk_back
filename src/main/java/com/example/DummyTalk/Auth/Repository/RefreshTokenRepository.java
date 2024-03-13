package com.example.DummyTalk.Auth.Repository;


import com.example.DummyTalk.Auth.Entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


}
