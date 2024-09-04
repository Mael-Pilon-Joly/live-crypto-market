import { Component, OnInit } from '@angular/core';
import { fetchAuthSession, fetchUserAttributes } from "aws-amplify/auth";
import { FormsModule } from '@angular/forms';
import { Portfolio, PortfolioCrypto, PortfolioService, PortfolioValue, Transaction, TransactionType, User } from './portfolio.service';
import { NgFor, NgIf } from '@angular/common';
import { CryptoDto, DashboardService } from '../dashboard/dashboard.service';
import { StockDetailsService } from '../stockdetails/stock-details.service';
import { of, switchMap } from 'rxjs';
import { Chart, registerables } from 'chart.js';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from "@angular/material/form-field";
import {MatDatepickerModule} from '@angular/material/datepicker';
import moment from 'moment';

@Component({
  selector: 'app-portfolio',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor, MatFormFieldModule, MatInputModule, MatDatepickerModule],
  templateUrl: './portfolio.component.html',
  styleUrl: './portfolio.component.css'
})
export class PortfolioComponent implements OnInit {
  

  email: string = "";
  portfolioName: string = '';
  selectedPortfolioId: number | null = null;
  selectedPortfolio: Portfolio | null = null;
  user: User = {
    email: "",
    userId: "",
    listPortfolios: ""
  };
  portfolios: Portfolio[] = [];

  action: TransactionType = TransactionType.BUY;
  transactionType = TransactionType;

  cryptoSearchTerm: string = '';
  filteredCryptos: PortfolioCrypto[] = [];
  amount: number = 0;
  transactionResult: string | null = null;

  selectedCrypto: PortfolioCrypto| null = null;
  allCryptos: CryptoDto[] = [];
  currentCryptoWorth: number = 0;
  currentPortfolioWorth: number = 0;
  portfolioCryptos: PortfolioCrypto[] = [];

  startDate: Date = new Date(Date.now() - 86400000);;
  endDate: Date = new Date();
  portfolioValues: PortfolioValue[] = [];
  chart: any;
  pieChart: any;
  portfolioTotalSpent: number = 0;
  unrealizedGainLoss: number = 0;
  unrealizedGainLossPercentage: number = 0;

  constructor(
    private portfolioService: PortfolioService, 
    private dashboardService: DashboardService, 
    private stockDetailService: StockDetailsService) {
    Chart.register(...registerables);
    }


    ngOnInit(): void {
      this.loadAllCryptos();
    
      this.fetchUserAttributes()
        .then(userAttributes => {
          console.log(userAttributes.email);
          this.email = userAttributes.email;
    
          this.portfolioService.getUserByEmail(this.email).subscribe({
            next: (data) => {
              this.user = data;
              this.loadPortfolios()
                .then(() => {
                  this.loadPortfolioTransactions();
                  return this.loadPortfolioCryptos(); 
                })
                .then(() => {
                  console.log('Portfolios and portfolio cryptos loaded successfully');
                })
                .catch((error) => {
                  console.error('Error loading portfolios or portfolio cryptos', error);
                });
            },
            error: (error) => {
              console.error('Error retrieving user', error);
            }
          });
        })
        .catch((error) => {
          console.error('Error fetching user attributes', error);
        });
    }

 loadAllCryptos() {
  this.dashboardService.fetchAllCryptos().subscribe({
    next: cryptos => this.allCryptos = cryptos,
    error: error => console.error('Error loading cryptos:', error)
  }
  );
}

  async fetchUserAttributes(): Promise<any> {
    try {
      const user = await fetchUserAttributes();
      return user; // assuming `attributes` contains the user attributes including email
    } catch (error) {
      console.error('Error retrieving user attributes:', error);
      throw error;
    }
  }

  createPortfolio() {
    if (this.portfolioName.trim() !== '') {
      console.log('Creating portfolio:', this.portfolioName);

      this.portfolioService.createPortfolio(this.portfolioName, this.email).subscribe({
        next: (createdPortfolio) => {
          console.log('Portfolio created successfully:', createdPortfolio);
          this.loadPortfolios();
        },
        error: (error) => {
          console.error('Error creating portfolio:', error);
          // You can also add more specific error handling logic here
        }
     });
      this.portfolioName = ''; 
      // Reset the input field after creation
    } else {
      alert('Please enter a portfolio name.');
    }
  }

