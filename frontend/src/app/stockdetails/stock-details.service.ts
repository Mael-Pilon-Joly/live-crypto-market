import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CryptoDto } from '../dashboard/dashboard.service';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StockDetailsService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  fetchCryptoByAssetId(assetId: string) : Observable<CryptoDto> {
    return this.http.get<CryptoDto>(`${this.apiUrl}/cryptos/asset-id/${assetId}`);
  }

}
