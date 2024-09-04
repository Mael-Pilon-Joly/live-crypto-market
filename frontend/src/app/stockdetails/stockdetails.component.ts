import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CryptoDto, CryptoHistory, DashboardService } from '../dashboard/dashboard.service';
import { StockDetailsService } from './stock-details.service';
import { NgIf } from '@angular/common';
import { Chart } from 'chart.js';
import moment from 'moment';

@Component({
  selector: 'app-stockdetails',
  standalone: true,
  imports: [NgIf],
  templateUrl: './stockdetails.component.html',
  styleUrl: './stockdetails.component.css'
})
export class StockdetailsComponent  implements OnInit {
  id!: string;
  cryptoDto: CryptoDto | null = null;
  private chartPrice: Chart | null = null;
  private chartVolume: Chart | null = null;
  cryptoHistory: CryptoHistory[] = [];


  constructor(private route: ActivatedRoute, private stockDetailsService: StockDetailsService, private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id')!;
    this.stockDetailsService.fetchCryptoByAssetId(this.id).subscribe({
      next: (data) => this.cryptoDto = data,
      error: (error) => console.error('Error fetching crypto:', error)
    });
    this.displayChart(this.id, true);
    this.displayChart(this.id, false);
  }

  loadCryptoHistory(assetId: string, price: boolean): void {
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
          if (price == true) {
          this.renderChart(this.cryptoHistory, 'chartCanvasPrice', 'increased', price, 'price');
          } else {
          this.renderChart(this.cryptoHistory, 'chartCanvasVolume', 'trending', price, 'volume per h');
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

  renderChart(cryptoHistory: CryptoHistory[], chartName: string, verb: string, increase: boolean, unity: string): void {
    console.log("Rendering chart...");

    // Destroy the existing chart if it exists
    if (increase && this.chartPrice) {
        this.chartPrice.destroy();
    }

    if (!increase && this.chartVolume){
      this.chartVolume.destroy();
    }

    // Convert your timestamp components to Date objects and calculate labels
    const parsedData = cryptoHistory.map(history => {
      if (increase) {
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
      } else {
        const timestampParts = history.timestamp.toString().split(',').map(Number);

        if (timestampParts.length === 7) {
            const [year, month, day, hour, minute, second, millisecond] = timestampParts;
            const utcDate = new Date(Date.UTC(year, month - 1, day, hour+4, minute, second));  // Convert to UTC Date
            const localTimestamp = moment(utcDate).tz(moment.tz.guess()).valueOf();  // Convert to local timezone
            return { x: localTimestamp, y: history.volume_1hrsUsd };
        } else {
            console.error(`Timestamp "${history.timestamp}" is in an unexpected format.`);
            return { x: NaN, y: NaN };
        }
      }
    });

    // Sort the parsed data by timestamp
    parsedData.sort((a, b) => a.x - b.x);

    // Determine the minimum and maximum time values using the sorted data
    const minTime = Math.min(...parsedData.map(item => item.x));
    const maxTime = Math.max(...parsedData.map(item => item.x));

    // Create the chart
    if (increase) {
    this.chartPrice = new Chart(chartName, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Crypto Price (USD) : Most ' + verb + ' ' + unity + ' in the last hour',
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
      this.chartVolume = new Chart(chartName, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Crypto Price (USD) : Most ' + verb + ' ' + unity + ' in the last hour',
                data: parsedData,  // Directly use parsedData with x and y properties
                borderColor: ' rgba(255, 0, 0)',
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
    };
}


  displayChart(assetId: string, increase: boolean): void {
    // Load the crypto history and render the chart in the next block
    this.loadCryptoHistory(assetId, increase);
  }
}
