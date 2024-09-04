import { Component, OnInit } from '@angular/core';
import { CryptoDto, CryptoHistory, DashboardService, ProfitableUser } from './dashboard.service';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Chart, ChartOptions, ChartData,  registerables } from 'chart.js';
import 'chartjs-adapter-moment';
import moment from 'moment-timezone';
import { Router } from '@angular/router';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgIf, NgFor, CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  top10MostValuableCryptos: CryptoDto[] = [];
  top10MostIncreased: any[] = [];
  top10MostDecreased: any[] = [];
  top10Trending: any[] = [];
  top10MostProfitableUsers: ProfitableUser[] = [];
  cryptoHistory: CryptoHistory[] = [];
  private chart: Chart | null = null;
  private chartTop10Trending: Chart | null = null;
  private chartTop10MostDecreased: Chart | null = null;



  constructor(private dashboardService: DashboardService, private router: Router) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.dashboardService.fetchAllCryptos().subscribe({
      next: (data) => {
        this.top10MostValuableCryptos = this.dashboardService.getTop10MostValuableCryptos();

      },
      error:
      (error) => {
        console.error('Error fetching cryptos:', error);
      }
    });

    this.dashboardService.getCryptoHistoryLastHour().subscribe({
      next: (data) => {
        this.top10MostIncreased = this.dashboardService.calculateTop10MostIncreased(data);
        this.displayChart(this.top10MostIncreased[0].assetId, true);
        this.top10MostDecreased = this.dashboardService.calculateTop10MostDecreased(data);
        this.displayChart(this.top10MostDecreased[0].assetId, false);
        this.top10Trending = this.dashboardService.calculateTop10Trending(data);
        this.renderTrendingChart(this.dashboardService.calculateTop10MostTrendingColor(this.top10Trending));
        this.dashboardService.getTop10MostProfitablePortfolios().subscribe({
          next: users => this.top10MostProfitableUsers = users,
          error: err => console.error("Error retrieving top 10 most profitable portfolios")
        })

      },
      error: (error) => {
        console.error('Error fetching historical data:', error);
      }
    }
    );
  }

  onNavigate(id: string) {
    this.router.navigate(['/stockdetails', id]);
  }

  getPercentageStyle(value: number): any {
    return {
      color: value > 0 ? 'green' : 'red'
    };
  }

 renderChart(cryptoHistory: CryptoHistory[], chartName: string, verb: string, increase: boolean): void {
    console.log("Rendering chart...");

    if (increase && this.chart) {
        this.chart.destroy();
    }

    if (!increase && this.chartTop10MostDecreased){
      this.chartTop10MostDecreased.destroy();
    }

    const parsedData = cryptoHistory.map(history => {
        const timestampParts = history.timestamp.toString().split(',').map(Number);

        if (timestampParts.length === 7) {
            const [year, month, day, hour, minute, second, millisecond] = timestampParts;
            const utcDate = new Date(Date.UTC(year, month - 1, day, hour+4, minute, second));  // Convert to UTC Date
            const localTimestamp = moment(utcDate).tz(moment.tz.guess()).valueOf();  // Convert to local timezone
            return { x: localTimestamp, y: history.priceUsd };
        } else {
            console.error(`Timestamp "${history.timestamp}" is in an unexpected format.`);
            return { x: NaN, y: NaN };
        }
    });

    parsedData.sort((a, b) => a.x - b.x);

    const minTime = Math.min(...parsedData.map(item => item.x));
    const maxTime = Math.max(...parsedData.map(item => item.x));

    if (increase) {
    this.chart = new Chart(chartName, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Crypto Price (USD) : Most ' + verb + ' value in the last hour',
                data: parsedData,  // Directly use parsedData with x and y properties
                borderColor: ' rgba(0, 255, 0)',
                borderWidth: 2,
                fill: true
            }]
        },
        options: {
            scales: {
                x: {
                    type: 'time',  // Ensure this is set to 'time'
                    time: {
                        unit: 'minute',  // Set the unit to match your data's granularity
                        tooltipFormat: 'll HH:mm',  // Customize this based on your needs
                        displayFormats: {
                            minute: 'h:mm a',  // Format to display time correctly
                        }
                    },
                    min: minTime,  // Use the calculated min time
                    max: maxTime,  // Use the calculated max time
                },
                y: {
                    beginAtZero: false
                }
            }
        }
      })
    } else {
      this.chartTop10MostDecreased = new Chart(chartName, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Crypto Price (USD) : Most ' + verb + ' value in the last hour',
                data: parsedData,  // Directly use parsedData with x and y properties
                borderColor: ' rgba(255, 0, 0)',
                borderWidth: 2,
                fill: true
            }]
        },
        options: {
            scales: {
                x: {
                    type: 'time',  
                    time: {
                        unit: 'minute',  
                        tooltipFormat: 'll HH:mm',  
                        displayFormats: {
                            minute: 'h:mm a', 
                        }
                    },
                    min: minTime, 
                    max: maxTime,  
                },
                y: {
                    beginAtZero: false
                }
            }
        }
      })
    };
}

  loadCryptoHistory(assetId: string, increase: boolean): void {
    console.log("loadCryptoHistory called")
    this.dashboardService.getCryptoHistoryForLast4Hours(assetId).subscribe({
      next: data => {
        this.cryptoHistory = data;
        console.log("this is cryptoHistory: "+ this.cryptoHistory + " this is data:" + data + "this is assetId:" + assetId)
        // After loading the data, render the chart
        console.log("Length of cryptoHistory:", this.cryptoHistory.length);
        console.log("Type of cryptoHistory:", typeof this.cryptoHistory);

        if (this.cryptoHistory && this.cryptoHistory.length > 0) {
          console.log("calling renderChart")
          if (increase == true) {
          this.renderChart(this.cryptoHistory, 'chartCanvas', 'increased', increase);
          } else {
          this.renderChart(this.cryptoHistory, 'chartCanvasTop10TrendingDown', 'decreased', increase);
          }
        } else {
          console.error('No history data available for this crypto.');
        }
      },
      error: error => {
        console.error('Error loading crypto history:', error);
      }
    });
  }

  renderTrendingChart(trendingData: { assetId: string, volumeIncrease: number, color: string }[]): void {
    const labels = trendingData.map(data => data.assetId);
    const data = trendingData.map(data => data.volumeIncrease);
    const backgroundColors = trendingData.map(data => data.color);
  
    if (this.chartTop10Trending) {
      this.chartTop10Trending.destroy(); // Destroy any existing chart before creating a new one
    }
  
    this.chartTop10Trending = new Chart('chartCanvasTop10MostTrending', {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Volume Increase (%)',
          data: data,
          backgroundColor: backgroundColors,
          borderColor: 'rgba(0, 0, 0, 0.1)',
          borderWidth: 1
        }]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function(value) {
                return value + "%"; // Add % to the y-axis values
              }
            }
          }
        }
      }
    });
  }
  
  displayChart(assetId: string, increase: boolean): void {
    // Load the crypto history and render the chart in the next block
    this.loadCryptoHistory(assetId, increase);
  }

}