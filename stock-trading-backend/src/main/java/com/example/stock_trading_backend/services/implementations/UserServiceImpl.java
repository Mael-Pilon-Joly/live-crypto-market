package com.example.stock_trading_backend.services.implementations;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.stock_trading_backend.entities.Portfolio;
import com.example.stock_trading_backend.entities.Transaction;
import com.example.stock_trading_backend.entities.User;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;
import com.example.stock_trading_backend.repositories.PortfolioRepository;
import com.example.stock_trading_backend.repositories.TransactionRepository;
import com.example.stock_trading_backend.repositories.UserRepository;
import com.example.stock_trading_backend.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioServiceImpl portfolioService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public User createUser(String token, String email) {
        DecodedJWT jwt = JWT.decode(token);
        String userId = jwt.getClaim("username").asString();
        User userToCreate = new User();
        userToCreate.setEmail(email);
        userToCreate.setUserId(userId);
        return userRepository.save(userToCreate);
    }

    @Override
    public User getUserById(Long id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Override
    public User updateUser(Long id, User user) throws EntityNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));

        // Update fields
        existingUser.setEmail(user.getEmail());
        // Add more fields to update if necessary

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) throws EntityNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        userRepository.delete(existingUser);
    }

    @Override
    public User getUserByEmail(String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email " + email));
    }

    public List<ProfitableUserDto> retrieveTop10MostProfitableUsers() throws EntityNotFoundException {
        List<ProfitableUserDto> mostProfitableUsers = new ArrayList<>();
        List<Portfolio> portfolios = portfolioRepository.findAll();

        for (Portfolio portfolio: portfolios) {
            double totalSpent = 0;
           List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());

           for (Transaction transaction: transactions) {
               if (transaction.getType() == Transaction.TransactionType.BUY){
                   totalSpent += transaction.getPrice() * transaction.getQuantity();
               } else {
                   totalSpent -= transaction.getPrice() * transaction.getQuantity();
               }
           }

           double currentPortfolioWorth = portfolioService.getCurrentPortfolioWorth(portfolio.getId());

           double percentageProfit = ((currentPortfolioWorth - totalSpent) / totalSpent) * 100;

           mostProfitableUsers.add(new ProfitableUserDto(portfolio.getUser().getEmail(), portfolio.getName(), percentageProfit));
        }

        return mostProfitableUsers;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static
    public class ProfitableUserDto {
        private String email;
        private String portfolioName;
        private double percentageProfit;
    }

}

