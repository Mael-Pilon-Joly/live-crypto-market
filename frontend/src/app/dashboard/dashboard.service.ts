import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { forkJoin, map, Observable, switchMap } from 'rxjs';
import { Portfolio, PortfolioService, TransactionType } from '../portfolio/portfolio.service';

export interface CryptoDto {
  id?: number;
  assetId: string;
  name: string;
  typeIsCrypto: boolean;
  dataQuoteStart: string;
  dataQuoteEnd: string;
  dataTradeStart: string;
  dataTradeEnd: string;
  dataSymbolsCount: number;
  volume1hrsUsd: number;
  volume1dayUsd: number;
  volume1mthUsd: number;
  priceUsd: number;
  standardPriceUsd?: number; 
}

export interface CryptoHistory {
  assetId: string;
  priceUsd: number;
  timestamp: string;  
  volume_1hrsUsd: number;
}

export interface ProfitableUser {
  email: string;
  portfolioName: string;
  percentageProfit: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = environment.apiUrl;
  private cryptos: CryptoDto[] = [];

  constructor(private http: HttpClient, private portfolioService: PortfolioService) {}


  fetchAllCryptos(): Observable<CryptoDto[]> {
    return this.http.get<CryptoDto[]>(this.apiUrl + "/cryptos").pipe(
      map((data) => {
        this.cryptos = data;
        return this.cryptos;
      })
    );
  }

  getCryptoHistoryLastHour(): Observable<CryptoHistory[]> {
    return this.http.get<CryptoHistory[]>(this.apiUrl + "/crypto-history/last-hour");
  }

  getCryptoHistoryForLast4Hours(assetId: string): Observable<CryptoHistory[]> {
  return this.http.get<CryptoHistory[]>(`${this.apiUrl}/crypto-history/last-4-hours/${assetId}`);
}

  getAllPortfolios(): Observable<Portfolio[]> {
    return this.http.get<Portfolio[]>(this.apiUrl + "/portfolios/portfolios");
  }

  loadAllPortfolios(): any[] {
    let portfolioWorthsAndUsers: any[] = [];
  
    this.getAllPortfolios().pipe(
      switchMap(portfolios => {
        const portfolioObservables = portfolios.map(portfolio =>
          this.portfolioService.getPortfolioWorthByPortfolioId(portfolio.id!).pipe(
            map(worth => ({
              user: portfolio.user.email,
              portfolio_id: portfolio.id,
              portfolio_name: portfolio.name,
              worth: worth
            }))
          )
        );
        return forkJoin(portfolioObservables);
      })
    ).subscribe({
      next: results => {
        portfolioWorthsAndUsers = results;
        console.log(portfolioWorthsAndUsers); // Ensure this contains the expected results
      },
      error: err => console.error("Error retrieving portfolio worths", err)
    });

    return portfolioWorthsAndUsers;
  }

  getTop10MostProfitablePortfolios(){
    return this.http.get<ProfitableUser[]>(this.apiUrl+ "/users/top10mostprofitableusers");
  }



  getTop10MostValuableCryptos(): CryptoDto[] {
    // Find Bitcoin's price in the list, the threshold for realistic usd value
    const bitcoin = this.cryptos.find(crypto => crypto.assetId === 'BTC');
  
    if (!bitcoin) {
      console.error('Bitcoin data is not available');
      return [];
    }
  
    const bitcoinPrice = bitcoin.priceUsd;
  
    return this.cryptos
      .filter(crypto => 
        crypto.typeIsCrypto === true && 
        crypto.priceUsd > 0 
      )
      .map(crypto => {
        return {
          ...crypto,
          standardPriceUsd: crypto.priceUsd 
        };
      })
      .sort((a, b) => b.standardPriceUsd - a.standardPriceUsd)
      .slice(0, 10);
  }

  calculateTop10MostIncreased(history: CryptoHistory[]): any[] {
    console.log('History Data:', history);
  
    const groupedHistory: { [key: string]: CryptoHistory[] } = history.reduce((acc, cur) => {
      if (!acc[cur.assetId]) {
        acc[cur.assetId] = [];
      }
      acc[cur.assetId].push(cur);
      return acc;
    }, {} as { [key: string]: CryptoHistory[] });
  
    console.log('Grouped History:', groupedHistory);
  
    const top10 = Object.keys(groupedHistory)
      .map(assetId => {
        const assetHistory = groupedHistory[assetId];
        assetHistory.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

        const firstPrice = assetHistory[0]?.priceUsd;  
        const lastPrice = assetHistory[assetHistory.length - 1]?.priceUsd; 
        if (firstPrice == null || lastPrice == null) {
          console.log(`Skipping ${assetId} due to missing price data`);
          return null;
        }
  
        const increase = ((lastPrice - firstPrice) / firstPrice) * 100;
  
        console.log(`Asset ID: ${assetId}, First Price: ${firstPrice}, Last Price: ${lastPrice}, Increase: ${increase}%`);
  
        return { assetId, increase, lastPrice };
      })
      .filter(item => item !== null) 
      .sort((a, b) => b.increase - a.increase)
      .slice(0, 10);
  
    console.log('Top 10 Most Increased:', top10);
  
    return top10;
  }

