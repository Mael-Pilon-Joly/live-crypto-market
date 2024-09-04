package com.example.stock_trading_backend.services.interfaces;

import com.example.stock_trading_backend.entities.User;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;

public interface UserService {
    User createUser(String token, String email);
    User getUserById(Long id) throws EntityNotFoundException, EntityNotFoundException;
    User updateUser(Long id, User user) throws EntityNotFoundException;
    void deleteUser(Long id) throws EntityNotFoundException;
    User getUserByEmail(String email) throws EntityNotFoundException;
}
