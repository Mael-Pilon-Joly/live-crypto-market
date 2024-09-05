import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { CryptoDto } from '../dashboard/dashboard.service';

export enum TransactionType {
  BUY = 'BUY',
  SELL = 'SELL'
}

export interface Transaction {
  id?: number; 
  crypto: CryptoDto; 
  portfolio: Portfolio;  
  cryptoName: string;  
  cryptoAssetId: string; 
  type: TransactionType;  
  price: number;  
  quantity: number;  
  pricePerCrypto: number;  
  transactionDate: string; 
}

export interface PortfolioCrypto {
  id?: number;
  portfolio?: Portfolio;
  crypto: CryptoDto;
  assetId: string;
  name: string;
  quantity: number;
  purchaseDate: Date;
}

export interface PortfolioValue {
  id?: number;
  portfolio: Portfolio;
  totalValue: number;
  date: string;
}

export interface Portfolio {
  id?: number;
  user: User;
  listPortfolioCryptos: PortfolioCrypto[];
  listPortfolioValues: any;
  listTransactions: any;
  name: string;
  createdAt: Date;

}

export interface User {
  email: string;
  userId: string;
  listPortfolios: any;
}

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {

  createPortfolioCrypto(portfolioCrypto: PortfolioCrypto): any {
    return this.http.post<PortfolioCrypto>(this.apiUrl+'/portfolio-cryptos', portfolioCrypto);
  }

  constructor(private http: HttpClient) { }

  private apiUrl = environment.apiUrl;

  createPortfolio(portfolioName: string, email: string): Observable<Portfolio> {
    console.log("calling service to create portfolio")
    return this.getUserByEmail(email).pipe(
      switchMap(userToCreate => {
        console.log("email and userId:" + userToCreate.email +","+ userToCreate.userId)
        const portfolio: Portfolio = {
          user: userToCreate,
          listPortfolioCryptos: [],
          listPortfolioValues: [],
          listTransactions: [],
          name: portfolioName,
          createdAt: new Date()
        };
  
        return this.http.post<Portfolio>(this.apiUrl + '/portfolios', portfolio);
      })
    );
  }
  

  getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/email/${email}`);
  }

  getPortfolioById(id: number): Observable<Portfolio> {
    return this.http.get<Portfolio>(`${this.apiUrl}/portfolios/${id}`);
  }
  createTransaction(transaction: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl + '/transactions', transaction);
  }

  getPortfolioWorthByPortfolioId(id: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/portfolios/worth/${id}`);
  }

  getPortfolioValuesByDateRange(portfolioId: number, startDate: string, endDate: string): Observable<PortfolioValue[]> {
    return this.http.get<PortfolioValue[]>(`${this.apiUrl}/portfolio-values/portfolio/${portfolioId}/range/${startDate}/${endDate}`);
  }

  getPortfolioCryptos(portfolioId: number): Observable<PortfolioCrypto[]> {
    return this.http.get<PortfolioCrypto[]>(`${this.apiUrl}/portfolio-cryptos/portfolio/${portfolioId}`);
  }

  getTransactionByPortfolioId(portfolioId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/transactions/portfolio/${portfolioId}`);
  }
}