  calculateTop10MostDecreased(history: CryptoHistory[]): any[] {

    console.log('History Data:', history.length);
  
    // Group the history by assetId
    const groupedHistory: { [key: string]: CryptoHistory[] } = history.reduce((acc, cur) => {
      if (!acc[cur.assetId]) {
        acc[cur.assetId] = [];
      }
      acc[cur.assetId].push(cur);
      return acc;
    }, {} as { [key: string]: CryptoHistory[] });
  
    console.log('Grouped History:', groupedHistory);
  
    // Sort each asset's history by timestamp to ensure correct first and last prices
    Object.keys(groupedHistory).forEach(assetId => {
      groupedHistory[assetId].sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());
    });

    // Filter out assets where the first price is 0
  const filteredHistory = Object.keys(groupedHistory).reduce((acc, assetId) => {
    if (groupedHistory[assetId][0]?.priceUsd - 0.001 > Number.EPSILON*Number.EPSILON) {
      acc[assetId] = groupedHistory[assetId];
    } else {
      console.log(`Skipping ${assetId} because the first price is 0`);
    }
    return acc;
  }, {} as { [key: string]: CryptoHistory[] });
  console.log("filteredHistoryLength:"+ filteredHistory)

    // Calculate the decrease for each asset and get the top 10
    const top10 = Object.keys(filteredHistory)
      .map(assetId => {
        const assetHistory = groupedHistory[assetId];

        console.log("not skipping")
  
        const firstPrice = assetHistory[0].priceUsd;
        const lastPrice = assetHistory[assetHistory.length - 1].priceUsd;
  
        if (firstPrice == null || lastPrice == null) {
          console.log(`Skipping ${assetId} due to missing price data`);
          return null;
        }

        if (lastPrice - firstPrice > Number.EPSILON*Number.EPSILON) {
          return null;
        }
  
        const decrease =  (lastPrice-firstPrice/ firstPrice) * 100;
  
        console.log(`Asset ID: ${assetId}, First Price: ${firstPrice}, Last Price: ${lastPrice}, Decrease: ${decrease}%`);
  
        return { assetId, decrease, lastPrice };
      })
      .filter(item => item !== null)
      .sort((a, b) => a.decrease - b.decrease)  // Sort by decrease in ascending order
      .slice(0, 10);
  
    console.log('Top 10 Most Decreased:', top10);
  
    return top10;
  }

  calculateTop10Trending(history: CryptoHistory[]): any[] {
    console.log('History Data:', history);
  
    const groupedHistory: { [key: string]: CryptoHistory[] } = history.reduce((acc, cur) => {
      if (!acc[cur.assetId]) {
        acc[cur.assetId] = [];
      }
      acc[cur.assetId].push(cur);
      return acc;
    }, {} as { [key: string]: CryptoHistory[] });
  
    console.log('Grouped History:', groupedHistory);
  
    const top10 = Object.keys(groupedHistory)
      .map(assetId => {
        const assetHistory = groupedHistory[assetId];
        
        assetHistory.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());
  
        const firstVolume = assetHistory[0]?.volume_1hrsUsd;
        const lastVolume = assetHistory[assetHistory.length - 1]?.volume_1hrsUsd;
  
        if (firstVolume == null || lastVolume == null || firstVolume === 0) {
          console.log(`Skipping ${assetId} due to missing or zero volume data`);
          return null;
        }
  
        const percentageIncrease = ((lastVolume - firstVolume) / firstVolume) * 100;
  
        console.log(`Asset ID: ${assetId}, First Volume = ${firstVolume}, Last Volume = ${lastVolume}, Percentage Increase = ${percentageIncrease}%`);
  
        return { assetId, percentageIncrease, lastVolume };
      })
      .filter(item => item !== null)
      .sort((a, b) => b.percentageIncrease - a.percentageIncrease)  // Sort by the largest percentage increase
      .slice(0, 10);
  
    console.log('Top 10 Trending:', top10);
  
    return top10;
  }
  
  calculateTop10MostTrendingColor(cryptos: {
    assetId: string;
    percentageIncrease: number;
    lastVolume: number;
    }[]
   ): { assetId: string, volumeIncrease: number, color: string }[] {
  
    // Prepare the data with colors based on the volume increase
    return cryptos.map(crypto => {
      const color = this.getVolumeColor(crypto.percentageIncrease);
      return {
        assetId: crypto.assetId,
        volumeIncrease: crypto.percentageIncrease,
        color: color
      };
    });
  }
  
  getVolumeColor(volumeIncrease: number): string {
    // Calculate the color based on the volume increase percentage
    const greenIntensity = Math.min(Math.max(volumeIncrease, 0), 100); // Clamp between 0 and 100
    return `rgba(0, ${greenIntensity * 2.55}, 0, 1)`; // Green intensity varies
  }

}