  loadPortfolios(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.portfolioService.getUserByEmail(this.email).pipe(
        switchMap(user => {
          this.user = user;
          this.portfolios = user.listPortfolios;
          console.log("portfolios", this.portfolios);
  
          if (this.portfolios && this.portfolios.length > 0) {
            this.selectedPortfolioId = this.portfolios[0].id!;
            this.selectedPortfolio = this.portfolios[0];
  
            return this.portfolioService.getPortfolioWorthByPortfolioId(this.selectedPortfolioId);
          } else {
            // If no portfolios, handle the empty state
            console.log('User has no portfolios.');
            this.selectedPortfolioId = null;
            this.selectedPortfolio = null;
            this.currentPortfolioWorth = 0;
            return of(null); 
          }
        })
      ).subscribe({
        next: totalWorth => {
          if (totalWorth !== null) {
            this.currentPortfolioWorth = totalWorth;
          }
          resolve(); 
        },
        error: (err) => {
          console.error('Error:', err);
          reject(err); 
        }
      });
    });
  }
  
  onPortfolioChange() {
    this.selectedPortfolio = this.portfolios.find(p => p.id === this.selectedPortfolioId) || null;
    this.loadPortfolioCryptos()
    }

  onSearchCrypto() {
    console.log("action:" + this.action)
    if (this.action ==  TransactionType.SELL && this.selectedPortfolioId) {
      const selectedPortfolio = this.portfolios.find(p => p.id === this.selectedPortfolioId);
      if (selectedPortfolio) {
        this.filteredCryptos = selectedPortfolio.listPortfolioCryptos.filter(
          crypto => crypto.name.toLowerCase().includes(this.cryptoSearchTerm.toLowerCase()) ||
                    crypto.assetId.toLowerCase().includes(this.cryptoSearchTerm.toLowerCase())
        );
      }
    } else if (this.action == TransactionType.BUY && this.selectedPortfolioId) {
      this.filteredCryptos = this.allCryptos.filter(
        crypto => crypto.name.toLowerCase().startsWith(this.cryptoSearchTerm.toLowerCase()) ||
                  crypto.assetId.toLowerCase().startsWith(this.cryptoSearchTerm.toLowerCase())
      ).map(crypto => this.convertToPortfolioCrypto(crypto));
    }
  }
  
  convertToPortfolioCrypto(crypto: CryptoDto): PortfolioCrypto {
    return {
      crypto: crypto,
      name: crypto.name,
      assetId: crypto.assetId,
      quantity: 0,  // Assuming quantity starts at 0 for new purchases
      purchaseDate: new Date(),  // Set to the current date
      portfolio: this.selectedPortfolio || undefined // Set the portfolioId to the selected one
    };
  }

  selectCrypto(crypto: PortfolioCrypto) {
    this.selectedCrypto = crypto;
    this.filteredCryptos = [];
    this.cryptoSearchTerm = crypto.assetId;
    
    if(this.selectedCrypto) {
    this.stockDetailService.fetchCryptoByAssetId(this.selectedCrypto?.assetId!).subscribe({
    next: cryptoAsset => this.currentCryptoWorth = cryptoAsset.priceUsd,
    error: err => console.log("Error retrieving user " + err)
    })
  }
  }

    executeTransaction() {
      if (!this.selectedPortfolioId || !this.selectedCrypto || this.amount <= 0) {
        this.transactionResult = 'Please fill in all fields.';
        return;
      }
  
      this.portfolioService.getPortfolioById(this.selectedPortfolioId).pipe(
        switchMap(currentPortfolio => {
          return this.portfolioService.getPortfolioCryptos(this.selectedPortfolioId!).pipe(
            switchMap(portfolioCryptos => {
              // Sum the quantities of all portfolioCryptos with the same assetId
              const totalQuantity = portfolioCryptos
                .filter(pc => pc.assetId === this.selectedCrypto!.assetId)
                .reduce((acc, pc) => acc + pc.quantity, 0);
  
              if (this.action === TransactionType.SELL) {
                
                if (totalQuantity === 0) {
                  this.transactionResult = 'You do not possess this cryptocurrency.';
                  return of(null); 
                }
  
                if (totalQuantity < this.amount) {
                  this.transactionResult = `You only possess ${totalQuantity} of this cryptocurrency.`;
                  return of(null); 
                }
              }
  
              return this.stockDetailService.fetchCryptoByAssetId(this.selectedCrypto!.assetId).pipe(
                switchMap(cryptoAsset => {
                  const transaction: Transaction = {
                    portfolio: currentPortfolio,
                    crypto: cryptoAsset,
                    quantity: this.amount,
                    type: this.action,
                    cryptoName: this.selectedCrypto!.name,
                    cryptoAssetId: this.selectedCrypto!.assetId,
                    price: cryptoAsset.priceUsd * this.amount,
                    pricePerCrypto: cryptoAsset.priceUsd,
                    transactionDate: new Date().toISOString()
                  };
  
                  return this.portfolioService.createTransaction(transaction);
                })
              );
            })
          );
        })
      ).subscribe({
        next: result => {
          if (result !== null) {
            this.transactionResult = "Transaction and PortfolioCrypto successfully completed";
            this.loadPortfolios();
            this.loadPortfolioTransactions();
            this.loadPortfolioCryptos();
          }
        },
        error: error => {
          console.error('Error executing transaction:', error);
          this.transactionResult = 'Transaction failed. Please try again.';
        }
      });
  }
  

  fetchPortfolioValues(): void {
    if (this.selectedPortfolioId && this.startDate && this.endDate) {
      const startDateStr = this.startDate.toISOString();
      const endDateStr = this.endDate.toISOString();

      this.portfolioService.getPortfolioValuesByDateRange(this.selectedPortfolioId, startDateStr, endDateStr).subscribe(data => {
        this.portfolioValues = data;
        this.createChart();
      });
    }
  }

  loadPortfolioCryptos(): void {
    if (this.selectedPortfolioId){
    this.portfolioService.getPortfolioCryptos(this.selectedPortfolioId).subscribe(data => {
    console.log('id:'+this.selectedPortfolioId)
    console.log('data' + data);
      this.portfolioCryptos = data;
      this.createPieChart();
    });
    }
  }

  loadPortfolioTransactions(): void {
    if (this.selectedPortfolioId) {
      this.portfolioService.getTransactionByPortfolioId(this.selectedPortfolioId).subscribe( transactions => {
          this.portfolioTotalSpent = 0;
          transactions.forEach(transaction => {
            transactions.forEach(() => console.log(transaction.type));

            if (transaction.type == TransactionType.BUY) {
              console.log("is type buy")
              this.portfolioTotalSpent += transaction.price * transaction.quantity;
            } else {
              console.log("is type sell")
              this.portfolioTotalSpent -= transaction.price * transaction.quantity;
            }
         })
          this.unrealizedGainLoss = this.currentPortfolioWorth - this.portfolioTotalSpent;
          this.unrealizedGainLossPercentage = (this.unrealizedGainLoss / this.portfolioTotalSpent) * 100;
        }
      )
    }
  }

  createChart(): void {

    if (this.chart) {
      this.chart.destroy();
  }

    const labels = this.portfolioValues.map(value => new Date(value.date).toLocaleString());
    const values = this.portfolioValues.map(value => value.totalValue);

    const parsedData = this.portfolioValues.map(value => {
      const timestampParts = value.date.toString().split(',').map(Number);

      if (timestampParts.length === 7) {
          const [year, month, day, hour, minute, second, millisecond] = timestampParts;
          const utcDate = new Date(Date.UTC(year, month - 1, day, hour+4, minute, second));  // Convert to UTC Date
          const localTimestamp = moment(utcDate).tz(moment.tz.guess()).valueOf();  // Convert to local timezone
          return { x: localTimestamp, y: value.totalValue };
      } else {
          console.error(`Timestamp "${value.date}" is in an unexpected format.`);
          return { x: NaN, y: NaN };
      }
  });

  
    this.chart = new Chart('canvas', {
      type: 'line',
      data: {
        datasets: [
          {
            label: 'Portfolio Value Over Time',
            data: parsedData,
            borderColor: '#3cba9f',
            fill: false
          }
        ]
      },
      options: {
        scales: {
          x: {
            type: 'time',
            time: {
              unit: 'hour',  
              tooltipFormat: 'MMM DD, YYYY HH:mm',  
              displayFormats: {
                hour: 'MMM DD, HH:mm',  
                day: 'MMM DD'  
              }
            },
            title: {
              display: true,
              text: 'Time'
            }
          },
          y: {
            title: {
              display: true,
              text: 'Total Value'
            }
          }
        }
      }
    });
  }  

  createPieChart(): void {

    if (this.pieChart){
      this.pieChart.destroy();
    }

    const groupedData = this.groupByCrypto(this.portfolioCryptos);

    console.log("grouped data:" + console.log(groupedData[0]))

    const labels = Object.keys(groupedData);
    const quantities = Object.values(groupedData);

    this.pieChart = new Chart('pieChartCanvas', {
      type: 'pie',
      data: {
        labels: labels,
        datasets: [
          {
            data: quantities,
            backgroundColor: this.generateColors(labels.length)
          }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'top'
          },
          tooltip: {
            callbacks: {
              label: (context) => `${context.label}: ${context.raw}`
            }
          }
        }
      }
    });
  }

  groupByCrypto(portfolioCryptos: PortfolioCrypto[]): { [key: string]: number } {
    return portfolioCryptos.reduce((acc, curr) => {
      const assetId = curr.assetId;
      if (!acc[assetId]) {
        acc[assetId] = 0;
      }
      acc[assetId] += curr.quantity;
      return acc;
    }, {} as { [key: string]: number });
  }

  generateColors(count: number): string[] {
    const colors = [];
    for (let i = 0; i < count; i++) {
      const color = `hsl(${(i * 360) / count}, 70%, 50%)`;
      colors.push(color);
    }
    return colors;
  }
}


